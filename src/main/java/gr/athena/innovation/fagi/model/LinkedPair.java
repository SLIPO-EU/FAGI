package gr.athena.innovation.fagi.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    EnumValidationAction validation = EnumValidationAction.UNDEFINED;

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
            if (validationRule.getActionRuleSet() == null) {
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
     * 
     * @throws WrongInputException error with given input.
     */
    public void fusePair(RuleSpecification ruleSpec, Map<String, IFunction> functionMap,
            EnumValidationAction validationAction) throws WrongInputException {

        //TODO: optimization: resolve validation action here 
        EnumDatasetAction defaultDatasetAction = ruleSpec.getDefaultDatasetAction();

        EntityData leftEntityData = leftNode.getEntityData();
        EntityData rightEntityData = rightNode.getEntityData();

        fuseDefaultDatasetAction(defaultDatasetAction);       

        List<Rule> rules = ruleSpec.getRules();

        int count = 0;
        for (Rule rule : rules) {
            LOG.debug("Fusing with Rule: " + rule);

            EnumFusionAction defaultFusionAction = rule.getDefaultFusionAction();

            //TODO: change #getRDFPropertyFromString to check for propertyB when ontology is different from source datasets
            Property rdfValueParentPropertyA = getRDFPropertyFromString(rule.getParentPropertyA());
            Property rdfValueParentPropertyB = getRDFPropertyFromString(rule.getParentPropertyB());
            Property rdfValuePropertyA = getRDFPropertyFromString(rule.getPropertyA());
            Property rdfValuePropertyB = getRDFPropertyFromString(rule.getPropertyA());

            //the property here is assumed to be one resource above the literal value in order  to align with the ontology.
            //For example the property is the p1 in the following linked triples.
            // s p1 o1 . o1 p2 o2 
            Literal literalA;
            Literal literalB;

            //child properties are always the properties that point to a literal.
            CustomRDFProperty customPropertyA = new CustomRDFProperty();
            CustomRDFProperty customPropertyB = new CustomRDFProperty();


            if (rule.getParentPropertyA() == null) {
                literalA = getLiteralValue(rule.getPropertyA(), leftEntityData.getModel());
                customPropertyA.setSingleLevel(true);
                customPropertyA.setValueProperty(rdfValuePropertyA);
            } else {
                literalA = getLiteralValueFromChain(rule.getParentPropertyA(), rule.getPropertyA(), leftEntityData.getModel());
                customPropertyA.setSingleLevel(false);
                customPropertyA.setParent(rdfValueParentPropertyA);
                customPropertyA.setValueProperty(rdfValuePropertyA);
            }

            if (rule.getParentPropertyB() == null) {
                literalB = getLiteralValue(rule.getPropertyB(), rightEntityData.getModel());
                customPropertyB.setSingleLevel(true);
                customPropertyA.setValueProperty(rdfValuePropertyB);
            } else {
                literalB = getLiteralValueFromChain(rule.getParentPropertyB(), rule.getPropertyB(), rightEntityData.getModel());
                customPropertyB.setSingleLevel(false);
                customPropertyB.setParent(rdfValueParentPropertyB);
                customPropertyB.setValueProperty(rdfValuePropertyB);                
            }

            if (literalA == null && literalB == null) {
                LOG.trace("both literals empty, skipping rule.");
                count++;
                continue;
            }

            LOG.trace("Nodes: " + leftNode.getLocalName() + " " + rightNode.getLocalName());
            LOG.trace("fusing: " + literalA + " " + literalB);
            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and continue to next rule.
            if (rule.getActionRuleSet() == null) {
                LOG.trace("Rule without ACTION RULE SET, use plain action: " + defaultFusionAction);
                if (defaultFusionAction != null) {
                    boolean rejected = fuseRuleAction(defaultFusionAction, validationAction, customPropertyA, literalA, literalB);
                    if(rejected){
                        return;
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
                        literalA, literalB, rule.getExternalProperties());

                actionRuleCount++;

                if (isActionRuleToBeApplied) {
                    LOG.debug("Condition : " + condition + " evaluated true. Fusion with action: " + fusionAction);
                    LOG.debug("Literals to be fused: " + literalA + " <--> " + literalB);

                    boolean rejected = fuseRuleAction(fusionAction, validationAction, customPropertyA, literalA, literalB);

                    if(rejected){
                        return;
                    }

                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action
            if (actionRuleToApply == false) {
                LOG.debug("All conditions evaluated to false in fusion rule. Using default fusion action: "
                        + defaultFusionAction);
                boolean rejected = fuseRuleAction(defaultFusionAction, validationAction, customPropertyA, literalA, literalB);
                
                if(rejected){
                    return;
                }
            }
        }

        if (count >= rules.size()) {
            LOG.trace("No rules were applied for this link. Failed to retrieve literals for any of the given properties. "
                    + "" + this.getLink().getKey());
        }
    }

    private void evaluateExternalProperty(Map.Entry<String, ExternalProperty> externalPropertyEntry,
            EntityData leftEntityData, EntityData rightEntityData) {

        //So, there are two cases here: (a) Single property refers to literal. (b) the external property contains a chain
        //separated by a whitespace.
        String extPropertyText = externalPropertyEntry.getValue().getProperty();
        Literal valueA;
        Literal valueB;

        if (extPropertyText.contains(" ")) {
            String[] chains = extPropertyText.split(" ");
            valueA = getLiteralValueFromChain(chains[0], chains[1], leftEntityData.getModel());
            valueB = getLiteralValueFromChain(chains[0], chains[1], rightEntityData.getModel());
        } else {
            valueA = getLiteralValue(externalPropertyEntry.getValue().getProperty(), leftEntityData.getModel());
            valueB = getLiteralValue(externalPropertyEntry.getValue().getProperty(), rightEntityData.getModel());
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
            EnumValidationAction validationAction, CustomRDFProperty customProperty, Literal literalA, Literal literalB) 
            throws WrongInputException {

        LOG.debug("fusing with action: " + action.toString());
        //TODO: Also, property coming from the caller is propertyA because it assumes same ontology
        //Maybe add propertyB and check them both if one does not exist in original.
        EntityData fusedEntityData = fusedEntity.getEntityData();

        Model ambiguousModel = AmbiguousDataset.getAmbiguousDataset().getModel();

        if (!isValidLink(validationAction, ambiguousModel, fusedEntityData)) {
            //stop fusion, link is rejected
            return true;
        }

        fuse(action, customProperty, literalA, literalB, fusedEntityData);
        
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

                if (isRejectedByPreviousRule(fusedEntityData.getModel())) {
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
        }
        return true;
    }

    private void fuse(EnumFusionAction action, CustomRDFProperty customProperty, Literal literalA, Literal literalB, 
            EntityData fusedEntityData) throws ApplicationException, WrongInputException {

        //todo replace actions to take literal parameters, as keepLeft.
        Model fusedModel = fusedEntityData.getModel();

        String aLexicalForm;
        String bLexicalForm;

        if(literalA == null){
            aLexicalForm = null;
        } else {
            aLexicalForm = literalA.getLexicalForm();
        }

        if(literalB == null){
            bLexicalForm = null;
        } else {
            bLexicalForm = literalB.getLexicalForm();
        }

        switch (action) {
            case KEEP_LEFT: {
                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLeft(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);

                break;
            }
            case KEEP_LEFT_MARK: {
                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLeft(fusedModel, customProperty, aLexicalForm, bLexicalForm, true);

                break;
            }
            case KEEP_RIGHT: {

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepRight(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);

                break;
            }
            case KEEP_RIGHT_MARK: {
                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepRight(fusedModel, customProperty, aLexicalForm, bLexicalForm, true);

                break;
            }            
            case KEEP_BOTH: {
                //todo: maybe explicitly insert literals in order to handle empty values.
                //fusedModel = keepBoth(fusedModel, customProperty, literalA.getLexicalForm(), literalB.getLexicalForm(), false);
                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                if(literalA != null && literalB != null){
                    keepBoth(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);
                } else if(literalA != null){
                    keepLeft(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);
                } else if(literalB != null){
                    keepRight(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);
                }

                break;
            }
            case KEEP_BOTH_MARK: {

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }
                
                if(literalA != null && literalB != null){
                    keepBoth(fusedModel, customProperty, aLexicalForm, bLexicalForm, true);
                } else if(literalA != null){
                    keepLeft(fusedModel, customProperty, aLexicalForm, bLexicalForm, true);
                } else if(literalB != null){
                    keepRight(fusedModel, customProperty, null, bLexicalForm, true);
                }

                break;
            }
            case KEEP_LONGEST: {
                checkPropertyType(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLongest(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);

                break;
            }
            case KEEP_LONGEST_MARK: {
                checkPropertyType(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepLongest(fusedModel, customProperty, aLexicalForm, bLexicalForm, true);

                break;
            }            
            case CONCATENATE: {
                checkPropertyType(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenate(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);

                break;
            }
            case CONCATENATE_MARK: {
                checkPropertyType(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenate(fusedModel, customProperty, aLexicalForm, bLexicalForm, true);

                break;
            }            
            case KEEP_MOST_RECENT: {

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMostRecent(fusedModel, customProperty, aLexicalForm, bLexicalForm, false);

                break;
            }
            case KEEP_MOST_RECENT_MARK: {

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMostRecent(fusedModel, customProperty, aLexicalForm, bLexicalForm, true);

                break;
            }            
            case KEEP_MORE_POINTS: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePoints(aLexicalForm, bLexicalForm, fusedModel, customProperty, false);

                break;
            }
            case KEEP_MORE_POINTS_MARK: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePoints(aLexicalForm, bLexicalForm, fusedModel, customProperty, true);

                break;
            }            
            case KEEP_MORE_POINTS_AND_SHIFT: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePointsAndShift(aLexicalForm, bLexicalForm, fusedModel, customProperty, false);

                break;
            }
            case KEEP_MORE_POINTS_AND_SHIFT_MARK: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                keepMorePointsAndShift(aLexicalForm, bLexicalForm, fusedModel, customProperty, true);

                break;
            }            
            case SHIFT_LEFT_GEOMETRY: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftLeftGeometry(aLexicalForm, bLexicalForm, fusedModel, customProperty, false);

                break;
            }
            case SHIFT_LEFT_GEOMETRY_MARK: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftLeftGeometry(aLexicalForm, bLexicalForm, fusedModel, customProperty, false);

                break;
            }
            case SHIFT_RIGHT_GEOMETRY: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftRightGeometry(aLexicalForm, bLexicalForm, fusedModel, customProperty, false);

                break;
            }
            case SHIFT_RIGHT_GEOMETRY_MARK: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                shiftRightGeometry(aLexicalForm, bLexicalForm, fusedModel, customProperty, true);

                break;
            }            
            case CONCATENATE_GEOMETRY: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenateGeometry(aLexicalForm, bLexicalForm, fusedModel, customProperty, false);

                break;
            }
            case CONCATENATE_GEOMETRY_MARK: {
                checkWKTProperty(customProperty.getValueProperty(), action);

                if (isRejectedByPreviousRule(fusedModel)) {
                    break;
                }

                concatenateGeometry(aLexicalForm, bLexicalForm, fusedModel, customProperty, true);

                break;
            }            
        }
    }

    private void keepBoth(Model fusedModel, CustomRDFProperty customProperty, String literalA, String literalB, boolean mark) {

        Resource resource = getResourceAndRemoveFromModel(customProperty, fusedModel, literalA, literalB);

        if(literalA != null){
            if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
                RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
                fusedModel.add(resource, customProperty.getValueProperty(),
                        ResourceFactory.createTypedLiteral(literalA, geometryDatatype));
            } else {
                fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalA));
            }
        }

        if(literalB != null){
            if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
                RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
                fusedModel.add(resource, customProperty.getValueProperty(), 
                        ResourceFactory.createTypedLiteral(literalB, geometryDatatype));
            } else {
                fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalB));
            }
        }
        
        if(mark){
            markAmbiguous(customProperty, resource, fusedModel);
        }

        EntityData fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void keepLeft(Model fusedModel, CustomRDFProperty customProperty, String literalA, String literalB, boolean mark) {

        Resource resource = getResourceAndRemoveFromModel(customProperty, fusedModel, literalA, literalB);

        if(resource == null){
            LOG.debug("Property " + customProperty.getValueProperty() + " is missing from fused. Construct chain from scratch.");
            resource = RDFUtils.getRootResource(leftNode, rightNode);
            Resource renamedResource = RDFUtils.resolveResource(leftNode, rightNode, customProperty);
            if(literalA != null){
                if(customProperty.isSingleLevel()){
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalA));
                } else {
                    fusedModel.add(resource, customProperty.getParent(), renamedResource);
                    fusedModel.add(renamedResource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalA));
                }
            }
        } else {
            if(literalA != null){
                if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
                    RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createTypedLiteral(literalA, geometryDatatype));
                } else {
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalA));
                }
            }
        }

        if(mark){
            markAmbiguous(customProperty, resource, fusedModel);
        }

        EntityData fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void keepRight(Model fusedModel, CustomRDFProperty customProperty, String literalA, String literalB, boolean mark) {

        Resource resource = getResourceAndRemoveFromModel(customProperty, fusedModel, literalA, literalB);

        if(resource == null){
            LOG.debug("Property " + customProperty.getValueProperty() + " is missing from fused. Construct chain from scratch.");
            resource = RDFUtils.getRootResource(leftNode, rightNode);
            Resource renamedResource = RDFUtils.resolveResource(leftNode, rightNode, customProperty);
            if(literalB != null){
                if(customProperty.isSingleLevel()){
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalB));
                } else {
                    fusedModel.add(resource, customProperty.getParent(), renamedResource);
                    fusedModel.add(renamedResource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalB));
                }
            }
        } else {
            if(literalB != null){
                if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
                    RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createTypedLiteral(literalB, geometryDatatype));
                } else {
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalB));
                }
            }
        }

        if(mark){
            markAmbiguous(customProperty, resource, fusedModel);
        }

        EntityData fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void concatenate(Model fusedModel, CustomRDFProperty customProperty, String literalA, String literalB, boolean mark) {

        Resource node = getResourceAndRemoveLiteral(fusedModel, customProperty, literalA, literalB);
        if(node == null){
            LOG.debug("Property " + customProperty.getValueProperty() + " is missing from fused. Construct chain from scratch.");
            return;
        }

        String concatenated = literalA + SpecificationConstants.Rule.CONCATENATION_SEP + literalB;
        fusedModel.add(node, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(concatenated));

        if(mark){
            markAmbiguous(customProperty, node, fusedModel);
        }

        EntityData fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }
    
    private void keepMostRecent(Model fusedModel, CustomRDFProperty customProperty, String literalA, String literalB, boolean mark) 
            throws ApplicationException {

        EnumDataset mostRecent = Configuration.getInstance().getMostRecentDataset();
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        Resource resource = getResourceAndRemoveFromModel(customProperty, fusedModel, literalA, literalB);

        if(resource == null){
            LOG.trace("null property value. Keep most recent will not be applied between literals {} - {}", literalA, literalB);
            return;
        }
        
        switch (mostRecent) {
            case LEFT: {
                if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createTypedLiteral(literalA, geometryDatatype));
                } else {
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalA));
                }
                break;
            }
            case RIGHT: {
                if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createTypedLiteral(literalB, geometryDatatype));
                } else {
                    fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(literalB));
                }
                break;
            }
            case UNDEFINED:
            default:
                //do not remove statement. Default dataset action should be kept in this case (no dates provided)
                break;
        }
        if(mark){
            markAmbiguous(customProperty, resource, fusedModel);
        }
    }

    private void keepMorePoints(String literalA, String literalB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        EntityData fusedEntityData;
        Geometry leftGeometry = parseGeometry(literalA);
        Geometry rightGeometry = parseGeometry(literalB);
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        Resource node = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA, literalB);
        
        if(node == null){
            LOG.trace("null property value. Keep more points will not be applied between literals {} - {}", literalA, literalB);
            return;
        }

        if (leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {

            Literal geometryLiteral = ResourceFactory.createTypedLiteral(literalA, geometryDatatype);
            fusedModel.add(node, customProperty.getValueProperty(), geometryLiteral);

            fusedEntityData = fusedEntity.getEntityData();
            fusedEntityData.setModel(fusedModel);

            fusedEntity.setEntityData(fusedEntityData);

        } else {

            Literal geometryLiteral = ResourceFactory.createTypedLiteral(literalB, geometryDatatype);
            fusedModel.add(node, customProperty.getValueProperty(), geometryLiteral);

            fusedEntityData = fusedEntity.getEntityData();
            fusedEntityData.setModel(fusedModel);

            fusedEntity.setEntityData(fusedEntityData);
        }

        if(mark){
            markAmbiguous(customProperty, node, fusedModel);
        }
    }

    private void keepMorePointsAndShift(String literalA, String literalB, Model fusedModel, 
            CustomRDFProperty customProperty, boolean mark) {
        
        EntityData fusedEntityData;
        Geometry leftGeometry = parseGeometry(literalA);
        Geometry rightGeometry = parseGeometry(literalB);
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        
        Resource node = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA, literalB);

        if(node == null){
            LOG.trace("null property value. Keep more points and shift will not be applied between literals {} - {}", literalA, literalB);
            return;
        }
        
        if (leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {
            CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
            Geometry fusedGeometry = centroidTranslator.shift(leftGeometry);
            String wktFusedGeometry = getWKTLiteral(fusedGeometry);

            String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
            Literal geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);

            fusedModel.add(node, customProperty.getValueProperty(), geometryLiteral);

            fusedEntityData = fusedEntity.getEntityData();
            fusedEntityData.setModel(fusedModel);
            fusedEntity.setEntityData(fusedEntityData);

        } else if (leftGeometry.getNumPoints() < rightGeometry.getNumPoints()) {

            CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
            Geometry fusedGeometry = centroidTranslator.shift(rightGeometry);
            String wktFusedGeometry = getWKTLiteral(fusedGeometry);

            String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
            Literal geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);

            fusedModel.add(node, customProperty.getValueProperty(), geometryLiteral);

            fusedEntityData = fusedEntity.getEntityData();
            fusedEntityData.setModel(fusedModel);
            fusedEntity.setEntityData(fusedEntityData);
        }

        if(mark){
            markAmbiguous(customProperty, node, fusedModel);
        }
    }

    private void shiftLeftGeometry(String literalA, String literalB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        EntityData fusedEntityData;
        Geometry leftGeometry = parseGeometry(literalA);
        Geometry rightGeometry = parseGeometry(literalB);
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
        Geometry shiftedToRightGeometry = centroidTranslator.shift(leftGeometry);
        String wktFusedGeometry = getWKTLiteral(shiftedToRightGeometry);
        Resource node = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA, literalB);

        if(node == null){
            LOG.trace("null property value. Shift left geometry will not be applied between literals {} - {}", literalA, literalB);
            return;
        }

        String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
        Literal geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);
        fusedModel.add(node, customProperty.getValueProperty(), geometryLiteral);
        fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);

        if(mark){
            markAmbiguous(customProperty, node, fusedModel);
        }
    }

    private void shiftRightGeometry(String literalA, String literalB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        EntityData fusedEntityData;
        Geometry leftGeometry = parseGeometry(literalA);
        Geometry rightGeometry = parseGeometry(literalB);
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
        Geometry shiftedToLeftGeometry = centroidTranslator.shift(rightGeometry);
        String wktFusedGeometry = getWKTLiteral(shiftedToLeftGeometry);
        Resource node = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA, literalB);

        if(node == null){
            LOG.trace("null property value. Shift right geometry will not be applied between literals {} - {}", literalA, literalB);
            return;
        }

        String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
        Literal geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);
        fusedModel.add(node, customProperty.getValueProperty(), geometryLiteral);
        fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);

        if(mark){
            markAmbiguous(customProperty, node, fusedModel);
        }
    }
    
    private void concatenateGeometry(String literalA, String literalB, Model fusedModel, CustomRDFProperty customProperty, boolean mark) {
        EntityData fusedEntityData;
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        Geometry leftGeometry = parseGeometry(literalA);
        Geometry rightGeometry = parseGeometry(literalB);
        Geometry[] geometries = new Geometry[]{leftGeometry, rightGeometry};
        Geometry geometryCollection = new GeometryCollection(geometries, new GeometryFactory());
        String wktFusedGeometry = getWKTLiteral(geometryCollection);
        Resource node = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA, literalB);

        if(node == null){
            LOG.trace("null property value. Concatenate geometry will not be applied between literals {} - {}", literalA, literalB);
            return;
        }

        String wktLiteralCRS = Namespace.CRS_4326 + " " + wktFusedGeometry;
        Literal geometryLiteral = ResourceFactory.createTypedLiteral(wktLiteralCRS, geometryDatatype);
        fusedModel.add(node, customProperty.getValueProperty(), geometryLiteral);
        fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);

        if(mark){
            markAmbiguous(customProperty, node, fusedModel);
        }
    }

    private void keepLongest(Model fusedModel, CustomRDFProperty customProperty, String literalA, String literalB, boolean mark) {

        EntityData fusedEntityData;

        String longest;
        
        if(literalA != null && literalB == null){
            longest = literalA;
        } else if(literalB != null && literalA == null){
            longest = literalB;
        } else {
            String sA = Normalizer.normalize(literalA, Normalizer.Form.NFC);
            String sB = Normalizer.normalize(literalB, Normalizer.Form.NFC);

            if (sA.length() > sB.length()) {
                longest = literalA;
            } else {
                longest = literalB;
            }
        }
        
        Resource resource = getResourceAndRemoveLiteral(fusedModel, customProperty, literalA, literalB);

        if(resource == null){
            LOG.debug("Property " + customProperty.getValueProperty() + " is missing from fused. Construct chain from scratch.");
            resource = RDFUtils.getRootResource(leftNode, rightNode);
            //todo: a resolved resource may contain a localname that has no type in the RDF. Maybe add the type from the mapping.
            Resource renamedResource = RDFUtils.resolveResource(leftNode, rightNode, customProperty);

            if(customProperty.isSingleLevel()){
                fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(longest));
            } else {
                fusedModel.add(resource, customProperty.getParent(), renamedResource);
                fusedModel.add(renamedResource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(longest));
            }
        } else {
            fusedModel.add(resource, customProperty.getValueProperty(), ResourceFactory.createStringLiteral(longest));
        }

        fusedEntityData = fusedEntity.getEntityData();
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);

        if(mark){
            markAmbiguous(customProperty, resource, fusedModel);
        }
    }

    private void markAmbiguous(CustomRDFProperty customProperty, Resource node, Model fusedModel) {
        Statement statement;
        Statement parentStatement;

        if(customProperty.isSingleLevel()){
            statement = getAmbiguousPropertyStatement(node.getURI(), customProperty.getValueProperty());
            fusedModel.add(statement);
        } else {
            parentStatement = getAmbiguousPropertyStatement(node.getURI(), customProperty.getParent());
            statement = getAmbiguousSubPropertyStatement(node.getURI(), customProperty.getValueProperty());
            fusedModel.add(parentStatement);
            fusedModel.add(statement);
        }
    }

    private Resource getResourceAndRemoveFromModel(CustomRDFProperty customProperty, Model fusedModel, String literalA, 
            String literalB) throws ApplicationException {

        Resource node;
        if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
            node = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA, literalB);
        } else {
            node = getResourceAndRemoveLiteral(fusedModel, customProperty, literalA, literalB);
        }

        if(node == null){
            //possible geometry without datatype. Try recovering resource as any other literal.
            node = getResourceAndRemoveLiteral(fusedModel, customProperty, literalA, literalB);
        }

        return node;
    }

