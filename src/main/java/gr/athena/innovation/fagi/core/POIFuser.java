package gr.athena.innovation.fagi.core;

import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.Action;
import gr.athena.innovation.fagi.model.AmbiguousDataset;
import gr.athena.innovation.fagi.rule.RuleSpecification;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.EntityData;
import gr.athena.innovation.fagi.model.FusionLog;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.utils.RDFUtils;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fusion core class. Contains methods for the fusion process.
 *
 * @author nkarag
 */
public class POIFuser implements Fuser {

    private static final Logger LOG = LogManager.getLogger(POIFuser.class);
    private int linkedEntitiesNotFoundInDataset = 0;
    private int fusedPairsCount = 0;
    private int rejectedCount = 0;
    private final Model tempModelA = ModelFactory.createDefaultModel();
    private final Model tempModelB = ModelFactory.createDefaultModel();
    private final List<String> fusionLogBuffer = new ArrayList<>();
    private final boolean verbose = Configuration.getInstance().isVerbose();
    private Double averageConfidence = null;
    private Double averageGain = null;
    private double maxGain = 0.0;

    /**
     * Fuses all links using the Rules defined in the XML file.
     *
     * @param config The configuration object.
     * @param ruleSpec The rule specification object.
     * @param functionMap The map containing the available functions.
     */
    @Override
    public List<LinkedPair> fuseAll(Configuration config, RuleSpecification ruleSpec,
            Map<String, IFunction> functionMap) throws WrongInputException, IOException {

        List<LinkedPair> fusedList = new ArrayList<>();

        linkedEntitiesNotFoundInDataset = 0;

        Model left = LeftDataset.getLeftDataset().getModel();
        Model right = RightDataset.getRightDataset().getModel();
        LinksModel links = LinksModel.getLinksModel();

        EnumOutputMode mode = config.getOutputMode();

        if (verbose) {
            //clean fusion log if exists already
            if (new File(config.getFusionLog()).exists()) {
                FileChannel.open(Paths.get(config.getFusionLog()), StandardOpenOption.WRITE).truncate(0).close();
            }
        }

        for (Link link : links.getLinks()) {

            if (link.isEnsemble()) {
                LOG.trace("resolving ensemble link... " + link.getKey());

                Map<String, Model> modelsA = new HashMap<>();
                Map<String, Model> modelsB = new HashMap<>();

                //construct models
                //create the jena models for each node of the pair and remove them from the source models.
                for (String a : link.getEnsemblesA()) {
                    Model tempA = constructEntityDataModel(a, left, tempModelA, config.getOptionalDepth());
                    modelsA.put(a, tempA);
                }

                if (link.getEnsemblesA().isEmpty()) {
                    Model modelA = constructEntityDataModel(link.getNodeA(), left, tempModelA, config.getOptionalDepth());
                    modelsA.put(link.getNodeA(), modelA);
                }

                for (String b : link.getEnsemblesB()) {
                    Model tempB = constructEntityDataModel(b, right, tempModelB, config.getOptionalDepth());
                    modelsB.put(b, tempB);
                }

                if (link.getEnsemblesB().isEmpty()) {
                    Model modelB = constructEntityDataModel(link.getNodeB(), right, tempModelB, config.getOptionalDepth());
                    modelsB.put(link.getNodeB(), modelB);
                }

                //validate
                //the validation accepts/rejects nodes in the ensemble. 
                //If the nodes from either A, B gets empty, the whole ensemble gets rejected
                EnsembleValidator ensembleValidator = new EnsembleValidator();

                boolean accepted = ensembleValidator.validateEnsemble(link, functionMap, ruleSpec, modelsA, modelsB);

                rejectedCount = ensembleValidator.getRejected();

                //fuse
                EnsembleFuser ensembleFuser = new EnsembleFuser();
                LinkedPair pair = new LinkedPair(EnumDatasetAction.UNDEFINED);
                Model fusedModel;
                
                if(accepted){
                    fusedModel = ensembleFuser.fuseEnsemble(link, modelsA, modelsB);
                    pair.setRejected(false);
                } else {
                    fusedModel = resolveFusedModelForRejectedEnsemble(link, modelsA, modelsB);
                    pair.setRejected(true);
                }

                pair.setLink(link);

                Entity fusedEntity = new Entity();
                EntityData entityData = new EntityData();
                entityData.setModel(fusedModel);

                fusedEntity.setEntityData(entityData);

                pair.setFusedEntity(fusedEntity);

                fusedList.add(pair);

                fusedPairsCount++;

                continue;
            }

            LOG.info("resolving simple link... " + link.getKey());

            //create the jena models for each node of the pair and remove them from the source models.
            Model modelA = constructEntityDataModel(link.getNodeA(), left, tempModelA, config.getOptionalDepth());
            Model modelB = constructEntityDataModel(link.getNodeB(), right, tempModelB, config.getOptionalDepth());

            if (modelA.size() == 0 || modelB.size() == 0) {  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            LinkedPair linkedPair = fuseLink(link, modelA, modelB, ruleSpec, functionMap, mode);
            if (linkedPair.isRejected()) {
                rejectedCount++;
            } else {
                fusedPairsCount++;
            }

            //Add scores only on accepted pairs.
            if (!linkedPair.isRejected()) {
                //Add interlinking score, fusion score, fusion confidence
                Model fusedModel = linkedPair.getFusedEntity().getEntityData().getModel();

                String fusedUri = resolveURI(mode, link.getNodeA(), link.getNodeB());

                Statement interlinkingScore;
                Float score = link.getScore();
                if (score != null) {
                    interlinkingScore = RDFUtils.getInterlinkingScore(fusedUri, score, modelA, modelB);
                    fusedModel.add(interlinkingScore);
                }

                Statement fusionGain = RDFUtils.getFusionGainStatement(fusedUri, link.getNodeA(), link.getNodeB(), modelA, modelB, fusedModel);
                Statement fusionConfidence = RDFUtils.getFusionConfidenceStatement(fusedUri, modelA, modelB, fusedModel);

                fusedModel.removeAll((Resource) null, ResourceFactory.createProperty(Namespace.FUSION_GAIN_NO_BRACKETS), (RDFNode) null);
                fusedModel.removeAll((Resource) null, ResourceFactory.createProperty(Namespace.FUSION_CONFIDENCE_NO_BRACKETS), (RDFNode) null);
                fusedModel.removeAll((Resource) null, ResourceFactory.createProperty(Namespace.INTERLINKING_SCORE), (RDFNode) null);

                fusedModel.add(fusionGain);
                fusedModel.add(fusionConfidence);

                double confidence = Double.parseDouble(fusionConfidence.getString());

                if (averageConfidence == null) {
                    averageConfidence = confidence;
                }

                averageConfidence = (averageConfidence + confidence) / 2;

                Double gain = RDFUtils.getLastFusionGainFromLiteral(fusionGain.getLiteral());
                if (averageGain == null) {
                    averageGain = gain;
                }

                if (gain > maxGain) {
                    maxGain = gain;
                }

                FusionLog log = linkedPair.getFusionLog();
                averageGain = (averageGain + gain) / 2;
                log.setConfidenceScore(fusionConfidence.getString());

                if (verbose) {
                    addProvenanceToModel(fusedUri, log, fusedModel);
                }
            }

            //add accepted and rejected to fused list. Fusion mode treats them differently at combine.
            fusedList.add(linkedPair);
        }

        //corner case when all links got rejected
        if (averageGain == null) {
            averageGain = 0.0;
        }

        if (averageConfidence == null) {
            averageConfidence = 0.0;
        }

        //flush fusionLogBuffer if not empty
        if (verbose) {
            if (fusionLogBuffer.size() > 0) {
                writeFusionLog(fusionLogBuffer);
                fusionLogBuffer.clear();
            }
        }

        //links.getLinks().removeAll(links.getRejected());
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);

        return fusedList;
    }

