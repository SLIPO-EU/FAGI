package gr.athena.innovation.fagi.quality;

/**
 *
 * @author nkarag
 */
public class MetricSelector {
    
    private String currentMetric;
    private String metricValue;

    public String getCurrentMetric() {
        return currentMetric;
    }

    public void setCurrentMetric(String currentMetric) {
        this.currentMetric = currentMetric;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }

}
