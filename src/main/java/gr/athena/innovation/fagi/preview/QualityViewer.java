package gr.athena.innovation.fagi.preview;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.similarity.Levenshtein;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.Metadata;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.quality.MetricSelector;
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

    public QualityViewer(List<InterlinkedPair> interlinkedEntitiesList) {
        this.interlinkedEntitiesList = interlinkedEntitiesList;
    }

    public void printResults(String path, FusionSpecification fusionSpecification, 
            RuleCatalog ruleCatalog, MetricSelector metricSelector) throws ParseException, IOException{

        pairsNotFound = 0;

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        BufferedWriter output = new BufferedWriter(new FileWriter(path, true));

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

            output.append(entityA.getResourceURI() + " " +  entityB.getResourceURI() 
                    + metricSelector.getCurrentMetric() + ": " + metricSelector.getMetricValue());
            output.newLine();

        }
        setPairsNotFound(pairsNotFound);
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
    
    public void setPairsNotFound(int pairsNotFound) {
        this.pairsNotFound = pairsNotFound;
    }    
}
