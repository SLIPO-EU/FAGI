package gr.athena.innovation.fagi.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumFusionAction;
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
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.utils.CentroidShiftTranslator;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing a pair of interlinked RDF entities.
 * 
 * @author nkarag
 */
public class LinkedPair {

    private static final Logger logger = LogManager.getLogger(LinkedPair.class);
    private Entity leftNode;
    private Entity rightNode;
    private Entity fusedEntity;

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
        if(fusedEntity == null){
            logger.fatal("Current pair is not fused: " + this);
            throw new ApplicationException("Current pair is not fused: " + this);
        }
        return fusedEntity;
    }

    public void fusePair(RuleCatalog ruleCatalog, Map<String, IFunction> functionMap) throws WrongInputException{

        EnumDatasetAction defaultDatasetAction = ruleCatalog.getDefaultDatasetAction();
        
        fusedEntity = new Entity();
        fusedEntity.setResourceURI(resolveFusedEntityURI(defaultDatasetAction));

        EntityData leftEntityData = leftNode.getEntityData();
        EntityData rightEntityData = rightNode.getEntityData();

        fuseDefaultDatasetAction(defaultDatasetAction);

        List<Rule> rules = ruleCatalog.getRules();

        for(Rule rule : rules){
            logger.trace("Fusing with Rule: " + rule);

            EnumFusionAction defaultAction = rule.getDefaultAction();

            //TODO: change #getRDFPropertyFromString to check for propertyB when ontology is different from source datasets
            Property rdfPropertyA = getRDFPropertyFromString(rule.getPropertyA());
            Property rdfPropertyB = getRDFPropertyFromString(rule.getPropertyB());
            
            String literalA = getLiteralValue(rule.getPropertyA(), leftEntityData.getModel());
            String literalB = getLiteralValue(rule.getPropertyB(), rightEntityData.getModel());
            logger.info("Found literals: {}, {}", literalA, literalB);

            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and break.
            if(rule.getActionRuleSet() == null){
                logger.trace("Rule without ACTION RULE SET, use plain action: " + defaultAction);
                if(defaultAction != null){
                    fuseRuleAction(defaultAction, rdfPropertyA, literalA, literalB);
                }
                break;
            }

            List<ActionRule> actionRules = rule.getActionRuleSet().getActionRuleList();
            int actionRuleCount = 0;
            boolean actionRuleToApply = false;
            for(ActionRule actionRule : actionRules){

                logger.info("-- Action rule: " + actionRuleCount);

                EnumFusionAction action = null;
                
                if(actionRule.getAction() != null){
                    action = actionRule.getAction();
                }
                
                Condition condition = actionRule.getCondition();

                //switch case for evaluation using external properties.
                for(Map.Entry<String, ExternalProperty> extProp : rule.getExternalProperties().entrySet()){

                    String valueA = getLiteralValue(extProp.getValue().getProperty(), leftEntityData.getModel());
                    String valueB = getLiteralValue(extProp.getValue().getProperty(), rightEntityData.getModel());         
                    
                    extProp.getValue().setValueA(valueA);
                    extProp.getValue().setValueB(valueB);
                }

                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, literalA, literalB, rule.getExternalProperties());

                actionRuleCount++;
                if(isActionRuleToBeApplied){
                    logger.trace("Replacing in model: " + literalA + " <--> " + literalB + " using " + action);
                    
                    fuseRuleAction(action, rdfPropertyA, literalA, literalB);
                    
                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action
            if(actionRuleToApply == false){
                fuseRuleAction(defaultAction, rdfPropertyA, literalA, literalB);
            }
        }
    }

    public void fuseDefaultDatasetAction(EnumDatasetAction datasetDefaultAction) throws WrongInputException{

        //default dataset action should be performed before the rules apply. The fused model should be empty:
        if(!fusedEntity.getEntityData().getModel().isEmpty()){
            throw new ApplicationException
                ("Default fusion action tries to overwrite already fused data!");
        }

        EntityData leftData = leftNode.getEntityData();
        EntityData rightData = rightNode.getEntityData();
        
        switch(datasetDefaultAction){
            case KEEP_LEFT:
                fusedEntity.setEntityData(leftData);
                break;
            case KEEP_RIGHT:
                fusedEntity.setEntityData(rightData);
                break;
            case KEEP_BOTH:
                fusedEntity.getEntityData().getModel().add(leftData.getModel()).add(rightData.getModel());
                break;
            case REJECT_LINK:
                fusedEntity.getEntityData().getModel().removeAll();
                break;                
            default:
                throw new WrongInputException("Dataset default fusion action is not defined.");
        }        
    }

    private void fuseRuleAction(EnumFusionAction action, 
            Property property, String literalA, String literalB) throws WrongInputException{
        //TODO: Check Keep both. 
        //TODO: Also, property coming from the caller is propertyA because it assumes same ontology
        //Maybe add propertyB and check them both if one does not exist in model.

        String fusedURI = fusedEntity.getResourceURI();

        EntityData fusedEntityData = fusedEntity.getEntityData();

        switch(action){
            case KEEP_LEFT:
            {
                Model fusedModel = fusedEntityData.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalA));                
                
                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }
            case KEEP_RIGHT:
            {
                Model fusedModel = fusedEntityData.getModel();
                fusedModel.removeAll(null, property, null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalB));                

                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);
                
                break;
            }
            case CONCATENATE:
            {
                Model fusedModel = fusedEntityData.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                
                String concatenated = literalA + SpecificationConstants.Rule.CONCATENATION_SEP + literalB;
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(concatenated));                
                
                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            } 
            case KEEP_LONGEST:
            {
                Model fusedModel = fusedEntityData.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                
                String sA = Normalizer.normalize(literalA, Normalizer.Form.NFD);
                String sB = Normalizer.normalize(literalB, Normalizer.Form.NFD);
                String longest;
                
                if(sA.length() > sB.length()){
                    longest = sA;
                } else {
                    longest = sB;
                }

                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(longest));                
                
                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }             
            case KEEP_BOTH:
            {
                    EntityData leftEntityData = leftNode.getEntityData();
                    EntityData rightEntityData = rightNode.getEntityData();

                    Model fusedModel = fusedEntityData.getModel().add(leftEntityData.getModel()).add(rightEntityData.getModel());
                    fusedEntityData.setModel(fusedModel);
                    fusedEntity.setEntityData(fusedEntityData);
                    
                    break;
            }
            case KEEP_MORE_POINTS:
            {
                checkWKTProperty(property,action);

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);

                if(leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {

                    Model fusedModel = fusedEntityData.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalA));                

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);

                    fusedEntity.setEntityData(fusedEntityData);

                } else {
                    Model fusedModel = fusedEntityData.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalB));                

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);

                    fusedEntity.setEntityData(fusedEntityData);
                }
                break;
            }
            case KEEP_MORE_POINTS_AND_SHIFT:
            {
                checkWKTProperty(property,action);

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);

                if(leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(leftGeometry);
                    String wktFusedGeometry = getWKTLiteral(fusedGeometry);

                    Model fusedModel = fusedEntityData.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);
                    fusedEntity.setEntityData(fusedEntityData);
                    
                } else if(leftGeometry.getNumPoints() < rightGeometry.getNumPoints()){

                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(rightGeometry);   
                    String wktFusedGeometry = getWKTLiteral(fusedGeometry);

                    Model fusedModel = fusedEntityData.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                    fusedEntityData = fusedEntity.getEntityData();
                    fusedEntityData.setModel(fusedModel);
                    fusedEntity.setEntityData(fusedEntityData);
                }
                
                break;
            }
            case SHIFT_LEFT_GEOMETRY:
            {
                checkWKTProperty(property,action);
                
                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);
                
                CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                Geometry shiftedToRightGeometry = centroidTranslator.shift(leftGeometry);
                String wktFusedGeometry = getWKTLiteral(shiftedToRightGeometry);

                Model fusedModel = fusedEntityData.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);
                
                break;
            }
            case SHIFT_RIGHT_GEOMETRY:
            {
                checkWKTProperty(property,action);  

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);
                
                CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                Geometry shiftedToLeftGeometry = centroidTranslator.shift(rightGeometry);   
                String wktFusedGeometry = getWKTLiteral(shiftedToLeftGeometry);

                Model fusedModel = fusedEntityData.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                fusedEntityData = fusedEntity.getEntityData();
                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);

                break;
            }            
            case REJECT_LINK:
            {
                Model fusedModel = fusedEntityData.getModel();
                
                if(!fusedModel.isEmpty()){
                    fusedModel.removeAll();
                }

                fusedEntityData.setModel(fusedModel);
                fusedEntity.setEntityData(fusedEntityData);                
                
                break;
            }             
        }        
    }

    private String getLiteralValue(String property, Model model){
        Property propertyRDF = getRDFPropertyFromString(property);
        
        if(propertyRDF != null){
            return SparqlRepository.getObjectOfProperty(propertyRDF, model);
        } else {
            logger.warn("Could not find literal for property {}", property);
            return "";
        }
    }
    
    private Property getRDFPropertyFromString(String property){
        //TODO: remove aliases of properties
        Property propertyRDF;
        if(property.equalsIgnoreCase("label")){
            propertyRDF = ResourceFactory.createProperty(Namespace.LABEL);
        } else if(property.equalsIgnoreCase("date")){
            propertyRDF = ResourceFactory.createProperty(Namespace.DATE_OSM_MODIFIED);
        } else if(property.equalsIgnoreCase("wkt")){
            propertyRDF = ResourceFactory.createProperty(Namespace.WKT);
        } else {
            propertyRDF = ResourceFactory.createProperty(property);
        }
        return propertyRDF;
    }

    private String resolveFusedEntityURI(EnumDatasetAction defaultDatasetAction) {
        String resourceURI;
        switch (defaultDatasetAction) {
            case KEEP_LEFT:
                leftNode.getResourceURI();
                resourceURI = leftNode.getResourceURI();
                break;
            case KEEP_RIGHT:
                resourceURI = rightNode.getResourceURI();
                break;
            case REJECT_LINK:
                resourceURI = "";
                break;                
            default:
                resourceURI = leftNode.getResourceURI();
                break;
        }
        return resourceURI;
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
    
    private void checkWKTProperty(Property property, EnumFusionAction action) throws WrongInputException{
        if(!property.toString().equals(Namespace.WKT)){
            logger.error("The selected action " + action.toString() + " applies only for WKT geometry literals");
            throw new WrongInputException
                ("The selected action " + action.toString() + " applies only for WKT geometry literals");
        }         
    }
}
