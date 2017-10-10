package gr.athena.innovation.fagi.core.transform.phone;

import gr.athena.innovation.fagi.core.functions.phone.IsPhoneNumberParsable;
import gr.athena.innovation.fagi.core.transform.ITransform;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for phone number normalization.
 * 
 * @author nkarag
 */
public class PhoneNumberNormalizer implements ITransform{
    
    /**
     * Normalizes a telephone number representation by keeping only the numeric characters. 
     * The plus symbol for international telephone numbers gets removed if exitCodeDigits is null or empty.
     * 
     * @param numberString
     * @param exitCodeDigits digits to replace the "+" symbol in an international telephone number. 
     * @return the String representation of the normalized telephone number.
     */
    public String normalizePhoneNumber(String numberString, String exitCodeDigits) {
        String normalizedNumber;
        
        IsPhoneNumberParsable isPhoneNumberParsable = new IsPhoneNumberParsable();
        
        if(isPhoneNumberParsable.evaluate(numberString)){
            return numberString;
            
        } else {

            if(numberString.startsWith("+")){
                if(StringUtils.isBlank(exitCodeDigits)){
                    normalizedNumber = removeNonNumericCharacters(numberString);
                } else {
                    String numberZeroReplaced = numberString.replaceAll("\\+", exitCodeDigits);
                    normalizedNumber = removeNonNumericCharacters(numberZeroReplaced);                    
                }
            } else {
                normalizedNumber = removeNonNumericCharacters(numberString);
            }
        }
        return normalizedNumber;
    }

    private static String removeNonNumericCharacters(String number){
        
        String numberNumerical = number.replaceAll("[^0-9]", "");
        
        return numberNumerical;
    }
    
    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
