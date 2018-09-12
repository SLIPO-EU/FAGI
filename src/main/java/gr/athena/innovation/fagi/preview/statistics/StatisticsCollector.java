package gr.athena.innovation.fagi.preview.statistics;

import java.util.List;

/**
 * Interface for the statistics collector.
 * 
 * @author nkarag
 */
public interface StatisticsCollector {
    
    /**
     * Returns a statistics container with the computed statistics.
     * 
     * @return the statistics container.
     */
    public StatisticsContainer collect();

    /**
     * Returns a statistics container with the selected statistics.
     * 
     * @param selected the selected statistics.
     * @return the statistics container.
     */
    public StatisticsContainer collect(List<String> selected);
    
}
