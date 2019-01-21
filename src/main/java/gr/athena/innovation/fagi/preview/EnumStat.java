package gr.athena.innovation.fagi.preview;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for the available statistics.
 * 
 * @author nkarag
 */
public enum EnumStat {

     /**
     * Undefined value of the statistic.
     */
    UNDEFINED("undefined"),

    /**
     * Number of POI entities in each input dataset.
     */
    TOTAL_POIS("totalPois"),
    
    /**
     * Total number of triples in each input dataset.
     */    
    TOTAL_TRIPLES("totalTriples"),
    
    /**
     * Number of non empty names in each input dataset.
     */    
    NON_EMPTY_NAMES("nonEmptyNames"),

    /**
     * Number of non empty phone numbers in each input dataset.
     */    
    NON_EMPTY_PHONES("nonEmptyPhones"),

    /**
     * Number of non empty phone numbers in each input dataset.
     */    
    NON_EMPTY_FAX("nonEmptyFax"),

    /**
     * Number of non empty street addresses in each input dataset.
     */    
    NON_EMPTY_STREETS("nonEmptyStreets"),

    /**
     * Number of non empty address street numbers in each input dataset.
     */    
    NON_EMPTY_STREET_NUMBERS("nonEmptyStreetNumbers"),
    
    /**
     * Number of non empty street addresses in each input dataset.
     */    
    NON_EMPTY_POSTCODE("nonEmptyPostcode"),

    /**
     * Number of non empty web-sites in each input dataset.
     */    
    NON_EMPTY_WEBSITES("nonEmptyWebsites"),
    
    /**
     * Number of non empty e-mails in each input dataset.
     */    
    NON_EMPTY_EMAILS("nonEmptyEmails"),
    
    /**
     * Number of non empty dates in each input dataset.
     */    
    NON_EMPTY_DATES("nonEmptyDates"),
    /**
     * Number of empty names in each input dataset.
     */    
    EMPTY_NAMES("emptyNames"),

    /**
     * Number of empty phone numbers in each input dataset.
     */    
    EMPTY_PHONES("emptyPhones"),

    /**
     * Number of empty street addresses in each input dataset.
     */    
    EMPTY_STREETS("emptyStreets"),

    /**
     * Number of empty address street numbers in each input dataset.
     */    
    EMPTY_STREET_NUMBERS("emptyStreetNumbers"),

    /**
     * Number of empty web-sites in each input dataset.
     */    
    EMPTY_WEBSITES("emptyWebsites"),
    
    /**
     * Number of empty e-mails in each input dataset.
     */    
    EMPTY_EMAILS("emptyEmails"),
    
    /**
     * Number of empty dates in each input dataset.
     */    
    EMPTY_DATES("emptyDates"),
    
    /**
     * Number of distinct properties in each input dataset.
     */    
    DISTINCT_PROPERTIES("distinctProperties"),
    
    /**
     * Percentage of dates that follow the primary format as described in the spec.
     */    
    PRIMARY_DATE_FORMATS_PERCENT("primaryDatesFormatPercent"),
    
    /**
     * Percentage of name property in each input dataset.
     */    
    NAMES_PERCENT("namesPercent"),  
    
    /**
     * Percentage of web-site property in each input dataset.
     */    
    WEBSITE_PERCENT("websitesPercent"),
    
    /**
     * Percentage of e-mail property in each input dataset.
     */    
    EMAIL_PERCENT("emailsPercent"),      

    /**
     * Percentage of phone property in each input dataset.
     */    
    PHONES_PERCENT("phonesPercent"),  

    /**
     * Percentage of street property in each input dataset.
     */    
    STREETS_PERCENT("streetsPercent"),  

    /**
     * Percentage of street number property in each input dataset.
     */    
    STREET_NUMBERS_PERCENT("streetNumbersPercent"),  

    /**
     * Percentage of locality property in each input dataset.
     */    
    LOCALITY_PERCENT("localityPercent"),

    /**
     * Percentage of dates property in each input dataset.
     */    
    DATES_PERCENT("datesPercent"),

    /**
     * Number of linked POIs in each input dataset.
     */    
    LINKED_POIS("linkedPois"),

