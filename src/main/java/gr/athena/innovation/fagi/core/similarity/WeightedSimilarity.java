package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.model.WeightedLiteral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class WeightedSimilarity {

    private static final Logger logger = LogManager.getLogger(WeightedSimilarity.class);

    /**
     * Computes the Jaccard distance for the given Strings.
     *
     * @param weightedLiteralA the first weighted literal.
     * @param weightedLiteralB the second weighted literal.
     * @param distance the distance.
     * @return the computed weighted distance.
     */
    public static double computeDistance(WeightedLiteral weightedLiteralA,
            WeightedLiteral weightedLiteralB, String distance) {

        switch (distance) {
            case "cosine": {
                double base = Cosine.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral());

                double specials = Cosine.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral());

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }
            case "jaccard": {
                double base = Jaccard.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral());

                double specials = Jaccard.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral());

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }
            case "levenstein": {
                double base = Levenshtein.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral(), null);

                double specials = Levenshtein.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral(), null);

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }
            case "jaro": {
                double base = Jaro.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral());

                double specials = Jaro.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral());

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }
            case "jarowinkler": {
                double base = JaroWinkler.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral());

                double specials = JaroWinkler.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral());

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }
            case "sortedjarowinkler": {
                double base = SortedJaroWinkler.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral());

                double specials = SortedJaroWinkler.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral());

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }
            case "longestcommonsubsequence": {
                double base = LongestCommonSubsequenceMetric.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral());

                double specials = LongestCommonSubsequenceMetric.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral());

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }
            case "2Gram": {
                double base = NGram.computeDistance(weightedLiteralA.getBaseLiteral(), weightedLiteralB.getBaseLiteral(), 2);

                double specials = NGram.computeDistance(weightedLiteralA.getTermsLiteral(), weightedLiteralB.getTermsLiteral(), 2);

                return base * weightedLiteralA.getBaseWeight() + specials * weightedLiteralA.getMisMatchedWeight();
            }

            default:
                logger.error("Similarity: \"" + distance + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }

    }

}
