package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionOneParameter;

/**
 * Class for evaluating a text phone number.
 * 
 * @author nkarag
 */
public class IsPhoneNumberParsable implements IFunction, IFunctionOneParameter{

    /**
     * Checks if the given number is represented as an integer. 
     * (Contains only numeric characters and no other symbols or spaces)
     * 
     * @param number The phone number to evaluate.
     * @return True if the telephone number representation can be parsed as an integer and false otherwise.
     * 
     */
    @Override
    public boolean evaluate(Literal number){
    
        if(number == null){
            return false;
        }
        
        if(StringUtils.isBlank(number.getLexicalForm())){
            return false;
        }
        
        boolean parsable = true;
        
        try {
            
            Integer.parseInt(number.getLexicalForm());
            
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
