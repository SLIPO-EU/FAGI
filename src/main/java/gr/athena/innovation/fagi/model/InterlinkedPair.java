package gr.athena.innovation.fagi.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.rule.model.ActionRule;
import gr.athena.innovation.fagi.core.rule.model.Condition;
import gr.athena.innovation.fagi.core.rule.model.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.utils.CentroidShiftTranslator;
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
public class InterlinkedPair {

    private static final Logger logger = LogManager.getLogger(InterlinkedPair.class);
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
            throw new RuntimeException();
        }
        return fusedEntity;
    }

    public void fuseWithRule(RuleCatalog ruleCatalog, Map<String, IFunction> functionMap){

        EnumDatasetAction defaultDatasetAction = ruleCatalog.getDefaultDatasetAction();
        
        fusedEntity = new Entity();
        fusedEntity.setResourceURI(resolveFusedEntityURI(defaultDatasetAction));

        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();

        fuseDefaultDatasetAction(defaultDatasetAction);

        List<Rule> rules = ruleCatalog.getRules();

        for(Rule rule : rules){
            logger.trace("Fusing with Rule: " + rule);

            EnumFusionAction defaultAction = rule.getDefaultAction();

            //TODO: change #getRDFPropertyFromString to check for propertyB when ontology is different from source datasets
            Property rdfPropertyA = getRDFPropertyFromString(rule.getPropertyA());
            Property rdfPropertyB = getRDFPropertyFromString(rule.getPropertyB());
            
            String literalA = getLiteralValue(rule.getPropertyA(), leftMetadata.getModel());
            String literalB = getLiteralValue(rule.getPropertyB(), rightMetadata.getModel());
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

                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, literalA, literalB);

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

    public void fuseDefaultDatasetAction(EnumDatasetAction datasetDefaultAction){

        //default dataset action should be performed before the rules apply. The fused model should be empty:
        if(!fusedEntity.getMetadata().getModel().isEmpty()){
            logger.fatal("Something is wrong. Default fusion action tries to overwrite already fused data!");
            throw new RuntimeException();
        }

        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();
        
        switch(datasetDefaultAction){
            case KEEP_LEFT:
                fusedEntity.setMetadata(leftMetadata);
                break;
            case KEEP_RIGHT:
                fusedEntity.setMetadata(rightMetadata);
                break;
            case KEEP_BOTH:
                fusedEntity.getMetadata().getModel().add(leftMetadata.getModel()).add(rightMetadata.getModel());
            default:
                logger.fatal("Dataset default fusion action is not defined.");
                throw new RuntimeException();
        }        
    }

    private void fuseRuleAction(EnumFusionAction action, 
            Property property, String literalA, String literalB){
        //TODO: Check Keep both. 
        //TODO: Also, property coming from the caller is propertyA because it assumes same ontology
        //Maybe add propertyB and check them both if one does not exist in model.

        String fusedURI = fusedEntity.getResourceURI();

        Metadata fusedMetadata = fusedEntity.getMetadata();

        switch(action){
            case KEEP_LEFT:
            {
                Model fusedModel = fusedMetadata.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalA));                
                
                fusedMetadata = fusedEntity.getMetadata();
                fusedMetadata.setModel(fusedModel);
                fusedEntity.setMetadata(fusedMetadata);

                break;
            }
            case KEEP_RIGHT:
            {
                Model fusedModel = fusedMetadata.getModel();
                fusedModel.removeAll(null, property, null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalB));                

                fusedMetadata.setModel(fusedModel);
                fusedEntity.setMetadata(fusedMetadata);
                
                break;
            }
            case KEEP_BOTH:
            {
                    Metadata leftMetadata = leftNode.getMetadata();
                    Metadata rightMetadata = rightNode.getMetadata();

                    Model fusedModel = fusedMetadata.getModel().add(leftMetadata.getModel()).add(rightMetadata.getModel());
                    fusedMetadata.setModel(fusedModel);
                    fusedEntity.setMetadata(fusedMetadata);
                    
                    break;
            }
            case KEEP_MORE_POINTS:
            {
                checkWKTProperty(property,action);

                Geometry leftGeometry = parseGeometry(literalA);
                Geometry rightGeometry = parseGeometry(literalB);

                if(leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {

                    Model fusedModel = fusedMetadata.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalA));                

                    fusedMetadata = fusedEntity.getMetadata();
                    fusedMetadata.setModel(fusedModel);

                    fusedEntity.setMetadata(fusedMetadata);

                } else {
                    Model fusedModel = fusedMetadata.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalB));                

                    fusedMetadata = fusedEntity.getMetadata();
                    fusedMetadata.setModel(fusedModel);

                    fusedEntity.setMetadata(fusedMetadata);
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

                    Model fusedModel = fusedMetadata.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                    fusedMetadata = fusedEntity.getMetadata();
                    fusedMetadata.setModel(fusedModel);
                    fusedEntity.setMetadata(fusedMetadata);
                    
                } else if(leftGeometry.getNumPoints() < rightGeometry.getNumPoints()){

                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(rightGeometry);   
                    String wktFusedGeometry = getWKTLiteral(fusedGeometry);

                    Model fusedModel = fusedMetadata.getModel();
                    fusedModel.removeAll(null, property, (RDFNode) null);
                    fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                    fusedMetadata = fusedEntity.getMetadata();
                    fusedMetadata.setModel(fusedModel);
                    fusedEntity.setMetadata(fusedMetadata);
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

                Model fusedModel = fusedMetadata.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                fusedMetadata = fusedEntity.getMetadata();
                fusedMetadata.setModel(fusedModel);
                fusedEntity.setMetadata(fusedMetadata);
                
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

                Model fusedModel = fusedMetadata.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(wktFusedGeometry));                

                fusedMetadata = fusedEntity.getMetadata();
                fusedMetadata.setModel(fusedModel);
                fusedEntity.setMetadata(fusedMetadata);

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
        Property propertyRDF;
        if(property.equalsIgnoreCase("label")){
            propertyRDF = ResourceFactory.createProperty(SpecificationConstants.LABEL);
        } else if(property.equalsIgnoreCase("date")){
            propertyRDF = ResourceFactory.createProperty(SpecificationConstants.DATE_OSM_MODIFIED);
        } else if(property.equalsIgnoreCase("wkt")){
            propertyRDF = ResourceFactory.createProperty(SpecificationConstants.WKT);
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
    
    private void checkWKTProperty(Property property, EnumFusionAction action){
        if(!property.toString().equals(SpecificationConstants.WKT)){
            logger.fatal("The selected action " + action.toString() + " applies only for WKT geometry literals");
            throw new RuntimeException();
        }         
    }
}
