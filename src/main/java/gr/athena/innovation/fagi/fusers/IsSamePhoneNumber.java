package gr.athena.innovation.fagi.fusers;

import static gr.athena.innovation.fagi.fusers.PhoneNumberValidator.normalizePhoneNumber;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumber {
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link #normalizePhoneNumber(String) normalizePhoneNumber} method.
     * 
     * @param number1
     * @param number2
     * @return true if the numbers are the same or false otherwise.
     */
    public static boolean isSamePhoneNumber(String number1, String number2){
        boolean isSame;
        if(number1.equals(number2)){
            isSame = true;
        } else {
            String normalizedNumber1 = normalizePhoneNumber(number1, null);
            String normalizedNumber2 = normalizePhoneNumber(number2, null);
            
            isSame = normalizedNumber1.equals(normalizedNumber2);
        }
        
        return isSame;
    }    
}