    private void addProvenanceToModel(String fusedUri, FusionLog log, Model fusedModel) {

        String provNodeString = Namespace.Prov.PROV_BOOK + RDFUtils.getIdFromResource(fusedUri);
        Resource uri = ResourceFactory.createResource(fusedUri);
        Property prop = ResourceFactory.createProperty(Namespace.Prov.DERIVED);
        Resource provNode = ResourceFactory.createResource(provNodeString);

        //type of agent
        Property typeOf = ResourceFactory.createProperty(Namespace.Prov.RDF_TYPE);
        Resource agentResource = ResourceFactory.createResource(Namespace.Prov.AGENT);

        //default fusion action
        Property defaultFusionActionProperty = ResourceFactory.createProperty(Namespace.Prov.PROV_DEFAULT_FUSION_ACTION);
        Literal defaultActionLiteral = ResourceFactory.createStringLiteral(log.getDefaultFusionAction().toString());

        Statement provDefaultFusionAction = ResourceFactory.createStatement(provNode,
                defaultFusionActionProperty, defaultActionLiteral);

        //score
        Property scoreProperty = ResourceFactory.createProperty(Namespace.Prov.PROV_SCORE);
        Literal scoreLiteral = ResourceFactory.createStringLiteral(log.getConfidenceScore());

        //left uri
        Property leftUriProperty = ResourceFactory.createProperty(Namespace.Prov.PROV_LEFT);
        Literal leftUriLiteral = ResourceFactory.createStringLiteral(log.getLeftURI());

        //right uri
        Property rightUriProperty = ResourceFactory.createProperty(Namespace.Prov.PROV_RIGHT);
        Literal rightUriLiteral = ResourceFactory.createStringLiteral(log.getRightURI());

        //validation action
        Property valProperty = ResourceFactory.createProperty(Namespace.Prov.PROV_VAL_ACTION);
        Literal valLiteral = ResourceFactory.createStringLiteral(log.getValidationAction().toString());

        Statement valStatement = ResourceFactory.createStatement(provNode, valProperty, valLiteral);
        Statement leftStatement = ResourceFactory.createStatement(provNode, leftUriProperty, leftUriLiteral);
        Statement rightStatement = ResourceFactory.createStatement(provNode, rightUriProperty, rightUriLiteral);
        Statement scoreStatement = ResourceFactory.createStatement(provNode, scoreProperty, scoreLiteral);
        Statement provStatement = ResourceFactory.createStatement(uri, prop, provNode);
        Statement agentStatement = ResourceFactory.createStatement(provNode, typeOf, agentResource);

        //fused URI agent
        fusedModel.add(valStatement);
        fusedModel.add(leftStatement);
        fusedModel.add(rightStatement);
        fusedModel.add(scoreStatement);
        fusedModel.add(provStatement);
        fusedModel.add(agentStatement);
        fusedModel.add(provDefaultFusionAction);

        List<Action> actions = log.getActions();
        for (Action action : actions) {
            int hash = action.getAttribute().hashCode();

            String actionString = provNodeString + "/" + hash;
            Resource actionResource = ResourceFactory.createResource(actionString);
            Property actionProperty = ResourceFactory.createProperty(Namespace.Prov.APLLIED_ACTION);

            //fusion action
            Property fusionAction = ResourceFactory.createProperty(Namespace.Prov.PROV_FUSION_ACTION);
            Literal literal = ResourceFactory.createStringLiteral(action.getFusionAction());

            //attribute
            Property attributeProperty = ResourceFactory.createProperty(Namespace.Prov.PROV_ATTRIBUTE);
            String attribute = action.getAttribute();

            //attribute has chain properties
            if (attribute.contains(" ")) {
                String[] spl = attribute.split(" ");
                String at1 = spl[0];
                String at2 = spl[1];
                Literal at1Lit = ResourceFactory.createStringLiteral(at1);
                Literal at2Lit = ResourceFactory.createStringLiteral(at2);
                Statement at1Stat = ResourceFactory.createStatement(actionResource, attributeProperty, at1Lit);
                Statement at2Stat = ResourceFactory.createStatement(actionResource, attributeProperty, at2Lit);
                fusedModel.add(at1Stat);
                fusedModel.add(at2Stat);

            } else {
                Literal atLit = ResourceFactory.createStringLiteral(attribute);
                Statement attStat = ResourceFactory.createStatement(actionResource, attributeProperty, atLit);
                fusedModel.add(attStat);
            }

            //valueA
            if (action.getValueA() != null) {
                Property leftValueProp = ResourceFactory.createProperty(Namespace.Prov.PROV_LEFT_VALUE);
                Literal leftValueLit = ResourceFactory.createStringLiteral(action.getValueA());
                Statement leftValStat = ResourceFactory.createStatement(actionResource, leftValueProp, leftValueLit);
                fusedModel.add(leftValStat);
            } else {
                Property leftValueProp = ResourceFactory.createProperty(Namespace.Prov.PROV_LEFT_VALUE);
                Literal leftValueLit = ResourceFactory.createStringLiteral("null");
                Statement leftValStat = ResourceFactory.createStatement(actionResource, leftValueProp, leftValueLit);
                fusedModel.add(leftValStat);
            }

            //valueB
            if (action.getValueB() != null) {
                Property rightValueProp = ResourceFactory.createProperty(Namespace.Prov.PROV_RIGHT_VALUE);
                Literal rightValueLit = ResourceFactory.createStringLiteral(action.getValueB());
                Statement rightValStat = ResourceFactory.createStatement(actionResource, rightValueProp, rightValueLit);
                fusedModel.add(rightValStat);
            } else {
                Property rightValueProp = ResourceFactory.createProperty(Namespace.Prov.PROV_RIGHT_VALUE);
                Literal rightValueLit = ResourceFactory.createStringLiteral("null");
                Statement rightValStat = ResourceFactory.createStatement(actionResource, rightValueProp, rightValueLit);
                fusedModel.add(rightValStat);
            }

            //fused value
            if (action.getFusedValue() != null) {
                Property fusedValueProp = ResourceFactory.createProperty(Namespace.Prov.PROV_FUSED_VALUE);
                Literal fusedValueLit = ResourceFactory.createStringLiteral(action.getFusedValue());
                Statement fusedValStat = ResourceFactory.createStatement(actionResource, fusedValueProp, fusedValueLit);
                fusedModel.add(fusedValStat);
            } else {
                Property fusedValueProp = ResourceFactory.createProperty(Namespace.Prov.PROV_FUSED_VALUE);
                Literal fusedValueLit = ResourceFactory.createStringLiteral("null");
                Statement fusedValStat = ResourceFactory.createStatement(actionResource, fusedValueProp, fusedValueLit);
                fusedModel.add(fusedValStat);
            }

            Statement s1 = ResourceFactory.createStatement(uri, actionProperty, actionResource);
            Statement s2 = ResourceFactory.createStatement(actionResource, typeOf, agentResource);
            Statement s3 = ResourceFactory.createStatement(actionResource, fusionAction, literal);

            fusedModel.add(s1);
            fusedModel.add(s2);
            fusedModel.add(s3);
        }
    }