//    private Resource getResourceAndRemoveFromModel(CustomRDFProperty customProperty, Model fusedModel, Literal literalA, 
//            Literal literalB) throws ApplicationException {
//
//        Resource resource;
//        if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
//            resource = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA.getLexicalForm(), literalB.getLexicalForm());
//        } else {
//            resource = getResourceAndRemoveLiteral(fusedModel, customProperty.getValueProperty(), literalA.getLexicalForm(), literalB.getLexicalForm());
//        }
//
//        if(resource == null){
//            //possible geometry without datatype. Try recovering resource as any other literal.
//            resource = getResourceAndRemoveLiteral(fusedModel, customProperty.getValueProperty(), literalA.getLexicalForm(), literalB.getLexicalForm());
//        }
//
//        if(resource == null){
//            //retrieve by property
//            if(customProperty.getValueProperty().getLocalName().equals(Namespace.WKT_LOCALNAME)){
//                resource = getResourceAndRemoveGeometry(fusedModel, customProperty.getValueProperty(), literalA.getLexicalForm(), literalB.getLexicalForm());
//            } else {
//                resource = getResourceAndRemoveLiteral(fusedModel, customProperty.getValueProperty(), literalA.getLexicalForm(), literalB.getLexicalForm());
//            }
//            
//        }
//        return resource;
//    }

    private void rejectMarkAmbiguous(Model ambiguousModel, EntityData fusedEntityData) {
        LinksModel.getLinksModel().getRejected().add(link);

        Model fusedModel = fusedEntityData.getModel();

        if (!fusedModel.isEmpty()) {
            fusedModel.removeAll();
        }

        EnumDataset dataset = resolveRejectedEntityModel();

        switch (dataset) {
            case LEFT: {
                ambiguousModel.add(leftNode.getEntityData().getModel());
                break;
            }
            case RIGHT: {
                ambiguousModel.add(rightNode.getEntityData().getModel());
                break;
            }
        }

        Statement statement = getAmbiguousLinkStatement(leftNode.getResourceURI(), rightNode.getResourceURI());

        ambiguousModel.add(statement);

        fusedModel.add(statement);
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void reject(EntityData fusedEntityData) {
        //removes link from the list and from the original also.
        LinksModel.getLinksModel().getRejected().add(link);
        Model fusedModel = fusedEntityData.getModel();
        if (!fusedModel.isEmpty()) {
            fusedModel.removeAll();
        }

        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    private void acceptMarkAmbiguous(Model ambiguousModel, EntityData fusedEntityData) {
        Statement statement = getAmbiguousLinkStatement(leftNode.getResourceURI(), rightNode.getResourceURI());

        Model fusedModel = fusedEntityData.getModel();

        ambiguousModel.add(leftNode.getEntityData().getModel());
        ambiguousModel.add(rightNode.getEntityData().getModel());
        ambiguousModel.add(statement);

        fusedModel.add(statement);
        fusedEntityData.setModel(fusedModel);
        fusedEntity.setEntityData(fusedEntityData);
    }

    //removes the triple that contains literalA or literalB in order to be replaced by another literal based on the action.
    //The method returns the resource that the literalA or B was found in order to be used as subject and preserve the triple chain.
    private Resource getResourceAndRemoveLiteral(Model model, CustomRDFProperty property, String literalA, String literalB) {
        //queries try to find subjects by using the literal values.
        //this is done to retrieve the exact resource of the literal, rather than a resource that has same properties.
        //E.g name -> nameValue could have different nodes like uri/name and uri/int_name
        Resource node;
        if(literalA == null){
            node = SparqlRepository.getSubjectWithLiteral(property.getValueProperty().toString(), literalB, model);
            if(node != null){
                //model.removeAll(resource, property, (RDFNode) null);
            }
        } else if(literalB == null){
            node = SparqlRepository.getSubjectWithLiteral(property.getValueProperty().toString(), literalA, model);
            if(node != null){
                //model.removeAll(resource, property, (RDFNode) null);
            }
        } else {
            //both literals not null, try to find subject:
            node = SparqlRepository.getSubjectWithLiteral(property.getValueProperty().toString(), literalA, model);
            if(node == null){
                node = SparqlRepository.getSubjectWithLiteral(property.getValueProperty().toString(), literalB, model);
            }
        }

        //find by property
        if(node == null){
            if(literalA == null){
                node = SparqlRepository.getSubjectOfProperty(property.getValueProperty().toString(), model);
            } else if(literalB == null){
                node = SparqlRepository.getSubjectOfProperty(property.getValueProperty().toString(), model);
            } else {
                //both literals not null, try to find subject:
                node = SparqlRepository.getSubjectOfProperty(property.getValueProperty().toString(), model);
                if(node == null){
                    node = SparqlRepository.getSubjectOfProperty(property.getValueProperty().toString(), model);
                }
            }
            
            if(node == null && literalB != null){ //literalA not null but failed to retriece resource.
                node = SparqlRepository.getSubjectOfProperty(property.getValueProperty().toString(), model);
            }
        }
        
        if(node != null){
            model.removeAll(node, property.getValueProperty(), (RDFNode) null);
        }

        return node;
    }

    //removes the triple that contains literalA or literalB in order to be replaced by another literal based on the action.
    //The method returns the resource that the literalA or B was found in order to be used as subject and preserve the triple chain.
    private Resource getResourceAndRemoveGeometry(Model model, Property property, String literalA, String literalB) {
        Resource node;
        if(literalA == null){
            node = SparqlRepository.getSubjectWithGeometry(property.toString(), literalB, model);
            if(node != null){
                model.removeAll(node, property, (RDFNode) null);
            }
        } else if(literalB == null){
            node = SparqlRepository.getSubjectWithGeometry(property.toString(), literalA, model);
            if(node != null){
                model.removeAll(node, property, (RDFNode) null);
            }
        } else {
            //both literals not null, try to find subject:
            node = SparqlRepository.getSubjectWithGeometry(property.toString(), literalA, model);
            if(node == null){
                node = SparqlRepository.getSubjectWithGeometry(property.toString(), literalB, model);
            }
            if(node != null){
                model.removeAll(node, property, (RDFNode) null);
            }
        }
        return node;
    }

    private Literal getLiteralValue(String property, Model model) {
        Property propertyRDF = getRDFPropertyFromString(property);

        if (propertyRDF != null) {
            return SparqlRepository.getObjectOfProperty(propertyRDF, model);
        } else {
            LOG.warn("Could not find literal with property {}", property);
            return ResourceFactory.createStringLiteral("");
        }
    }

    private Literal getLiteralValueFromChain(String property1, String property2, Model model) {
        if (property1 != null) {
            Literal literal = SparqlRepository.getObjectOfPropertyChain(property1, property2, model, true);
            if(literal == null){
                literal = SparqlRepository.getObjectOfPropertyChain(property1, property2, model, false);
            }
            return literal;
        } else {
            LOG.warn("Could not find literal with properties {}", property1, property2);
            return ResourceFactory.createStringLiteral("");
        }
    }

    private Property getRDFPropertyFromString(String property) {

        if (StringUtils.isBlank(property)) {
            return null;
        }

        Property propertyRDF = ResourceFactory.createProperty(property);

        return propertyRDF;
    }

    private Geometry parseGeometry(String literal) {

        String literalWithoutCRS = literal.replace(Namespace.CRS_4326 + " ", "");

        int sourceSRID = 4326; //All features assumed in WGS84 lon/lat coordinates

        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(), sourceSRID);

        WKTReader wellKnownTextReader = new WKTReader(geomFactory);

        Geometry geometry = null;
        try {
            geometry = wellKnownTextReader.read(literalWithoutCRS);
        } catch (ParseException ex) {
            LOG.fatal("Error parsing geometry literal " + literalWithoutCRS);
            LOG.fatal(ex);
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
            LOG.error("The selected action " + action.toString() + " applies only for WKT geometry literals");
            throw new WrongInputException("The selected action " + action.toString() + " applies only for WKT geometry literals");
        }
    }

    private void checkPropertyType(Property property, EnumFusionAction action) throws WrongInputException {
        if (property.toString().equals(Namespace.WKT)) {
            LOG.error("The selected action " + action.toString() + " does not apply on geometries.");
            throw new WrongInputException("The selected action " + action.toString() + " does not apply on geometries.");
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

    private Statement getAmbiguousPropertyStatement(String uri, Property property) {

        Property ambiguousProperty = ResourceFactory.createProperty(Namespace.HAS_AMBIGUOUS_PROPERTY);

        Resource resource = ResourceFactory.createResource(uri);

        Statement statement = ResourceFactory.createStatement(resource, ambiguousProperty, property);

        return statement;
    }

    private Statement getAmbiguousSubPropertyStatement(String uri, Property subProperty) {

        Property ambiguousSubProperty = ResourceFactory.createProperty(Namespace.HAS_AMBIGUOUS_SUB_PROPERTY);

        Resource resource = ResourceFactory.createResource(uri);

        Statement statement = ResourceFactory.createStatement(resource, ambiguousSubProperty, subProperty);

        return statement;
    }

    private void resolveModeURIs() {
        EnumOutputMode mode = Configuration.getInstance().getOutputMode();

        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
                renameResourceURIs(rightNode, leftNode);         
                break;
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                renameResourceURIs(leftNode, rightNode);
                break;
        }
    }

    private EnumDataset resolveRejectedEntityModel() {
        EnumOutputMode mode = Configuration.getInstance().getOutputMode();
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

    private void renameResourceURIs(Entity entityToBeRenamed, Entity entity) {
        Model original = entityToBeRenamed.getEntityData().getModel();
        Iterator<Statement> statementIterator = original.listStatements().toList().iterator();

        Model newModel = ModelFactory.createDefaultModel();
        while (statementIterator.hasNext()) {
            
            Statement statement = statementIterator.next();

            String newSub = statement.getSubject().toString().replaceAll(entityToBeRenamed.getResourceURI(), entity.getResourceURI());
            String newPred = statement.getPredicate().toString().replaceAll(entityToBeRenamed.getResourceURI(), entity.getResourceURI());
            String newOb = statement.getObject().toString().replaceAll(entityToBeRenamed.getResourceURI(), entity.getResourceURI());
            
            Resource subject = ResourceFactory.createResource(newSub);
            Property predicate = ResourceFactory.createProperty(newPred);

            RDFNode object;
            if(statement.getObject().isResource()){
                object = ResourceFactory.createResource(newOb);
            } else if(statement.getObject().isLiteral()){
                object = statement.getObject();
            } else {
                object = statement.getObject();
            }

            Statement newStatement = ResourceFactory.createStatement(subject, predicate, object);

            newModel.add(newStatement);
        }
        entityToBeRenamed.getEntityData().setModel(newModel);
    }
}
