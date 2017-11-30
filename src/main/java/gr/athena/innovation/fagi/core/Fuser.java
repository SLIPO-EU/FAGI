package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.Metadata;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fusion core class. Contains methods for the fusion process and writing to output.
 * 
 * @author nkarag
 */
public class Fuser implements IFuser{ 
    
    private static final Logger logger = LogManager.getLogger(Fuser.class);
    private final List<InterlinkedPair> interlinkedEntitiesList;
    private int linkedEntitiesNotFoundInDataset = 0;
    private int fusedPairsCount = 0;
    
    /**
     *
     * @param interlinkedEntitiesList
     */
    public Fuser(List<InterlinkedPair> interlinkedEntitiesList) {
        this.interlinkedEntitiesList = interlinkedEntitiesList;
    }

    /**
     * Fuses all links using the Rules from file.
     * 
     * @param fusionSpecification
     * @param ruleCatalog
     * @param functionMap
     * @throws ParseException
     */
    @Override
    public void fuseAllWithRules(FusionSpecification fusionSpecification, RuleCatalog ruleCatalog, 
            Map<String, IFunction> functionMap) throws ParseException, WrongInputException{
        
        linkedEntitiesNotFoundInDataset = 0;
        WKTReader wellKnownTextReader = new WKTReader();

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        for (Link link : links.getLinks()){

            Model modelA = constructEntityMetadataModel(link.getNodeA(), left, fusionSpecification.getOptionalDepth());
            Model modelB = constructEntityMetadataModel(link.getNodeB(), right, fusionSpecification.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            InterlinkedPair pair = new InterlinkedPair();

            Entity entityA = constructEntity(modelA, link.getNodeA(), wellKnownTextReader);
            Entity entityB = constructEntity(modelB, link.getNodeB(), wellKnownTextReader);
            
            pair.setLeftNode(entityA);
            pair.setRightNode(entityB);
            
            pair.fuseWithRule(ruleCatalog, functionMap);
            
            fusedPairsCount++;
            interlinkedEntitiesList.add(pair);
        }
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);
    }
    
    /**
     * Constructs the output result by creating a new graph to the specified output 
     * or combining the fused entities into the source datasets.
     * 
     * @param fusionSpecification
     * @param interlinkedEntitiesList
     * @param defaultDatasetAction
     * @throws FileNotFoundException
     */
    public void combineFusedAndWrite(FusionSpecification fusionSpecification, 
            List<InterlinkedPair> interlinkedEntitiesList, EnumDatasetAction defaultDatasetAction) 
                    throws FileNotFoundException{
        
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

                    Model fusedMetadataModel = p.getFusedEntity().getMetadata().getModel();
                    leftModel.add(fusedMetadataModel);
                }

                leftModel.write(out, fusionSpecification.getOutputRDFFormat()) ;

                break;
            case RIGHT:
                Model rightModel = RightModel.getRightModel().getModel();
                
                for(InterlinkedPair p : interlinkedEntitiesList){

                    Model fusedMetadataModel = p.getFusedEntity().getMetadata().getModel();
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

                    Model fusedMetadataModel = pair.getFusedEntity().getMetadata().getModel();
                    newModel.add(fusedMetadataModel);
                }

                newModel.write(out, fusionSpecification.getOutputRDFFormat());                
                break;
        }
    }
    
    private Entity constructEntity(Model model, String resourceURI, WKTReader wellKnownTextReader) throws ParseException {
        
        Entity entity = new Entity();
        Metadata metadata = new Metadata(model);
        entity.setResourceURI(resourceURI);
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
