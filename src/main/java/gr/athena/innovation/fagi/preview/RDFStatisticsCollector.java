package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class RDFStatisticsCollector implements StatisticsCollector{

    private static final Logger logger = LogManager.getLogger(RDFStatisticsCollector.class);

    @Override
    public StatisticsContainer collect(){
        
        StatisticsContainer container = new StatisticsContainer();
        
        int distinctProperties = countDistinctProperties();
        //int frequentCategoryValues = countCategoryFrequency();
        
        container.setDistinctProperties(distinctProperties);
        //container.setCategoryFrequency(frequentCategoryValues);

        return container;
    }

    private int countDistinctProperties(){
        int distinctProperties = SparqlRepository.countDistinctProperties(LeftModel.getLeftModel().getModel());
        return distinctProperties;
    }
}