    private LinkedPair fuseLink(Link link, Model modelA, Model modelB, RuleSpecification ruleSpec,
            Map<String, IFunction> functionMap, EnumOutputMode mode) throws WrongInputException, IOException {

        LinkedPair linkedPair = new LinkedPair(ruleSpec.getDefaultDatasetAction());
        linkedPair.setLink(link);

        String leftURI = link.getNodeA();
        String leftLocalName = link.getLocalNameA();
        String rightURI = link.getNodeB();
        String rightLocalName = link.getLocalNameB();

        Entity entityA = constructEntity(modelA, leftURI, leftLocalName);
        Entity entityB = constructEntity(modelB, rightURI, rightLocalName);

        linkedPair.setLeftNode(entityA);
        linkedPair.setRightNode(entityB);

        /* VALIDATION */
        EnumValidationAction validation = linkedPair.validateLink(ruleSpec.getValidationRules(), functionMap);

        Entity newFusedEntity = new Entity();

        String targetURI = resolveURI(mode, leftURI, rightURI);
        String targetLocalName = resolveLocalName(mode, leftLocalName, rightLocalName);

        newFusedEntity.setResourceURI(targetURI);
        newFusedEntity.setLocalName(targetLocalName);

        linkedPair.setFusedEntity(newFusedEntity);

        /* FUSION */
        FusionLog fusionLog = linkedPair.fusePair(ruleSpec, functionMap, validation);

        if (verbose) {
            String fusedUri = resolveURI(mode, link.getNodeA(), link.getNodeB());
            Statement fusionConfidence = RDFUtils.getFusionConfidenceStatement(fusedUri, modelA, modelB,
                    linkedPair.getFusedEntity().getEntityData().getModel());

            fusionLog.setConfidenceScore(fusionConfidence.getString());

            fusionLogBuffer.add(fusionLog.toJson());

            if (fusionLogBuffer.size() > SpecificationConstants.FUSION_LOG_BUFFER_SIZE) {
                writeFusionLog(fusionLogBuffer);
                fusionLogBuffer.clear();
            }
        }

        return linkedPair;
    }