    /**
     * Number of linked vs total POIS in the datasets.
     */
    LINKED_VS_TOTAL("linkedVsTotal"),

    /**
     * Number of linked triples from input datasets.
     */
    LINKED_TRIPLES("linkedTriples"),

    /**
     * Number of non empty names of linked entities.
     */
    LINKED_NON_EMPTY_NAMES("linkedNonEmptyNames"),

    /**
     * Number of non empty phones of linked entities.
     */
    LINKED_NON_EMPTY_PHONES("linkedNonEmptyPhones"),

    /**
     * Number of non empty streets of linked entities.
     */
    LINKED_NON_EMPTY_STREETS("linkedNonEmptyStreets"),

    /**
     * Number of non empty street numbers of linked entities.
     */
    LINKED_NON_EMPTY_STREET_NUMBERS("linkedNonEmptyStreetNumbers"),

    /**
     * Number of non empty web-sites of linked entities.
     */
    LINKED_NON_EMPTY_WEBSITES("linkedNonEmptyWebsites"),

    /**
     * Number of non empty e-mails of linked entities.
     */
    LINKED_NON_EMPTY_EMAILS("linkedNonEmptyEmails"),

    /**
     * Number of non empty dates of linked entities.
     */
    LINKED_NON_EMPTY_DATES("linkedNonEmptyDates"),
    
    /**
     * Number of empty names of linked entities.
     */
    LINKED_EMPTY_NAMES("linkedEmptyNames"),

    /**
     * Number of empty phones of linked entities.
     */
    LINKED_EMPTY_PHONES("linkedEmptyPhones"),

    /**
     * Number of empty streets of linked entities.
     */
    LINKED_EMPTY_STREETS("linkedEmptyStreets"),

    /**
     * Number of empty street numbers of linked entities.
     */
    LINKED_EMPTY_STREET_NUMBERS("linkedEmptyStreetNumbers"),

    /**
     * Number of empty web-sites of linked entities.
     */
    LINKED_EMPTY_WEBSITES("linkedEmptyWebsites"),

    /**
     * Number of empty e-mails of linked entities.
     */
    LINKED_EMPTY_EMAILS("linkedEmptyEmails"),

    /**
     * Number of empty dates of linked entities.
     */
    LINKED_EMPTY_DATES("linkedEmptyDates"),
    
    /**
     * Total number of non empty properties.
     */
    TOTAL_NON_EMPTY_PROPERTIES("totalNonEmptyProperties"),
    
    /**
     * Total number of empty properties.
     */
    TOTAL_EMPTY_PROPERTIES("totalEmptyProperties"),
    
    /**
     * Percentage of total properties of each input dataset.
     */
    TOTAL_PROPERTIES_PERCENTAGE("totalPropertiesPercentage"),
    
    /**
     * Average properties per POI in each dataset.
     */
    AVERAGE_PROPERTIES_PER_POI("averagePropertiesPerPoi"),
    
    /**
     * Average empty properties per POI in each dataset.
     */
    AVERAGE_EMPTY_PROPERTIES_PER_POI("averageEmptyPropertiesPerPoi"),
    
    /**
     * Average number of properties of linked entities.
     */
    LINKED_AVERAGE_PROPERTIES("averageLinkedProperties"),
    
    /**
     * Average number of empty properties of linked entities.
     */
    LINKED_AVERAGE_EMPTY_PROPERTIES("averageLinkedEmptyProperties"),
    
    /**
     * Number of POI name properties from dataset A that are longer than the names of the corresponding (linked) POIs from dataset B.
     */
    LONGER_NAMES("namesLonger"),
    
    /**
     * Number of POI phone properties from dataset A that are longer than the phones of the corresponding (linked) POIs from dataset B.
     */
    LONGER_PHONES("phonesLonger"),
    
    /**
     * Number of fully matching address streets between linked POIs in the two datasets.
     */
    FULL_MATCH_STREETS("fullMatchingStreets"),
    
    /**
     * Number of fully matching address street numbers between linked POIs in the two datasets.
     */
    FULL_MATCH_STREET_NUMBERS("fullMatchingStreetNumbers"),

    /**
     * Number of fused POIs vs initial links.
     */
    FUSED_VS_LINKED("fusedVsLinked"),
    
