package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.EntityData;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fusion core class. Contains methods for the fusion process.
 * 
 * @author nkarag
 */
public class Fuser implements IFuser{ 
    
    private static final Logger logger = LogManager.getLogger(Fuser.class);
    private int linkedEntitiesNotFoundInDataset = 0;
    private int fusedPairsCount = 0;

    /**
     * Fuses all links using the Rules defined in the XML file.
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

        Model left = LeftDataset.getLeftDataset().getModel();
        Model right = RightDataset.getRightDataset().getModel();
        LinksModel links = LinksModel.getLinksModel();

        EnumOutputMode mode = fusionSpec.getOutputMode();

        for (Link link : links.getLinks()){

            LinkedPair linkedPair = new LinkedPair();
            linkedPair.setLink(link);

            //create the jena models for each node of the pair and remove them from the source models.
            Model modelA = constructEntityDataModel(link.getNodeA(), left, fusionSpec.getOptionalDepth());
            Model modelB = constructEntityDataModel(link.getNodeB(), right, fusionSpec.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                linkedEntitiesNotFoundInDataset++;
                continue;
            }

            String leftURI = link.getNodeA();
            String leftLocalName = link.getLocalNameA();
            String rightURI = link.getNodeB();
            String rightLocalName = link.getLocalNameB();

            Entity entityA = constructEntity(modelA, leftURI, leftLocalName);
            Entity entityB = constructEntity(modelB, rightURI, rightLocalName);

            linkedPair.setLeftNode(entityA);
            linkedPair.setRightNode(entityB);

            /* VALIDATION */
            EnumValidationAction validation = linkedPair.validateLink(ruleCatalog.getValidationRules(), functionMap);

            Entity newFusedEntity = new Entity();

            String targetURI = resolveURI(mode, leftURI, rightURI);
            String targetLocalName = resolveLocalName(mode, leftLocalName, rightLocalName);

            newFusedEntity.setResourceURI(targetURI);
            newFusedEntity.setLocalName(targetLocalName);

            linkedPair.setFusedEntity(newFusedEntity);

            /* FUSION */
            linkedPair.fusePair(ruleCatalog, functionMap, validation);

            fusedList.add(linkedPair);