    /**
     * Produces the output result by creating a new graph to the specified output or combines the fused entities with
     * the source datasets based on the fusion mode.
     *
     * @param configuration The configuration object.
     * @param fusedEntities The list with fused <code>LinkedPair</code> objects.
     * @param defaultDatasetAction the default dataset action enumeration.
     * @throws FileNotFoundException Thrown when file was not found.
     */
    @Override
    public void combine(Configuration configuration,
            List<LinkedPair> fusedEntities, EnumDatasetAction defaultDatasetAction)
            throws FileNotFoundException, IOException {

        String fused = configuration.getFused();
        String remaining = configuration.getRemaining();
        String ambiguous = configuration.getAmbiguousDatasetFilepath();

        OutputStream fusedStream = new FileOutputStream(fused, false);
        //OutputStreamWriter fusedStream2 = new OutputStreamWriter(new FileOutputStream(fused, false), StandardCharsets.UTF_8);
        //OutputStream remainingStream = new FileOutputStream(remaining, false);
        OutputStream ambiguousStream = new FileOutputStream(ambiguous, false);

        EnumOutputMode mode = configuration.getOutputMode();

        switch (mode) {
            case AA_MODE: {
                aaMode(fused, fusedEntities, fusedStream, configuration);
                break;
            }
            case BB_MODE: {
                bbMode(fused, fusedEntities, fusedStream, configuration);
                break;
            }
            case L_MODE: {
                lMode(fused, fusedEntities, fusedStream, configuration);
                break;
            }
            case AB_MODE: {
                abMode(fused, fusedEntities, fusedStream, configuration);
                break;
            }
            case BA_MODE: {
                baMode(fused, fusedEntities, fusedStream, configuration);
                break;
            }
            case A_MODE: {
                aMode(fused, remaining, fusedEntities, fusedStream, configuration);
                break;
            }
            case B_MODE: {
                bMode(remaining, fused, fusedEntities, fusedStream, configuration);
                break;
            }
            default:
                throw new UnsupportedOperationException("Wrong Output mode!");
        }

        Model ambiguousModel = AmbiguousDataset.getAmbiguousDataset().getModel();

        ambiguousModel.write(ambiguousStream, configuration.getOutputRDFFormat());
    }

