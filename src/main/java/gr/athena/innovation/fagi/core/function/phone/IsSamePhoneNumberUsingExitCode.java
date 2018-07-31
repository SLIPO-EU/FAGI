package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionThreeLiteralStringParameters;

/**
 * Class for evaluating phone numbers using country's exit code digits. 
 * 
 * @author nkarag
 */
public class IsSamePhoneNumberUsingExitCode implements IFunction, IFunctionThreeLiteralStringParameters{
    
    /**
     * Checks if two telephone numbers are the same using String.equals and if that fails the check is done upon the
     * normalized version described at the {@link gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer#normalize(String,String) normalize} method.
     * 
     * @param number1 The first phone number as literal.
     * @param number2 The second phone number as literal.
     * @param exitCodeDigits Digits to replace the "+" symbol in an international telephone number. 
     * @return true if the numbers are the same or false otherwise.
     */
    @Override
    public boolean evaluate(Literal number1, Literal number2, String exitCodeDigits){
        
        if(number1 == null || number2 == null){
            return false;
        }

        String numberString1 = number1.getString();
        String numberString2 = number2.getString();

        if(StringUtils.isBlank(numberString1) || StringUtils.isBlank(numberString2)){
            return false;
        }
        
        boolean isSame;
        if(number1.equals(number2)){
            isSame = true;
        } else {
            
            PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
            //TODO: using normalized values without asking the user here.
            String normalizedNumber1 = phoneNumberNormalizer.normalize(numberString1, exitCodeDigits);
            String normalizedNumber2 = phoneNumberNormalizer.normalize(numberString2, exitCodeDigits);
            
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
