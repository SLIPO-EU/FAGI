package gr.athena.innovation.fagi.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.learning.FeaturePreprocessor;
import gr.athena.innovation.fagi.rule.model.ActionRule;
import gr.athena.innovation.fagi.rule.model.Condition;
import gr.athena.innovation.fagi.rule.model.Rule;
import gr.athena.innovation.fagi.rule.RuleSpecification;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.rule.model.ExternalProperty;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.utils.CentroidShiftTranslator;
import gr.athena.innovation.fagi.utils.RDFUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.SerializationHelper;

/**
 * Class representing a pair of interlinked RDF entities.
 *
 * @author nkarag
 */
public class LinkedPair {

    private static final Logger LOG = LogManager.getLogger(LinkedPair.class);
    private Link link;
    private Entity leftNode;
    private Entity rightNode;
    private Entity fusedEntity;
    private boolean rejected;
    private final FusionLog fusionLog = new FusionLog();
    private EnumValidationAction validation = EnumValidationAction.UNDEFINED;
    private final EnumDatasetAction defaultDatasetAction;
    private final boolean verbose = Configuration.getInstance().isVerbose();

    public LinkedPair(EnumDatasetAction defaultDatasetAction) {
        this.defaultDatasetAction = defaultDatasetAction;
    }

    /**
     *
     * @return the Link.
     */
    public Link getLink() {
        return link;
    }

    /**
     *
     * @param link the link.
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     *
     * @return the resource of the left dataset.
     */
    public Entity getLeftNode() {
        return leftNode;
    }

    /**
     *
     * @param leftNode the resource of the left dataset.
     */
    public void setLeftNode(Entity leftNode) {
        this.leftNode = leftNode;
    }

    /**
     *
     * @return the resource of the right dataset.
     */
    public Entity getRightNode() {
        return rightNode;
    }

    /**
     *
     * @param rightNode the resource of the right dataset.
     */
    public void setRightNode(Entity rightNode) {
        this.rightNode = rightNode;
    }

    /**
     *
     * @return the fused entity if the pair is fused. Application exception is thrown otherwise.
     */
    public Entity getFusedEntity() {
        if (fusedEntity == null) {
            LOG.fatal("Current pair is not fused: " + this);
            throw new ApplicationException("Current pair is not fused: " + this);
        }
        return fusedEntity;
    }

    /**
     *
     * @param fusedEntity the fused entity.
     */
    public void setFusedEntity(Entity fusedEntity) {
        this.fusedEntity = fusedEntity;
    }

