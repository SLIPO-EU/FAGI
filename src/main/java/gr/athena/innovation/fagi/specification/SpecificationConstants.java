package gr.athena.innovation.fagi.specification;

import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class with the specification constants used across the application.
 * 
 * @author nkarag
 */
public class SpecificationConstants {
    
    /**
     * Connector/separator for building strings in FAGI.
     */
    public static final int CONNECTOR = ' ';

    /**
     * Help message.
     */
    public static final String HELP = "Usage:\n java -jar fagi.jar -spec <configFile>\n"
                                    + "-spec requires the config.xml file path\n";
    
    /**
     * Configuration class groups anything that has to do with the XML configuration constants.
     */
    public static class Config {

        /**
         * Filename for the rules XML specification.
         */
        public static final String RULES_XML = "rules.xml";

        /**
         * Filename for the rules XSD that describes the rules XML file.
         */
        public static final String RULES_XSD = "rules.xsd";

        /**
         * Filename for the configuration XML file.
         */
        public static final String CONFIG_XML = "config.xml";

        /**
         * Filename for the configuration XSD file that describes the configuration XML file.
         */
        public static final String CONFIG_XSD = "config.xsd";

        /**
         * Name for input format tag in XML.
         */
        public static final String INPUT_FORMAT = "inputFormat";

        /**
         * Name for output format tag in XML.
         */
        public static final String OUTPUT_FORMAT = "outputFormat";

        /**
         * Name for locale tag in XML.
         */
        public static final String LOCALE = "locale";

        /**
         * Name for similarity tag in XML.
         */
        public static final String SIMILARITY = "similarity";

        /**
         * Name for rules (file-path) tag in XML. 
         */
        public static final String RULES = "rules";

        /**
         * Name for left dataset tag in XML.
         */
        public static final String LEFT_DATASET = "left";

        /**
         * Name for right dataset tag in XML.
         */
        public static final String RIGHT_DATASET = "right";

        /**
         * Name for target tag in XML.
         */
        public static final String TARGET = "target";

        /**
         * Name for mode tag in XML.
         */
        public static final String MODE = "mode";

        /**
         * Name for links tag in XML.
         */
        public static final String LINKS = "links";

        /**
         * Name for categories tag in XML.
         */
        public static final String CATEGORIES = "categories";

        /**
         * Name for date tag in XML.
         */
        public static final String DATE = "date";

        /**
         * Name for date format tag in XML.
         */
        public static final String DATE_FORMAT = "yyyy-MM-dd";

        /**
         * Name for id tag in XML.
         */
        public static final String ID = "id";

        /**
         * Name for file tag in XML.
         */
        public static final String FILE = "file";

        /**
         * Name for endpoint tag in XML.
         */
        public static final String ENDPOINT = "endpoint";

        /**
         * Name for output directory tag in XML.
         */
        public static final String OUTPUT_DIR = "outputDir";
        
        /**
         * Name for fused tag in XML.
         */
        public static final String FUSED = "fused";

        /**
         * Name for remaining tag in XML.
         */
        public static final String REMAINING = "remaining";

        /**
         * Name for ambiguous tag in XML.
         */
        public static final String AMBIGUOUS = "ambiguous";
        
        /**
         * Name for default fused filename.
         */
        public static final String DEFAULT_FUSED_FILENAME = "fused.nt";

        /**
         * Name for default remaining filename.
         */
        public static final String DEFAULT_REMAINING_FILENAME = "remaining.nt";
        
        /**
         * Name for statistics tag in XML.
         */
        public static final String STATISTICS = "statistics";
        
        /**
         * Name for default ambiguous filename.
         */
        public static final String DEFAULT_AMBIGUOUS_FILENAME = "ambiguous.nt";

        /**
         * Name for default statistics filename.
         */
        public static final String DEFAULT_STATS_FILENAME = "stats.json";

        /**
         * Suffix for naming frequency-related files for dataset A.
         */
        public static final String FREQ_SUFFIX_A = ".a.freq.txt";

        /**
         * Suffix for naming frequency-related files for dataset B.
         */
        public static final String FREQ_SUFFIX_B = ".b.freq.txt";
        
        /**
         * Filename for extracted features CSV file.
         */
        public static final String FEATURES_CSV = "features_export.csv";
        
    }  

    /**
     * Rule class groups anything that has to do with the XML rule specification constants.
     */
    public static class Rule {

        /**
         * Name for rule tag in XML.
         */
        public static final String RULE = "rule";

