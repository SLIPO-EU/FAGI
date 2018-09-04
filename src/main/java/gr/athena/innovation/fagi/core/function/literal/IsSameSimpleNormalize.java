package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.WeightedSimilarity;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.specification.Configuration;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionThreeLiteralStringParameters;
import gr.athena.innovation.fagi.exception.ApplicationException;

/**
 * Class evaluating similarity between two literals given a threshold. Uses the simple normalization process.
 * 
 * @author nkarag
 */
public class IsSameSimpleNormalize implements IFunction, IFunctionThreeLiteralStringParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameSimpleNormalize.class);

    /**
     * Compares the two literals and returns true if they are same. If the standard equals is not true, it normalizes
     * the literals using the simple normalization process and re-checks. If the final similarity is above the threshold
     * the method returns true.
     *
     * @param literalA the literal A
     * @param literalB the literal B
     * @param threshold the similarity threshold. The threshold is not localized and it accepts only dot as decimal. The
     * threshold is for future use if a similarity metric is going to be used instead of equals.
     * @return true if the similarity of the literals is above the provided threshold.
     */
    @Override
    public boolean evaluate(Literal literalA, Literal literalB, String threshold) {

        if(literalA == null || literalB == null){
            return false;
        }

        if(StringUtils.isBlank(literalA.getLexicalForm()) || StringUtils.isBlank(literalB.getLexicalForm())){
            return false;
        }

        Locale locale = Configuration.getInstance().getLocale();

        if (literalA.equals(literalB)) {
            return true;
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
        
        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();

        NormalizedLiteral normA = normalizer.getNormalizedLiteral(literalA.getLexicalForm(), literalB.getLexicalForm(), locale);
        NormalizedLiteral normB = normalizer.getNormalizedLiteral(literalB.getLexicalForm(), literalA.getLexicalForm(), locale);
        
        String simName = Configuration.getInstance().getSimilarity();
        
        double result = WeightedSimilarity.computeBSimilarity(normA, normB, simName);
        
        return result > thrs;
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
