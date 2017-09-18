package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.core.specification.FusionConfig;
import gr.athena.innovation.fagi.core.specification.FusionSpecification;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.Metadata;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.utils.Namespace;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fusion core class. Contains methods for the fusion process and writing to output.
 * 
 * @author nkarag
 */
public class Fuser implements IFuser{ 
    
    private static final Logger logger = LogManager.getLogger(Fuser.class);
    private final ArrayList<InterlinkedPair> interlinkedEntitiesList;
    private int linkedEntitiesNotFoundInDataset = 0;
    private int fusedPairsCount = 0;
    
    /**
     *
     * @param interlinkedEntitiesList
     */
    public Fuser(ArrayList<InterlinkedPair> interlinkedEntitiesList) {
        this.interlinkedEntitiesList = interlinkedEntitiesList;
    }

    /**
     * Fuses all links by using the parameters from the fusion configuration file.
     * 
     * @param config
     * @throws ParseException
     */
    @Override
    public void fuseAll(FusionConfig config) throws ParseException{
        linkedEntitiesNotFoundInDataset = 0;
        WKTReader wellKnownTextReader = new WKTReader();

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        for (Link link : links.getLinks()){

            Model modelA = constructEntityMetadataModel(link.getNodeA(), left, config.getOptionalDepth());
            Model modelB = constructEntityMetadataModel(link.getNodeB(), right, config.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            InterlinkedPair pair = new InterlinkedPair();

            Entity entityA = constructEntity(modelA, link.getNodeA(), wellKnownTextReader);
            Entity entityB = constructEntity(modelB, link.getNodeB(), wellKnownTextReader);
            
            pair.setLeftNode(entityA);
            pair.setRightNode(entityB);

            pair.fuse(config.getGeoAction(), config.getMetaAction());
            fusedPairsCount++;
            interlinkedEntitiesList.add(pair);
        }
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);
    }

    /**
     * Fuses all links using the Rules from file.
     * 
     * @throws ParseException
     */
    @Override
    public void fuseAllWithRules(FusionSpecification config, RuleCatalog ruleCatalog) throws ParseException{
        linkedEntitiesNotFoundInDataset = 0;
        WKTReader wellKnownTextReader = new WKTReader();

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        for (Link link : links.getLinks()){

            Model modelA = constructEntityMetadataModel(link.getNodeA(), left, config.getOptionalDepth());
            Model modelB = constructEntityMetadataModel(link.getNodeB(), right, config.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            InterlinkedPair pair = new InterlinkedPair();

            Entity entityA = constructEntity(modelA, link.getNodeA(), wellKnownTextReader);
            Entity entityB = constructEntity(modelB, link.getNodeB(), wellKnownTextReader);
            
            pair.setLeftNode(entityA);
            pair.setRightNode(entityB);
            //pair.fuse(config.getGeoAction(), config.getMetaAction());
            
            pair.fuseWithRule(ruleCatalog);
            
            fusedPairsCount++;
            interlinkedEntitiesList.add(pair);
        }
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);
    }
    