        /**
         * Name for validation rule tag in XML.
         */
        public static final String VALIDATION_RULE = "validationRule";

        /**
         * Name for default dataset action tag in XML.
         */
        public static final String DEFAULT_DATASET_ACTION = "defaultDatasetAction";

        /**
         * Name for default action tag in XML.
         */
        public static final String DEFAULT_ACTION = "defaultAction";

        /**
         * Name for property of dataset A tag in XML.
         */
        public static final String PROPERTY_A = "propertyA";

        /**
         * Name for property B tag in XML.
         */
        public static final String PROPERTY_B = "propertyB";

        /**
         * Name for external property tag in XML.
         */
        public static final String EXTERNAL_PROPERTY = "externalProperty";

        /**
         * Name for condition tag in XML.
         */
        public static final String CONDITION = "condition";

        /**
         * Name for action rule set tag in XML.
         */
        public static final String ACTION_RULE_SET = "actionRuleSet";

        /**
         * Name for action rule tag in XML.
         */
        public static final String ACTION_RULE = "actionRule";

        /**
         * Name for action tag in XML.
         */
        public static final String ACTION = "action";

        /**
         * Name for expression tag in XML.
         */
        public static final String EXPRESSION = "expression";

        /**
         * Name for function tag in XML.
         */
        public static final String FUNCTION = "function";

        /**
         * Name for AND operation tag in XML.
         */
        public static final String AND = "and";

        /**
         * Name for OR operation tag in XML.
         */
        public static final String OR = "or";

        /**
         * Name for NOT operation tag in XML.
         */
        public static final String NOT = "not";

        /**
         * Name indicating parameter from dataset a (left).
         */
        public static final String A = "a";

        /**
         * Name indicating parameter from dataset b (right).
         */
        public static final String B = "b";

        /**
         * Concatenation separator.
         */
        public static final String CONCATENATION_SEP = ", ";
    }  

    /**
     * Property mapping for constructing nodes.
     */
    public static class Mapping {
        
        public static final Map<String, String> PROPERTY_MAPPINGS;
        static {
            Map<String, String> map = new HashMap<>();
            map.put("http://slipo.eu/def#openingHours", "timeSlot");
            map.put("http://slipo.eu/def#fax", "fax");
            map.put("http://slipo.eu/def#address", "address");
            map.put("http://slipo.eu/def#phone", "phone");
            map.put("http://slipo.eu/def#email", "email");
            map.put("http://www.opengis.net/ont/geosparql#hasGeometry", "geom");
            map.put("http://slipo.eu/def#name", "name");
            map.put("http://slipo.eu/def#source", "sourceInfo");
            PROPERTY_MAPPINGS = Collections.unmodifiableMap(map);
        }
    }

    /**
     * Similarity class groups anything that has to do with similarity constants.
     */
    public static class Similarity {

        /**
         * Defines the strength of the collator. Collator is used to compare words.
         */
        public static final int COLLATOR_STRENGTH = Collator.IDENTICAL;

        /**
         * Similarity accepted error.
         */
        public static final double SIMILARITY_ACCEPTED_ERROR = 0.05;

        /**
         * Similarity score is considered 1 if it is greater than this value. 
         */
        public static final double SIMILARITY_MAX = 0.999;

        /**
         * Similarity score is considered 0 if it is less than this value. 
         */
        public static final double SIMILARITY_MIN = 0.001;

        /**
         * 5 decimal digits rounding.
         */
        public static final int ROUND_DECIMALS_5 = 5;

        /**
         * 3 decimal digits rounding.
         */
        public static final int ROUND_DECIMALS_3 = 3;

        /**
         * 2 decimal digits rounding.
         */
        public static final int ROUND_DECIMALS_2 = 2;
        
        /**
         * Name for Cosine metric.
         */
        public static final String COSINE = "cosine";

        /**
         * Name for Jaccard metric.
         */
        public static final String JACCARD = "jaccard";

        /**
         * Name for Levenshtein metric.
         */
        public static final String LEVENSHTEIN = "levenshtein";

        /**
         * Name for Jaro metric.
         */
        public static final String JARO = "jaro";

        /**
         * Name for Jaro-Winkler metric.
         */
        public static final String JARO_WINKLER = "jarowinkler";

        /**
         * Name for Sorted Jaro-Winkler metric.
         */
        public static final String SORTED_JARO_WINKLER = "sortedjarowinkler";

