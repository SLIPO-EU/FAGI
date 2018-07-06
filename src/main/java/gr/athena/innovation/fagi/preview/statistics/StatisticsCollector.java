package gr.athena.innovation.fagi.preview.statistics;

import java.util.List;

/**
 *
 * @author nkarag
 */
public interface StatisticsCollector {
    
    public StatisticsContainer collect();
    
    public StatisticsContainer collect(List<String> selected);
    
}