    private void bMode(String remaining, String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream, Configuration configuration) throws IOException {
        LOG.info(EnumOutputMode.B_MODE + ": Output results will be written to " + remaining
                + " and " + fused + ". Unlinked entities will be excluded from A.");

        Model rightModel = RightDataset.getRightDataset().getModel();

        Set<String> leftLocalNamesToBeExcluded = new HashSet<>();
        for (LinkedPair pair : fusedEntities) {

            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            rightModel.add(fusedDataModel);

            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should be considered unlinked.
            if (!pair.isRejected()) {
                String localName = pair.getLeftNode().getLocalName();
                leftLocalNamesToBeExcluded.add(localName);
            }
        }

        rightModel.write(fusedStream, configuration.getOutputRDFFormat());

        removeUnlinkedTriples(LeftDataset.getLeftDataset().getFilepath(), leftLocalNamesToBeExcluded, remaining);
    }

    private void aMode(String fused, String remaining, List<LinkedPair> fusedEntities, OutputStream fusedStream, Configuration configuration) throws IOException {
        LOG.info(EnumOutputMode.A_MODE + ": Output results will be written to " + fused
                + " and " + remaining + ". Unlinked entities will be excluded from B.");

        Model leftModel = LeftDataset.getLeftDataset().getModel();

        Set<String> rightLocalNamesToBeExcluded = new HashSet<>();
        for (LinkedPair pair : fusedEntities) {
            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            leftModel.add(fusedDataModel);

            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should be considered unlinked.
            if (!pair.isRejected()) {
                String localName = pair.getRightNode().getLocalName();
                rightLocalNamesToBeExcluded.add(localName);
            }
        }

        leftModel.write(fusedStream, configuration.getOutputRDFFormat());

        removeUnlinkedTriples(RightDataset.getRightDataset().getFilepath(), rightLocalNamesToBeExcluded, remaining);
    }

