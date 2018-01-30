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
    public void collect(){
        countDistinctProperties();
    }
    
    private void countDistinctProperties(){
        logger.debug("counting..");
        int la = SparqlRepository.countDistinctPRoperties(LeftModel.getLeftModel().getModel());
    }
    
}
