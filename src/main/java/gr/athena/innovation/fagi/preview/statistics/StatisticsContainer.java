package gr.athena.innovation.fagi.preview.statistics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class StatisticsContainer {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(StatisticsContainer.class);
    
    @JsonIgnore
    private transient boolean valid;
    
    private StatisticResultPair totalPOIs;
    private StatisticResultPair totalTriples;
    
    private StatisticResultPair nonEmptyNames;
    private StatisticResultPair nonEmptyPhones;
    private StatisticResultPair nonEmptyStreets;
    private StatisticResultPair nonEmptyStreetNumbers;
    private StatisticResultPair nonEmptyWebsites;
    private StatisticResultPair nonEmptyEmails;
    private StatisticResultPair nonEmptyDates;
    private StatisticResultPair totalNonEmptyProperties;
    
    private StatisticResultPair emptyNames;
    private StatisticResultPair emptyPhones;
    private StatisticResultPair emptyStreets;
    private StatisticResultPair emptyStreetNumbers;
    private StatisticResultPair emptyWebsites;
    private StatisticResultPair emptyEmails;
    private StatisticResultPair emptyDates;
    private StatisticResultPair totalEmptyProperties;
    
    private StatisticResultPair linkedNonEmptyNames;
    
    private StatisticResultPair distinctProperties;
    private StatisticResultPair percentageOfDateKnownFormats;
    private StatisticResultPair namePercentage;
    private StatisticResultPair websitePercentage;
    private StatisticResultPair phonePercentage;
    private StatisticResultPair streetPercentage;
    private StatisticResultPair streetNumberPercentage;
    private StatisticResultPair localityPercentage;
    private StatisticResultPair datePercentage;
    private StatisticResultPair percentNonEmptyTotalProperties;
    
    private StatisticResultPair linkedVsUnlinked;

    @Override
    public String toString() {
        return "StatisticsContainer{" 
                + "valid=" + valid + ", totalPOIs=" + totalPOIs + ", totalTriples=" + totalTriples 
                + ", nonEmptyNames=" + nonEmptyNames + ", nonEmptyPhones=" + nonEmptyPhones + ", nonEmptyStreets=" 
                + nonEmptyStreets + ", nonEmptyStreetNumbers=" + nonEmptyStreetNumbers + ", nonEmptyWebsites=" 
                + nonEmptyWebsites + ", nonEmptyEmails=" + nonEmptyEmails + ", nonEmptyDates=" + nonEmptyDates 
                + ", totalNonEmptyProperties=" + totalNonEmptyProperties + ", emptyNames=" + emptyNames 
                + ", emptyPhones=" + emptyPhones + ", emptyStreets=" + emptyStreets + ", emptyStreetNumbers=" 
                + emptyStreetNumbers + ", emptyWebsites=" + emptyWebsites + ", emptyEmails=" + emptyEmails 
                + ", emptyDates=" + emptyDates + ", totalEmptyProperties=" + totalEmptyProperties 
                + ", distinctProperties=" + distinctProperties + ", percentageOfDateKnownFormats=" 
                + percentageOfDateKnownFormats + ", namePercentage=" + namePercentage + ", websitePercentage=" 
                + websitePercentage + ", phonePercentage=" + phonePercentage + ", streetPercentage=" 
                + streetPercentage + ", streetNumberPercentage=" + streetNumberPercentage + ", localityPercentage=" 
                + localityPercentage + ", datePercentage=" + datePercentage + ", nonEmptyTotalProperties=" 
                + percentNonEmptyTotalProperties + '}';
    }

    public String toJson() {
        
        String formattedJson = null;
        
        try {
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            
            String originalJson = objectMapper.writeValueAsString(this);

            JsonNode tree = objectMapper.readTree(originalJson);
            formattedJson = objectMapper.writeValueAsString(tree);

        } catch (IOException ex) {
            LOG.error(ex);
            throw new ApplicationException("Json serialization failed.");
        }
        
        return formattedJson;
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

    public StatisticResultPair getDatePercentage() {
        return datePercentage;
    }

    public void setDatePercentage(StatisticResultPair datePercentage) {
        this.datePercentage = datePercentage;
    }
    
    public StatisticResultPair getPercentNonEmptyTotalProperties() {
        return percentNonEmptyTotalProperties;
    }

    public void setPercentNonEmptyTotalProperties(StatisticResultPair percentNonEmptyTotalProperties) {
        this.percentNonEmptyTotalProperties = percentNonEmptyTotalProperties;
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

    public StatisticResultPair getEmptyNames() {
        return emptyNames;
    }

    public void setEmptyNames(StatisticResultPair emptyNames) {
        this.emptyNames = emptyNames;
    }

    public StatisticResultPair getEmptyPhones() {
        return emptyPhones;
    }

    public void setEmptyPhones(StatisticResultPair emptyPhones) {
        this.emptyPhones = emptyPhones;
    }

    public StatisticResultPair getEmptyStreets() {
        return emptyStreets;
    }

    public void setEmptyStreets(StatisticResultPair emptyStreets) {
        this.emptyStreets = emptyStreets;
    }

    public StatisticResultPair getEmptyStreetNumbers() {
        return emptyStreetNumbers;
    }

    public void setEmptyStreetNumbers(StatisticResultPair emptyStreetNumbers) {
        this.emptyStreetNumbers = emptyStreetNumbers;
    }

    public StatisticResultPair getEmptyWebsites() {
        return emptyWebsites;
    }

    public void setEmptyWebsites(StatisticResultPair emptyWebsites) {
        this.emptyWebsites = emptyWebsites;
    }

    public StatisticResultPair getEmptyEmails() {
        return emptyEmails;
    }

    public void setEmptyEmails(StatisticResultPair emptyEmails) {
        this.emptyEmails = emptyEmails;
    }

    public StatisticResultPair getEmptyDates() {
        return emptyDates;
    }

    public void setEmptyDates(StatisticResultPair emptyDates) {
        this.emptyDates = emptyDates;
    }

    public StatisticResultPair getTotalNonEmptyProperties() {
        return totalNonEmptyProperties;
    }

    public void setTotalNonEmptyProperties(StatisticResultPair totalNonEmptyProperties) {
        this.totalNonEmptyProperties = totalNonEmptyProperties;
    }

    public StatisticResultPair getTotalEmptyProperties() {
        return totalEmptyProperties;
    }

    public void setTotalEmptyProperties(StatisticResultPair totalEmptyProperties) {
        this.totalEmptyProperties = totalEmptyProperties;
    }

    public StatisticResultPair getLinkedVsUnlinked() {
        return linkedVsUnlinked;
    }

    public void setLinkedVsUnlinked(StatisticResultPair linkedVsUnlinked) {
        this.linkedVsUnlinked = linkedVsUnlinked;
    }

    public StatisticResultPair getLinkedNonEmptyNames() {
        return linkedNonEmptyNames;
    }

    public void setLinkedNonEmptyNames(StatisticResultPair linkedNonEmptyNames) {
        this.linkedNonEmptyNames = linkedNonEmptyNames;
    }
}