    private void baMode(String fused, List<LinkedPair> fusedEntities, OutputStream remainingStream, Configuration configuration)
            throws IOException {
        LOG.info(EnumOutputMode.BA_MODE + ": Output result will be written to " + fused);
        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();

        Set<String> leftLocalNamesToBeExcluded = new HashSet<>();
        for (LinkedPair pair : fusedEntities) {
            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            rightModel.add(fusedDataModel);

            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should come from the other.
            if (!pair.isRejected()) {
                String localName = pair.getRightNode().getLocalName();
                leftLocalNamesToBeExcluded.add(localName);
            }
        }

        leftModel.write(remainingStream, configuration.getOutputRDFFormat());

        addUnlinkedTriples(fused, LeftDataset.getLeftDataset().getFilepath(), leftLocalNamesToBeExcluded);

        writeRemaining(LeftDataset.getLeftDataset().getFilepath(), Configuration.getInstance().getRemaining());
    }

    private void abMode(String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream, Configuration configuration) throws IOException {
        LOG.info(EnumOutputMode.AB_MODE + ": Output result will be written to " + fused);
        Model leftModel = LeftDataset.getLeftDataset().getModel();

        Set<String> rightLocalNamesToBeExcluded = new HashSet<>();
        for (LinkedPair pair : fusedEntities) {

            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            leftModel.add(fusedDataModel);

            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should come from the other.
            if (!pair.isRejected()) {
                String localName = pair.getRightNode().getLocalName();
                rightLocalNamesToBeExcluded.add(localName);
            }
        }

        leftModel.write(fusedStream, configuration.getOutputRDFFormat());

        addUnlinkedTriples(fused, RightDataset.getRightDataset().getFilepath(), rightLocalNamesToBeExcluded);

        writeRemaining(RightDataset.getRightDataset().getFilepath(), Configuration.getInstance().getRemaining());
    }