            fusedPairsCount++;
        }
        setLinkedEntitiesNotFoundInDataset(linkedEntitiesNotFoundInDataset);
        
        return fusedList;
    }
    
    /**
     * Produces the output result by creating a new graph to the specified output 
     * or combines the fused entities with the source datasets based on the fusion mode.
     * 
     * @param fusionSpecification
     * @param fusedEntities
     * @param defaultDatasetAction
     * @throws FileNotFoundException
     */
    public void combineFusedAndWrite(FusionSpecification fusionSpecification, 
            List<LinkedPair> fusedEntities, EnumDatasetAction defaultDatasetAction) 
                    throws FileNotFoundException, IOException{

        String outputPathA = fusionSpecification.getFileA();
        String outputPathB = fusionSpecification.getFileB();
        String outputPathC = fusionSpecification.getFileC();

        OutputStream outputStreamA = new FileOutputStream(outputPathA, false);
        OutputStream outputStreamB = new FileOutputStream(outputPathB, false);
        OutputStream outputStreamC = new FileOutputStream(outputPathC, false);
        
        EnumOutputMode mode = fusionSpecification.getOutputMode();

        switch(mode) {
            case AA_MODE:
            {
                logger.info(EnumOutputMode.AA_MODE + ": Output result will be written to " + outputPathA);
                
                Model leftModel = LeftDataset.getLeftDataset().getModel();
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    
                    leftModel.add(fusedDataModel);
                }

                leftModel.write(outputStreamA, fusionSpecification.getOutputRDFFormat());
                
                addMessageToEmptyOutput(outputPathB);
                addMessageToEmptyOutput(outputPathC);
                
                break;
            }
            case BB_MODE:
            {
                logger.info(EnumOutputMode.BB_MODE + ": Output result will be written to " + outputPathB);

                Model rightModel = RightDataset.getRightDataset().getModel();
                
                for(LinkedPair p : fusedEntities){

                    Model fusedModel = p.getFusedEntity().getEntityData().getModel();
                    rightModel.add(fusedModel);
                }

                rightModel.write(outputStreamB, fusionSpecification.getOutputRDFFormat());
                
                addMessageToEmptyOutput(outputPathA);
                addMessageToEmptyOutput(outputPathC);
                
                break;
            }
            case L_MODE:
            {
                logger.info(EnumOutputMode.L_MODE + ": Output result will be written to " + outputPathC);
                
                Model newModel = ModelFactory.createDefaultModel();
                
                for(LinkedPair pair : fusedEntities){

                    Model fusedModel = pair.getFusedEntity().getEntityData().getModel();
                    newModel.add(fusedModel);
                }

                newModel.write(outputStreamC, fusionSpecification.getOutputRDFFormat());

                addMessageToEmptyOutput(outputPathA);
                addMessageToEmptyOutput(outputPathB);
                
                break; 
            }
            case AB_MODE:
            {
                logger.info(EnumOutputMode.AB_MODE + ": Output result will be written to " + outputPathA);
                Model leftModel = LeftDataset.getLeftDataset().getModel();
                
                Set<String> leftLocalNames = new HashSet<>();
                for(LinkedPair pair : fusedEntities){

                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    leftModel.add(fusedDataModel);
                    String localName = pair.getLeftNode().getLocalName();
                    leftLocalNames.add(localName);                    

                }

                leftModel.write(outputStreamA, fusionSpecification.getOutputRDFFormat());
                
                addUnlinkedTriples(outputPathA, RightDataset.getRightDataset().getFilepath(), leftLocalNames);
                
                addMessageToEmptyOutput(outputPathB);
                addMessageToEmptyOutput(outputPathC);

                break;
            }
            case BA_MODE:
            {
                logger.info(EnumOutputMode.BA_MODE + ": Output result will be written to " + outputPathB);
                Model leftModel = LeftDataset.getLeftDataset().getModel();
                Model rightModel = RightDataset.getRightDataset().getModel();
                
                Set<String> rightLocalNames = new HashSet<>();
                for(LinkedPair pair : fusedEntities){
                    
                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    rightModel.add(fusedDataModel);
                    String localName = pair.getRightNode().getLocalName();
                    rightLocalNames.add(localName);
                }

                leftModel.write(outputStreamB, fusionSpecification.getOutputRDFFormat());   

                addUnlinkedTriples(outputPathB, LeftDataset.getLeftDataset().getFilepath(), rightLocalNames);
                
                addMessageToEmptyOutput(outputPathA);
                addMessageToEmptyOutput(outputPathC);

                break;
            }
            case A_MODE:
            {
                logger.info(EnumOutputMode.A_MODE + ": Output results will be written to " + outputPathA 
                        + " and " + outputPathB + ". Unlinked entities will be excluded from B.");
                
                Model leftModel = LeftDataset.getLeftDataset().getModel();

                Set<String> rightLocalNames = new HashSet<>();
                for(LinkedPair pair : fusedEntities){

                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    leftModel.add(fusedDataModel);
                    String localName = pair.getRightNode().getLocalName();
                    rightLocalNames.add(localName);                    
                    
                }

                leftModel.write(outputStreamA, fusionSpecification.getOutputRDFFormat());
                
                removeUnlinkedTriples(RightDataset.getRightDataset().getFilepath(), rightLocalNames, outputPathB);

                addMessageToEmptyOutput(outputPathC);

                break;
            }
            case B_MODE:
            {
                logger.info(EnumOutputMode.B_MODE + ": Output results will be written to " + outputPathB 
                        + " and " + outputPathA + ". Unlinked entities will be excluded from A.");
                
                Model rightModel = RightDataset.getRightDataset().getModel();
                
                Set<String> leftLocalNames = new HashSet<>();
                for(LinkedPair pair : fusedEntities){

                    Model fusedDataModel = pair.getFusedEntity().getEntityData().getModel();
                    rightModel.add(fusedDataModel);
                    String localName = pair.getLeftNode().getLocalName();
                    leftLocalNames.add(localName);                    
                    
                }

                rightModel.write(outputStreamB, fusionSpecification.getOutputRDFFormat());

                removeUnlinkedTriples(LeftDataset.getLeftDataset().getFilepath(), leftLocalNames, outputPathA);

                addMessageToEmptyOutput(outputPathC);

                break;
            }
            default:
                throw new UnsupportedOperationException("Wrong Output mode!");               
        }
    }
    
    private Entity constructEntity(Model model, String resourceURI, String localName) throws ParseException {
        
        Entity entity = new Entity();
        EntityData entityData = new EntityData(model);
        entity.setResourceURI(resourceURI);
        entity.setLocalName(localName);
        entity.setEntityData(entityData);
        
        return entity;
    }

    //creates a jena rdf model for this node and removes the node from the source dataset
    private Model constructEntityDataModel(String node, Model sourceModel, int depth){

        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct(); //todo - maybe exclude geometry from model

        StmtIterator jenaIterator = model.listStatements();

        //SourceModel.remove(model) does not remove the model from the source for some reason.
        //Also, strange concurrent modification exception when using jena statement iterator. Using list iterator instead.
        List<Statement> stList = jenaIterator.toList();
        Iterator<Statement> stIterator = stList.iterator();
        while ( stIterator.hasNext() ) {
            Statement st = stIterator.next();
            sourceModel.remove(st);
        }         

        return model;
    }

    private String resolveURI(EnumOutputMode mode, String leftURI, String rightURI) {
        String resourceURI;
        switch (mode) {
            
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                resourceURI = leftURI;
                break;                
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                resourceURI = rightURI;
                break;
            default:
                logger.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
        return resourceURI;
    }

    private String resolveLocalName(EnumOutputMode mode, String leftLocalName, String rightLocalName) {
        String localName;
        switch (mode) {
            
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                localName = leftLocalName;
                break;                
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                localName = rightLocalName;
                break;
            default:
                logger.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
        return localName;
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

    private void addUnlinkedTriples(String outputPath, String datasetPath, Set<String> uriSet) throws IOException {

        try (BufferedReader br = Files.newBufferedReader(Paths.get(datasetPath), StandardCharsets.UTF_8); 
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath, true))) {

            for (String line; (line = br.readLine()) != null;) {
                String[] parts = line.split(" ");
                String idPart = parts[0];

                String id = getResourceURI(idPart);

                if(!uriSet.contains(id)){
                    bufferedWriter.append(line);
                    bufferedWriter.newLine();
                }
            }
        }   
    }

    private void removeUnlinkedTriples(String datasetPath, Set<String> localNames, String outputPath) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(datasetPath), StandardCharsets.UTF_8); 
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath, false))) {
            
            for (String line; (line = br.readLine()) != null;) {
                String[] parts = line.split(" ");
                String idPart = parts[0];

                String localName = getResourceURI(idPart);

                if(!localNames.contains(localName)){
                    bufferedWriter.append(line);
                    bufferedWriter.newLine();
                }
            }
        }   
    }
    
    private static String getResourceURI(String part) {
        int endPosition = StringUtils.lastIndexOf(part, "/");
        int startPosition = StringUtils.ordinalIndexOf(part, "/", 5) + 1;
        
        String res;
        if(part.substring(startPosition).contains("/")){
            res = part.subSequence(startPosition, endPosition).toString();
        } else {
            res = part.subSequence(startPosition, part.length()-1).toString();
        }

        return res;
    }
    
    private void addMessageToEmptyOutput(String outputPath) throws IOException{
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath, false))) {
            bufferedWriter.append(SpecificationConstants.EMPTY_OUTPUT_MESSAGE);
        }          
    }
}