    /**
     * Number of rejected POIs vs initial links.
     */
    FUSED_REJECTED_VS_LINKED("fusedRejected"),
    
    /**
     * Number of POIs in the initial datasets and number of POIs in the fused dataset.
     */
    FUSED_INITIAL("fusedInitial"),
    
    /**
     * Number of POIs that have the name-nameValue property in the fused dataset.
     */
    FUSED_NAMES("fusedNames"),

    /**
     * Number of POIs that have the phone-contactValue property in the fused dataset.
     */
    FUSED_PHONES("fusedPhones"),
    
    /**
     * Number of POIs that have the email-contactValue property in the fused dataset.
     */
    FUSED_EMAILS("fusedEmails"),
    
    /**
     * Number of POIs that have the homepage property in the fused dataset.
     */
    FUSED_HOMEPAGE("fusedWebsites"),
    
    /**
     * Number of POIs that have the postcode property in the fused dataset.
     */
    FUSED_POSTCODE("fusedPostcode"),
    
    /**
     * Number of POIs that have the address-street property in the fused dataset.
     */
    FUSED_STREET("fusedStreet"),

    /**
     * Number of POIs that have the address-street number property in the fused dataset.
     */
    FUSED_STREET_NUMBER("fusedStreetNumber"),
    
    /**
     * Number of POIs that have the fax property in the fused dataset.
     */
    FUSED_FAX("fusedFax");

    private final String value;

    private static final Map<String, EnumStat> stringToTypeMap = new HashMap<>();

    static {
        for (EnumStat type : EnumStat.values()) {
            stringToTypeMap.put(type.value, type);
        }
    }

    private EnumStat(String value) {
            this.value = value;
    }

    /**
     * Returns the string value (key) of the statistic.
     * @return The value.
     */
    public String getKey() {
        return this.value;
    }

    /**
     * Returns the mapping of keys to enumStat type.
     * @return The map.
     */
    public static Map<String, EnumStat> getMap() {
	return EnumStat.stringToTypeMap;
    }
    
    /**
     * Returns the EnumStat object from its String value or UNDEFINED if the type does not exist.
     * 
     * @param value The statistic value.
     * @return The statistic.
     */
    public static EnumStat fromString(String value) {
        for (EnumStat item : EnumStat.values()) {
            if (item.toString().equalsIgnoreCase(value)) {
                return item;
            }
        }
        return EnumStat.UNDEFINED;
    }

    /**
     * Deserialization class. 
     */
    public static class Deserializer extends JsonDeserializer<EnumStat> {

