package gr.athena.innovation.fagi.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.rule.model.ActionRule;
import gr.athena.innovation.fagi.rule.model.Condition;
import gr.athena.innovation.fagi.rule.model.Rule;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.rule.model.ExternalProperty;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.utils.CentroidShiftTranslator;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.ResourceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing a pair of interlinked RDF entities.
 *
 * @author nkarag
 */
public class LinkedPair {

    private static final Logger logger = LogManager.getLogger(LinkedPair.class);
    private Link link;
    private Entity leftNode;
    private Entity rightNode;
    private Entity fusedEntity;

    EnumValidationAction validation = EnumValidationAction.UNDEFINED;

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
    
    public Entity getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Entity leftNode) {
        this.leftNode = leftNode;
    }

    public Entity getRightNode() {
        return rightNode;
    }

    public void setRightNode(Entity rightNode) {
        this.rightNode = rightNode;
    }

    public Entity getFusedEntity() {
        if (fusedEntity == null) {
            logger.fatal("Current pair is not fused: " + this);
            throw new ApplicationException("Current pair is not fused: " + this);
        }
        return fusedEntity;
    }

    public void setFusedEntity(Entity fusedEntity) {
        this.fusedEntity = fusedEntity;
    }

    public EnumValidationAction validateLink(List<Rule> validationRules, Map<String, IFunction> functionMap)
            throws WrongInputException {

        EntityData leftEntityData = leftNode.getEntityData();
        EntityData rightEntityData = rightNode.getEntityData();

        for (Rule validationRule : validationRules) {
            logger.trace("Validating with Rule: " + validationRule);

            String validationProperty;

            //the property here is assumed to be one node above the literal value in order  to align with the ontology.
            //For example the property is the p1 in the following linked triples.
            // s p1 o1 . o1 p2 o2 
            String literalA;
            String literalB;

            if (validationRule.getParentPropertyA() == null) {
                validationProperty = validationRule.getPropertyA();
                literalA = getLiteralValue(validationRule.getPropertyA(), leftEntityData.getModel());

            } else {
                validationProperty = validationRule.getParentPropertyA();
                literalA = getLiteralValueFromChain(validationRule.getParentPropertyA(), validationRule.getPropertyA(),
                        leftEntityData.getModel());
            }

            if (validationRule.getParentPropertyB() == null) {
                literalB = getLiteralValue(validationRule.getPropertyB(), rightEntityData.getModel());
            } else {
                literalB = getLiteralValueFromChain(validationRule.getParentPropertyB(), validationRule.getPropertyB(),
                        rightEntityData.getModel());
            }

            if (literalA == null && literalB == null) {
                continue;
            }

            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and break.
            if (validationRule.getActionRuleSet() == null) {
                logger.trace("Rule without ACTION RULE SET, accepting link.");

                validation = EnumValidationAction.ACCEPT;

                break;
            }

            List<ActionRule> actionRules = validationRule.getActionRuleSet().getActionRuleList();
            int actionRuleCount = 0;
            boolean actionRuleToApply = false;
            for (ActionRule actionRule : actionRules) {

                logger.info("-- Action rule: " + actionRuleCount);

                EnumValidationAction validationAction = null;

                if (actionRule.getValidationAction() != null) {
                    validationAction = actionRule.getValidationAction();
                }

                Condition condition = actionRule.getCondition();

                //switch case for evaluation using external properties.
                for (Map.Entry<String, ExternalProperty> externalPropertyEntry : validationRule.getExternalProperties().entrySet()) {

                    //The rule model does not represent the external properties with chain relationships.
                    //So, there are two cases here: Property refers to literal the external property contains a chain
                    //separated by a whitespace.
                    String extPropertyText = externalPropertyEntry.getValue().getProperty();
                    String valueA;
                    String valueB;

                    if (extPropertyText.contains(" ")) {
                        String[] chains = extPropertyText.split(" ");
                        valueA = getLiteralValueFromChain(chains[0], chains[1], leftEntityData.getModel());
                        valueB = getLiteralValueFromChain(chains[0], chains[1], rightEntityData.getModel());
                    } else {
                        valueA = getLiteralValue(externalPropertyEntry.getValue().getProperty(), leftEntityData.getModel());
                        valueB = getLiteralValue(externalPropertyEntry.getValue().getProperty(), rightEntityData.getModel());
                    }

                    externalPropertyEntry.getValue().setValueA(valueA);
                    externalPropertyEntry.getValue().setValueB(valueB);
                }

                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, this, validationProperty,
                        literalA, literalB, validationRule.getExternalProperties());

                actionRuleCount++;

                if (isActionRuleToBeApplied) {
                    logger.debug("Condition : " + condition + " evaluated true. Validating link with: " + validationAction);

                    validation = validationAction;

                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action (accept)
            if (actionRuleToApply == false) {
                
                EnumValidationAction defaultAction = validationRule.getDefaultValidationAction();
                
                logger.debug("All conditions evaluated to false in validation. Using default validation action: " 
                        + defaultAction);
                
                validation = defaultAction;
            }
        }

        return validation;
    }

    public void fusePair(RuleCatalog ruleCatalog, Map<String, IFunction> functionMap,
            EnumValidationAction validationAction) throws WrongInputException {

        //TODO: optimization: resolve validation action here 
        EnumDatasetAction defaultDatasetAction = ruleCatalog.getDefaultDatasetAction();

        EntityData leftEntityData = leftNode.getEntityData();
        EntityData rightEntityData = rightNode.getEntityData();

        fuseDefaultDatasetAction(defaultDatasetAction);

        List<Rule> rules = ruleCatalog.getRules();

        for (Rule rule : rules) {
            logger.trace("Fusing with Rule: " + rule);

            EnumFusionAction defaultFusionAction = rule.getDefaultFusionAction();

            //TODO: change #getRDFPropertyFromString to check for propertyB when ontology is different from source datasets
            Property rdfValuePropertyA = getRDFPropertyFromString(rule.getPropertyA());
            Property rdfValuePropertyB = getRDFPropertyFromString(rule.getPropertyB());
            String fusionProperty;

            //the property here is assumed to be one node above the literal value in order  to align with the ontology.
            //For example the property is the p1 in the following linked triples.
            // s p1 o1 . o1 p2 o2 
            String literalA;
            String literalB;

            if (rule.getParentPropertyA() == null) {
                fusionProperty = rule.getPropertyA();
                literalA = getLiteralValue(rule.getPropertyA(), leftEntityData.getModel());

            } else {
                fusionProperty = rule.getParentPropertyA();
                literalA = getLiteralValueFromChain(rule.getParentPropertyA(), rule.getPropertyA(), leftEntityData.getModel());
            }

            if (rule.getParentPropertyB() == null) {
                literalB = getLiteralValue(rule.getPropertyB(), rightEntityData.getModel());
            } else {
                literalB = getLiteralValueFromChain(rule.getParentPropertyB(), rule.getPropertyB(), rightEntityData.getModel());
            }

            if (literalA == null && literalB == null) {
                continue;
            }

            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and break.
            if (rule.getActionRuleSet() == null) {
                logger.trace("Rule without ACTION RULE SET, use plain action: " + defaultFusionAction);
                if (defaultFusionAction != null) {
                    fuseRuleAction(defaultFusionAction, validationAction, rdfValuePropertyA, literalA, literalB);
                }
                break;
            }

            List<ActionRule> actionRules = rule.getActionRuleSet().getActionRuleList();
            int actionRuleCount = 0;
            boolean actionRuleToApply = false;
            for (ActionRule actionRule : actionRules) {

                logger.info("-- Action rule: " + actionRuleCount);

                EnumFusionAction fusionAction = null;

                if (actionRule.getFusionAction() != null) {
                    fusionAction = actionRule.getFusionAction();
                }

                Condition condition = actionRule.getCondition();

                //switch case for evaluation using external properties.
                for (Map.Entry<String, ExternalProperty> externalPropertyEntry : rule.getExternalProperties().entrySet()) {

                    //The rule model does not represent the external properties with chain relationships.
                    //So, there are two cases here: Property refers to literal the external property contains a chain
                    //separated by a whitespace.
                    String extPropertyText = externalPropertyEntry.getValue().getProperty();
                    String valueA;
                    String valueB;

                    if (extPropertyText.contains(" ")) {
                        String[] chains = extPropertyText.split(" ");
                        valueA = getLiteralValueFromChain(chains[0], chains[1], leftEntityData.getModel());
                        valueB = getLiteralValueFromChain(chains[0], chains[1], rightEntityData.getModel());
                    } else {
                        valueA = getLiteralValue(externalPropertyEntry.getValue().getProperty(), leftEntityData.getModel());
                        valueB = getLiteralValue(externalPropertyEntry.getValue().getProperty(), rightEntityData.getModel());
                    }

                    externalPropertyEntry.getValue().setValueA(valueA);
                    externalPropertyEntry.getValue().setValueB(valueB);
                }

                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, this, fusionProperty,
                        literalA, literalB, rule.getExternalProperties());

                actionRuleCount++;

                if (isActionRuleToBeApplied) {
                    logger.debug("Condition : " + condition + " evaluated true. Fusion with action: " + fusionAction);
                    logger.debug("Literals to be fused: " + literalA + " <--> " + literalB);
                    
                    fuseRuleAction(fusionAction, validationAction, rdfValuePropertyA, literalA, literalB);

                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action
            if (actionRuleToApply == false) {
                logger.debug("All conditions evaluated to false in fusion rule. Using default fusion action: " 
                        + defaultFusionAction);                
                fuseRuleAction(defaultFusionAction, validationAction, rdfValuePropertyA, literalA, literalB);
            }
        }
    }

    public void fuseDefaultDatasetAction(EnumDatasetAction datasetDefaultAction) throws WrongInputException {

        //default dataset action should be performed before the rules apply. The fused model should be empty:
        if (!fusedEntity.getEntityData().getModel().isEmpty()) {
            throw new ApplicationException("Default fusion action tries to overwrite already fused data!");
        }

        EntityData fusedData = new EntityData();

        Model fusedModel = ModelFactory.createDefaultModel();

        EntityData leftData = leftNode.getEntityData();
        EntityData rightData = rightNode.getEntityData();

        //TODO: decide what happens with URIs based on fused dataset mode. 
        //E.g keep-right will bring all right triple-chain URIs to A. 
        switch (datasetDefaultAction) {
            case KEEP_LEFT: {

                resolveModeURIs(leftNode, rightNode);
                fusedModel.add(leftData.getModel());
                fusedData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedData);

                break;
            }
            case KEEP_RIGHT: {
                
                resolveModeURIs(leftNode, rightNode);
                fusedModel.add(rightData.getModel());
                fusedData.setModel(rightData.getModel());
                fusedEntity.setEntityData(fusedData);

                break;
            }
            case KEEP_BOTH: {
                fusedModel.add(leftData.getModel().add(rightData.getModel()));
                fusedData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedData);

                break;
            }
            default:
                throw new WrongInputException("Dataset default fusion action is not defined.");
        }
    }

    private void fuseRuleAction(EnumFusionAction action, EnumValidationAction validationAction, Property property,
            String literalA, String literalB) throws WrongInputException {

        //TODO: Check Keep both. 
        //TODO: Also, property coming from the caller is propertyA because it assumes same ontology
        //Maybe add propertyB and check them both if one does not exist in model.
        EntityData fusedEntityData = fusedEntity.getEntityData();

        Model fusedModel = fusedEntityData.getModel();
        Model ambiguousModel = AmbiguousDataset.getAmbiguousDataset().getModel();

        switch (validationAction) {
            case ACCEPT:
                //do nothing
                break;
            case ACCEPT_MARK_AMBIGUOUS: {

                Statement statement = getAmbiguousLinkStatement(leftNode.getResourceURI(), rightNode.getResourceURI());

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                ambiguousModel.add(statement);
                ambiguousModel.add(leftNode.getEntityData().getModel());
                ambiguousModel.add(rightNode.getEntityData().getModel());

                fusedModel.add(statement);
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }                
            case REJECT: {

                //removes link from the list and from the model also.
                //LinksModel.getLinksModel().removeLink(link);
                LinksModel.getLinksModel().getRejected().add(link);

                if (!fusedModel.isEmpty()) {
                    fusedModel.removeAll();
                }

                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                return; //stop link fusion
            }
            case REJECT_MARK_AMBIGUOUS: {
                
                //LinksModel.getLinksModel().removeLink(link);
                LinksModel.getLinksModel().getRejected().add(link);

                if (!fusedModel.isEmpty()) {
                    fusedModel.removeAll();
                }

                EnumDataset dataset = resolveRejectedEntityModel();

                switch(dataset){
                    case LEFT:
                        ambiguousModel.add(leftNode.getEntityData().getModel());
                        break;
                    case RIGHT:
                        ambiguousModel.add(rightNode.getEntityData().getModel());
                        break;                        
                }
                
                Statement statement = getAmbiguousLinkStatement(leftNode.getResourceURI(), rightNode.getResourceURI());

                ambiguousModel.add(statement);

                fusedModel.add(statement);
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);
                
                return; //stop link fusion
            }
        }

        switch (action) {
            case KEEP_LEFT: {
                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                //TODO: check when model does not contain literalA or B
                if (node != null) {
                    fusedModel.add(node, property, ResourceFactory.createStringLiteral(literalA));
                }

                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
            case KEEP_RIGHT: {

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                fusedModel.add(node, property, ResourceFactory.createStringLiteral(literalB));

                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
            case CONCATENATE: {

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                String concatenated = literalA + SpecificationConstants.Rule.CONCATENATION_SEP + literalB;

                fusedModel.add(node, property, ResourceFactory.createStringLiteral(concatenated));

                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
            case KEEP_LONGEST: {

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                String sA = Normalizer.normalize(literalA, Normalizer.Form.NFC);
                String sB = Normalizer.normalize(literalB, Normalizer.Form.NFC);
                String longest;

                if (sA.length() > sB.length()) {
                    longest = literalA;
                } else {
                    longest = literalB;
                }

                fusedModel.add(node, property, ResourceFactory.createStringLiteral(longest));

                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
            case KEEP_BOTH: {
                EntityData leftEntityData = leftNode.getEntityData();
                EntityData rightEntityData = rightNode.getEntityData();

                fusedModel = fusedEntityData.getModel().add(leftEntityData.getModel()).add(rightEntityData.getModel());

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
            case KEEP_MORE_POINTS: {
                checkWKTProperty(property, action);

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);

                if (leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {

                    if (isRejectedByPreviousRule(fusedModel)) {
                        break;
                    }

                    Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                    fusedModel.add(node, property, ResourceFactory.createStringLiteral(literalA));

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);

                    fusedEntity.setEntityData(fusedEntityData);

                } else {

                    if (isRejectedByPreviousRule(fusedModel)) {
                        break;
                    }

                    Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                    fusedModel.add(node, property, ResourceFactory.createStringLiteral(literalB));

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);

                    fusedEntity.setEntityData(fusedEntityData);
                }
                break;
            }
            case KEEP_MORE_POINTS_AND_SHIFT: {
                checkWKTProperty(property, action);

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);

                if (leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(leftGeometry);
                    String wktFusedGeometry = getWKTLiteral(fusedGeometry);

                    if (isRejectedByPreviousRule(fusedModel)) {
                        break;
                    }

                    Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                    fusedModel.add(node, property, ResourceFactory.createStringLiteral(wktFusedGeometry));

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);
                    fusedEntity.setEntityData(fusedEntityData);

                } else if (leftGeometry.getNumPoints() < rightGeometry.getNumPoints()) {

                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(rightGeometry);
                    String wktFusedGeometry = getWKTLiteral(fusedGeometry);

                    if (isRejectedByPreviousRule(fusedModel)) {
                        break;
                    }

                    Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                    fusedModel.add(node, property, ResourceFactory.createStringLiteral(wktFusedGeometry));

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);
                    fusedEntity.setEntityData(fusedEntityData);
                }

                break;
            }
            case SHIFT_LEFT_GEOMETRY: {
                checkWKTProperty(property, action);

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);

                CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                Geometry shiftedToRightGeometry = centroidTranslator.shift(leftGeometry);
                String wktFusedGeometry = getWKTLiteral(shiftedToRightGeometry);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                fusedModel.add(node, property, ResourceFactory.createStringLiteral(wktFusedGeometry));

                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
            case SHIFT_RIGHT_GEOMETRY: {
                checkWKTProperty(property, action);

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);

                CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                Geometry shiftedToLeftGeometry = centroidTranslator.shift(rightGeometry);
                String wktFusedGeometry = getWKTLiteral(shiftedToLeftGeometry);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                Resource node = getResourceAndRemoveLiteral(fusedModel, property, literalA, literalB);

                fusedModel.add(node, property, ResourceFactory.createStringLiteral(wktFusedGeometry));

                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
        }
    }

    //removes the triple that contains literalA or literalB in order to be replaced by another literal based on the action.
    //The method also returns the resource that the literalA or B was found in order to be used as subject and preserve
    //the triple chain
    private Resource getResourceAndRemoveLiteral(Model model, Property property, String literalA, String literalB) {
        Resource node = SparqlRepository.getSubjectWithLiteral(property.toString(), literalA, model);
        if (node == null) {
            node = SparqlRepository.getSubjectWithLiteral(property.toString(), literalB, model);
            if (node != null) {
                model.removeAll(node, property, (RDFNode) null);
            }
        } else {
            model.removeAll(node, property, (RDFNode) null);
        }
        return node;
    }

    private String getLiteralValue(String property, Model model) {
        Property propertyRDF = getRDFPropertyFromString(property);

        if (propertyRDF != null) {
            return SparqlRepository.getObjectOfProperty(propertyRDF, model);
        } else {
            logger.warn("Could not find literal with property {}", property);

            return "";
        }
    }

    private String getLiteralValueFromChain(String property1, String property2, Model model) {

        if (property1 != null) {
            return SparqlRepository.getObjectOfPropertyChain(property1, property2, model);
        } else {
            logger.warn("Could not find literal with property {}", property1);
            return "";
        }
    }

    private Property getRDFPropertyFromString(String property) {

        if (property == null) {
            return null;
        }
        //TODO: remove aliases of properties
        Property propertyRDF;
        if (property.equalsIgnoreCase("label")) {
            propertyRDF = ResourceFactory.createProperty(Namespace.LABEL);
        } else if (property.equalsIgnoreCase("date")) {
            propertyRDF = ResourceFactory.createProperty(Namespace.DATE_OSM_MODIFIED);
        } else if (property.equalsIgnoreCase("wkt")) {
            propertyRDF = ResourceFactory.createProperty(Namespace.WKT);
        } else {
            propertyRDF = ResourceFactory.createProperty(property);
        }
        return propertyRDF;
    }

    private Geometry parseGeometry(String literal) {

        WKTReader wellKnownTextReader = new WKTReader();
        Geometry geometry = null;
        try {
            geometry = wellKnownTextReader.read(literal);
        } catch (ParseException ex) {
            logger.fatal("Error parsing geometry literal " + literal);
            logger.fatal(ex);
        }

        return geometry;
    }

    private String getWKTLiteral(Geometry geometry) {

        WKTWriter wellKnownTextWriter = new WKTWriter();
        String wktString = wellKnownTextWriter.write(geometry);

        return wktString;
    }

    private void checkWKTProperty(Property property, EnumFusionAction action) throws WrongInputException {
        if (!property.toString().equals(Namespace.WKT)) {
            logger.error("The selected action " + action.toString() + " applies only for WKT geometry literals");
            throw new WrongInputException("The selected action " + action.toString() + " applies only for WKT geometry literals");
        }
    }

    private boolean isRejectedByPreviousRule(Model model) {
        //the link has been rejected (or rejected and marked ambiguous) by previous rule.
        //TODO: if size is 1, maybe strict check if the triple contains the ambiguity

        return model.isEmpty() || model.size() == 1;
    }

    private Statement getAmbiguousLinkStatement(String uri1, String uri2) {

        Property ambiguousLink = ResourceFactory.createProperty(Namespace.LINKED_AMBIGUOUSLY);

        Resource resource1 = ResourceFactory.createResource(uri1);
        Resource resource2 = ResourceFactory.createResource(uri2);

        Statement statement = ResourceFactory.createStatement(resource1, ambiguousLink, resource2);

        return statement;
    }
    
    private void resolveModeURIs(Entity entity1, Entity entity2) {
        EnumOutputMode mode = FusionSpecification.getInstance().getOutputMode();
        switch (mode) {
            case AA_MODE:
            case AB_MODE:     
            case A_MODE:
            case L_MODE:
                renameResourceURIs(entity2, entity1);
                break;
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                renameResourceURIs(entity1, entity2);
                break;
        }
    }

    private EnumDataset resolveRejectedEntityModel() {
        EnumOutputMode mode = FusionSpecification.getInstance().getOutputMode();
        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
                return EnumDataset.RIGHT;
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                return EnumDataset.LEFT;
            default:
                throw new ApplicationException("Wrong output mode: " + mode);
        }
    }
    
    private void renameResourceURIs(Entity entity1, Entity entity2) {
        Model rightEntityModel = entity1.getEntityData().getModel();
        
        String entity2ResourceUri = entity2.getResourceURI();
        
        Resource resource1 = rightEntityModel.getResource(entity1.getResourceURI());
        ResourceUtils.renameResource(resource1, entity2ResourceUri);

        List<Statement> list = rightEntityModel.listStatements().toList();
        Iterator<Statement> statementIterator = list.iterator();
        
        while (statementIterator.hasNext()) {
            Statement statement = statementIterator.next();
            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();
            ResourceUtils.renameResource(subject, entity2ResourceUri);
            if (predicate.isURIResource()) {
                ResourceUtils.renameResource(predicate, entity2ResourceUri);
            }
            if (object.isURIResource()) {
                ResourceUtils.renameResource(object.asResource(), entity2ResourceUri);
            }
        }
    }
}
