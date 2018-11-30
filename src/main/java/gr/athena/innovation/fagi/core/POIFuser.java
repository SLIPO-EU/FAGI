package gr.athena.innovation.fagi.core;

import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
public class POIFuser implements Fuser{ 

    private static final Logger LOG = LogManager.getLogger(POIFuser.class);
    private int linkedEntitiesNotFoundInDataset = 0;
    private int fusedPairsCount = 0;
    private int rejectedCount = 0;
    private final Model tempModelA = ModelFactory.createDefaultModel();
    private final Model tempModelB = ModelFactory.createDefaultModel();
    private final List<FusionLog> fusionLogBuffer = new ArrayList<>();
    private final boolean verbose = Configuration.getInstance().isVerbose();

    /**
     * Fuses all links using the Rules defined in the XML file.
     * 
     * @param configuration The configuration object.
     * @param ruleSpec The rule specification object.
     * @param functionMap The map containing the available functions.
     */
    @Override
    public List<LinkedPair> fuseAll(Configuration configuration, RuleSpecification ruleSpec, 
            Map<String, IFunction> functionMap) throws WrongInputException, IOException{

        List<LinkedPair> fusedList = new ArrayList<>();

        linkedEntitiesNotFoundInDataset = 0;

        Model left = LeftDataset.getLeftDataset().getModel();
        Model right = RightDataset.getRightDataset().getModel();
        LinksModel links = LinksModel.getLinksModel();

        EnumOutputMode mode = configuration.getOutputMode();

        if(verbose){
            //clean fusion log if exists already
            if(new File(configuration.getFusionLog()).exists()){
                FileChannel.open(Paths.get(configuration.getFusionLog()), StandardOpenOption.WRITE).truncate(0).close();
            }
        }

        for (Link link : links.getLinks()){

            //create the jena models for each node of the pair and remove them from the source models.
            Model modelA = constructEntityDataModel(link.getNodeA(), left, tempModelA, configuration.getOptionalDepth());
            Model modelB = constructEntityDataModel(link.getNodeB(), right, tempModelB, configuration.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            LinkedPair linkedPair = fuseLink(link, modelA, modelB, ruleSpec, functionMap, mode);
            if(linkedPair.isRejected()){
                rejectedCount++;
            } else {
                fusedPairsCount++;
            }
            
            //Add scores only on accepted pairs.
            if(!linkedPair.isRejected()){
                //Add interlinking score, fusion score, fusion confidence
                Model fusedModel = linkedPair.getFusedEntity().getEntityData().getModel();

                String fusedUri = resolveURI(mode, link.getNodeA(), link.getNodeB());
                Statement interlinkingScore = RDFUtils.getInterlinkingScore(fusedUri, link.getScore(), modelA, modelB);

                Statement fusionScore = RDFUtils.getFusionScoreStatement(fusedUri, link.getNodeA(), link.getNodeB(), modelA, modelB, fusedModel);
                Statement fusionConfidence = RDFUtils.getFusionConfidenceStatement(fusedUri, modelA, modelB, fusedModel);

                fusedModel.removeAll((Resource) null, ResourceFactory.createProperty(Namespace.FUSION_GAIN_NO_BRACKETS), (RDFNode) null);
                fusedModel.removeAll((Resource) null, ResourceFactory.createProperty(Namespace.FUSION_CONFIDENCE_NO_BRACKETS), (RDFNode) null);
                fusedModel.removeAll((Resource) null, ResourceFactory.createProperty(Namespace.INTERLINKING_SCORE), (RDFNode) null);

                fusedModel.add(fusionConfidence);
                fusedModel.add(fusionScore);
                fusedModel.add(interlinkingScore);
            }
            
            //add accepted and rejected to fused list. Fusion mode treats them differently at combine.
            fusedList.add(linkedPair); 
        }

        //flush fusionLogBuffer if not empty
        if(verbose){
            if(fusionLogBuffer.size() > 0){
                writeFusionLog(fusionLogBuffer);
                fusionLogBuffer.clear();
            }
        }

        //links.getLinks().removeAll(links.getRejected());
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);

        return fusedList;
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

        if(verbose){
            fusionLogBuffer.add(fusionLog);

            if(fusionLogBuffer.size() > SpecificationConstants.FUSION_LOG_BUFFER_SIZE){
                writeFusionLog(fusionLogBuffer);
                fusionLogBuffer.clear();
            }
        }

        return linkedPair;
    }

