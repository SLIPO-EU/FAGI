package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumber implements IFunction, IFunctionTwoLiteralParameters{
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSamePhoneNumber.class);
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer#normalize(String, String) normalize} method.
     * 
     * @param number1 The first phone number as literal.
     * @param number2 The second phone number as literal. 
     * @return True if the numbers are the same or false otherwise.
     */
    @Override
    public boolean evaluate(Literal number1, Literal number2){
        
        if(number1 == null || number2 == null){
            return false;
        }
        
        if(StringUtils.isBlank(number1.getString()) || StringUtils.isBlank(number2.getString())){
            return false;
        }
        
        boolean isSame;

        if(number1.equals(number2)){
            isSame = true;
        } else {
            
            PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
            //TODO: using normalized values without asking the user here.
            String normalizedNumber1 = phoneNumberNormalizer.normalize(number1.getString(), null);
            String normalizedNumber2 = phoneNumberNormalizer.normalize(number2.getString(), null);
            
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
