package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.util.Set;
import org.apache.logging.log4j.LogManager;

/**
 * Class container for terms.
 * 
 * @author nkarag
 */
public class TermResolver {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(TermResolver.class);
    
    private static TermResolver termResolver;
    private static Set<String> terms;

    private TermResolver(){}

    /**
     * Constructor of TermResolver, returns a new instance or an existing.
     * 
     * @return the TermResolver instance.
     * @throws ApplicationException application exception.
     */
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

    /**
     * Set value for the specialTerms Set.
     * @param specialTerms the special terms set.
     */
    public static void setTerms(Set<String> specialTerms) {
        terms = specialTerms;
    }

    /**
     * Returns the set of terms.
     * 
     * @return the terms set.
     */
    public Set<String> getTerms(){
        return terms;
    }
}
