package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;

/**
 *
 * @author nkarag
 */
public class IsSameNormalized implements IFunction, IFunctionTwoLiteralParameters{
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameNormalized.class);
    
    /**
     * Compares the two literals and returns true if they are same. 
     * If the standard equals is not true, it normalizes the literals using th MultipleGenericNormalizer and re-checks.
     * 
     * @param literalA the literal A
     * @param literalB the literal B
     * @return true if the literals are found same before or after normalization.
     */
    @Override
    public boolean evaluate(Literal literalA, Literal literalB) {

        if(literalA == null || literalB == null){
            return false;
        }

        if(StringUtils.isBlank(literalA.getString()) || StringUtils.isBlank(literalB.getString())){
            return false;
        }

        if(literalA.equals(literalB)){
            return true;
        }

        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();

        String a = normalizer.normalize(literalA.getString(), literalB.getString());
        String b = normalizer.normalize(literalB.getString(), literalA.getString());
        
        return a.equals(b);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }   
}
