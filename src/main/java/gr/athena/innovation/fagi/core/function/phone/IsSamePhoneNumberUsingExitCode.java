package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionThreeParameters;
import gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for evaluating phone numbers using country's exit code digits. 
 * 
 * @author nkarag
 */
public class IsSamePhoneNumberUsingExitCode implements IFunction, IFunctionThreeParameters{
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer#normalize(String,String) normalize} method.
     * 
     * @param number1 The first phone number.
     * @param number2 The second phone number.
     * @param exitCodeDigits Digits to replace the "+" symbol in an international telephone number. 
     * @return true if the numbers are the same or false otherwise.
     */
    @Override
    public boolean evaluate(String number1, String number2, String exitCodeDigits){
        
        if(StringUtils.isBlank(number1) || StringUtils.isBlank(number2)){
            return false;
        }
        
        boolean isSame;
        if(number1.equals(number2)){
            isSame = true;
        } else {
            
            PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
            //TODO: using normalized values without asking the user here.
            String normalizedNumber1 = phoneNumberNormalizer.normalize(number1, exitCodeDigits);
            String normalizedNumber2 = phoneNumberNormalizer.normalize(number2, exitCodeDigits);
            
            isSame = normalizedNumber1.equals(normalizedNumber2);
        }
        
        return isSame;
    }    

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
