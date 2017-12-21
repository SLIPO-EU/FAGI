package gr.athena.innovation.fagi.specification;

import java.math.BigDecimal;
import java.text.Collator;

/**
 * Class with the specification constants used across the application.
 * 
 * @author nkarag
 */
public class SpecificationConstants {

    public static final String RULES_XML = "rules.xml";
    public static final String RULES_XSD = "rules.xsd";
    public static final String SPEC_XML = "spec.xml";
    public static final String SPEC_XSD = "spec.xsd";
    
    public static final String SPECIFICATION = "specification";
    public static final String INPUT_FORMAT = "inputFormat";
    public static final String OUTPUT_FORMAT = "outputFormat";

    public static final String LEFT_DATASET = "left";
    public static final String RIGHT_DATASET = "right";
    
    public static final String TARGET_DATASET = "target";
    public static final String TARGET_RESOURCE_URI = "resourceURI";
    public static final String MERGE_WITH = "mergeWith";
    public static final String LINKS = "links";

    public static final String ID = "id";
    public static final String FILE = "file";
    public static final String ENDPOINT = "endpoint";

    public static final String RULE = "rule";
    public static final String DEFAULT_DATASET_ACTION = "defaultDatasetAction";
    public static final String DEFAULT_ACTION = "defaultAction";
    public static final String PROPERTY_A = "propertyA";
    public static final String PROPERTY_B = "propertyB";
    public static final String CONDITION = "condition";
    public static final String ACTION_RULE_SET = "actionRuleSet";
    public static final String ACTION_RULE = "actionRule";
    public static final String ACTION = "action";
    public static final String EXPRESSION = "expression";
    public static final String FUNCTION = "function";
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String NOT = "not";

    public static final String A = "a";
    public static final String B = "b";
    
    public static final int COLLATOR_STRENGTH = Collator.TERTIARY;
    public static final int CONNECTOR = ' ';

    public static final String LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    public static final String DATE_OSM_MODIFIED = "http://purl.org/dc/terms/modified";
    public static final String WKT = "http://www.opengis.net/ont/geosparql#asWKT";

    public static final BigDecimal SIMILARITY_ABSOLUTE_ACCEPTED_ERROR = new BigDecimal(0.01);
    public static final BigDecimal SIMILARITY_RELATIVE_ACCEPTED_ERROR = new BigDecimal(0.1);
    public static final double SIMILARITY_ACCEPTED_ERROR = 0.05;
    public static final double ERROR_E = 1e-3;
    public static final double SIMILARITY_MAX = 0.999;
    public static final double SIMILARITY_MIN = 0.001;
    public static final int ROUND_DECIMALS = 3;

    public static final String HELP = "Usage:\n java -jar fagi-1.0-SNAPSHOT.jar -spec <specFile> -rules <rulesFile>\n"
                                    + "-spec requires the spec.xml file path\n"
                                    + "-rules requires the rules.xml file path\n";

    

    public class Regex {
        
        //((?:[A-Z]\.)+[A-Z]?|[a-zA-Z']+)
        
        public static final String ABBR_REGEX = "\\b(?:[A-Z][a-z]*){2,}";
        public static final String ABBR_REGEX2 = "((?:[A-Z]\\.)+[A-Z]?|[a-zA-Z']+)";
        public static final String ABBR_REGEX3 = "\\b(?:[a-zA-Z]\\.){2,}";
        public static final String UPPER_CASE_2 = "^(.*?[A-Z]){2,}";
        public static final String NON_WORD_CHARACTERS_REGEX = "\\W";
        public static final String NON_WORD_EXCEPT_PARENTHESIS_REGEX = "[^(),a-zA-Z]";
        public static final String NON_WORD_EXCEPT_PARENTHESIS_REGEX_2 = "[^\\p{L}\\p{Nd}]+";
        public static final String PUNCTUATION_EXCEPT_PARENTHESIS_REGEX = "[\\p{Punct}&&[^()]]";
        
    }    
    
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
                "dd/mm/yyyy",                   "dd MM yyyy",
                "yyyy/MM/dd"};

    public class Functions {

        public static final String IS_DATE_KNOWN_FORMAT = "isdateknownformat";
        public static final String IS_VALID_DATE = "isvaliddate";
        public static final String IS_LITERAL_ABBREVIATION = "isliteralabbreviation";
        public static final String IS_PHONE_NUMBER_PARSABLE = "isphonenumberparsable";
        public static final String IS_SAME_PHONE_NUMBER = "issamephonenumber";
        public static final String IS_SAME_PHONE_NUMBER_EXIT_CODE = "issamephonenumberusingexitcode";
        public static final String IS_GEOMETRY_MORE_COMPLICATED = "isgeometrymorecomplex";
        public static final String IS_SAME_NORMALIZED = "issamenormalized";

    }

    public class Normalize {
        
        public static final String PHONE_NUMBER_NORMALIZER = "phonenumbernormalizer";
        public static final String NORMALIZE_DATE_TO_FORMAT = "normalizedatetoformat";
        public static final String NORMALIZE_ALPHABETICALLY = "alphabeticalnormalizer";
        public static final String TO_LOWER_CASE = "tolowercase";
        public static final String REMOVE_SPECIAL_CHARACTERS = "removespecialcharacters";
        //public static final String MULTIPLE_NORMALIZER = "multiplegenericnormalizer";
        public static final String BASIC_NORMALIZER = "basicgenericnormalizer";
    }
}
