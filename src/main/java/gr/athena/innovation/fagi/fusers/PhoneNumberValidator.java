package gr.athena.innovation.fagi.fusers;

import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class PhoneNumberValidator {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PhoneNumberValidator.class);
    
    public static String normalizePhoneNumber(String number) {
        
//        try {    
//            int phoneNumber = Integer.parseInt(number);
//        }catch(NumberFormatException e){
//            parsable = false;
//        }        
        
    
        return "";
    }
    
    public static boolean isPhoneNumberParsable(String number){
        boolean parsable = true;
        try {
            int phoneNumber = Integer.parseInt(number);
        }catch(NumberFormatException e){
            parsable = false;
        }           

        return parsable;
    }
    
    private static Integer removeNonNumericCharacters(String number){
        Integer phoneNumber = null;
        return phoneNumber;
    }
    
    public static boolean isSamePhoneNumber(String number1, String number2){
        return false;
    }
}
