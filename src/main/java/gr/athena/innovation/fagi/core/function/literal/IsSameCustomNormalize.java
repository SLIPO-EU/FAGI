package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionThreeParameters;
import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class IsSameCustomNormalize  implements IFunction, IFunctionThreeParameters{
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsSameCustomNormalize.class);
    
    /**
     * Compares the two literals and returns true if they are same. 
     * If the standard equals is not true, it normalizes the literals using th BasicGenericNormalizer and re-checks.
     * If all the above fail, the two literals are normalized further using the AdvancedGenericNormalizer. 
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

        Locale locale = FusionSpecification.getInstance().getLocale();
        
        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();

        NormalizedLiteral normA = normalizer.getNormalizedLiteral(literalA, literalB, locale);
        NormalizedLiteral normB = normalizer.getNormalizedLiteral(literalB, literalA, locale);

        if(normA.getNormalized().equals(normB.getNormalized())){
            return true;
        }
        
        AdvancedGenericNormalizer advancedNormalizer = new AdvancedGenericNormalizer();
        
        WeightedPairLiteral weightedPair = advancedNormalizer.getWeightedPair(normA, normB, locale);
        
        String a = weightedPair.getCompleteA();
        String b = weightedPair.getCompleteB();

        //TODO: add similarity metric instead of equals. Use threshold        
        return a.equals(b);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
