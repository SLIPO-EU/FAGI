package gr.athena.innovation.fagi.specification;

import java.math.BigDecimal;

/**
 *
 * @author nkarag
 */
public class SpecificationConstants {

    public static final String RULES_XML = "rules.xml";
    public static final String RULES_XSD = "rules.xsd";
    public static final String SPEC_XML = "spec.xml";
    public static final String SPEC_XSD = "spec.xsd";
    
    public static final String SPECIFICATION = "SPECIFICATION";
    public static final String INPUT_FORMAT = "INPUT_FORMAT";
    public static final String OUTPUT_FORMAT = "OUTPUT_FORMAT";

    public static final String LEFT_DATASET = "LEFT";
    public static final String RIGHT_DATASET = "RIGHT";
    
    public static final String TARGET_DATASET = "TARGET";
    public static final String TARGET_RESOURCE_URI = "RESOURCE_URI";
    public static final String MERGE_WITH = "MERGE_WITH";
    public static final String LINKS = "LINKS";

    public static final String ID = "ID";
    public static final String FILE = "FILE";
    public static final String ENDPOINT = "ENDPOINT";

    public static final String DEFAULT_DATASET_ACTION = "DEFAULT_DATASET_ACTION";
    public static final String PROPERTY_A = "PROPERTYA";
    public static final String PROPERTY_B = "PROPERTYB";
    public static final String CONDITION = "CONDITION";
    public static final String ACTION_RULE_SET = "ACTION_RULE_SET";
    public static final String EXPRESSION = "EXPRESSION";
    public static final String FUNCTION = "FUNCTION";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";

    public static final String A = "A";
    public static final String B = "B";

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
        public static final String NON_WORD_CHARACTERS_REGEX = "\\W";
        public static final String NON_WORD_EXCEPT_PARENTHESIS_REGEX = "[^(),a-zA-Z]";
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
        public static final String IS_GEOMETRY_MORE_COMPLICATED = "isgeometrymorecomplicated";

    }

    public class Normalize {
        
        public static final String PHONE_NUMBER_NORMALIZER = "phonenumbernormalizer";
        public static final String NORMALIZE_DATE_TO_FORMAT = "normalizedatetoformat";
        public static final String NORMALIZE_ALPHABETICALLY = "alphabeticalnormalizer";
        public static final String TO_LOWER_CASE = "tolowercase";
        public static final String REMOVE_SPECIAL_CHARACTERS = "removespecialcharacters";
    }
}
