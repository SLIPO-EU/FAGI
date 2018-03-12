package gr.athena.innovation.fagi.preview;

/**
 *
 * @author nkarag
 */
public class StatisticsContainer {
    
    private boolean valid;
    
    private StatisticResultPair distinctProperties;
    private StatisticResultPair nonEmptyDates;
    private StatisticResultPair percentageOfDateKnownFormats;
    private StatisticResultPair totalEntities;
    private StatisticResultPair namePercentage;
    private StatisticResultPair websitePercentage;
    private StatisticResultPair phonePercentage;
    private StatisticResultPair streetPercentage;
    private StatisticResultPair streetNumberPercentage;
    private StatisticResultPair localityPercentage;
    private StatisticResultPair nonEmptyTotalProperties;

    @Override
    public String toString() {
        return "\n" + distinctProperties + "\n" + nonEmptyDates + "\n" + percentageOfDateKnownFormats + 
               "\n" + totalEntities + "\n" + namePercentage + "\n" + websitePercentage + "\n" + phonePercentage + 
               "\n" + streetPercentage + "\n" + streetNumberPercentage + "\n" + localityPercentage + 
               "\n" + nonEmptyTotalProperties;
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

    public StatisticResultPair getTotalEntities() {
        return totalEntities;
    }

    public void setTotalEntities(StatisticResultPair totalEntities) {
        this.totalEntities = totalEntities;
    }

    public StatisticResultPair getNamePercentage() {
        return namePercentage;
    }

    public void setNamePercentage(StatisticResultPair namePercentage) {
        this.namePercentage = namePercentage;
    }

    public StatisticResultPair getWebsitePercentage() {
        return websitePercentage;
    }

    public void setWebsitePercentage(StatisticResultPair websitePercentage) {
        this.websitePercentage = websitePercentage;
    }

    public StatisticResultPair getPhonePercentage() {
        return phonePercentage;
    }

    public void setPhonePercentage(StatisticResultPair phonePercentage) {
        this.phonePercentage = phonePercentage;
    }

    public StatisticResultPair getStreetPercentage() {
        return streetPercentage;
    }

    public void setStreetPercentage(StatisticResultPair streetPercentage) {
        this.streetPercentage = streetPercentage;
    }

    public StatisticResultPair getStreetNumberPercentage() {
        return streetNumberPercentage;
    }

    public void setStreetNumberPercentage(StatisticResultPair streetNumberPercentage) {
        this.streetNumberPercentage = streetNumberPercentage;
    }

    public StatisticResultPair getLocalityPercentage() {
        return localityPercentage;
    }

    public void setLocalityPercentage(StatisticResultPair localityPercentage) {
        this.localityPercentage = localityPercentage;
    }

    public StatisticResultPair getNonEmptyTotalProperties() {
        return nonEmptyTotalProperties;
    }

    public void setNonEmptyTotalProperties(StatisticResultPair nonEmptyTotalProperties) {
        this.nonEmptyTotalProperties = nonEmptyTotalProperties;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
