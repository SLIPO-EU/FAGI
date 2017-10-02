package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.core.action.EnumMetadataActions;
import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import com.vividsolutions.jts.geom.Geometry;
import gr.athena.innovation.fagi.core.action.EnumDatasetActions;
import gr.athena.innovation.fagi.core.rule.model.ActionRule;
import gr.athena.innovation.fagi.core.rule.model.Condition;
import gr.athena.innovation.fagi.core.rule.model.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import gr.athena.innovation.fagi.core.functions.geo.CentroidShiftTranslator;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import java.util.HashMap;
import java.util.List;
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

    public void fuseWithRule(RuleCatalog ruleCatalog, HashMap<String, Object> functionMap){

        EnumDatasetActions defaultDatasetAction = ruleCatalog.getDefaultDatasetAction();
        
        fusedEntity = new Entity();
        fusedEntity.setResourceURI(resolveFusedEntityURI(defaultDatasetAction));

        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();

        
        
        fuseDefault(defaultDatasetAction);

        List<Rule> rules = ruleCatalog.getRules();

        for(Rule rule : rules){
            logger.trace("Fusing with Rule: " + rule);

            EnumGeometricActions defaultGeoAction = rule.getDefaultGeoAction();
            EnumMetadataActions defaultMetaAction = rule.getDefaultMetaAction();

            //TODO: change #getRDFPropertyFromString to check for propertyB when ontology is different from source datasets
            Property rdfPropertyA = getRDFPropertyFromString(rule.getPropertyA());
            Property rdfPropertyB = getRDFPropertyFromString(rule.getPropertyB());
            
            String literalA = getLiteralValue(rule.getPropertyA(), leftMetadata.getModel());
            String literalB = getLiteralValue(rule.getPropertyB(), rightMetadata.getModel());
            logger.info("Found literals: {}, {}", literalA, literalB);
            
            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and break.
            if(rule.getActionRuleSet() == null){
                logger.trace("Rule without ACTION RULE SET, use plain action: " + defaultGeoAction + " " 
                        + defaultMetaAction);
                if(defaultMetaAction != null){
                    replaceLiteralInFusedModel(defaultMetaAction, rdfPropertyA, literalA, literalB);
                }
                break;
            }
            
            List<ActionRule> actionRules = rule.getActionRuleSet().getActionRuleList();
            int actionRuleCount = 0;
            boolean actionRuleToApply = false;
            for(ActionRule actionRule : actionRules){

                logger.info("-- Action rule: " + actionRuleCount);

                EnumGeometricActions geoAction = null;
                EnumMetadataActions metaAction = null;

                if(actionRule.getGeoAction() != null){
                    geoAction = actionRule.getGeoAction();
                }

                if(actionRule.getMetaAction() != null){
                    metaAction = actionRule.getMetaAction();
                }

                Condition condition = actionRule.getCondition();

                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, literalA, literalB);

                actionRuleCount++;
                if(isActionRuleToBeApplied){
                    //fuseGeometry(geoAction);
                    //fuseMetadata(metaAction);
                    logger.trace("Replacing in model: " + literalA + " with " + literalB);
                    replaceLiteralInFusedModel(metaAction, rdfPropertyA, literalA, literalB);
                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Actions
//            if(actionRuleToApply == false){
//                fuseGeometry(defaultGeoAction);
//                fuseMetadata(defaultMetaAction);
//            }

        }
    }

    public void fuseDefault(EnumDatasetActions datasetDefaultAction){

        //default action should be performed before the rules apply. The fused model should be empty:
        if(!fusedEntity.getMetadata().getModel().isEmpty()){
            logger.fatal("Something is wrong. Default fusion action tries to overwrite already fused data!");
            throw new RuntimeException();
        }

        Metadata leftMetadata = leftNode.getMetadata();
        Geometry leftGeometry = leftNode.getGeometry();
        Metadata rightMetadata = rightNode.getMetadata();
        Geometry rightGeometry = rightNode.getGeometry();
        
        switch(datasetDefaultAction){
            case KEEP_LEFT:
                fusedEntity.setGeometry(leftGeometry);
                fusedEntity.setMetadata(leftMetadata);
                break;
            case KEEP_RIGHT:
                fusedEntity.setGeometry(rightGeometry);
                fusedEntity.setMetadata(rightMetadata);
                break;
            case KEEP_BOTH:
                fusedEntity.getMetadata().getModel().add(leftMetadata.getModel()).add(rightMetadata.getModel());
            default:
                logger.fatal("Dataset default fusion action is not defined.");
                throw new RuntimeException();
        }        
    }
    
    private void fuseGeometry(EnumGeometricActions geoAction){
        logger.trace("Fusing geometry with: " + geoAction);
        if(geoAction == null){
            //action rule refers to metadataAction
            return;
        }
        
        Geometry leftGeometry = leftNode.getGeometry();
        Geometry rightGeometry = rightNode.getGeometry();
        switch(geoAction){
            case KEEP_LEFT_GEOMETRY:
                fusedEntity.setGeometry(leftGeometry);
                break;
            case KEEP_RIGHT_GEOMETRY:
                fusedEntity.setGeometry(rightGeometry);
                break;
            case KEEP_MORE_POINTS:
                if(leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {
                    fusedEntity.setGeometry(leftGeometry);
                } else {
                    fusedEntity.setGeometry(rightGeometry);
                }
                break;
            case KEEP_MORE_POINTS_AND_SHIFT:
                if(leftGeometry.getNumPoints() > rightGeometry.getNumPoints()) {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(leftGeometry);
                    fusedEntity.setGeometry(fusedGeometry);
                } else if(leftGeometry.getNumPoints() < rightGeometry.getNumPoints()){
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(rightGeometry);                    
                    fusedEntity.setGeometry(fusedGeometry);
                } else {
                    fusedEntity.setGeometry(leftGeometry);
                }
                break;
            case SHIFT_LEFT_GEOMETRY:
                {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                    Geometry shiftedToRightGeometry = centroidTranslator.shift(leftGeometry);
                    fusedEntity.setGeometry(shiftedToRightGeometry);
                    break;
                }
            case SHIFT_RIGHT_GEOMETRY:
                {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                    Geometry shiftedToLeftGeometry = centroidTranslator.shift(rightGeometry);
                    fusedEntity.setGeometry(shiftedToLeftGeometry);
                    break;
                }
            case KEEP_BOTH_GEOMETRIES:
                logger.fatal("Keep both geometries not supported yet.");
                throw new UnsupportedOperationException("Keep both geometries not supported yet.");                
            default:
                logger.fatal("Geometric fusion action is not defined.");
                throw new RuntimeException();
        }
    }

    private void replaceLiteralInFusedModel(EnumMetadataActions metaAction, 
            Property property, String literalA, String literalB){
        //TODO: Check Keep both. 
        //TODO: Also, property coming from the caller is propertyA because it assumes same ontology
        //Maybe add propertyB and check them both if one does not exist in model.
        
        String fusedURI = fusedEntity.getResourceURI();

        Metadata fusedMetadata = fusedEntity.getMetadata();

        switch(metaAction){
            case KEEP_LEFT_METADATA:
            {
                Model fusedModel = fusedMetadata.getModel();
                fusedModel.removeAll(null, property, (RDFNode) null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalA));                
                
                fusedMetadata = fusedEntity.getMetadata();
                fusedMetadata.setModel(fusedModel);
                
                fusedEntity.setMetadata(fusedMetadata);
                break;
            }
            case KEEP_RIGHT_METADATA:
            {
                Model fusedModel = fusedMetadata.getModel();
                fusedModel.removeAll(null, property, null);
                fusedModel.add(ResourceFactory.createResource(fusedURI), property, ResourceFactory.createStringLiteral(literalB));                

                fusedMetadata.setModel(fusedModel);
                
                fusedEntity.setMetadata(fusedMetadata);
                break;
            }
            case KEEP_BOTH_METADATA:
                {
                    
//                    Metadata fusedMetadata = fusedEntity.getMetadata();
//                    fusedModel.add(leftMetadata.getModel()).add(rightMetadata.getModel());
//                    fusedMetadata.setModel(fusedModel);
//                    fusedEntity.setMetadata(fusedMetadata);
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

    private String resolveFusedEntityURI(EnumDatasetActions defaultDatasetAction) {
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
}
