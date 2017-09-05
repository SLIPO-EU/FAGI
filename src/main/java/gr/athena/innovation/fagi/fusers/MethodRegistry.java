package gr.athena.innovation.fagi.fusers;

import gr.athena.innovation.fagi.core.rule.Rule;
import java.util.HashSet;
import java.util.List;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class MethodRegistry {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MethodRegistry.class);
    private HashSet<String> methodRegistry;
    private boolean isInitialized = false;
    
    public void init(){
        
        methodRegistry = new HashSet<>();
        
        DateFuser dateFuser = new DateFuser();
        Exists exists = new Exists();
        LiteralFuser literalFuser = new LiteralFuser();

        methodRegistry.add(dateFuser.getName());
        methodRegistry.add(exists.getName());
        methodRegistry.add(literalFuser.getName());
        
        isInitialized = true;
    }

    public boolean validateRules(List<Rule> rules){
        
        //for()
        return true;
    }
    
    public HashSet<String> getMethodRegistryList(){
        if(!isInitialized){
            logger.fatal("Method registry is not initialized.");
            throw new RuntimeException();
        } else {
            return methodRegistry;
        }
    }
}
