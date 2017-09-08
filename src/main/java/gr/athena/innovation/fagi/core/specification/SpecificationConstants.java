package gr.athena.innovation.fagi.core.specification;

/**
 *
 * @author nkarag
 */
public class SpecificationConstants {

    public static final String SPECIFICATION = "SPECIFICATION";
    public static final String INPUT_FORMAT = "INPUTFORMAT";
    public static final String OUTPUT_FORMAT = "OUTPUTFORMAT";

    public static final String LEFT_DATASET = "LEFT";
    public static final String RIGHT_DATASET = "RIGHT";
    public static final String TARGET_DATASET = "TARGET";
    public static final String LINKS = "LINKS";

    public static final String ID = "ID";
    public static final String FILE = "FILE";
    public static final String ENDPOINT = "ENDPOINT";

    public static final String PROPERTY_A = "PROPERTYA";
    public static final String PROPERTY_B = "PROPERTYB";
    public static final String CONDITION = "CONDITION";
    public static final String ACTION_RULE_SET = "ACTION_RULE_SET";
    public static final String FUNCTION = "FUNCTION";
    public static final String EXPRESSION = "EXPRESSION";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";
    
    public static final String ABBR_REGEX = "\\b(?:[A-Z][a-z]*){2,}";
    public static final String ABBR_REGEX2 = "((?:[A-Z]\\.)+[A-Z]?|[a-zA-Z']+)";
    
    public static final String HELP = "Usage:\n java -jar fagi-1.0-SNAPSHOT.jar -spec <specFile> -rules <rulesFile>\n"
                                    + "-spec requires the spec.xml file path\n"
                                    + "-rules requires the rules.xml file path\n";
    
    //((?:[A-Z]\.)+[A-Z]?|[a-zA-Z']+)
    
    public static final String[] DATE_FORMATS = {
                "yyyy-MM-dd'T'HH:mm:ss'Z'",     "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss",        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",   "yyyy-MM-dd HH:mm:ss", 
                "MM/dd/yyyy HH:mm:ss",          "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'", 
                "MM/dd/yyyy'T'HH:mm:ss.SSSZ",   "MM/dd/yyyy'T'HH:mm:ss.SSS", 
                "MM/dd/yyyy'T'HH:mm:ssZ",       "MM/dd/yyyy'T'HH:mm:ss", 
                "yyyy:MM:dd HH:mm:ss",          "yyyy-MM-dd", 
                "dd mm yyyy",                   "yyyy/MM/dd",
                "dd-mm-yyyy",                   "dd/mm/yyyy", 
                "dd MM yyyy",                   };
}
