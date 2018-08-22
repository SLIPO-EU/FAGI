package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 * Class container for calling codes.
 * @author nkarag
 */
public class CallingCodeResolver {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(CallingCodeResolver.class);
    
    private static CallingCodeResolver resolver;
    private static Map<String, String> codes;

    private CallingCodeResolver(){}

    /**
     * Returns a new instance of CallingCodeResolver or an existing one.
     * 
     * @return the instance.
     * @throws ApplicationException application exception.
     */
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

    /**
     * Set the value of codes.
     * 
     * @param codes the codes map.
     */
    public static void setCodes(Map<String, String> codes) {
        CallingCodeResolver.codes = codes;
    }

    /**
     * Return the value of codes.
     * 
     * @return the codes map.
     */
    public Map<String, String> getCodes(){
        return codes;
    }    
}
