package gr.athena.innovation.fagi.fusers;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * The PhoneNumberValidator class contains utilities for telephone number transformation.
 * 
 * @author nkarag
 */
public class PhoneNumberValidator {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PhoneNumberValidator.class);
    
    /**
     * Normalizes a telephone number representation by keeping only the numeric characters. 
     * The plus symbol for international telephone numbers gets removed if exitCodeDigits is null or empty.
     * 
     * @param number
     * @param exitCodeDigits digits to replace the "+" symbol in an international telephone number. 
     * @return the String representation of the normalized telephone number.
     */
    public static String normalizePhoneNumber(String number, String exitCodeDigits) {
        String normalizedNumber;
        
        if(isPhoneNumberParsable(number)){
            return number;
            
        } else {

            if(number.startsWith("+")){
                if(StringUtils.isBlank(exitCodeDigits)){
                    normalizedNumber = removeNonNumericCharacters(number);
                } else {
                    String numberZeroReplaced = number.replaceAll("\\+", exitCodeDigits);
                    normalizedNumber = removeNonNumericCharacters(numberZeroReplaced);                    
                }
            } else {
                normalizedNumber = removeNonNumericCharacters(number);
            }
        }
        return normalizedNumber;
    }
    
    /**
     * Checks if the given number is represented as an integer. 
     * (Contains only numeric characters and no other symbols or spaces)
     * 
     * @param number
     * @return true if the telephone number representation can be parsed as an integer and false otherwise.
     * 
     */
    public static boolean isPhoneNumberParsable(String number){
        
        boolean parsable = true;
        
        try {
            
            Integer.parseInt(number);
            
        }catch(NumberFormatException e){
            //logger.debug("Number is not parsable, but it is ok. \n");
            parsable = false;
        }           

        return parsable;
    }
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link #normalizePhoneNumber(String) normalizePhoneNumber} method.
     * 
     * @param number1
     * @param number2
     * @return true if the numbers are the same or false otherwise.
     */
    public static boolean isSamePhoneNumber(String number1, String number2){
        boolean isSame = false;
        if(number1.equals(number2)){
            isSame = true;
        } else {
            String normalizedNumber1 = normalizePhoneNumber(number1, null);
            String normalizedNumber2 = normalizePhoneNumber(number2, null);
            
            isSame = normalizedNumber1.equals(normalizedNumber2);
        }
        
        return isSame;
    }

    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link #normalizePhoneNumber(String) normalizePhoneNumber} method.
     * 
     * @param number1
     * @param number2
     * @param exitCodeDigits digits to replace the "+" symbol in an international telephone number. 
     * @return true if the numbers are the same or false otherwise.
     */
    public static boolean isSamePhoneNumber(String number1, String number2, String exitCodeDigits){
        boolean isSame;
        if(number1.equals(number2)){
            isSame = true;
        } else {
            String normalizedNumber1 = normalizePhoneNumber(number1, exitCodeDigits);
            String normalizedNumber2 = normalizePhoneNumber(number2, exitCodeDigits);
            
            isSame = normalizedNumber1.equals(normalizedNumber2);
        }
        
        return isSame;
    }
    
    private static String removeNonNumericCharacters(String number){
        
        String numberNumerical = number.replaceAll("[^0-9]", "");
        
        return numberNumerical;
    }
}