    private void lMode(String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream, Configuration configuration) {
        LOG.info(EnumOutputMode.L_MODE + ": Output result will be written to " + fused);

        Model newModel = ModelFactory.createDefaultModel();

        for (LinkedPair pair : fusedEntities) {
            //only accepted links should appear in the fused.
            if (!pair.isRejected()) {
                Model fusedModel = pair.getFusedEntity().getEntityData().getModel();
                newModel.add(fusedModel);
            }
        }

        newModel.write(fusedStream, configuration.getOutputRDFFormat());
    }

    private void bbMode(String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream,
            Configuration configuration) throws IOException {
        LOG.info(EnumOutputMode.BB_MODE + ": Output result will be written to " + fused);

        Model rightModel = RightDataset.getRightDataset().getModel();

        for (LinkedPair pair : fusedEntities) {
            //add both accepted and rejected to fused model, because the rejected have been removed from the right model.
            Model fusedModel = pair.getFusedEntity().getEntityData().getModel();
            rightModel.add(fusedModel);
        }

        rightModel.write(fusedStream, configuration.getOutputRDFFormat());

        writeRemaining(LeftDataset.getLeftDataset().getFilepath(), Configuration.getInstance().getRemaining());
    }

    private void aaMode(String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream,
            Configuration configuration) throws IOException {
        LOG.info(EnumOutputMode.AA_MODE + ": Output result will be written to " + fused);

        Model leftModel = LeftDataset.getLeftDataset().getModel();

        for (LinkedPair pair : fusedEntities) {
            //add both accepted and rejected to fused model, because the rejected have been removed from the left model.
            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            leftModel.add(fusedDataModel);
        }

        leftModel.write(fusedStream, configuration.getOutputRDFFormat());

        writeRemaining(RightDataset.getRightDataset().getFilepath(), Configuration.getInstance().getRemaining());
    }

    private void writeFusionLog(List<String> fusionLogBuffer) throws IOException {
        String path = Configuration.getInstance().getFusionLog();
        FileUtils.writeLines(new File(path), "UTF-8", fusionLogBuffer, "\n", true);
    }

    private Entity constructEntity(Model model, String resourceURI, String localName) {

        Entity entity = new Entity();
        EntityData entityData = new EntityData(model);
        entity.setResourceURI(resourceURI);
        entity.setLocalName(localName);
        entity.setEntityData(entityData);

        return entity;
    }

    //creates a jena rdf model for this node and removes the node from the source dataset
    private Model constructEntityDataModel(String node, Model sourceModel, Model temp, int depth) {

        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct();

        StmtIterator jenaIterator = model.listStatements();

        //SourceModel.remove(model) does not remove the model from the source for some reason.
        //Also, strange concurrent modification exception when using jena statement iterator. Using list iterator instead.
        List<Statement> stList = jenaIterator.toList();
        Iterator<Statement> stIterator = stList.iterator();

        if (stList.isEmpty()) {
            //One entity links to multiple: the entity has been removed from the source model.
            //Recover the entity from the temp model.
            queryExecution = QueryExecutionFactory.create(query, temp);
            model = queryExecution.execConstruct();
            jenaIterator = model.listStatements();
            stList = jenaIterator.toList();
            stIterator = stList.iterator();
        }

        while (stIterator.hasNext()) {
            Statement st = stIterator.next();
            sourceModel.remove(st);

            //add statement to the temp model, in case the same entity links to multiple.
            temp.add(st);
        }

        return model;
    }

