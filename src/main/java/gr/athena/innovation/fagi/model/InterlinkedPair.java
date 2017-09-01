package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.core.action.EnumMetadataActions;
import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import com.vividsolutions.jts.geom.Geometry;
import gr.athena.innovation.fagi.core.rule.ActionRule;
import gr.athena.innovation.fagi.core.rule.ExpressionTag;
import gr.athena.innovation.fagi.core.rule.LogicalExpressionTag;
import gr.athena.innovation.fagi.core.rule.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.fusers.CentroidShiftTranslator;
import java.util.LinkedList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
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

    public void fuseWithRule(RuleCatalog ruleCatalog){
        logger.debug("Fusing with rule..");
        fusedEntity = new Entity();
        
        Geometry leftGeometry = leftNode.getGeometry();
        Geometry rightGeometry = rightNode.getGeometry();

        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();

        List<Rule> rules = ruleCatalog.getRules();
        
        int j = 0;
        
        for(Rule rule : rules){
        
            logger.trace("Rule number: " + j);
            String propertyA = rule.getPropertyA();
            String propertyB = rule.getPropertyB();
            EnumGeometricActions defaultGeoAction = rule.getDefaultGeoAction();
            EnumMetadataActions defaultMetaAction = rule.getDefaultMetaAction();
            
            List<ActionRule> actionRules = rule.getActionRuleSet().getActionRuleList();
            for(ActionRule actionRule : actionRules){
                
                EnumGeometricActions gA = null;
                EnumMetadataActions mA = null;
                //String mA = "";

                if(actionRule.getGeoAction() != null){
                    gA = actionRule.getGeoAction();
                }
                
                if(actionRule.getMetaAction() != null){
                    mA = actionRule.getMetaAction();
                }

                ExpressionTag expressionTag = actionRule.getConditionTag().getExpressionTag();
                if(actionRule.getConditionTag().getTagList().isEmpty()){

                    //logger.debug("expressionTag: " + expressionTag);
                    //single function
                    String expression = expressionTag.getExpression();
                    logger.debug("Check if FUNCTION exists in function list. Function: " + expression + " , actions: " + gA + " " + mA);
                    

                } else {
                    if(expressionTag instanceof LogicalExpressionTag){
                        LogicalExpressionTag logEx = (LogicalExpressionTag) expressionTag;
                        String operation = logEx.getLogicalOp();

                        for(ExpressionTag exTag : logEx.getExpressionTags()){
                            String expression = exTag.getExpression();
                            logger.info("should be a Logical expression: " + expression + " level: " + ruleCatalog.getMaxLevelOfActionRule(actionRule));
                        }
                        //String expression = logEx.getExpressionTags().get(0).getExpression();

                    } else {
                        logger.fatal("expressionTag not logical expression");
                    }
                }

                int level = ruleCatalog.getMaxLevelOfActionRule(actionRule);
                logger.trace("Max Level: " + level);
                int i = level;
                while (i >= level){
                    logger.trace("Top level: " + i);
                   i--; 
                }

                ExpressionTag expression = actionRule.getConditionTag().getExpressionTag();
                LinkedList<LogicalExpressionTag> tagList = actionRule.getConditionTag().getTagList();
//                for(LogicalExpressionTag tag : tagList){
//                    tag.getLevel();
//                }

            }
            j++;
        }
 
        System.out.println("Temp End.");
        System.exit(0);
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
        //rule.getPropertyA()
        
    }

    public void fuse(EnumGeometricActions geoAction, EnumMetadataActions metaAction){
        fusedEntity = new Entity();

        fuseGeometry(geoAction);
        fuseMetadata(metaAction);
    }

    private void fuseGeometry(EnumGeometricActions geoAction){

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

    private void fuseMetadata(EnumMetadataActions metaAction){
        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();

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
}
