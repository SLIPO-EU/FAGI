package gr.athena.innovation.fagi.preview;

/**
 *
 * @author nkarag
 */
public class StatisticsContainer {
    
    private int distinctProperties;
    private int categoryFrequency;
    
    public int getDistinctProperties() {
        return distinctProperties;
    }

    public void setDistinctProperties(int distinctProperties) {
        this.distinctProperties = distinctProperties;
    }

    public int getCategoryFrequency() {
        return categoryFrequency;
    }

    public void setCategoryFrequency(int categoryFrequency) {
        this.categoryFrequency = categoryFrequency;
    }
}
