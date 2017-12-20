package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class CallingCodeResolver {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CallingCodeResolver.class);
    
    private static CallingCodeResolver resolver;
    private static Map<String, String> codes;

    private CallingCodeResolver(){}

    public static CallingCodeResolver getInstance() throws ApplicationException{
        //lazy init
        if(resolver == null){
            resolver= new CallingCodeResolver();
        }
        
        if(resolver == null){
            throw new ApplicationException("Calling codes is not initialized.");
        }
        return resolver;
    }

    public static void setCodes(Map<String, String> codes) {
        CallingCodeResolver.codes = codes;
    }

    public Map<String, String> getCodes(){
        return codes;
    }    
}
