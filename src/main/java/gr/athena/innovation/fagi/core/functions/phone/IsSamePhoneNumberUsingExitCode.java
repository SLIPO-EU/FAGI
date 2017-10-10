package gr.athena.innovation.fagi.core.functions.phone;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.functions.IFunctionThreeParameters;
import gr.athena.innovation.fagi.core.transform.phone.PhoneNumberNormalizer;

/**
 * Class for evaluating phone numbers using country's exit code digits. 
 * 
 * @author nkarag
 */
public class IsSamePhoneNumberUsingExitCode implements IFunction, IFunctionThreeParameters{
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link #normalizePhoneNumber(String) normalizePhoneNumber} method.
     * 
     * @param number1
     * @param number2
     * @param exitCodeDigits digits to replace the "+" symbol in an international telephone number. 
     * @return true if the numbers are the same or false otherwise.
     */
    @Override
    public boolean evaluate(String number1, String number2, String exitCodeDigits){
        boolean isSame;
        if(number1.equals(number2)){
            isSame = true;
        } else {
            
            PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
            //TODO: using normalized values without asking the user here.
            String normalizedNumber1 = phoneNumberNormalizer.normalizePhoneNumber(number1, exitCodeDigits);
            String normalizedNumber2 = phoneNumberNormalizer.normalizePhoneNumber(number2, exitCodeDigits);
            
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