    /**
     * Produces the output result by creating a new graph to the specified output 
     * or combines the fused entities with the source datasets based on the fusion mode.
     * 
     * @param configuration The configuration object.
     * @param fusedEntities The list with fused <code>LinkedPair</code> objects. 
     * @param defaultDatasetAction the default dataset action enumeration.
     * @throws FileNotFoundException Thrown when file was not found.
     */
    @Override
    public void combine(Configuration configuration, 
            List<LinkedPair> fusedEntities, EnumDatasetAction defaultDatasetAction) 
                    throws FileNotFoundException, IOException{

        String fused = configuration.getFused();
        String remaining = configuration.getRemaining();
        String ambiguous = configuration.getAmbiguousDatasetFilepath();

        OutputStream fusedStream = new FileOutputStream(fused, false);
        //OutputStreamWriter fusedStream2 = new OutputStreamWriter(new FileOutputStream(fused, false), StandardCharsets.UTF_8);
        OutputStream remainingStream = new FileOutputStream(remaining, false);
        OutputStream ambiguousStream = new FileOutputStream(ambiguous, false);

        EnumOutputMode mode = configuration.getOutputMode();

        switch(mode) {
            case AA_MODE:
            {
                aaMode(fused, fusedEntities, fusedStream, configuration, remaining);
                break;
            }
            case BB_MODE:
            {
                bbMode(fused, fusedEntities, fusedStream, configuration, remaining);
                break;
            }
            case L_MODE:
            {
                lMode(fused, fusedEntities, fusedStream, configuration);
                break; 
            }
            case AB_MODE:
            {
                abMode(fused, fusedEntities, fusedStream, configuration, remaining);
                break;
            }
            case BA_MODE:
            {
                baMode(fused, fusedEntities, remainingStream, configuration, remaining);
                break;
            }
            case A_MODE:
            {
                aMode(fused, remaining, fusedEntities, fusedStream, configuration);
                break;
            }
            case B_MODE:
            {
                bMode(remaining, fused, fusedEntities, fusedStream, configuration);
                break;
            }
            default:
                throw new UnsupportedOperationException("Wrong Output mode!");               
        }

        Model ambiguousModel = AmbiguousDataset.getAmbiguousDataset().getModel();

        if(ambiguousModel.isEmpty()){
            //addMessageToEmptyOutput(ambiguous);
        } else {
            ambiguousModel.write(ambiguousStream, configuration.getOutputRDFFormat());
        }
    }

