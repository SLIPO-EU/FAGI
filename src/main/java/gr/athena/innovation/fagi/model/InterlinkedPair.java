package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.core.action.EnumMetadataActions;
import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import com.vividsolutions.jts.geom.Geometry;
import gr.athena.innovation.fagi.core.action.EnumDatasetActions;
import gr.athena.innovation.fagi.core.rule.ActionRule;
import gr.athena.innovation.fagi.core.rule.Condition;
import gr.athena.innovation.fagi.core.rule.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import gr.athena.innovation.fagi.fusers.CentroidShiftTranslator;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import java.util.HashMap;
import java.util.List;
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

    public void fuseWithRule(RuleCatalog ruleCatalog, HashMap<String, Object> functionMap, String resourceUri){

        fusedEntity = new Entity();

        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();

        EnumDatasetActions defaultAction = ruleCatalog.getDefaultDatasetAction();
        List<Rule> rules = ruleCatalog.getRules();

        for(Rule rule : rules){
            logger.debug("Fusing with Rule: " + rule);

            EnumGeometricActions defaultGeoAction = rule.getDefaultGeoAction();
            EnumMetadataActions defaultMetaAction = rule.getDefaultMetaAction();

            //objectA = 
            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and break.
            if(rule.getActionRuleSet() == null){
                logger.fatal("Rule without ACTION RULE SET, use plain action: " + defaultGeoAction + " " 
                        + defaultMetaAction);
                
                if(defaultGeoAction != null){
                    
                    
                    fuseGeometry(defaultGeoAction);
                }

                if(defaultMetaAction != null){
                    
                    
                    fuseMetadata(defaultMetaAction);
                }
                break;
            }

            String literalA = getLiteralValue(rule.getPropertyA(), leftMetadata.getModel());
            String literalB = getLiteralValue(rule.getPropertyB(), rightMetadata.getModel());
            
            Property rdfPropertyA = getRDFPropertyFromString(rule.getPropertyA());
            Property rdfPropertyB = getRDFPropertyFromString(rule.getPropertyB());

            logger.info("Found literals: {}, {}", literalA, literalB);

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
                    logger.fatal("Replacing in model: " + literalA + " " + literalB);
                    replaceLiteralInFusedModel(geoAction, metaAction, rdfPropertyA, literalA, literalB);
                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Actions
            if(actionRuleToApply == false){
                fuseGeometry(defaultGeoAction);
                fuseMetadata(defaultMetaAction);
            }

        }

        Model tempModel = leftMetadata.getModel();
        // iterate over the triples
        for (StmtIterator i = leftMetadata.getModel().listStatements( null, null, (RDFNode) null ); i.hasNext(); ) {
            Statement originalStatement = i.nextStatement();
            //tempModel.add(originalStatement);
            Resource s = originalStatement.getSubject();
            Property p = originalStatement.getPredicate();
            RDFNode o = originalStatement.getObject();
        }
        //fuseGeometry(geoAction);
        //fuseMetadata(metaAction);        
        //rule.getPropertyA();
    }

    public void fuse(EnumGeometricActions geoAction, EnumMetadataActions metaAction){
        fusedEntity = new Entity();

        fuseGeometry(geoAction);
        fuseMetadata(metaAction);
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
                Metadata fusedMetadata = fusedEntity.getMetadata();
                fusedMetadata.getModel().add(leftMetadata.getModel()).add(rightMetadata.getModel());            
            default:
                logger.fatal("Dataset default fusion action is not defined.");
                throw new RuntimeException();
        }        
    }
    
    private void fuseGeometry(EnumGeometricActions geoAction){
        logger.debug("Fusing geometry with: " + geoAction);
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

    private void replaceLiteralInFusedModel(EnumGeometricActions geoAction, EnumMetadataActions metaAction, 
            Property property, String objectA, String objectB){
        //TODO: Check Keep both. 
        //TODO: Also, property coming from the caller is propertyA because it assumes same ontology
        //Maybe add propertyB and check them both if one does not exist in model.
        
        logger.trace("Fused entity URI: " + fusedEntity.getResourceURI());
        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();        

        Metadata fusedMetadata = fusedEntity.getMetadata();
        
        switch(metaAction){
            case KEEP_LEFT_METADATA:
                Model leftModel = leftMetadata.getModel();
                leftModel.removeAll(null, property, (RDFNode) null);
                leftModel.add(ResourceFactory.createResource(leftNode.getResourceURI()), property, ResourceFactory.createStringLiteral(objectA));                
                
                fusedMetadata = fusedEntity.getMetadata();
                fusedMetadata.setModel(leftModel);
                
                fusedEntity.setMetadata(fusedMetadata);
                break;
            case KEEP_RIGHT_METADATA:
                Model rightModel = leftMetadata.getModel();
                rightModel.removeAll(null, property, null);
                rightModel.add(ResourceFactory.createResource(rightNode.getResourceURI()), property, ResourceFactory.createStringLiteral(objectB));                

                
                fusedMetadata.setModel(rightModel);
                
                fusedEntity.setMetadata(rightMetadata);
                break;
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
    
    private void fuseMetadata(EnumMetadataActions metaAction){
        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();

        //TODO: if current fusedModel is empty, create the new model. Else use the already.
        Model fusedModel = ModelFactory.createDefaultModel();
        switch(metaAction){
            case KEEP_LEFT_METADATA:
                fusedEntity.setMetadata(leftMetadata);
                break;
            case KEEP_RIGHT_METADATA:
                fusedEntity.setMetadata(rightMetadata);
                break;
            case KEEP_BOTH_METADATA:
                {
                    Metadata fusedMetadata = fusedEntity.getMetadata();
                    fusedModel.add(leftMetadata.getModel()).add(rightMetadata.getModel());
                    fusedMetadata.setModel(fusedModel);
                    fusedEntity.setMetadata(fusedMetadata);
                    break;
                }
//            case FLATTEN_LEFT_METADATA:
//                Metadata fusedMetadata = fusedEntity.getMetadata();
//                Model mod = leftMetadata.getModel();
//                Model tempModel = leftMetadata.getModel();
//                // iterate over the triples
//                for (StmtIterator i = mod.listStatements( null, null, (RDFNode) null ); i.hasNext(); ) {
//                    
//                    Statement originalStatement = i.nextStatement();
//                    tempModel.add(originalStatement);
//                    
//                    Resource s = originalStatement.getSubject();
//                    Property p = originalStatement.getPredicate();
//                    RDFNode o = originalStatement.getObject();
//                    
//                    
//                    
//                    if(o.isLiteral()){ //last triple of chain
//                        System.out.println("#####" + originalStatement.toString());
//                        //check if the subject of this triple is an object to another
//                        if(mod.contains(null, null, s)){
//                            Statement st = mod.getProperty(s, null);
//                            System.out.println("->##########" + st.toString());
//                        }
//                    } else {
//                        System.out.println("*****" + originalStatement.toString());
//                    }
//                    
////                    if(o.isResource()){
////
////                        boolean objectIsAlsoSubject = mod.contains(o.asResource(), null, (RDFNode) null);
////
////                        if(objectIsAlsoSubject){
////                            tempModel.remove(originalStatement);
////                            //ResourceFactory.createProperty(uriref);
////                            Statement flattenedStatement = ResourceFactory.createStatement(s, p, o);
////                            tempModel.add(flattenedStatement);
////                            //
////
////                            //remove statement from model, add flatend
////                        }
////
////                        //System.out.println("&&&&&&" + cont);
////                        //logger.debug(cont);
////                        //System.exit(0);
////                    }
//                }
//                //use SPARQL query
//                String q = SparqlConstructor.selectAll(2);
//                Query query = QueryFactory.create(q);
//                QueryExecution queryExecution = QueryExecutionFactory.create(query, mod);
//                ResultSet resultSet = queryExecution.execSelect();
//                System.out.println("\n\nFlatening");
//
//                //resultSet.
//                while ( resultSet.hasNext() ) {
//                    QuerySolution qs = resultSet.next();
//                    
//                    //?s ?p1 ?o1 ?p2 ?o2 ?p3 ?o3 ?p4 ?o4
//                    RDFNode s = qs.get("s");
//                    RDFNode p1 = qs.get("p1");
//                    RDFNode o1 = qs.get("o1");
//                    
//                    RDFNode p2 = qs.get("p2");
//                    RDFNode o2 = qs.get("o2");
//                    
//                    RDFNode p3 = qs.get("p3");
//                    RDFNode o3 = qs.get("o3");
//                    
//                    RDFNode p4 = qs.get("p4");
//                    RDFNode o4 = qs.get("o4");
//
//                    Iterator<String> vars = qs.varNames();
//                    while(vars.hasNext()){
//                        String var = vars.next();
//                        
//                        RDFNode node = qs.get(var);
//                        if(node.isResource()){
//                            
//                        }
//                        
//                    }
//                    if(o4 != null){
//                        logger.debug("o4 chain");
//                        logger.debug(s + " " + p1+ " " + o1 
//                            + "\n" + o1 + " " + p2 + " " + o2 
//                            + "\n" + o2 + p3 + " " + o3 
//                            + "\n" + o3 + p4 + " " + o4);
//                        if(o4.isLiteral()){
//                            
//                            //found literal, flatten chain in a single triple
//                            
//                        }
//                    } else if(o3 !=null){
//                        logger.warn("o3 chain");
//                        logger.debug(s + " " + p1+ " " + o1 
//                            + "\n" + o1 + " " + p2 + " " + o2 
//                            + "\n" + o2 + p3 + " " + o3);
//                    } else if(o2 != null){
//                        logger.debug("o2 chain");
//                        logger.debug(s + " " + p1+ " " + o1 
//                            + "\n" + o1 + " " + p2 + " " + o2);
//                    }
//                }
//                
//                
//                //mod.
////                Metadata fusedMetadata = fusedEntity.getMetadata();
//                fusedModel.add(leftMetadata.getModel()).add(rightMetadata.getModel());
//                fusedMetadata.setModel(fusedModel);
//                System.out.println(fusedMetadata.getModel().write(System.out));
//                fusedEntity.setMetadata(fusedMetadata);
//                break;
////            case KEEP_BOTH_METADATA:
//////                Metadata fusedMetadata = fusedEntity.getMetadata();
//////                fusedModel.add(leftMetadata.getModel()).add(rightMetadata.getModel());
//////                fusedMetadata.setModel(fusedModel);
////                fusedEntity.setMetadata(fusedMetadata);
////                break;                           
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
        Property propertyRDF = null;
        if(property.equalsIgnoreCase("label")){
            propertyRDF = ResourceFactory.createProperty(SpecificationConstants.LABEL);
        } else if(property.equalsIgnoreCase("date")){
            propertyRDF = ResourceFactory.createProperty(SpecificationConstants.DATE_OSM_MODIFIED);
        } else if(property.equalsIgnoreCase("wkt")){
            propertyRDF = ResourceFactory.createProperty(SpecificationConstants.WKT);
        }
        return propertyRDF;
    }    
}