    private String resolveURI(EnumOutputMode mode, String leftURI, String rightURI) {
        String resourceURI;
        switch (mode) {

            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                resourceURI = leftURI;
                break;
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                resourceURI = rightURI;
                break;
            default:
                LOG.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
        return resourceURI;
    }

    private String resolveLocalName(EnumOutputMode mode, String leftLocalName, String rightLocalName) {
        String localName;
        switch (mode) {

            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                localName = leftLocalName;
                break;
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                localName = rightLocalName;
                break;
            default:
                LOG.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
        return localName;
    }

    /**
     * Returns the count of linked entities that were not found in the source datasets.
     *
     * @return the number of linked entities not found in the dataset.
     */
    public int getLinkedEntitiesNotFoundInDataset() {
        return linkedEntitiesNotFoundInDataset;
    }

    /**
     * Set the number of linked entities that were not found in the dataset.
     *
     * @param linkedEntitiesNotFoundInDataset the number of linked entities not found in the dataset.
     */
    public void setLinkedEntitiesNotFoundInDataset(int linkedEntitiesNotFoundInDataset) {
        this.linkedEntitiesNotFoundInDataset = linkedEntitiesNotFoundInDataset;
    }

    /**
     * Returns the total fused entities.
     *
     * @return the number of the fused entities.
     */
    public int getFusedPairsCount() {
        return fusedPairsCount;
    }

    /**
     * Returns the total rejected links.
     *
     * @return the number of the rejected links.
     */
    public int getRejectedCount() {
        return rejectedCount;
    }

    private void addUnlinkedTriples(String outputPath, String datasetPath, Set<String> uriSet) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(Paths.get(datasetPath), StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath, true))) {
            String previousId = "";
            for (String line; (line = br.readLine()) != null;) {
                String[] parts = line.split(" ");
                String idPart = parts[0];

                String id = RDFUtils.getIdFromResourcePart(idPart);

                if (!uriSet.contains(id)) {
                    //add original flag to poi. Exclude flags from classification triples
                    //todo: this does not bring any score existed, but flags it original
                    if (!idPart.contains("term") && !id.equals(previousId)) {
                        String fl = RDFUtils.getUnlinkedFlag(idPart);
                        bufferedWriter.append(fl);
                        bufferedWriter.newLine();
                    }
                    previousId = id;
                    bufferedWriter.append(line);
                    bufferedWriter.newLine();
                }
                //else the line belongs to interlinked entity
            }
        }
    }

    private void removeUnlinkedTriples(String datasetPath, Set<String> localNames, String outputPath) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(datasetPath), StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath, false))) {

            for (String line; (line = br.readLine()) != null;) {
                String[] parts = line.split(" ");
                String idPart = parts[0];

                String localName = RDFUtils.getIdFromResourcePart(idPart);

                if (!localNames.contains(localName)) {
                    bufferedWriter.append(line);
                    bufferedWriter.newLine();
                }
            }
        }
    }

    private void writeRemaining(String inputDatasetPath, String remainingPath) throws IOException {
        Path inputPath = Paths.get(inputDatasetPath);
        Path remaining = Paths.get(remainingPath);
        Files.copy(inputPath, remaining, StandardCopyOption.REPLACE_EXISTING);
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(SpecificationConstants.POSIX_FILE_PERMISSIONS_STRING);
        Files.setPosixFilePermissions(remaining, perms);
    }

    /**
     * Return the average confidence score.
     *
     * @return
     */
    public Double getAverageConfidence() {
        return averageConfidence;
    }

    /**
     * Return the average gain score.
     *
     * @return
     */
    public Double getAverageGain() {
        return averageGain;
    }

    /**
     * Return the max gain score.
     *
     * @return
     */
    public Double getMaxGain() {
        return maxGain;
    }

    private Model resolveFusedModelForRejectedEnsemble(Link link, Map<String, Model> modelsA, Map<String, Model> modelsB) {

        EnumOutputMode mode = Configuration.getInstance().getOutputMode();
        Model model;
        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                model = modelsA.get(link.getNodeA());
                break;
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                model = modelsB.get(link.getNodeB());
                break;
            default:
                LOG.fatal("Cannot resolve fused model for ensemble. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
        return model;
    }
}