    /**
     * Validates a link between two entities. The validation is based on the provided rules.
     * 
     * @param validationRules the validation rules list.
     * @param functionMap the map containing the evaluation functions.
     * @return the validation action enumeration value.
     * 
     * @throws WrongInputException error with the given input.
     */
    public EnumValidationAction validateLink(List<Rule> validationRules, Map<String, IFunction> functionMap)
            throws WrongInputException {

        EntityData leftEntityData = leftNode.getEntityData();
        EntityData rightEntityData = rightNode.getEntityData();

        for (Rule validationRule : validationRules) {
            LOG.trace("Validating with Rule: " + validationRule);

            //assign nulls. Validation rule does not use basic properties, only external properties. 
            //These values will be ignored at condition evaluation. Todo: Consider a refactoring
            CustomRDFProperty validationProperty = null;
            Literal literalA = null;
            Literal literalB = null;

            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and break.
            if (validationRule.getActionRuleSet() == null || validationRule.getActionRuleSet().getActionRuleList().isEmpty()) {
                LOG.trace("Rule without ACTION RULE SET, using default validation action.");

                validation = validationRule.getDefaultValidationAction();

                break;
            }

            List<ActionRule> actionRules = validationRule.getActionRuleSet().getActionRuleList();
            int actionRuleCount = 0;
            boolean actionRuleToApply = false;
            for (ActionRule actionRule : actionRules) {

                LOG.debug("-- Action rule: " + actionRuleCount);

                EnumValidationAction validationAction = null;

                if (actionRule.getValidationAction() != null) {
                    validationAction = actionRule.getValidationAction();
                }

                Condition condition = actionRule.getCondition();

                //switch case for evaluation using external properties.
                for (Map.Entry<String, ExternalProperty> externalPropertyEntry : validationRule.getExternalProperties().entrySet()) {
                    evaluateExternalProperty(externalPropertyEntry, leftEntityData, rightEntityData);
                }

                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, this, validationProperty,
                        literalA, literalB, validationRule.getExternalProperties());

                actionRuleCount++;

                if (isActionRuleToBeApplied) {
                    LOG.debug("Condition : " + condition + " evaluated true. Validating link with: " + validationAction);

                    validation = validationAction;

                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action (accept)
            if (actionRuleToApply == false) {

                EnumValidationAction defaultAction = validationRule.getDefaultValidationAction();

                LOG.debug("All conditions evaluated to false in validation. Using default validation action: "
                        + defaultAction);

                validation = defaultAction;
            }
        }
        return validation;
    }

    /**
     * Fuse the pair.
     * 
     * @param ruleSpec the rule specification.
     * @param functionMap the map containing the evaluation functions.
     * @param validationAction the validation action.
     * @return the fusion info for this pair.
     * 
     * @throws WrongInputException error with given input.
     */
    public FusionLog fusePair(RuleSpecification ruleSpec, Map<String, IFunction> functionMap,
            EnumValidationAction validationAction) throws WrongInputException {

        if(verbose){
            fusionLog.setLeftURI(leftNode.getResourceURI());
            fusionLog.setRightURI(rightNode.getResourceURI());
            fusionLog.setDefaultFusionAction(defaultDatasetAction);
            fusionLog.setValidationAction(validationAction);
        }

        LOG.debug("fusing: " + leftNode.getResourceURI() + " " + rightNode.getResourceURI());
        //TODO: optimization: resolve validation action here

        EntityData leftEntityData = leftNode.getEntityData();
        EntityData rightEntityData = rightNode.getEntityData();

        fuseDefaultDatasetAction(defaultDatasetAction);       

        List<Rule> rules = ruleSpec.getRules();

        int count = 0;
        for (Rule rule : rules) {
            LOG.debug("Fusing with Rule: " + rule);

            EnumFusionAction defaultFusionAction = rule.getDefaultFusionAction();

            //TODO: change #getRDFPropertyFromString to check for propertyB when ontology is different from source datasets
            Property rdfValueParentPropertyA = RDFUtils.getRDFPropertyFromString(rule.getParentPropertyA());
            Property rdfValueParentPropertyB = RDFUtils.getRDFPropertyFromString(rule.getParentPropertyB());
            Property rdfValuePropertyA = RDFUtils.getRDFPropertyFromString(rule.getPropertyA());
            Property rdfValuePropertyB = RDFUtils.getRDFPropertyFromString(rule.getPropertyA());

            //the property here is assumed to be one resource above the node value in order  to align with the ontology.
            //For example the property is the p1 in the following linked triples.
            // s p1 o1 . o1 p2 o2 

            RDFNode nodeA;
            RDFNode nodeB;

            //child properties are always the properties that point to a node.
            CustomRDFProperty customPropertyA = new CustomRDFProperty();
            CustomRDFProperty customPropertyB = new CustomRDFProperty();

            if (rule.getParentPropertyA() == null) {
                nodeA = RDFUtils.getRDFNode(rule.getPropertyA(), leftEntityData.getModel());
                customPropertyA.setSingleLevel(true);
                customPropertyA.setValueProperty(rdfValuePropertyA);
            } else {
                nodeA = RDFUtils.getRDFNodeFromChain(rule.getParentPropertyA(), rule.getPropertyA(), leftEntityData.getModel());
                customPropertyA.setSingleLevel(false);
                customPropertyA.setParent(rdfValueParentPropertyA);
                customPropertyA.setValueProperty(rdfValuePropertyA);
            }

            if (rule.getParentPropertyB() == null) {
                nodeB = RDFUtils.getRDFNode(rule.getPropertyB(), rightEntityData.getModel());
                customPropertyB.setSingleLevel(true);
                customPropertyB.setValueProperty(rdfValuePropertyB);
            } else {
                nodeB = RDFUtils.getRDFNodeFromChain(rule.getParentPropertyB(), rule.getPropertyB(), rightEntityData.getModel());
                customPropertyB.setSingleLevel(false);
                customPropertyB.setParent(rdfValueParentPropertyB);
                customPropertyB.setValueProperty(rdfValuePropertyB);                
            }

            if (nodeA == null && nodeB == null) {
                LOG.trace("both nodes empty, skipping rule.");
                count++;
                continue;
            }

            LOG.trace("Nodes: " + leftNode.getLocalName() + " " + rightNode.getLocalName());
            LOG.debug("Fusing values: " + nodeA + " " + nodeB);
            //LOG.trace("fusing: " + literalA + " " + literalB);
            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and continue to next rule.
            if (rule.getActionRuleSet() == null || rule.getActionRuleSet().getActionRuleList().isEmpty()) {
                LOG.trace("Rule without ACTION RULE SET, use plain action: " + defaultFusionAction);
                if (defaultFusionAction != null) {
                    boolean rejectedFromRule = fuseRuleAction(defaultFusionAction, validationAction, customPropertyA, nodeA, nodeB);
                    if(rejectedFromRule){
                        this.rejected = true;
                        fusionLog.setValidationAction(EnumValidationAction.REJECT);
                        return fusionLog;
                    }
                }
                continue;
            }

            List<ActionRule> actionRules = rule.getActionRuleSet().getActionRuleList();
            int actionRuleCount = 0;
            boolean actionRuleToApply = false;
            for (ActionRule actionRule : actionRules) {

                LOG.debug("-- Action rule: " + actionRuleCount);

                EnumFusionAction fusionAction = null;

                if (actionRule.getFusionAction() != null) {
                    fusionAction = actionRule.getFusionAction();
                }

                Condition condition = actionRule.getCondition();

                for (Map.Entry<String, ExternalProperty> externalPropertyEntry : rule.getExternalProperties().entrySet()) {
                    evaluateExternalProperty(externalPropertyEntry, leftEntityData, rightEntityData);
                }
                
                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, this, customPropertyA,
                        nodeA, nodeB, rule.getExternalProperties());

                actionRuleCount++;

                if (isActionRuleToBeApplied) {
                    LOG.debug("Condition : " + condition + " evaluated true. Fusion with action: " + fusionAction);

                    boolean rejectedFromRule = fuseRuleAction(fusionAction, validationAction, customPropertyA, nodeA, nodeB);

                    if(rejectedFromRule){
                        this.rejected = true;
                        fusionLog.setValidationAction(EnumValidationAction.REJECT);
                        return fusionLog;
                    }

                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action
            if (actionRuleToApply == false) {
                LOG.debug("All conditions evaluated to false in fusion rule. Using default fusion action: "
                        + defaultFusionAction);
                boolean rejectedFromRule = fuseRuleAction(defaultFusionAction, validationAction, customPropertyA, nodeA, nodeB);
                
                if(rejectedFromRule){
                    this.rejected = true;
                    fusionLog.setValidationAction(EnumValidationAction.REJECT);
                    return fusionLog;
                }
            }
        }

        if (count >= rules.size()) {
            LOG.trace("No rules were applied for this link. Failed to retrieve literals for any of the given properties. "
                    + "" + this.getLink().getKey());
        }
        LOG.debug(fusionLog);
        return fusionLog;
    }

    private void evaluateExternalProperty(Map.Entry<String, ExternalProperty> externalPropertyEntry,
            EntityData leftEntityData, EntityData rightEntityData) {

        //There are two cases here: (a) Single property refers to node. (b) the external property contains a chain
        //separated by a whitespace.
        String extPropertyText = externalPropertyEntry.getValue().getProperty();
        Literal valueA;
        Literal valueB;

        if (extPropertyText.contains(" ")) {
            String[] chains = extPropertyText.split(" ");
            valueA = RDFUtils.getLiteralValueFromChain(chains[0], chains[1], leftEntityData.getModel());
            valueB = RDFUtils.getLiteralValueFromChain(chains[0], chains[1], rightEntityData.getModel());
        } else {
            valueA = RDFUtils.getLiteralValue(externalPropertyEntry.getValue().getProperty(), leftEntityData.getModel());
            valueB = RDFUtils.getLiteralValue(externalPropertyEntry.getValue().getProperty(), rightEntityData.getModel());
        }

        LOG.debug("valueA: " + valueA);
        LOG.debug("valueB: " + valueB);
        
        externalPropertyEntry.getValue().setValueA(valueA);
        externalPropertyEntry.getValue().setValueB(valueB);
    }

    /**
     * Fuse this pair with the default dataset action.
     * 
     * @param datasetDefaultAction the default dataset action.
     * 
     * @throws WrongInputException wrong input.
     */
    public void fuseDefaultDatasetAction(EnumDatasetAction datasetDefaultAction) throws WrongInputException {

        //default dataset action should be performed before the rules apply. The original fused model should be empty:
        if (!fusedEntity.getEntityData().getModel().isEmpty()) {
            throw new ApplicationException("Default fusion action tries to overwrite already fused data!");
        }

        EntityData fusedData = new EntityData();

        Model fusedModel = ModelFactory.createDefaultModel();

        EntityData leftData = leftNode.getEntityData();
        EntityData rightData = rightNode.getEntityData();

        switch (datasetDefaultAction) {
            case KEEP_LEFT: {

                resolveModeURIs();
                fusedModel.add(leftData.getModel());
                fusedData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedData);

                break;
            }
            case KEEP_RIGHT: {

                resolveModeURIs();
                fusedModel.add(rightData.getModel());
                fusedData.setModel(rightData.getModel());
                fusedEntity.setEntityData(fusedData);

                break;
            }
            case KEEP_BOTH: {

                resolveModeURIs();
                Model union = ModelFactory.createDefaultModel();

                union.add(leftData.getModel());
                union.add(rightData.getModel());

                fusedModel = fusedData.getModel().add(union);

                fusedData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedData);

                break;
            }
            default:
                throw new WrongInputException("Dataset default fusion action is not defined.");
        }
    }