        /**
         * Name for longest common subsequence metric.
         */
        public static final String LCS = "longestcommonsubsequence";

        /**
         * Name for 2-gram metric.
         */
        public static final String GRAM_2 = "2Gram";
    
    }  

    /**
     * Evaluation class groups anything that has to do with constants used in the evaluation process.
     */
    public static class Evaluation {

        /**
         * Default mismatch threshold.
         */
        public static final double MISMATCH_THRESHOLD = 0.75;

        /**
         * Default base weight.
         */
        public static Double BASE_WEIGHT = 0.7;

        /**
         * Default mismatch weight.
         */
        public static Double MISMATCH_WEIGHT = 0.3;

        /**
         * Sum of base and mismatch weights.
         */
        public static Double MERGED_BASE_MISMATCH_WEIGHT = 1.0;

        /**
         * Special terms weight.
         */
        public static Double SPECIAL_TERMS_WEIGHT = 0.0;

        /**
         * Common special terms weight.
         */
        public static Double COMMON_SPECIAL_TERM_WEIGHT = 0.0;

        /**
         * Lower case vowels. Used for checking the presence of vowels in words.
         */
        public static final String LOWERCASE_VOWELS = "aeiouäöü";
    
    }  

    /**
     * Stat class groups anything that has to do with statistic constants.
     */
    public static class Stats {

        /**
         * Delimeter used in the statistic process.
         */
        public static final String DELIMETER = ".";

        /**
         * Names property constant.
         */
        public static final String NAMES = "names";

        /**
         * Phones property constant.
         */
        public static final String PHONES = "phones";

        /**
         * Empty property constant.
         */
        public static final String EMPTY = "empty";

        /**
         * Non-empty property constant.
         */
        public static final String NON_EMPTY = "nonEmpty";

        /**
         * Percent constant.
         */
        public static final String PERCENT = "percent";

        /**
         * Total constant.
         */
        public static final String TOTAL = "total";

        /**
         * Name for input dataset A (left).
         */
        public static final String INPUT_A = "dataset_a";

        /**
         * Name for input dataset B (right).
         */
        public static final String INPUT_B = "dataset_b";
    
    } 
    
    /**
     * Regex class contains values of all regexes used.
     */
    public static class Regex {

        //v0.1

        /**
         * Regex matching abbreviations 1.
         */
        public static final String ABBR_REGEX = "\\b(?:[A-Z][a-z]*){2,}";

        /**
         * Regex matching abbreviations 2.
         */
        public static final String ABBR_REGEX2 = "((?:[A-Z]\\.)+[A-Z]?|[a-zA-Z']+)";

        /**
         * Regex matching abbreviations 3.
         */
        public static final String ABBR_REGEX3 = "\\b(?:[a-zA-Z]\\.){2,}";

        /**
         * Regex matching uppercase acronyms.
         */
        public static final String UPPER_CASE_2 = "^(.*?[A-Z]){2,}";

        /**
         * Regex matching numeric strings.
         */
        public static final String NUMERIC = "\\d+";

        /**
         * Regex matching non-numeric strings.
         */
        public static final String NON_NUMERIC = "[^0-9]";
        //public static final String NON_WORD_CHARACTERS_REGEX = "\\W";
        //public static final String NON_WORD_EXCEPT_PARENTHESIS_REGEX = "[^(),a-zA-Z]";
        //public static final String NON_WORD_EXCEPT_PARENTHESIS_REGEX_2 = "[^\\p{L}\\p{Nd}]+";

        /**
         * Regex matching punctuation except parenthesis.
         */
        public static final String PUNCTUATION_EXCEPT_PARENTHESIS_REGEX = "[\\p{Punct}&&[^()]]";

        //v0.2
        //removes - _ / @ 

        /**
         * Regex matching custom symbols. Used for removing - _ / @ from literals.
         */
        public static final String SPECIAL_CHARS = "[\\-\\_\\/\\@]";

        /**
         * Regex matching custom characters. Matches " , . - @
         */
        public static final String SIMPLE_SPECIAL_CHARS = "[\\\"\\,\\.\\-\\_\\@]";
    }    

    /**
     * Functions class contains all name constants of the evaluation functions.
     */
    public static class Functions {

        /* Dates */

        public static final String IS_DATE_KNOWN_FORMAT = "isdateknownformat";
        public static final String IS_DATE_PRIMARY_FORMAT = "isdateprimaryformat";
        public static final String IS_VALID_DATE = "isvaliddate";
        public static final String DATES_ARE_SAME = "datesaresame";
        
