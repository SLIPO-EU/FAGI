package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.EntityData;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
    private int linkedEntitiesNotFoundInDataset = 0;
    private int fusedPairsCount = 0;

    /**
     * Fuses all links using the Rules from file.
     * 
     * @param fusionSpec
     * @param ruleCatalog
     * @param functionMap
     * @throws ParseException
     */
    @Override
    public List<LinkedPair> fuseAll(FusionSpecification fusionSpec, RuleCatalog ruleCatalog, 
            Map<String, IFunction> functionMap) throws ParseException, WrongInputException{
        
        List<LinkedPair> fusedList = new ArrayList<>();
        
        linkedEntitiesNotFoundInDataset = 0;

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        for (Link link : links.getLinks()){

            Model modelA = constructEntityMetadataModel(link.getNodeA(), left, fusionSpec.getOptionalDepth());
            Model modelB = constructEntityMetadataModel(link.getNodeB(), right, fusionSpec.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            LinkedPair pair = new LinkedPair();

            Entity entityA = constructEntity(modelA, link.getNodeA());
            Entity entityB = constructEntity(modelB, link.getNodeB());
            
            pair.setLeftNode(entityA);
            pair.setRightNode(entityB);
            
            pair.fusePair(ruleCatalog, functionMap);
            
            fusedPairsCount++;
            fusedList.add(pair);
        }
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);
        
        return fusedList;
    }
    
    /**
     * Constructs the output result by creating a new graph to the specified output 
     * or combining the fused entities into the source datasets.
     * 
     * @param fusionSpecification
     * @param fusedEntities
     * @param defaultDatasetAction
     * @throws FileNotFoundException
     */
    public void combineFusedAndWrite(FusionSpecification fusionSpecification, 
            List<LinkedPair> fusedEntities, EnumDatasetAction defaultDatasetAction) 
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
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedMetadataModel = pair.getFusedEntity().getEntityData().getModel();
                    leftModel.add(fusedMetadataModel);
                }

                leftModel.write(out, fusionSpecification.getOutputRDFFormat()) ;

                break;
            case RIGHT:
                Model rightModel = RightModel.getRightModel().getModel();
                
                for(LinkedPair p : fusedEntities){

                    Model fusedMetadataModel = p.getFusedEntity().getEntityData().getModel();
                    rightModel.add(fusedMetadataModel);
                }

                rightModel.write(out, fusionSpecification.getOutputRDFFormat());
                break;
            case NEW:
            case DEFAULT:                
            default:
                //user default is NEW dataset.
                Model newModel = ModelFactory.createDefaultModel();
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedMetadataModel = pair.getFusedEntity().getEntityData().getModel();
                    newModel.add(fusedMetadataModel);
                }

                newModel.write(out, fusionSpecification.getOutputRDFFormat());                
                break;
        }
    }
    
    private Entity constructEntity(Model model, String resourceURI) throws ParseException {
        
        Entity entity = new Entity();
        EntityData metadata = new EntityData(model);
        entity.setResourceURI(resourceURI);
        entity.setEntityData(metadata);
        
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
