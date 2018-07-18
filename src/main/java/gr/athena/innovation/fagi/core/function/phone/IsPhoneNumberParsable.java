package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionSingleParameter;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for evaluating a text phone number.
 * 
 * @author nkarag
 */
public class IsPhoneNumberParsable implements IFunction, IFunctionSingleParameter{
    
    /**
     * Checks if the given number is represented as an integer. 
     * (Contains only numeric characters and no other symbols or spaces)
     * 
     * @param number The phone number to evaluate.
     * @return True if the telephone number representation can be parsed as an integer and false otherwise.
     * 
     */
    @Override
    public boolean evaluate(String number){
    
        if(StringUtils.isBlank(number)){
            return false;
        }
        
        boolean parsable = true;
        
        try {
            
            Integer.parseInt(number);
            
        }catch(NumberFormatException e){
            //LOG.debug("Number is not parsable, but it is ok. \n");
            parsable = false;
        }           

        return parsable;
    }
    
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    
}
