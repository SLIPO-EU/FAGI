package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionThreeParameters;
import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.WeightedSimilarity;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.Configuration;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class IsSameCustomNormalize implements IFunction, IFunctionThreeParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameCustomNormalize.class);

    /**
     * Compares the two literals and returns true if they are same. If the standard equals is not true, it normalizes
     * the literals using s simple normalization process and re-checks. If all the above fail, the two literals are
     * normalized further using the AdvancedGenericNormalizer. If the final similarity is above the threshold the method
     * returns true.
     *
     * @param literalA the literal A
     * @param literalB the literal B
     * @param threshold the similarity threshold. The threshold is not localized and it accepts only dot as decimal.
     * point.
     * @return true if the similarity of the literals is above the provided threshold.
     */
    @Override
    public boolean evaluate(String literalA, String literalB, String threshold) {

        if(StringUtils.isBlank(literalA) || StringUtils.isBlank(literalB)){
            return false;
        }
        
        double thres = Double.parseDouble(threshold);

        if (literalA.equals(literalB)) {
            return true;
        }

        Locale locale = Configuration.getInstance().getLocale();

        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();

        NormalizedLiteral normA = normalizer.getNormalizedLiteral(literalA, literalB, locale);
        NormalizedLiteral normB = normalizer.getNormalizedLiteral(literalB, literalA, locale);

        if (normA.getNormalized().equals(normB.getNormalized())) {
            return true;
        }

        AdvancedGenericNormalizer advancedNormalizer = new AdvancedGenericNormalizer();

        WeightedPairLiteral weightedPair = advancedNormalizer.getWeightedPair(normA, normB, locale);

        String simName = Configuration.getInstance().getSimilarity();

        double result = WeightedSimilarity.computeDSimilarity(weightedPair, simName);

        return result > thres;
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