        /**
         * Deserializes the EnumStat
         * 
         * @param parser the Json parser.
         * @param context the deserialization context.
         * @return the EnumStat
         * @throws java.io.IOException I/O exception. 
         * @throws com.fasterxml.jackson.core.JsonProcessingException Error with Json processing.
         */
        @Override
        public EnumStat deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            return EnumStat.fromString(parser.getValueAsString());
        }
    }

    @Override
    public String toString() {
        switch(this) {
            case UNDEFINED: return "Could not compute";
            case TOTAL_POIS: return "Number of POI entities in each input dataset.";
            case TOTAL_TRIPLES: return "Total number of triples in each input dataset.";
            case NON_EMPTY_NAMES: return "Total number of POIs that have the name property in each input dataset.";
            case NON_EMPTY_PHONES: return "Total number of POIs that have the phone property in each input dataset.";
            case NON_EMPTY_STREETS: return "Total number of POIs that have the address street property in each input dataset.";
            case NON_EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in each input dataset.";
            case NON_EMPTY_POSTCODE: return "Total number of POIs that have the postcode property in each input dataset.";
            case NON_EMPTY_FAX: return "Total number of POIs that have the fax property in each input dataset.";
            case NON_EMPTY_WEBSITES: return "Total number of POIs that have the website property in each input dataset.";
            case NON_EMPTY_EMAILS: return "Total number of POIs that have the email property in each input dataset.";
            case NON_EMPTY_DATES: return "Total number of POIs that have the date property in each input dataset.";
            case EMPTY_NAMES: return "Total number of POIs that have the name property in each input dataset.";
            case EMPTY_PHONES: return "Total number of POIs that have the phone property in each input dataset.";
            case EMPTY_STREETS: return "Total number of POIs that have the address street property in each input dataset.";
            case EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in each input dataset.";
            case EMPTY_WEBSITES: return "Total number of POIs that have the website property in each input dataset.";
            case EMPTY_EMAILS: return "Total number of POIs that have the email property in each input dataset.";
            case EMPTY_DATES: return "Total number of POIs that have the date property in each input dataset.";
            case DISTINCT_PROPERTIES: return "Number of distinct properties in each input dataset.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Percentage of primary date formats in each input dataset.";
            case NAMES_PERCENT: return "Percentage of name property in each input dataset.";
            case WEBSITE_PERCENT: return "Percentage of website property in each input dataset.";
            case EMAIL_PERCENT: return "Percentage of email property in each input dataset.";
            case PHONES_PERCENT: return "Percentage of phone property in each input dataset.";
            case STREETS_PERCENT: return "Percentage of street property in each input dataset.";
            case STREET_NUMBERS_PERCENT: return "Percentage of street number property in each input dataset.";
            case LOCALITY_PERCENT: return "Percentage of locality property in each input dataset.";
            case DATES_PERCENT: return "Percentage of locality property in each input dataset.";
            case LINKED_POIS: return "Number of linked POIs in each input dataset.";
            case LINKED_VS_TOTAL: return "Number of linked vs total POIs in the datasets.";
            case LINKED_TRIPLES: return "Number of linked triples from each dataset.";
            case LINKED_NON_EMPTY_NAMES: return "Number of linked POIs that have the name property in each input dataset.";
            case LINKED_NON_EMPTY_PHONES: return "Number of linked POIs that have the phone property in each input dataset.";
            case LINKED_NON_EMPTY_STREETS: return "Number of linked POIs that have the street property in each input dataset.";
            case LINKED_NON_EMPTY_STREET_NUMBERS: return "Number of linked POIs that have the street number property in each input dataset.";
            case LINKED_NON_EMPTY_WEBSITES: return "Number of linked POIs that have the website property in each input dataset.";
            case LINKED_NON_EMPTY_EMAILS: return "Number of linked POIs that have the e-mail property in each input dataset.";
            case LINKED_NON_EMPTY_DATES: return "Number of linked POIs that have the date property in each input dataset.";
            case LINKED_EMPTY_NAMES: return "Number of linked POIs that don' t have the name property in each input dataset.";
            case LINKED_EMPTY_PHONES: return "Number of linked POIs that don' t have the phone property in each input dataset.";
            case LINKED_EMPTY_STREETS: return "Number of linked POIs that don' t have the street property in each input dataset.";
            case LINKED_EMPTY_STREET_NUMBERS: return "Number of linked POIs that don' t have the street number property in each input dataset.";
            case LINKED_EMPTY_WEBSITES: return "Number of linked POIs that don' t have the website property in each input dataset.";
            case LINKED_EMPTY_EMAILS: return "Number of linked POIs that don' t have the e-mail property in each input dataset.";
            case LINKED_EMPTY_DATES: return "Number of linked POIs that don' t have the date property in each input dataset.";
            case TOTAL_NON_EMPTY_PROPERTIES: return "Total number of non empty properties in each input dataset.";
            case TOTAL_EMPTY_PROPERTIES: return "Total number of empty properties in each input dataset.";
            case TOTAL_PROPERTIES_PERCENTAGE: return "Percentage of total properties in each input dataset.";
            case AVERAGE_PROPERTIES_PER_POI: return "Average properties per POI in each dataset.";
            case AVERAGE_EMPTY_PROPERTIES_PER_POI: return "Average empty properties per POI in each dataset.";
            case LINKED_AVERAGE_PROPERTIES: return "Average number of properties of linked POIs.";
            case LINKED_AVERAGE_EMPTY_PROPERTIES: return "Average number of empty properties of linked POIs.";
            case LONGER_NAMES: return "Number of name values from dataset A that are longer than the names of the corresponding POIs from dataset B.";
            case LONGER_PHONES: return "Number of phone values from dataset A that are longer than the phones of the corresponding POIs from dataset B.";
            case FULL_MATCH_STREETS: return "Number of fully matching address streets between linked POIs in the two datasets.";
            case FULL_MATCH_STREET_NUMBERS: return "Number of fully matching address street numbers between linked POIs in the two datasets.";
            case FUSED_VS_LINKED: return "Number of fused POIs vs. initial links.";
            case FUSED_REJECTED_VS_LINKED: return "Number of rejected POIs vs. initial links.";
            case FUSED_INITIAL: return "Number of initial POIs vs number of POIs in fused.";
            case FUSED_PHONES: return "Number of POIs with phones in the fused dataset.";
            case FUSED_NAMES: return "Number of POIs with names in the fused dataset.";
            case FUSED_EMAILS: return "Number of POIs with emails in the fused dataset.";
            case FUSED_HOMEPAGE: return "Number of POIs with websites in the fused dataset.";
            case FUSED_POSTCODE: return "Number of POIs with postcode in the fused dataset.";
            case FUSED_STREET: return "Number of POIs with adrees-street in the fused dataset.";
            case FUSED_STREET_NUMBER: return "Number of POIs with street-number in the fused dataset.";
            case FUSED_FAX: return "Number of POIs with fax in the fused dataset.";
            default: return "undefined";
        }
    }

    /**
     * Return the legend that describes a total value.
     * 
     * @return the legend.
     */
    public String getLegendTotal() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Total POIs.";
            case TOTAL_TRIPLES: return "Total Triples.";
            case NON_EMPTY_NAMES: return "Names";
            case NON_EMPTY_PHONES: return "Phones";
            case NON_EMPTY_STREETS: return "Streets";
            case NON_EMPTY_STREET_NUMBERS: return "Street Numbers";
            case NON_EMPTY_POSTCODE: return "Postcode";
            case NON_EMPTY_WEBSITES: return "Websites";
            case NON_EMPTY_EMAILS: return "E-mails";
            case NON_EMPTY_DATES: return "Dates";
            case EMPTY_NAMES: return "Empty Names";
            case EMPTY_PHONES: return "Empty Phones";
            case EMPTY_STREETS: return "Empty Streets";
            case EMPTY_STREET_NUMBERS: return "Empty Street Numbers";
            case EMPTY_WEBSITES: return "Empty Websites";
            case EMPTY_EMAILS: return "Empty E-mails";
            case EMPTY_DATES: return "Empty dates";
            case DISTINCT_PROPERTIES: return "Distinct Properties.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Primary date formats.";
            case NAMES_PERCENT: return "Names";
            case WEBSITE_PERCENT: return "Websites";
            case EMAIL_PERCENT: return "E-mails";
            case PHONES_PERCENT: return "Phones";
            case STREETS_PERCENT: return "Streets";
            case STREET_NUMBERS_PERCENT: return "Street Numbers";
            case LOCALITY_PERCENT: return "Locality";
            case DATES_PERCENT: return "Dates";
            case LINKED_POIS: return "Linked POIs.";
            case LINKED_VS_TOTAL: return "Linked vs total POIs";
            case LINKED_TRIPLES: return "Linked Triples";
            case LINKED_NON_EMPTY_NAMES: return "Linked names";
            case LINKED_NON_EMPTY_PHONES: return "Linked phones";
            case LINKED_NON_EMPTY_STREETS: return "Linked streets";
            case LINKED_NON_EMPTY_STREET_NUMBERS: return "Linked street numbers";
            case LINKED_NON_EMPTY_WEBSITES: return "Linked websites";
            case LINKED_NON_EMPTY_EMAILS: return "Linked e-mails";
            case LINKED_NON_EMPTY_DATES: return "Linked dates";
            case LINKED_EMPTY_NAMES: return "Linked empty names";
            case LINKED_EMPTY_PHONES: return "Linked empty phones";
            case LINKED_EMPTY_STREETS: return "Linked empty streets";
            case LINKED_EMPTY_STREET_NUMBERS: return "Linked empty street numbers";
            case LINKED_EMPTY_WEBSITES: return "Linked empty websites";
            case LINKED_EMPTY_EMAILS: return "Linked empty e-mails";
            case LINKED_EMPTY_DATES: return "Linked empty dates";
            case TOTAL_NON_EMPTY_PROPERTIES: return "Total properties";
            case TOTAL_EMPTY_PROPERTIES: return "Total empty properties";
            case TOTAL_PROPERTIES_PERCENTAGE: return "Total properties";
            case AVERAGE_PROPERTIES_PER_POI: return "Average properties per POI";
            case AVERAGE_EMPTY_PROPERTIES_PER_POI: return "Average empty properties per POI";
            case LINKED_AVERAGE_PROPERTIES: return "Average linked properties";
            case LINKED_AVERAGE_EMPTY_PROPERTIES: return "Average linked empty properties";
            case LONGER_NAMES: return "Longer name values";
            case LONGER_PHONES: return "Longer phone values";
            case FULL_MATCH_STREETS: return "Full matching streets";
            case FULL_MATCH_STREET_NUMBERS: return "Full matching street numbers";
            case FUSED_VS_LINKED: return "Fused vs linked";
            case FUSED_REJECTED_VS_LINKED: return "Rejected vs linked";
            case FUSED_INITIAL: return "Initial vs fused POIs";
            case FUSED_NAMES: return "Initial vs fused name";
            case FUSED_PHONES: return "Initial vs fused phones";
            case FUSED_EMAILS: return "Initial vs fused emails";
            case FUSED_HOMEPAGE: return "Initial vs fused websites";
            case FUSED_POSTCODE: return "Initial vs fused postcodes";
            case FUSED_STREET: return "Initial vs fused address-streets";
            case FUSED_STREET_NUMBER: return "Initial vs fused street-numbers";
            case FUSED_FAX: return "Initial vs fused fax values";
            default: return "undefined";
        }
    }
    
    /**
     * Return the legend of A.
     * 
     * @return the legend.
     */
    public String getLegendA() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Number of POIs in A.";
            case TOTAL_TRIPLES: return "Number of triples in A.";
            case NON_EMPTY_NAMES: return "Total number of POIs that have the name property in dataset A.";
            case NON_EMPTY_PHONES: return "Total number of POIs that have the phone property in dataset A.";
            case NON_EMPTY_STREETS: return "Total number of POIs that have the address street property in dataset A.";
            case NON_EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in dataset A.";
            case NON_EMPTY_WEBSITES: return "Total number of POIs that have the website property in dataset A.";
            case NON_EMPTY_EMAILS: return "Total number of POIs that have the email property in dataset A.";
            case NON_EMPTY_DATES: return "Total number of POIs that have the date property in dataset A.";
            case EMPTY_NAMES: return "Total number of POIs that don' t have the name property in dataset A.";
            case EMPTY_PHONES: return "Total number of POIs that don' t have the phone property in dataset A.";
            case EMPTY_STREETS: return "Total number of POIs that don' t have the address street property in dataset A.";
            case EMPTY_STREET_NUMBERS: return "Total number of POIs that don' t have the address street number property in dataset A.";
            case EMPTY_WEBSITES: return "Total number of POIs that don' t have the website property in dataset A.";
            case EMPTY_EMAILS: return "Total number of POIs that don' t have the email property in dataset A.";
            case EMPTY_DATES: return "Total number of POIs that don' t have the date property in dataset A.";
            case DISTINCT_PROPERTIES: return "Number of distinct properties in dataset A.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Percentage of primary date formats in dataset A.";
            case NAMES_PERCENT: return "Percentage of name property in dataset A.";
            case WEBSITE_PERCENT: return "Percentage of website property in dataset A.";
            case EMAIL_PERCENT: return "Percentage of e-mail property in dataset A.";
            case PHONES_PERCENT: return "Percentage of phone property in dataset A.";
            case STREETS_PERCENT: return "Percentage of streets property in dataset A.";
            case STREET_NUMBERS_PERCENT: return "Percentage of street number property in dataset A.";
            case LOCALITY_PERCENT: return "Percentage of locality property in dataset A.";
            case DATES_PERCENT: return "Percentage of date property in dataset A.";
            case LINKED_POIS: return "Linked POIs from dataset A.";
            case LINKED_VS_TOTAL: return "Linked POIs.";
            case LINKED_TRIPLES: return "Linked triples from A.";
            case LINKED_NON_EMPTY_NAMES: return "Number of linked POIs that have the name property in dataset A.";
            case LINKED_NON_EMPTY_PHONES: return "Number of linked POIs that have the phone property in dataset A.";
            case LINKED_NON_EMPTY_STREETS: return "Number of linked POIs that have the street property in dataset A.";
            case LINKED_NON_EMPTY_STREET_NUMBERS: return "Number of linked POIs that have the street number property in dataset A.";
            case LINKED_NON_EMPTY_WEBSITES: return "Number of linked POIs that have the website property in dataset A.";
            case LINKED_NON_EMPTY_EMAILS: return "Number of linked POIs that have the e-mail property in dataset A.";
            case LINKED_NON_EMPTY_DATES: return "Number of linked POIs that have the date property in dataset A.";
            case LINKED_EMPTY_NAMES: return "Number of linked POIs that don' t have the name property in dataset A.";
            case LINKED_EMPTY_PHONES: return "Number of linked POIs that don' t have the phone property in dataset A.";
            case LINKED_EMPTY_STREETS: return "Number of linked POIs that don' t have the street property in dataset A.";
            case LINKED_EMPTY_STREET_NUMBERS: return "Number of linked POIs that don' t have the street number property in dataset A.";
            case LINKED_EMPTY_WEBSITES: return "Number of linked POIs that don' t have the website property in dataset A.";
            case LINKED_EMPTY_EMAILS: return "Number of linked POIs that don' t have the e-mail property in dataset A.";
            case LINKED_EMPTY_DATES: return "Number of linked POIs that don' t have the date property in dataset A.";
            case TOTAL_NON_EMPTY_PROPERTIES: return "Total number of non empty properties in dataset A.";
            case TOTAL_EMPTY_PROPERTIES: return "Total number of empty properties in dataset A.";
            case TOTAL_PROPERTIES_PERCENTAGE: return "Percentage of total properties in dataset A.";
            case AVERAGE_PROPERTIES_PER_POI: return "Average properties per POI in dataset A.";
            case AVERAGE_EMPTY_PROPERTIES_PER_POI: return "Average properties per POI in dataset A.";
            case LINKED_AVERAGE_PROPERTIES: return "Average linked properties in dataset A.";
            case LINKED_AVERAGE_EMPTY_PROPERTIES: return "Average linked empty properties in dataset A.";
            case LONGER_NAMES: return "Name values from dataset A that are longer than the corresponding values from dataset B.";
            case LONGER_PHONES: return "Phone values from dataset A that are longer than the corresponding values from dataset B.";
            case FULL_MATCH_STREETS: return "Number of fully matching address streets from Dataset A.";
            case FULL_MATCH_STREET_NUMBERS: return "Number of fully matching address street numbers from Dataset A.";
            default: return "undefined";
        }
    }
    
    /**
     * Return the legend of B.
     * 
     * @return the legend.
     */
    public String getLegendB() {
        switch(this) {
            case UNDEFINED: return "undefined";
            case TOTAL_POIS: return "Number of POIs in B.";
            case TOTAL_TRIPLES: return "Number of triples in B.";
            case NON_EMPTY_NAMES: return "Total number of POIs that have the name property in dataset B.";
            case NON_EMPTY_PHONES: return "Total number of POIs that have the phone property in dataset B.";
            case NON_EMPTY_STREETS: return "Total number of POIs that have the address street property in dataset B.";
            case NON_EMPTY_STREET_NUMBERS: return "Total number of POIs that have the address street number property in dataset B.";
            case NON_EMPTY_WEBSITES: return "Total number of POIs that have the website property in dataset B.";
            case NON_EMPTY_EMAILS: return "Total number of POIs that have the email property in dataset B.";
            case NON_EMPTY_DATES: return "Total number of POIs that have the date property in dataset B.";
            case EMPTY_NAMES: return "Total number of POIs that don' t have the name property in dataset B.";
            case EMPTY_PHONES: return "Total number of POIs that don' t have the phone property in dataset B.";
            case EMPTY_STREETS: return "Total number of POIs that don' t have the address street property in dataset B.";
            case EMPTY_STREET_NUMBERS: return "Total number of POIs that don' t have the address street number property in dataset B.";
            case EMPTY_WEBSITES: return "Total number of POIs that don' t have the website property in dataset B.";
            case EMPTY_EMAILS: return "Total number of POIs that don' t have the email property in dataset B.";
            case EMPTY_DATES: return "Total number of POIs that don' t have the date property in dataset B.";
            case DISTINCT_PROPERTIES: return "Number of distinct properties in dataset B.";
            case PRIMARY_DATE_FORMATS_PERCENT: return "Percentage of primary date formats in dataset B.";
            case NAMES_PERCENT: return "Percentage of name property in dataset B.";
            case WEBSITE_PERCENT: return "Percentage of website property in dataset B.";
            case EMAIL_PERCENT: return "Percentage of e-mail property in dataset B.";
            case PHONES_PERCENT: return "Percentage of phone property in dataset B.";
            case STREETS_PERCENT: return "Percentage of street property in dataset B.";
            case STREET_NUMBERS_PERCENT: return "Percentage of street number property in dataset B.";
            case LOCALITY_PERCENT: return "Percentage of locality property in dataset B.";
            case DATES_PERCENT: return "Percentage of dates property in dataset B.";
            case LINKED_POIS: return "Linked POIs from dataset B.";
            case LINKED_VS_TOTAL: return "Total POIs.";
            case LINKED_TRIPLES: return "Linked triples from B.";
            case LINKED_NON_EMPTY_NAMES: return "Number of linked POIs that have the name property in dataset B.";
            case LINKED_NON_EMPTY_PHONES: return "Number of linked POIs that have the phone property in dataset B.";
            case LINKED_NON_EMPTY_STREETS: return "Number of linked POIs that have the street property in dataset B.";
            case LINKED_NON_EMPTY_STREET_NUMBERS: return "Number of linked POIs that have the street number property in dataset B.";
            case LINKED_NON_EMPTY_WEBSITES: return "Number of linked POIs that have the website property in dataset B.";
            case LINKED_NON_EMPTY_EMAILS: return "Number of linked POIs that have the e-mail property in dataset B.";
            case LINKED_NON_EMPTY_DATES: return "Number of linked POIs that have the date property in dataset B.";
            case LINKED_EMPTY_NAMES: return "Number of linked POIs that don' t have the name property in dataset B.";
            case LINKED_EMPTY_PHONES: return "Number of linked POIs that don' t have the phone property in dataset B.";
            case LINKED_EMPTY_STREETS: return "Number of linked POIs that don' t have the street property in dataset B.";
            case LINKED_EMPTY_STREET_NUMBERS: return "Number of linked POIs that don' t have the street number property in dataset B.";
            case LINKED_EMPTY_WEBSITES: return "Number of linked POIs that don' t have the website property in dataset B.";
            case LINKED_EMPTY_EMAILS: return "Number of linked POIs that don' t have the e-mail property in dataset B.";
            case LINKED_EMPTY_DATES: return "Number of linked POIs that don' t have the date property in dataset B.";
            case TOTAL_NON_EMPTY_PROPERTIES: return "Total number of non empty properties in dataset B.";
            case TOTAL_EMPTY_PROPERTIES: return "Total number of empty properties in dataset B.";
            case TOTAL_PROPERTIES_PERCENTAGE: return "Percentage of total properties in dataset B.";
            case AVERAGE_PROPERTIES_PER_POI: return "Average properties per POI in dataset B.";
            case AVERAGE_EMPTY_PROPERTIES_PER_POI: return "Average properties per POI in dataset B.";
            case LINKED_AVERAGE_PROPERTIES: return "Average linked properties in dataset B.";
            case LINKED_AVERAGE_EMPTY_PROPERTIES: return "Average linked empty properties in dataset B.";
            case LONGER_NAMES: return "Total name values of linked POIs.";
            case LONGER_PHONES: return "Total phone values of linked POIs.";
            case FULL_MATCH_STREETS: return "Number of fully matching address streets from Dataset B.";
            case FULL_MATCH_STREET_NUMBERS: return "Number of fully matching address street numbers from Dataset B.";
            default: return "undefined";
        }
    }      
}