        /* String literals */

        public static final String IS_LITERAL_ABBREVIATION = "isliteralabbreviation";
        public static final String IS_SAME_NORMALIZED = "issamenormalized";
        public static final String IS_SAME_SIMPLE_NORMALIZE = "issamesimplenormalize";
        public static final String IS_SAME_CUSTOM_NORMALIZE = "issamecustomnormalize";
        public static final String IS_LITERAL_NUMERIC = "isliteralnumeric";
        public static final String LITERAL_CONTAINS = "literalcontains";
        public static final String LITERAL_CONTAINS_THE_OTHER = "literalcontainstheother";
        public static final String LITERAL_HAS_LANGUAGE_ANNOTATION = "literalhaslanguageannotation";
        public static final String LITERALS_HAVE_SAME_LANG = "literalshavesamelanguageannotation";
        
        /* Phone number literals */

        public static final String IS_PHONE_NUMBER_PARSABLE = "isphonenumberparsable";
        public static final String IS_SAME_PHONE_NUMBER = "issamephonenumber";
        public static final String IS_SAME_PHONE_NUMBER_CUSTOM_NORMALIZE = "issamephonenumbercustomnormalize";
        public static final String IS_SAME_PHONE_NUMBER_EXIT_CODE = "issamephonenumberusingexitcode";
        public static final String PHONE_HAS_MORE_DIGITS = "phonehasmoredigits";

        /* Property */

        public static final String EXISTS = "exists";
        public static final String NOT_EXISTS = "notexists";
        
        /* Geometry literals */

        public static final String IS_GEOMETRY_MORE_COMPLEX = "isgeometrymorecomplex";
        public static final String IS_SAME_CENTROID = "issamecentroid";
        public static final String IS_POINT_GEOMETRY = "ispointgeometry";
        public static final String GEOMETRIES_INTERSECT = "geometriesintersect";
        public static final String GEOMETRIES_CLOSER_THAN = "geometriescloserthan";
        public static final String IS_GEOMETRY_COVERED_BY = "isgeometrycoveredby";
        public static final String GEOMETRIES_HAVE_SAME_AREA = "geometrieshavesamearea";
    }

    /**
     * Functions class contains all name constants of the normalize functions.
     */
    public static class Normalize {

        public static final String PHONE_NUMBER_NORMALIZER = "phonenumbernormalizer";
        public static final String NORMALIZE_DATE_TO_FORMAT = "normalizedatetoformat";
        public static final String NORMALIZE_ALPHABETICALLY = "alphabeticalnormalizer";
        public static final String TO_LOWER_CASE = "tolowercase";
        public static final String REMOVE_SPECIAL_CHARACTERS = "removespecialcharacters";
        public static final String BASIC_NORMALIZER = "basicgenericnormalizer";
    }

    /**
     * Constant for EPSG:4326 coordinate reference system.
     */
    public static final String CRS_EPSG_4326 = "EPSG:4326";

    /**
     * Constant for EPSG:3857 coordinate reference system.
     */
    public static final String CRS_EPSG_3857 = "EPSG:3857";

    /**
     * Array constant with the date formats treat as "known date formats" in FAGI.
     */
    public static final String[] DATE_FORMATS = {
                "yyyy-MM-dd'T'HH:mm:ss'Z'",     "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss",        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",   "yyyy-MM-dd HH:mm:ss", 
                "MM/dd/yyyy HH:mm:ss",          "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'", 
                "MM/dd/yyyy'T'HH:mm:ss.SSSZ",   "MM/dd/yyyy'T'HH:mm:ss.SSS", 
                "MM/dd/yyyy'T'HH:mm:ssZ",       "MM/dd/yyyy'T'HH:mm:ss", 
                "yyyy:MM:dd HH:mm:ss",          "yyyy-MM-dd", 
                "dd mm yyyy",                   "yyyy/MM/dd",
                "dd-mm-yyyy",                   "dd-MM-yyyy",
                "dd/mm/yyyy",                   "dd MM yyyy"};

    /**
     * Array constant with the date formats considered "primary" in FAGI.
     */
    public static final String[] PRIMARY_DATE_FORMATS = {
                "yyyy-MM-dd",                   "yyyy/MM/dd",
                "dd-mm-yyyy",                   "dd-MM-yyyy",
                "dd/mm/yyyy",                   "dd/MM/yyyy"};
}