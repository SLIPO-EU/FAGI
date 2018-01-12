package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionThreeParameters;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class IsSameSimpleNormalize implements IFunction, IFunctionThreeParameters{
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsSameSimpleNormalize.class);
    
    /**
     * Compares the two literals and returns true if they are same. 
     * If the standard equals is not true, it normalizes the literals using the BasicGenericNormalizer and re-checks.
     * 
     * @param literalA the literal A
     * @param literalB the literal B
     * @param threshold the similarity threshold. 
     * The threshold is for future use if a similarity metric is going to be used instead of equals.
     * @return true if the literals are found same before or after normalization.
     */
    @Override
    public boolean evaluate(String literalA, String literalB, String threshold) {
        
        if(literalA.equals(literalB)){
            return true;
        }

        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();
        
        String a = normalizer.normalize(literalA, literalB);
        String b = normalizer.normalize(literalB, literalA);
        
        return a.equals(b);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }      
}
