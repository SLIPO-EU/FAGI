package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoParameters;
import gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumber implements IFunction, IFunctionTwoParameters{
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSamePhoneNumber.class);
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer#normalize(String, String) normalize} method.
     * 
     * @param number1 The first phone number.
     * @param number2 The second phone number. 
     * @return True if the numbers are the same or false otherwise.
     */
    @Override
    public boolean evaluate(String number1, String number2){
        
        if(StringUtils.isBlank(number1) || StringUtils.isBlank(number2)){
            return false;
        }
        
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