    private boolean fuseRuleAction(EnumFusionAction action, 
            EnumValidationAction validationAction, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB) 
            throws WrongInputException {

        LOG.debug("fusing with action: " + action.toString());
        //TODO: Also, property coming from the caller is propertyA because it assumes same ontology
        //Maybe add propertyB and check them both if one does not exist in original.
        EntityData fusedEntityData = fusedEntity.getEntityData();

        Model ambiguousModel = AmbiguousDataset.getAmbiguousDataset().getModel();

        if (!isValidLink(validationAction, ambiguousModel, fusedEntityData)) {
            //LOG.info("link rejectedFromRule");
            //stop fusion, link is rejectedFromRule
            return true;
        }

        fuse(action, customProperty, nodeA, nodeB, fusedEntityData);
        
        return false;
    }

    //link validation
    private boolean isValidLink(EnumValidationAction validationAction, Model ambiguousModel,
            EntityData fusedEntityData) {

        switch (validationAction) {
            case ACCEPT:
                //do nothing
                break;
            case ACCEPT_MARK_AMBIGUOUS: {

                if (RDFUtils.isRejectedByPreviousRule(fusedEntityData.getModel())) {
                    break;
                }

                acceptMarkAmbiguous(ambiguousModel, fusedEntityData);

                break;
            }
            case REJECT: {
                reject(fusedEntityData);
                return false; //stop link fusion
            }
            case REJECT_MARK_AMBIGUOUS: {
                rejectMarkAmbiguous(ambiguousModel, fusedEntityData);
                return false; //stop link fusion
            }
            case ML_VALIDATION: {
                
                try {
                    String validationModelPath = Configuration.getInstance().getValidationModelPath();
                    
                    RandomForest validationClassifier = (RandomForest) SerializationHelper.read(new FileInputStream(validationModelPath));
                    
                    RDFNode left = RDFUtils.getRDFNodeFromChain(Namespace.NAME_NO_BRACKETS, 
                            Namespace.NAME_VALUE_NO_BRACKETS, leftNode.getEntityData().getModel());
                    RDFNode right = RDFUtils.getRDFNodeFromChain(Namespace.NAME_NO_BRACKETS, 
                            Namespace.NAME_VALUE_NO_BRACKETS, rightNode.getEntityData().getModel());
                    
                    DenseInstance instance = FeaturePreprocessor.createNameInst(
                            left.asLiteral().getLexicalForm(), right.asLiteral().getLexicalForm());
                    
                    double[] probabilities = validationClassifier.distributionForInstance(instance);
                    
                    double maxValue = -1;
                    int maxIndex = 0;
                    for(int i = 0; i < probabilities.length; i++){
                        if(probabilities[i] > maxValue){
                            maxIndex = i;
                        }
                    }

                    //0:accept |1: reject 
                    switch(maxIndex){
                        case 0:
                            //Accept. Do nothing
                            break;
                        case 1:
                            //Reject
                            reject(fusedEntityData);
                            return false; //stop link fusion
                        default:
                            LOG.warn("ML failed to recommend validation action. Accepting link.");
                            //Accept. Do nothing
                            break;
                    }

                } catch (FileNotFoundException ex) {
                    LOG.error(ex);
                    throw new ApplicationException("Validation ML model not found.");
                } catch (Exception ex) {
                    LOG.error(ex);
                    throw new ApplicationException("Failed to recommend validation action.");
                }
            }
        }

        return true;
    }

    private void fuse(EnumFusionAction action, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, 
            EntityData fusedEntityData) throws ApplicationException, WrongInputException {

        Model fusedModel = fusedEntityData.getModel();

        switch (action) {
            case KEEP_LEFT: {
                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLeft(fusedModel, customProperty, nodeA, nodeB, false);

                break;
            }
            case KEEP_LEFT_MARK: {
                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLeft(fusedModel, customProperty, nodeA, nodeB, true);

                break;
            }
            case KEEP_RIGHT: {
                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepRight(fusedModel, customProperty, nodeA, nodeB, false);

                break;
            }
            case KEEP_RIGHT_MARK: {
                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepRight(fusedModel, customProperty, nodeA, nodeB, true);

                break;
            }
            case KEEP_BOTH: {
                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepBoth(fusedModel, customProperty, nodeA, nodeB, false);

                break;
            }
            case KEEP_BOTH_MARK: {
                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepBoth(fusedModel, customProperty, nodeA, nodeB, true);

                break;
            }
            case KEEP_LONGEST: {
                RDFUtils.validateActionForProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLongest(fusedModel, customProperty, nodeA, nodeB, false);

                break;
            }
            case KEEP_LONGEST_MARK: {
                RDFUtils.validateActionForProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLongest(fusedModel, customProperty, nodeA, nodeB, true);

                break;
            }
            case CONCATENATE: {
                RDFUtils.validateActionForProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenate(fusedModel, customProperty, nodeA, nodeB, false);

                break;
            }
            case CONCATENATE_MARK: {
                RDFUtils.validateActionForProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenate(fusedModel, customProperty, nodeA, nodeB, true);

                break;
            }
            case KEEP_MOST_RECENT: {
                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMostRecent(fusedModel, customProperty, nodeA, nodeB, false);

                break;
            }
            case KEEP_MOST_RECENT_MARK: {

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMostRecent(fusedModel, customProperty, nodeA, nodeB, true);

                break;
            }
            case KEEP_MORE_POINTS: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePoints(nodeA, nodeB, fusedModel, customProperty, false);

                break;
            }
            case KEEP_MORE_POINTS_MARK: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePoints(nodeA, nodeB, fusedModel, customProperty, true);

                break;
            }
            case KEEP_MORE_POINTS_AND_SHIFT: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePointsAndShift(nodeA, nodeB, fusedModel, customProperty, false);

