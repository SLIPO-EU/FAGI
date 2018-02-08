package gr.athena.innovation.fagi.preview;

/**
 *
 * @author nkarag
 */
public class StatisticsContainer {
    
    private StatisticResultPair distinctProperties;
    private StatisticResultPair nonEmptyDates;
    private StatisticResultPair percentageOfDateKnownFormats;

    @Override
    public String toString() {
        return "\n" + distinctProperties+ "\n" + nonEmptyDates + "\n" + percentageOfDateKnownFormats;
    }

    public StatisticResultPair getDistinctProperties() {
        return distinctProperties;
    }

    public void setDistinctProperties(StatisticResultPair distinctProperties) {
        this.distinctProperties = distinctProperties;
    }

    public StatisticResultPair getPercentageOfDateKnownFormats() {
        return percentageOfDateKnownFormats;
    }

    public void setPercentageOfDateKnownFormats(StatisticResultPair percentageOfDateKnownFormats) {
        this.percentageOfDateKnownFormats = percentageOfDateKnownFormats;
    }

    public StatisticResultPair getNonEmptyDates() {
        return nonEmptyDates;
    }

    public void setNonEmptyDates(StatisticResultPair nonEmptyDates) {
        this.nonEmptyDates = nonEmptyDates;
    }

}
