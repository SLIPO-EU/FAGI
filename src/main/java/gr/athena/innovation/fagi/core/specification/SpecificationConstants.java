package gr.athena.innovation.fagi.core.specification;

/**
 *
 * @author nkarag
 */
public class SpecificationConstants {

    public static final String PROPERTY_A = "PROPERTYA";
    public static final String PROPERTY_B = "PROPERTYB";
    public static final String CONDITION = "CONDITION";
    public static final String ACTION_RULE_SET = "ACTION_RULE_SET";
    public static final String FUNCTION = "FUNCTION";
    public static final String EXPRESSION = "EXPRESSION";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";

//    public static final String DATE_FORMAT_1 = "yyyy-MM-dd";
//    public static final String DATE_FORMAT_2 = "yyyy/MM/dd";
//    public static final String DATE_FORMAT_3 = "dd mm yyyy";
//    public static final String DATE_FORMAT_4 = "dd-MM-yy:HH:mm:SS";
//    public static final String DATE_FORMAT_5 = "dd-MM-yy:HH:mm:SS Z";
    
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