    /**
     * Constructs the output result by creating a new grapgh to the specified output 
     * or combining the fused entities into the source datasets.
     * 
     * @param fusionSpecification
     * @param interlinkedEntitiesList
     * @throws FileNotFoundException
     */
    public void combineFusedAndWrite(FusionSpecification fusionSpecification, 
            ArrayList<InterlinkedPair> interlinkedEntitiesList) throws FileNotFoundException{
        
        OutputStream out;
        if(fusionSpecification.getPathOutput().equalsIgnoreCase("System.out")){
            out = System.out;
        } else {
            out = new FileOutputStream(fusionSpecification.getPathOutput());
        }

        switch(fusionSpecification.getFinalDataset()) {
            case LEFT:
                Model leftModel = LeftModel.getLeftModel().getModel();
                
                for(InterlinkedPair p : interlinkedEntitiesList){

                    Resource leftResource = p.getLeftNode().getGeometryNode().asResource();
                    Property wktProperty = ResourceFactory.createProperty(Namespace.WKT);
                    Literal fusedGeometryLiteral = ResourceFactory.createPlainLiteral(p.getFusedEntity().getWKTLiteral());

                    Statement statementFused = ResourceFactory.createStatement(leftResource, wktProperty, fusedGeometryLiteral);
                    
                    Model fusedMetadataModel = p.getFusedEntity().getMetadata().getModel();

                    fusedMetadataModel.add(statementFused);
                    leftModel.add(fusedMetadataModel);
                }

                leftModel.write(out, fusionSpecification.getOutputRDFFormat()) ;

                break;
            case RIGHT:
                Model rightModel = RightModel.getRightModel().getModel();
                
                for(InterlinkedPair p : interlinkedEntitiesList){

                    Resource leftResource = p.getLeftNode().getGeometryNode().asResource();
                    Property wktProperty = ResourceFactory.createProperty(Namespace.WKT);
                    Literal fusedGeometryLiteral = ResourceFactory.createPlainLiteral(p.getFusedEntity().getWKTLiteral());

                    Statement statementFused = ResourceFactory.createStatement(leftResource, wktProperty, fusedGeometryLiteral);
                    
                    Model fusedMetadataModel = p.getFusedEntity().getMetadata().getModel();

                    fusedMetadataModel.add(statementFused);
                    rightModel.add(fusedMetadataModel);
                }

                rightModel.write(out, fusionSpecification.getOutputRDFFormat());
                break;
            case NEW:
            case DEFAULT:                
            default:
                //user default is NEW dataset.
                Model newModel = ModelFactory.createDefaultModel();
                
                for(InterlinkedPair pair : interlinkedEntitiesList){

                    Resource geometryNode = pair.getLeftNode().getGeometryNode().asResource();
                    Property wktProperty = ResourceFactory.createProperty(Namespace.WKT);
                    Property hasGeometry = ResourceFactory.createProperty(Namespace.GEOSPARQL_HAS_GEOMETRY);
                    Literal fusedGeometryLiteral = ResourceFactory.createPlainLiteral(pair.getFusedEntity().getWKTLiteral());
                    

                    Resource resourceURI = ResourceFactory.createResource(pair.getLeftNode().getResourceURI());
                    Statement statementFused1 = ResourceFactory.createStatement(resourceURI, hasGeometry, geometryNode);        
                    Statement statementFused2 = ResourceFactory.createStatement(geometryNode, wktProperty, fusedGeometryLiteral);
                    
                    Model fusedMetadataModel = pair.getFusedEntity().getMetadata().getModel();

                    fusedMetadataModel.add(statementFused1);
                    fusedMetadataModel.add(statementFused2);
                    newModel.add(fusedMetadataModel);
                }

                newModel.write(out, fusionSpecification.getOutputRDFFormat());                
                break;
        }
    }
    
    private Entity constructEntity(Model model, String resourceURI, WKTReader wellKnownTextReader) throws ParseException {
        
        Entity entity = new Entity();
        //We assume the entity has one single geometry representation as WKT
        StmtIterator statements = model.listStatements(
                new SimpleSelector(null, ResourceFactory.createProperty(Namespace.WKT), (RDFNode) null));
        
        Statement geometryStatement = statements.next();
        Resource geometryNode = geometryStatement.getSubject();
        
        Geometry geo = wellKnownTextReader.read(geometryStatement.getLiteral().getString());
        
        //remove geometry from metadata model.
        //TODO - remove any orphaned chain when ontology is defined
        //model.removeAll(null, ResourceFactory.createProperty(Namespace.GEOSPARQL_HAS_GEOMETRY), (RDFNode) null);
        model.removeAll(null, ResourceFactory.createProperty(Namespace.WKT), (Literal) null);

        Metadata metadata = new Metadata(model);
        
        entity.setGeometryNode(geometryNode);
        entity.setResourceURI(resourceURI);
        entity.setGeometry(geo);
        entity.setMetadata(metadata);
        
        return entity;
    }
    
    private Model constructEntityMetadataModel(String node, Model sourceModel, int depth){
        
        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct(); //todo - maybe exclude geometry from model

        //removing constructed model from source model.
        sourceModel.remove(model);
        return model;
    }

    /**
     * Returns the count of linked entities that were not found in the source datasets.
     * 
     * @return
     */
    public int getLinkedEntitiesNotFoundInDataset() {
        return linkedEntitiesNotFoundInDataset;
    }

    /**
     *
     * @param linkedEntitiesNotFoundInDataset
     */
    public void setLinkedEntitiesNotFoundInDataset(int linkedEntitiesNotFoundInDataset) {
        this.linkedEntitiesNotFoundInDataset = linkedEntitiesNotFoundInDataset;
    }

    /**
     * Returns the total fused entities.
     * 
     * @return
     */
    public int getFusedPairsCount() {
        return fusedPairsCount;
    }
    
}
