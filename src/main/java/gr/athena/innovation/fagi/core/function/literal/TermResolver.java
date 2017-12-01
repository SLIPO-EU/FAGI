package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.util.Set;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class TermResolver {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(TermResolver.class);
    
    private static TermResolver termResolver;
    private static Set<String> terms;

    private TermResolver(){}

    public static TermResolver getInstance() throws ApplicationException{
        //lazy init
        if(termResolver == null){
            termResolver= new TermResolver();
        }
        
        if(termResolver == null){
            throw new ApplicationException("Special terms set is not initialized.");
        }
        return termResolver;
    }

    public static void setTerms(Set<String> specialTerms) {
        terms = specialTerms;
    }

    public Set<String> getTerms(){
        return terms;
    }
}