    private void bMode(String remaining, String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream, Configuration configuration) throws IOException {
        LOG.info(EnumOutputMode.B_MODE + ": Output results will be written to " + remaining
                + " and " + fused + ". Unlinked entities will be excluded from A.");
        
        Model rightModel = RightDataset.getRightDataset().getModel();
        
        Set<String> leftLocalNamesToBeExcluded = new HashSet<>();
        for(LinkedPair pair : fusedEntities){
            
            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            rightModel.add(fusedDataModel);

            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should be considered unlinked.
            if(!pair.isRejected()){
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
        for(LinkedPair pair : fusedEntities){
            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            leftModel.add(fusedDataModel);
            
            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should be considered unlinked.
            if(!pair.isRejected()){
                String localName = pair.getRightNode().getLocalName();
                rightLocalNamesToBeExcluded.add(localName);
            }
        }

        leftModel.write(fusedStream, configuration.getOutputRDFFormat());

        removeUnlinkedTriples(RightDataset.getRightDataset().getFilepath(), rightLocalNamesToBeExcluded, remaining);
    }

    private void baMode(String fused, List<LinkedPair> fusedEntities, OutputStream remainingStream, Configuration configuration, String remaining) throws IOException {
        LOG.info(EnumOutputMode.BA_MODE + ": Output result will be written to " + fused);
        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();
        
        Set<String> leftLocalNamesToBeExcluded = new HashSet<>();
        for(LinkedPair pair : fusedEntities){
            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            rightModel.add(fusedDataModel);
            
            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should come from the other.
            if(!pair.isRejected()){
                String localName = pair.getRightNode().getLocalName();
                leftLocalNamesToBeExcluded.add(localName);
            }
        }
        
        leftModel.write(remainingStream, configuration.getOutputRDFFormat());
        
        addUnlinkedTriples(fused, LeftDataset.getLeftDataset().getFilepath(), leftLocalNamesToBeExcluded);
        
        writeRemaining(LeftDataset.getLeftDataset().getFilepath(), Configuration.getInstance().getRemaining());
    }

    private void abMode(String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream, Configuration configuration, String remaining) throws IOException {
        LOG.info(EnumOutputMode.AB_MODE + ": Output result will be written to " + fused);
        Model leftModel = LeftDataset.getLeftDataset().getModel();

        Set<String> rightLocalNamesToBeExcluded = new HashSet<>();
        for(LinkedPair pair : fusedEntities){

            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            leftModel.add(fusedDataModel); 
            
            //Accepted pairs should be excluded from the "unlinked POIs" list. Both rejected and unlinked should come from the other.
            if(!pair.isRejected()){
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

        for(LinkedPair pair : fusedEntities){
            //only accepted links should appear in the fused.
            if(!pair.isRejected()){
                Model fusedModel = pair.getFusedEntity().getEntityData().getModel();
                newModel.add(fusedModel);
            }
        }

        newModel.write(fusedStream, configuration.getOutputRDFFormat());
    }

    private void bbMode(String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream, 
            Configuration configuration, String remaining) throws IOException {
        LOG.info(EnumOutputMode.BB_MODE + ": Output result will be written to " + fused);

        Model rightModel = RightDataset.getRightDataset().getModel();

        for(LinkedPair pair : fusedEntities){
            //add both accepted and rejected to fused model, because the rejected have been removed from the right model.
            Model fusedModel = pair.getFusedEntity().getEntityData().getModel();
            rightModel.add(fusedModel);
        }

        rightModel.write(fusedStream, configuration.getOutputRDFFormat());
        
        writeRemaining(LeftDataset.getLeftDataset().getFilepath(), Configuration.getInstance().getRemaining());
    }

    private void aaMode(String fused, List<LinkedPair> fusedEntities, OutputStream fusedStream, 
            Configuration configuration, String remaining) throws IOException {
        LOG.info(EnumOutputMode.AA_MODE + ": Output result will be written to " + fused);

        Model leftModel = LeftDataset.getLeftDataset().getModel();

        for(LinkedPair pair : fusedEntities){
            //add both accepted and rejected to fused model, because the rejected have been removed from the left model.
            Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
            leftModel.add(fusedDataModel);
        }
        
        leftModel.write(fusedStream, configuration.getOutputRDFFormat());

        writeRemaining(RightDataset.getRightDataset().getFilepath(), Configuration.getInstance().getRemaining());
    }

    private void writeFusionLog(List<FusionLog> fusionLogBuffer) throws IOException {
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
    private Model constructEntityDataModel(String node, Model sourceModel, Model temp, int depth){

        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct();

        StmtIterator jenaIterator = model.listStatements();

        //SourceModel.remove(model) does not remove the model from the source for some reason.
        //Also, strange concurrent modification exception when using jena statement iterator. Using list iterator instead.
        List<Statement> stList = jenaIterator.toList();
        Iterator<Statement> stIterator = stList.iterator();
        
        if(stList.isEmpty()){
            //One entity links to multiple: the entity has been removed from the source model.
            //Recover the entity from the temp model.
            queryExecution = QueryExecutionFactory.create(query, temp);
            model = queryExecution.execConstruct();
            jenaIterator = model.listStatements();
            stList = jenaIterator.toList();
            stIterator = stList.iterator();
        }

        while ( stIterator.hasNext() ) {
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

                String id = getResourceURI(idPart);

                if(!uriSet.contains(id)){
                    //add original flag to poi. Exclude flags from classification triples
                    //todo: this does not bring any score existed, but flags it original
                    if(!idPart.contains("term") && !id.equals(previousId)){
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

                String localName = getResourceURI(idPart);

                if(!localNames.contains(localName)){
                    bufferedWriter.append(line);
                    bufferedWriter.newLine();
                }
            }
        }
    }

    private static String getResourceURI(String part) {
        int endPosition = StringUtils.lastIndexOf(part, "/");
        int startPosition = StringUtils.ordinalIndexOf(part, "/", 5) + 1;
        String res;
        if(part.substring(startPosition).contains("/")){
            res = part.subSequence(startPosition, endPosition).toString();
        } else {
            res = part.subSequence(startPosition, part.length()-1).toString();
        }

        return res;
    }

    private void writeRemaining(String inputDatasetPath, String remainingPath) throws IOException{
        Path inputPath = Paths.get(inputDatasetPath);
        Path remaining = Paths.get(remainingPath);
        Files.copy(inputPath, remaining, StandardCopyOption.REPLACE_EXISTING);        
    }
}
