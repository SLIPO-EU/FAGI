package gr.athena.innovation.fagi.preview;

import com.google.gson.Gson;

/**
 *
 * @author nkarag
 */
public class StatisticsContainer {
    
    private boolean valid;
    
    private StatisticResultPair totalPOIs;
    private StatisticResultPair totalTriples;
    
    private StatisticResultPair nonEmptyNames;
    private StatisticResultPair nonEmptyPhones;
    private StatisticResultPair nonEmptyStreets;
    private StatisticResultPair nonEmptyStreetNumbers;
    private StatisticResultPair nonEmptyWebsites;
    private StatisticResultPair nonEmptyEmails;
    private StatisticResultPair nonEmptyDates;
    
    private StatisticResultPair distinctProperties;
    private StatisticResultPair percentageOfDateKnownFormats;
    private StatisticResultPair namePercentage;
    private StatisticResultPair websitePercentage;
    private StatisticResultPair phonePercentage;
    private StatisticResultPair streetPercentage;
    private StatisticResultPair streetNumberPercentage;
    private StatisticResultPair localityPercentage;
    private StatisticResultPair nonEmptyTotalProperties;

    @Override
    public String toString() {

        return "StatisticsContainer{" 
                + "totalPOIs=" + totalPOIs 
                + ", totalTriples=" + totalTriples 
                + ", nonEmptyNames=" + nonEmptyNames 
                + ", nonEmptyPhones=" + nonEmptyPhones 
                + ", nonEmptyStreets=" + nonEmptyStreets 
                + ", nonEmptyStreetNumbers=" + nonEmptyStreetNumbers 
                + ", nonEmptyWebsites=" + nonEmptyWebsites 
                + ", nonEmptyEmails=" + nonEmptyEmails 
                + ", nonEmptyDates=" + nonEmptyDates 
                + ", distinctProperties=" + distinctProperties 
                + ", percentageOfDateKnownFormats=" + percentageOfDateKnownFormats 
                + ", namePercentage=" + namePercentage 
                + ", websitePercentage=" + websitePercentage 
                + ", phonePercentage=" + phonePercentage 
                + ", streetPercentage=" + streetPercentage 
                + ", streetNumberPercentage=" + streetNumberPercentage 
                + ", localityPercentage=" + localityPercentage 
                + ", nonEmptyTotalProperties=" + nonEmptyTotalProperties + '}';
    }

    public String toJson() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;        
    }

    public StatisticResultPair getTotalPOIs() {
        return totalPOIs;
    }

    public void setTotalPOIs(StatisticResultPair totalPOIs) {
        this.totalPOIs = totalPOIs;
    }
    
    public StatisticResultPair getTotalTriples() {
        return totalTriples;
    }

    public void setTotalTriples(StatisticResultPair totalTriples) {
        this.totalTriples = totalTriples;
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

    public StatisticResultPair getNonEmptyNames() {
        return nonEmptyNames;
    }

    public void setNonEmptyNames(StatisticResultPair nonEmptyNames) {
        this.nonEmptyNames = nonEmptyNames;
    }

    public StatisticResultPair getNonEmptyPhones() {
        return nonEmptyPhones;
    }

    public void setNonEmptyPhones(StatisticResultPair nonEmptyPhones) {
        this.nonEmptyPhones = nonEmptyPhones;
    }

    public StatisticResultPair getNonEmptyStreets() {
        return nonEmptyStreets;
    }

    public void setNonEmptyStreets(StatisticResultPair nonEmptyStreets) {
        this.nonEmptyStreets = nonEmptyStreets;
    }

    public StatisticResultPair getNonEmptyStreetNumbers() {
        return nonEmptyStreetNumbers;
    }

    public void setNonEmptyStreetNumbers(StatisticResultPair nonEmptyStreetNumbers) {
        this.nonEmptyStreetNumbers = nonEmptyStreetNumbers;
    }

    public StatisticResultPair getNonEmptyWebsites() {
        return nonEmptyWebsites;
    }

    public void setNonEmptyWebsites(StatisticResultPair nonEmptyWebsites) {
        this.nonEmptyWebsites = nonEmptyWebsites;
    }

    public StatisticResultPair getNonEmptyEmails() {
        return nonEmptyEmails;
    }

    public void setNonEmptyEmails(StatisticResultPair nonEmptyEmails) {
        this.nonEmptyEmails = nonEmptyEmails;
    }
}
