package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.WeightedSimilarity;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.Configuration;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.logging.log4j.LogManager;
import gr.athena.innovation.fagi.core.function.IFunctionThreeLiteralStringParameters;

/**
 * Class for evaluating similarity between two literals given a threshold.
 * 
 * @author nkarag
 */
public class IsSameCustomNormalize implements IFunction, IFunctionThreeLiteralStringParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameCustomNormalize.class);

    /**
     * Compares the two literals and returns true if they are same. If the standard equals is not true, it normalizes
     * the literals using s simple normalization process and re-checks. If all the above fail, the two literals are
     * normalized further using the AdvancedGenericNormalizer. If the final similarity is above the threshold the method
     * returns true.
     *
     * @param literalA the literal A.
     * @param literalB the literal B.
     * @param threshold the similarity threshold. The threshold is not localized and it accepts only dot as decimal.
     * point.
     * @return true if the similarity of the literals is above the provided threshold.
     */
    @Override
    public boolean evaluate(Literal literalA, Literal literalB, String threshold) {

        if(literalA == null || literalB == null){
            return false;
        }

        String literalStringA = literalA.getString();
        String literalStringB = literalB.getString();

        if(StringUtils.isBlank(literalStringA) || StringUtils.isBlank(literalStringB)){
            return false;
        }
        
        double thrs = 0;
        if(!StringUtils.isBlank(threshold)){
            try {
                thrs = Double.parseDouble(threshold);
                if(thrs < 0 || thrs > 1){
                    throw new ApplicationException("Threshold out of range [0,1]: " + threshold);
                }
            } catch(NumberFormatException ex){
                throw new ApplicationException("Cannot parse threshold as a double number: " + threshold);
            }
        }

        if (literalA.equals(literalB)) {
            return true;
        }

        Locale locale = Configuration.getInstance().getLocale();

        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();

        NormalizedLiteral normA = normalizer.getNormalizedLiteral(literalStringA, literalStringB, locale);
        NormalizedLiteral normB = normalizer.getNormalizedLiteral(literalStringB, literalStringA, locale);

        if (normA.getNormalized().equals(normB.getNormalized())) {
            return true;
        }

        AdvancedGenericNormalizer advancedNormalizer = new AdvancedGenericNormalizer();

        WeightedPairLiteral weightedPair = advancedNormalizer.getWeightedPair(normA, normB, locale);

        String simName = Configuration.getInstance().getSimilarity();

        double result = WeightedSimilarity.computeDSimilarity(weightedPair, simName);

        return result > thrs;
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
