package gr.athena.innovation.fagi.preview;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.Metadata;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.quality.MetricSelector;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class QualityViewer {

    private static final Logger logger = LogManager.getLogger(QualityViewer.class);
    private final List<InterlinkedPair> interlinkedEntitiesList;
    private int pairsNotFound = 0;
    private int pairsChecked = 0;
    private final MetricSelector metricSelector;
    private final RuleCatalog ruleCatalog;
    private final FusionSpecification fusionSpecification;

    public QualityViewer(List<InterlinkedPair> interlinkedEntitiesList, RuleCatalog ruleCatalog, 
            MetricSelector metricSelector, FusionSpecification fusionSpecification) {
        
        this.interlinkedEntitiesList = interlinkedEntitiesList;
        this.metricSelector = metricSelector;
        this.ruleCatalog = ruleCatalog;
        this.fusionSpecification = fusionSpecification;
    }

    public void printSimilarityResults(List<String> rdfProperties) throws ParseException, IOException{

        for(String rdfProperty : rdfProperties){
            String propertyPath = fusionSpecification.getPathOutput() + "_" + rdfProperty;
            
            computeQualityOnProperty(rdfProperty, propertyPath);
        }

        setPairsNotFound(pairsNotFound);
    }

    public String computeProperty(String path, String property, InterlinkedPair pair) throws ParseException, IOException{
        String literalA = getLiteralValue(property, pair.getLeftNode().getMetadata().getModel());
        String literalB = getLiteralValue(property, pair.getRightNode().getMetadata().getModel());
        logger.info("Literals: {}, {}", literalA, literalB);
        return "";
    }

    private Entity constructEntity(Model model, String resourceURI) throws ParseException {
        
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
        return ResourceFactory.createProperty(property);
    }

    public void setPairsNotFound(int pairsNotFound) {
        this.pairsNotFound = pairsNotFound;
    }    

    private void computeQualityOnProperty(String rdfProperty, String propertyPath) throws ParseException, IOException {

        pairsNotFound = 0;

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        BufferedWriter output = new BufferedWriter(new FileWriter(propertyPath, true));

        for (Link link : links.getLinks()){

            Model modelA = constructEntityMetadataModel(link.getNodeA(), left, fusionSpecification.getOptionalDepth());
            Model modelB = constructEntityMetadataModel(link.getNodeB(), right, fusionSpecification.getOptionalDepth());

            if(modelA.size() == 0 || modelB.size() == 0){  //one of the two entities not found in dataset, skip iteration.
                pairsNotFound++;
                continue;
            }

            InterlinkedPair pair = new InterlinkedPair();

            Entity entityA = constructEntity(modelA, link.getNodeA());
            Entity entityB = constructEntity(modelB, link.getNodeB());

            pair.setLeftNode(entityA);
            pair.setRightNode(entityB);
            //Levenshtein.computeDistance(entityA., path, pairsChecked)

            pairsChecked++;
            interlinkedEntitiesList.add(pair);
            
            
            String line = entityA.getResourceURI() + " " +  entityB.getResourceURI() + 
                        rdfProperty + metricSelector.getCurrentMetric() + ": " + metricSelector.getMetricValue();

            output.append(line);
            output.newLine();
        }
    }
}
