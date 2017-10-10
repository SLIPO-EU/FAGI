package gr.athena.innovation.fagi.core.functions.phone;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.functions.IFunctionTwoParameters;
import gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumber implements IFunction, IFunctionTwoParameters{
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link #normalizePhoneNumber(String) normalizePhoneNumber} method.
     * 
     * @param number1
     * @param number2
     * @return true if the numbers are the same or false otherwise.
     */
    @Override
    public boolean evaluate(String number1, String number2){
        boolean isSame;
        if(number1.equals(number2)){
            isSame = true;
        } else {
            
            PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
            //TODO: using normalized values without asking the user here.
            String normalizedNumber1 = phoneNumberNormalizer.normalize(number1, null);
            String normalizedNumber2 = phoneNumberNormalizer.normalize(number2, null);
            
            isSame = normalizedNumber1.equals(normalizedNumber2);
        }
        
        return isSame;
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