                break;
            }
            case KEEP_MORE_POINTS_AND_SHIFT_MARK: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePointsAndShift(nodeA, nodeB, fusedModel, customProperty, true);

                break;
            }
            case SHIFT_LEFT_GEOMETRY: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftLeftGeometry(nodeA, nodeB, fusedModel, customProperty, false);

                break;
            }
            case SHIFT_LEFT_GEOMETRY_MARK: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftLeftGeometry(nodeA, nodeB, fusedModel, customProperty, true);

                break;
            }
            case SHIFT_RIGHT_GEOMETRY: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftRightGeometry(nodeA, nodeB, fusedModel, customProperty, false);

                break;
            }
            case SHIFT_RIGHT_GEOMETRY_MARK: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftRightGeometry(nodeA, nodeB, fusedModel, customProperty, true);

                break;
            }
            case CONCATENATE_GEOMETRY: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenateGeometry(nodeA, nodeB, fusedModel, customProperty, false);

                break;
            }
            case CONCATENATE_GEOMETRY_MARK: {
                RDFUtils.validateGeometryProperty(customProperty.getValueProperty(), action);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenateGeometry(nodeA, nodeB, fusedModel, customProperty, true);

                break;
            }
            case KEEP_MOST_COMPLETE_NAME: {
                RDFUtils.validateNameAction(customProperty);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMostCompleteName(fusedModel, customProperty, nodeA, nodeB, false);

                break;
            }
            case KEEP_MOST_COMPLETE_NAME_MARK: {
                RDFUtils.validateNameAction(customProperty);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMostCompleteName(fusedModel, customProperty, nodeA, nodeB, true);

                break;
            }
            case KEEP_RECOMMENDED: {
                RDFUtils.validateNameAction(customProperty);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepRecommended(fusedModel,customProperty, nodeA, nodeB, false);

                break;
            }
            case KEEP_RECOMMENDED_MARK: {
                RDFUtils.validateNameAction(customProperty);

                if (RDFUtils.isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepRecommended(fusedModel,customProperty, nodeA, nodeB, true);

                break;
            }
            default:
                throw new ApplicationException("fusion action not supported: " + action);
        }
    }

    private void keepBoth(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) {
        if(nodeA == null && nodeB == null){
            //keep both cannot be applied here. Do nothing and let fusion mode and default dataset action to decide what is kept
            return;
        }

        Resource rootResource = RDFUtils.getRootResource(leftNode, rightNode);
        if(customProperty.isSingleLevel()){
            //remove rootResource - valueProperty - anyNodes
            fusedModel.removeAll((Resource) null, customProperty.getValueProperty(), (RDFNode) null);
            //add new rootResource - valueProperty - leftNode
            //add new rootResource - valueProperty - rightNode
            if(nodeA != null){
                fusedModel.add(rootResource, customProperty.getValueProperty(), nodeA);
            }

            if(nodeB != null){
                fusedModel.add(rootResource, customProperty.getValueProperty(), nodeB);
            }

        } else {
            Property parentProperty = customProperty.getParent();
            Property valueProperty = customProperty.getValueProperty();
            Model sourceModel = leftNode.getEntityData().getModel(); 
            //find o1 and remove both statements
            RDFNode o1 = SparqlRepository.getObjectOfProperty(rootResource, customProperty.getParent(), sourceModel);
            if(o1 == null){
                sourceModel = rightNode.getEntityData().getModel();
                o1 = SparqlRepository.getObjectOfProperty(rootResource, customProperty.getParent(), sourceModel);
            }
            if(o1.isResource()){
                //remove rootResource - parentProperty - o1 . o1 - valueProperty - anyNode
                fusedModel.removeAll(rootResource, parentProperty, (RDFNode) null);
                fusedModel.removeAll(o1.asResource(), valueProperty, (RDFNode) null);
                //add rootResource - valueProperty - o1 . o1 - valueProperty - leftNode
                fusedModel.add(rootResource, parentProperty, o1);
                
                if(nodeA != null){
                    fusedModel.add(o1.asResource(), valueProperty, nodeA);
                }
                
                if(nodeB != null){
                    fusedModel.add(o1.asResource(), valueProperty, nodeB);
                }
            } else {
                throw new ApplicationException("object " + o1 + " is not a resource.");
            }
        }

        if(mark){
            markAmbiguous(customProperty, rootResource, fusedModel);
        }

        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_BOTH_MARK, customProperty, nodeA, nodeB);
            } else {
                addToLog(EnumFusionAction.KEEP_BOTH, customProperty, nodeA, nodeB);
            }
        }
    }

    private void keepMostCompleteName(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) {
        if(nodeA == null && nodeB == null){
            //nothing to do here
            return;
        }

        Model union = ModelFactory.createDefaultModel();

        union.add(leftNode.getEntityData().getModel());
        union.add(rightNode.getEntityData().getModel());

        NameModel nameAttributes = SparqlRepository.getNameAttributes(union);
        NodeIterator nameNodes = SparqlRepository.getObjectsOfProperty(customProperty.getValueProperty(), union);

        while(nameNodes.hasNext()){
            RDFNode node = nameNodes.next();
            fusedModel.removeAll((Resource) null, (Property) null, node.asResource());
            fusedModel.removeAll(node.asResource(), (Property) null, (RDFNode) null);
        }

        fusedModel.removeAll((Resource) null, customProperty.getValueProperty(), (RDFNode) null);

        StringJoiner joiner = new StringJoiner(SpecificationConstants.Rule.CONCATENATION_SEP);
        for(TypedNameAttribute typed : nameAttributes.getTyped()){
            fusedModel.add(typed.getStatements());
            joiner.add(typed.getNameValue().toString());
        }

        for(NameAttribute noType : nameAttributes.getWithoutType()){
            fusedModel.add(noType.getStatements());
            joiner.add(noType.getNameValue().toString());
        }

        if(verbose){
            //todo: nodeA and nodeB do not contain all name properties that participated in this fusion action.
            if(mark){
                addToLog(EnumFusionAction.KEEP_MOST_COMPLETE_NAME_MARK, customProperty, nodeA, nodeB, joiner.toString());
            } else {
                addToLog(EnumFusionAction.KEEP_MOST_COMPLETE_NAME, customProperty, nodeA, nodeB, joiner.toString());
            }
        }
    }

    private void keepLeft(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) {
        if(nodeA == null){
            //keep left cannot be applied here. Do nothing and let fusion mode and default dataset action to decide what is kept
            return;
        }

        Model sourceModel = leftNode.getEntityData().getModel();
        updateFusedModel(customProperty, fusedModel, nodeA, sourceModel, mark);
        
        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_LEFT_MARK, customProperty, nodeA, nodeB, nodeA);
            } else {
                addToLog(EnumFusionAction.KEEP_LEFT, customProperty, nodeA, nodeB, nodeA);
            }
        }
    }

    private void keepRight(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) {
        if(nodeB == null){
            //keep right cannot be applied here. Do nothing and let fusion mode and default dataset action to decide what is kept
            return;
        }

        Model sourceModel = rightNode.getEntityData().getModel();
        updateFusedModel(customProperty, fusedModel, nodeB, sourceModel, mark);
        
        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_RIGHT_MARK, customProperty, nodeA, nodeB, nodeB);
            } else {
                addToLog(EnumFusionAction.KEEP_RIGHT, customProperty, nodeA, nodeB, nodeB);
            }
        }
    }

    private void keepLongest(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) {

        RDFNode longest;
        Model sourceModel;
        if(nodeA == null){
            longest = nodeB;
            sourceModel = rightNode.getEntityData().getModel();
        } else if( nodeB == null){
            longest = nodeA;
            sourceModel = leftNode.getEntityData().getModel();
        } else {
            sourceModel = leftNode.getEntityData().getModel();
            String sA;
            String sB;
            if(nodeA.isLiteral() && nodeB.isLiteral()){
                String literalA = nodeA.asLiteral().getLexicalForm();
                String literalB = nodeB.asLiteral().getLexicalForm();
                sA = Normalizer.normalize(literalA, Normalizer.Form.NFC);
                sB = Normalizer.normalize(literalB, Normalizer.Form.NFC);
            } else {
                //not literals, treat as a string.
                sA = nodeA.toString();
                sB = nodeB.toString();
            }
            
            if (sA.length() > sB.length()) {
                longest = nodeA;
            } else {
                longest = nodeB;
            }
        }

        updateFusedModel(customProperty, fusedModel, longest, sourceModel, mark);

        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_LONGEST_MARK, customProperty, nodeA, nodeB, longest);
            } else {
                addToLog(EnumFusionAction.KEEP_LONGEST, customProperty, nodeA, nodeB, longest);
            }
        }
    }

    private void concatenate(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) {
        //todo: resolve language tag if exists
        Model sourceModel;
        String sep = SpecificationConstants.Rule.CONCATENATION_SEP;
        RDFNode concatenated;
        if(nodeA == null){
            concatenated = nodeB;
            sourceModel = rightNode.getEntityData().getModel();
        } else if(nodeB == null){
            concatenated = nodeA;
            sourceModel = leftNode.getEntityData().getModel();
        } else {
            sourceModel = leftNode.getEntityData().getModel();
            if(nodeA.isLiteral() && nodeB.isLiteral()){
                String con = nodeA.asLiteral().getLexicalForm() + sep + nodeB.asLiteral().getLexicalForm();
                concatenated = ResourceFactory.createStringLiteral(con);
            } else {
                LOG.warn("Concatenating resources, not literals: " + nodeA + " " + nodeB);
                String con = nodeA.asResource().getURI() + sep + nodeB.asResource().getURI();
                concatenated = ResourceFactory.createResource(con);
                //todo: allow this action to happen?
                //throw new ApplicationException("Concatenation cannot be applied on resources (" + nodeA + " " + nodeB + ")");
            }
        }

        updateFusedModel(customProperty, fusedModel, concatenated, sourceModel, mark);

        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.CONCATENATE_MARK, customProperty, nodeA, nodeB, concatenated);
            } else {
                addToLog(EnumFusionAction.CONCATENATE, customProperty, nodeA, nodeB, concatenated);
            }
        }
    }

    private void keepMostRecent(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) 
            throws ApplicationException {

        RDFNode fusedValue;
        if(nodeA == null){
            Model sourceModel = rightNode.getEntityData().getModel();
            fusedValue = nodeB;
            updateFusedModel(customProperty, fusedModel, fusedValue, sourceModel, mark);
        } else if(nodeB == null){
            fusedValue = nodeA;
            Model sourceModel = leftNode.getEntityData().getModel();
            updateFusedModel(customProperty, fusedModel, fusedValue, sourceModel, mark);
        } else {
            
            EnumDataset mostRecent = Configuration.getInstance().getMostRecentDataset();
            switch (mostRecent) {
                case LEFT: {
                    Model sourceModel = leftNode.getEntityData().getModel();
                    fusedValue = nodeA;
                    updateFusedModel(customProperty, fusedModel, nodeA, sourceModel, mark);
                    break;
                }
                case RIGHT: {
                    Model sourceModel = rightNode.getEntityData().getModel();
                    fusedValue = nodeB;
                    updateFusedModel(customProperty, fusedModel, fusedValue, sourceModel, mark);
                    break;
                }
                case UNDEFINED:
                default:
                    fusedValue = null;
                    //do not remove statement. Default dataset action should be kept in this case (no dates provided)
                    break;
            }
        }
        
        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_MOST_RECENT_MARK, customProperty, nodeA, nodeB, fusedValue);
            } else {
                addToLog(EnumFusionAction.KEEP_MOST_RECENT, customProperty, nodeA, nodeB, fusedValue);
            }
        }
    }

    private void keepMorePoints(RDFNode nodeA, RDFNode nodeB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        RDFNode fused;
        Model sourceModel;
        if(nodeA == null){
            fused = nodeB;
            sourceModel = rightNode.getEntityData().getModel();
        } else if(nodeB == null){
            fused = nodeA;
            sourceModel = leftNode.getEntityData().getModel();
        } else {
            if(!nodeA.isLiteral() || !nodeB.isLiteral()){
                throw new ApplicationException("Trying to fuse non literal geometry: " + nodeA + " " + nodeB);
            }

            sourceModel = leftNode.getEntityData().getModel();
            Geometry leftGeometry = RDFUtils.parseGeometry(nodeA.asLiteral().getLexicalForm());
            Geometry rightGeometry = RDFUtils.parseGeometry(nodeB.asLiteral().getLexicalForm());
        
            if (leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {
                fused = nodeA;
            } else {
                fused = nodeB;
            }
        }

        updateFusedModel(customProperty, fusedModel, fused, sourceModel, mark);

        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_MORE_POINTS_MARK, customProperty, nodeA, nodeB, fused);
            } else {
                addToLog(EnumFusionAction.KEEP_MORE_POINTS, customProperty, nodeA, nodeB, fused);
            }
        }
    }

    private void keepMorePointsAndShift(RDFNode nodeA, RDFNode nodeB, Model fusedModel, 
            CustomRDFProperty customProperty, boolean mark) {

        RDFNode geometryLiteral;
        Model sourceModel;
        if(nodeA == null){
            geometryLiteral = nodeB;
            sourceModel = rightNode.getEntityData().getModel();
        } else if(nodeB == null){
            geometryLiteral = nodeA;
            sourceModel = leftNode.getEntityData().getModel();
        } else {
            if(!nodeA.isLiteral() || !nodeB.isLiteral()){
                throw new ApplicationException("Trying to fuse non literal geometry: " + nodeA + " " + nodeB);
            }

            sourceModel = leftNode.getEntityData().getModel();
            Geometry leftGeometry = RDFUtils.parseGeometry(nodeA.asLiteral().getLexicalForm());
            Geometry rightGeometry = RDFUtils.parseGeometry(nodeB.asLiteral().getLexicalForm());
            RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
            
            if (leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {
                CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                Geometry fusedGeometry = centroidTranslator.shift(leftGeometry);
                String wktFusedGeometry = RDFUtils.getWKTLiteral(fusedGeometry);
                String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
                //explicit add datatype because geometry object does not contain the CRS prefix
                geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);
            } else {
                CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                Geometry fusedGeometry = centroidTranslator.shift(rightGeometry);
                String wktFusedGeometry = RDFUtils.getWKTLiteral(fusedGeometry);

                String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
                //explicit add datatype because geometry object does not contain the CRS prefix
                geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);
            }
        }

        updateFusedModel(customProperty, fusedModel, geometryLiteral, sourceModel, mark);

        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_MORE_POINTS_AND_SHIFT_MARK, customProperty, nodeA, nodeB, geometryLiteral);
            } else {
                addToLog(EnumFusionAction.KEEP_MORE_POINTS_AND_SHIFT, customProperty, nodeA, nodeB, geometryLiteral);
            }
        }
    }

    private void shiftLeftGeometry(RDFNode nodeA, RDFNode nodeB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        if(nodeA == null){
            //right geometry cannot be shifted onto the left.
            //let default dataset action decide what happens with this property
            return;
        }
        if(nodeB == null){
            //there is no geometry to shift.
            //let default dataset action decide what happens with this property
            return;
        }
        if(!nodeA.isLiteral() || !nodeB.isLiteral()) {
            throw new ApplicationException("Trying to fuse non literal geometry: " + nodeA + " " + nodeB);
        }

        if(mark){
            shiftGeometry(nodeB, nodeA, customProperty, fusedModel, mark, EnumFusionAction.SHIFT_LEFT_GEOMETRY_MARK);
        } else {
            shiftGeometry(nodeB, nodeA, customProperty, fusedModel, mark, EnumFusionAction.SHIFT_LEFT_GEOMETRY);
        }
    }

    private void shiftRightGeometry(RDFNode nodeA, RDFNode nodeB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        if(nodeA == null){
            //right geometry cannot be shifted onto the left.
            //let default dataset action decide what happens with this property
            return;
        }
        if(nodeB == null){
            //there is no geometry to shift.
            //let default dataset action decide what happens with this property
            return;
        }
        if(!nodeA.isLiteral() || !nodeB.isLiteral()) {
            throw new ApplicationException("Trying to fuse non literal geometry: " + nodeA + " " + nodeB);
        }
        
        if(mark){
            shiftGeometry(nodeA, nodeB, customProperty, fusedModel, mark, EnumFusionAction.SHIFT_RIGHT_GEOMETRY_MARK);
        } else {
            shiftGeometry(nodeA, nodeB, customProperty, fusedModel, mark, EnumFusionAction.SHIFT_RIGHT_GEOMETRY);
        }
    }

    private void shiftGeometry(RDFNode nodeA, RDFNode nodeB, CustomRDFProperty customProperty, Model fusedModel, 
            boolean mark, EnumFusionAction fusionAction) throws ApplicationException {
        
        String literalA = nodeA.asLiteral().getLexicalForm();
        String literalB = nodeB.asLiteral().getLexicalForm();

        Geometry leftGeometry = RDFUtils.parseGeometry(literalA);
        Geometry rightGeometry = RDFUtils.parseGeometry(literalB);
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
        Geometry shiftedToLeftGeometry = centroidTranslator.shift(rightGeometry);
        String wktFusedGeometry = RDFUtils.getWKTLiteral(shiftedToLeftGeometry);
        String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
        //explicit add datatype because geometry object does not contain the CRS prefix
        Literal geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);
        Model sourceModel = rightNode.getEntityData().getModel();
        
        updateFusedModel(customProperty, fusedModel, geometryLiteral, sourceModel, mark);

        if(verbose){
            addToLog(fusionAction, customProperty, nodeA, nodeB, geometryLiteral);
        }
    }
    
    private void concatenateGeometry(RDFNode nodeA, RDFNode nodeB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        RDFNode concatenatedGeom;
        Model sourceModel;
        if(nodeA == null){
            concatenatedGeom = nodeB;
            sourceModel = rightNode.getEntityData().getModel();
        } else if(nodeB == null){
            concatenatedGeom = nodeA;
            sourceModel = leftNode.getEntityData().getModel();
        } else {
            if(!nodeA.isLiteral() || !nodeB.isLiteral()){
                throw new ApplicationException("Trying to fuse non literal geometry: " + nodeA + " " + nodeB);
            }
            sourceModel = leftNode.getEntityData().getModel();
            RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
            Geometry leftGeometry = RDFUtils.parseGeometry(nodeA.asLiteral().getLexicalForm());
            Geometry rightGeometry = RDFUtils.parseGeometry(nodeB.asLiteral().getLexicalForm());
            Geometry[] geometries = new Geometry[]{leftGeometry, rightGeometry};
            Geometry geometryCollection = new GeometryCollection(geometries, new GeometryFactory());
            String wktFusedGeometry = RDFUtils.getWKTLiteral(geometryCollection);
            String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
            //explicit add datatype because geometry object does not contain the CRS prefix
            concatenatedGeom = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);
        }

        updateFusedModel(customProperty, fusedModel, concatenatedGeom, sourceModel, mark);

        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.CONCATENATE_GEOMETRY_MARK, customProperty, nodeA, nodeB, concatenatedGeom);
            } else {
                addToLog(EnumFusionAction.CONCATENATE_GEOMETRY, customProperty, nodeA, nodeB, concatenatedGeom);
            }
        }
    }

    private void keepRecommended(Model fusedModel, CustomRDFProperty customProperty, RDFNode nodeA, RDFNode nodeB, boolean mark) 
            throws ApplicationException {

        Configuration config = Configuration.getInstance();

        try {

            String prop;
            if(customProperty.isSingleLevel()){
                prop = customProperty.getValueProperty().toString();
            } else {
                prop = customProperty.getParent().toString();
            }

            switch (prop) {
                case Namespace.NAME_NO_BRACKETS: {
                    Model sourceA = leftNode.getEntityData().getModel();
                    Model sourceB = rightNode.getEntityData().getModel();
                    int countA = SparqlRepository.countObjectsOfPropertyChain(Namespace.NAME_NO_BRACKETS, 
                            Namespace.NAME_VALUE_NO_BRACKETS, sourceA);
                    int countB = SparqlRepository.countObjectsOfPropertyChain(Namespace.NAME_NO_BRACKETS, 
                            Namespace.NAME_VALUE_NO_BRACKETS, sourceB);

                    //if the property is name and the name values are multiple, apply "keep-most-complete-name"
                    //This is a shortcut, as the model is supposed to predict this action when multiple name values exist. 
                    //In any other case, send the pair to the ML model.
                    if(countA > 1 || countB > 1){
                        //overriding model in order to ensure the "keepMostCompleteModel" action in case of multiple name properties.
                        keepMostCompleteName(fusedModel, customProperty, nodeA, nodeB, mark);
                        break;
                    }

                    RandomForest nameClassifier = (RandomForest) SerializationHelper.read(new FileInputStream(config.getNameModelPath()));
                    RDFNode left = RDFUtils.getRDFNodeFromChain(Namespace.NAME_NO_BRACKETS, 
                            Namespace.NAME_VALUE_NO_BRACKETS, nodeA.getModel());
                    RDFNode right = RDFUtils.getRDFNodeFromChain(Namespace.NAME_NO_BRACKETS, 
                            Namespace.NAME_VALUE_NO_BRACKETS, nodeB.getModel());
                    DenseInstance nameInstance = FeaturePreprocessor.createNameInst(
                            left.asLiteral().getLexicalForm(), right.asLiteral().getLexicalForm());

                    double[] probabilities = nameClassifier.distributionForInstance(nameInstance);
                    applyRecommendedAction(probabilities, fusedModel, customProperty, nodeA, nodeB, mark);
  
                    break;
                }
                case Namespace.ADDRESS_NO_BRACKETS:
                case Namespace.STREET_NO_BRACKETS:
                case Namespace.STREET_NUMBER_NO_BRACKETS: {

                    //get same action for address street and address number to avoid cross-mixing values.
                    RandomForest classifier = (RandomForest) SerializationHelper.read(new FileInputStream(config.getNameModelPath()));
                    RDFNode left = RDFUtils.getRDFNodeFromChain(Namespace.ADDRESS_NO_BRACKETS, 
                            Namespace.STREET_NO_BRACKETS, nodeA.getModel());
                    RDFNode right = RDFUtils.getRDFNodeFromChain(Namespace.ADDRESS_NO_BRACKETS, 
                            Namespace.STREET_NO_BRACKETS, nodeB.getModel());
                    DenseInstance dInstance = FeaturePreprocessor.createStreetInst(
                            left.asLiteral().getLexicalForm(), right.asLiteral().getLexicalForm());

                    double[] probabilities = classifier.distributionForInstance(dInstance);
                    applyRecommendedAction(probabilities, fusedModel, customProperty, nodeA, nodeB, mark);

                    break;
                }
                case Namespace.HOMEPAGE_NO_BRACKETS: {
                    RandomForest classifier = (RandomForest) SerializationHelper.read(new FileInputStream(config.getNameModelPath()));
                    RDFNode left = RDFUtils.getRDFNode(Namespace.HOMEPAGE_NO_BRACKETS, nodeA.getModel());
                    RDFNode right = RDFUtils.getRDFNode(Namespace.HOMEPAGE_NO_BRACKETS, nodeB.getModel());
                    DenseInstance dInstance = FeaturePreprocessor.createWebInst(
                            left.asLiteral().getLexicalForm(), right.asLiteral().getLexicalForm());

                    double[] probabilities = classifier.distributionForInstance(dInstance);
                    applyRecommendedAction(probabilities, fusedModel, customProperty, nodeA, nodeB, mark);

                    break;
                }
                case Namespace.PHONE_NO_BRACKETS: {

                    RandomForest classifier = (RandomForest) SerializationHelper.read(new FileInputStream(config.getNameModelPath()));
                    RDFNode left = RDFUtils.getRDFNodeFromChain(Namespace.PHONE_NO_BRACKETS, 
                            Namespace.CONTACT_VALUE_NO_BRACKETS, nodeA.getModel());
                    RDFNode right = RDFUtils.getRDFNodeFromChain(Namespace.PHONE_NO_BRACKETS, 
                            Namespace.CONTACT_VALUE_NO_BRACKETS, nodeB.getModel());
                    DenseInstance dInstance = FeaturePreprocessor.createTeleInst(
                            left.asLiteral().getLexicalForm(), right.asLiteral().getLexicalForm());

                    double[] probabilities = classifier.distributionForInstance(dInstance);
                    applyRecommendedAction(probabilities, fusedModel, customProperty, nodeA, nodeB, mark);

                    break;
                }
                case Namespace.EMAIL_NO_BRACKETS: {

                    RandomForest classifier = (RandomForest) SerializationHelper.read(new FileInputStream(config.getNameModelPath()));
                    RDFNode left = RDFUtils.getRDFNodeFromChain(Namespace.EMAIL_NO_BRACKETS, 
                            Namespace.CONTACT_VALUE_NO_BRACKETS, nodeA.getModel());
                    RDFNode right = RDFUtils.getRDFNodeFromChain(Namespace.EMAIL_NO_BRACKETS, 
                            Namespace.CONTACT_VALUE_NO_BRACKETS, nodeB.getModel());
                    DenseInstance dInstance = FeaturePreprocessor.createEmailInst(
                            left.asLiteral().getLexicalForm(), right.asLiteral().getLexicalForm());

                    double[] probabilities = classifier.distributionForInstance(dInstance);
                    applyRecommendedAction(probabilities, fusedModel, customProperty, nodeA, nodeB, mark);

                    break;
                }
                case Namespace.GEOSPARQL_HAS_GEOMETRY: {
                    //overriding ML model to ensure the "keep-more-points" action will be applied
                    keepMorePoints(nodeA, nodeB, fusedModel, customProperty, mark);
                    break;
                }
            }

        } catch (FileNotFoundException ex) {
            LOG.error(ex);
            throw new ApplicationException(ex.getMessage());
        } catch (Exception ex) {
            LOG.error(ex);
            throw new ApplicationException(ex.getMessage());
        }

        RDFNode fused = null;
        if(verbose){
            if(mark){
                addToLog(EnumFusionAction.KEEP_RECOMMENDED_MARK, customProperty, nodeA, nodeB, fused);
            } else {
                addToLog(EnumFusionAction.KEEP_RECOMMENDED, customProperty, nodeA, nodeB, fused);
            }
        }
    }

    private void updateFusedModel(CustomRDFProperty customProperty, Model fusedModel, RDFNode fusedNode, Model sourceModel, 
            boolean mark) throws ApplicationException {
        
        Resource rootResource = RDFUtils.getRootResource(leftNode, rightNode);
        if(customProperty.isSingleLevel()){
            //remove rootResource - valueProperty - anyNodes
            fusedModel.removeAll((Resource) null, customProperty.getValueProperty(), (RDFNode) null);
            //add new rootResource - valueProperty - leftNode
            fusedModel.add(rootResource, customProperty.getValueProperty(), fusedNode);
        } else {
            Property parentProperty = customProperty.getParent();
            Property valueProperty = customProperty.getValueProperty();
            //find o1 and remove both statements
            RDFNode o1 = SparqlRepository.getObjectOfProperty(rootResource, customProperty.getParent(), sourceModel);
            if(o1.isResource()){
                //remove rootResource - parentProperty - o1 . o1 - valueProperty - anyNode
                fusedModel.removeAll((Resource) null, parentProperty, o1.asResource());
                fusedModel.removeAll(o1.asResource(), valueProperty, (RDFNode) null);
                //add rootResource - valueProperty - o1 . o1 - valueProperty - leftNode
                fusedModel.add(rootResource, parentProperty, o1.asResource());
                fusedModel.add(o1.asResource(), valueProperty, fusedNode);
            } else {
                throw new ApplicationException("object " + o1 + " is not a resource.");
            }
        }

        if(mark){
            markAmbiguous(customProperty, rootResource, fusedModel);
        }
    }

    private void markAmbiguous(CustomRDFProperty customProperty, Resource node, Model fusedModel) {
        Statement statement;
        Statement parentStatement;

        if(customProperty.isSingleLevel()){
            statement = RDFUtils.getAmbiguousPropertyStatement(node.getURI(), customProperty.getValueProperty());
            fusedModel.add(statement);
        } else {
            parentStatement = RDFUtils.getAmbiguousPropertyStatement(node.getURI(), customProperty.getParent());
            statement = RDFUtils.getAmbiguousSubPropertyStatement(node.getURI(), customProperty.getValueProperty());
            fusedModel.add(parentStatement);
            fusedModel.add(statement);
        }
    }

    private void rejectMarkAmbiguous(Model ambiguousModel, EntityData fusedEntityData) {
        if(!LinksModel.getLinksModel().getRejected().contains(link)){
            LinksModel.getLinksModel().getRejected().add(link);
        }

        Model fusedModel = fusedEntityData.getModel();

        EnumOutputMode mode = Configuration.getInstance().getOutputMode();
        switch(mode) {
            case AA_MODE:
            case AB_MODE:  
            case A_MODE:    
            {
                if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_RIGHT)){
                    fusedModel.removeAll();
                } else if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_BOTH)){
                    fusedModel.removeAll();
                    fusedModel.add(leftNode.getEntityData().getModel());
                }

                Statement statement = RDFUtils.getAmbiguousLinkStatement(leftNode.getResourceURI(), rightNode.getResourceURI());
                fusedModel.add(statement);

                ambiguousModel.add(rightNode.getEntityData().getModel());
                break;
            }
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
            {
                
                if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_LEFT)){
                    fusedModel.removeAll();
                } else if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_BOTH)){
                    fusedModel.removeAll();
                    fusedModel.add(rightNode.getEntityData().getModel());
                }
                
                Statement statement = RDFUtils.getAmbiguousLinkStatement(rightNode.getResourceURI(), leftNode.getResourceURI());
                fusedModel.add(statement);
                ambiguousModel.add(leftNode.getEntityData().getModel());
                break;
            }
            case L_MODE:
            {
                fusedModel.removeAll();
                Statement statement = RDFUtils.getAmbiguousLinkStatement(leftNode.getResourceURI(), rightNode.getResourceURI());
                fusedModel.add(statement);
                break; 
            }

            default:
                throw new UnsupportedOperationException("Wrong Output mode!");               
        }

        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void reject(EntityData fusedEntityData) {
        if(!LinksModel.getLinksModel().getRejected().contains(link)){
            LinksModel.getLinksModel().getRejected().add(link);
        }
        
        Model fusedModel = fusedEntityData.getModel();
        if (!fusedModel.isEmpty()) {
            EnumOutputMode mode = Configuration.getInstance().getOutputMode();
            switch(mode) {
                case AA_MODE:
                case AB_MODE:  
                case A_MODE:    
                {
                    if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_RIGHT)){
                        fusedModel.removeAll();
                    } else if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_BOTH)){
                        fusedModel.removeAll();
                        fusedModel.add(leftNode.getEntityData().getModel());
                    }
                    break;
                }
                case BB_MODE:
                case BA_MODE:
                case B_MODE:
                {
                    if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_LEFT)){
                        fusedModel.removeAll();
                    } else if(defaultDatasetAction.equals(EnumDatasetAction.KEEP_BOTH)){
                        fusedModel.removeAll();
                        fusedModel.add(rightNode.getEntityData().getModel());
                    }
                    break;
                }
                case L_MODE:
                {
                    fusedModel.removeAll();
                    break; 
                }

                default:
                    throw new UnsupportedOperationException("Wrong Output mode!");               
            }
        }

        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void acceptMarkAmbiguous(Model ambiguousModel, EntityData fusedEntityData) {
        Statement statement = RDFUtils.getAmbiguousLinkStatement(leftNode.getResourceURI(), rightNode.getResourceURI());

        Model fusedModel = fusedEntityData.getModel();

        ambiguousModel.add(leftNode.getEntityData().getModel());
        ambiguousModel.add(rightNode.getEntityData().getModel());
        ambiguousModel.add(statement);

        fusedModel.add(statement);
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void resolveModeURIs() {
        EnumOutputMode mode = Configuration.getInstance().getOutputMode();

        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
                RDFUtils.renameResourceURIs(rightNode, leftNode);         
                break;
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                RDFUtils.renameResourceURIs(leftNode, rightNode);
                break;
        }
    }

    public boolean isRejected() {
        return rejected;
    }

    private void addToLog(EnumFusionAction fusionAction, CustomRDFProperty customProperty, RDFNode a, RDFNode b, RDFNode fused) {
        String valA;
        String valB;
        String fusedValue;
        if(a == null){
            valA = null;
        } else {
            valA = a.toString();
        }
        if(b == null){
            valB = null;
        } else {
            valB = b.toString();
        }
        if(fused == null){
            fusedValue = null;
        } else {
            fusedValue = fused.toString();
        }
        Action action;
        String valueProperty = customProperty.getValueProperty().toString();
        if(customProperty.isSingleLevel()){
            action = new Action(valueProperty, fusionAction.toString(), valA, valB, fusedValue);
        } else {
            String parentProperty = customProperty.getParent().toString();
            action = new Action(parentProperty + " " + valueProperty, fusionAction.toString(), valA, valB, fusedValue);
        }
        fusionLog.addAction(action);
    }

    private void addToLog(EnumFusionAction fusionAction, CustomRDFProperty customProperty, RDFNode a, RDFNode b, String fused) {
        String valA;
        String valB;
        String fusedValue;
        if(a == null){
            valA = null;
        } else {
            valA = a.toString();
        }
        if(b == null){
            valB = null;
        } else {
            valB = b.toString();
        }
        if(fused == null){
            fusedValue = null;
        } else {
            fusedValue = fused;
        }
        Action action;
        String valueProperty = customProperty.getValueProperty().toString();
        if(customProperty.isSingleLevel()){
            action = new Action(valueProperty, fusionAction.toString(), valA, valB, fusedValue);
        } else {
            String parentProperty = customProperty.getParent().toString();
            action = new Action(parentProperty + " " + valueProperty, fusionAction.toString(), valA, valB, fusedValue);
        }
        fusionLog.addAction(action);
    }

    private void addToLog(EnumFusionAction fusionAction, CustomRDFProperty customProperty, RDFNode a, RDFNode b) {
        String valA;
        String valB;
        String fusedValue = null;
        if(a == null){
            valA = null;
        } else {
            valA = a.toString();
        }
        if(b == null){
            valB = null;
        } else {
            valB = b.toString();
        }
        Action action;
        String valueProperty = customProperty.getValueProperty().toString();
        if(customProperty.isSingleLevel()){
            action = new Action(valueProperty, fusionAction.toString(), valA, valB, fusedValue);
        } else {
            String parentProperty = customProperty.getParent().toString();
            action = new Action(parentProperty + " " + valueProperty, fusionAction.toString(), valA, valB, 
                    valA + SpecificationConstants.Rule.CONCATENATION_SEP + valB);
        }
        fusionLog.addAction(action);
    }

    private void applyRecommendedAction(double[] probabilities, Model fusedModel, 
            CustomRDFProperty customProp, RDFNode a, RDFNode b, boolean mark) {

        double maxValue = -1;
        int maxIndex = 0;
        for(int i = 0; i < probabilities.length; i++){
            if(probabilities[i] > maxValue){
                maxIndex = i;
            }
        }
        //0:keep left |1: keep right |2: keep both |3: keep any
        switch(maxIndex){
            case 0:
            case 3:
                //keep any is recommended when both values are the same. Keeping the left value in this case
                keepLeft(fusedModel, customProp, a, b, mark);
                break;
            case 1:
                keepRight(fusedModel, customProp, a, b, mark);
                break;
            case 2:
                concatenate(fusedModel, customProp, a, b, mark);
                break;
            default:
                LOG.warn("ML failed to recommend an action for property: " + customProp.getValueProperty() + ". Keeping left.");
                keepLeft(fusedModel, customProp, a, b, mark);
                break;
        }
    }

    public FusionLog getFusionLog() {
        return fusionLog;
    }
}