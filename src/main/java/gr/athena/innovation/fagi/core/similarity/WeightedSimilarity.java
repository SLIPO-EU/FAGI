package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.model.LinkedTerm;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for computing the available distances when using weighted literals.
 * 
 * 
 * @author nkarag
 */
public class WeightedSimilarity {

    private static final Logger logger = LogManager.getLogger(WeightedSimilarity.class);
    
    /**
     * Computes the provided distance for the given normalized literals.
     *
     * @param normA the first normalized literal
     * @param normB the second normalized literal
     * @param distance the distance.
     * @return the given distance using normalized literals.
     */
    public static double computeNormalized(NormalizedLiteral normA, NormalizedLiteral normB, String distance) {

        String normalizedValueA = normA.getNormalized();
        String normalizedValueB = normB.getNormalized();
        switch (distance) {
            case "cosine": {
                return Cosine.computeDistance(normalizedValueA, normalizedValueB);
            }
            case "jaccard": {
                return Jaccard.computeDistance(normalizedValueA, normalizedValueB);
            }
            case "levenshtein": {
                return Levenshtein.computeDistance(normalizedValueA, normalizedValueB, null);
            }
            case "jaro": {
                return Jaro.computeDistance(normalizedValueA, normalizedValueB);
            }
            case "jarowinkler": {
                return JaroWinkler.computeDistance(normalizedValueA, normalizedValueB);
            }
            case "sortedjarowinkler": {
                return SortedJaroWinkler.computeDistance(normalizedValueA, normalizedValueB);
            }
            case "longestcommonsubsequence": {
                return LongestCommonSubsequenceMetric.computeDistance(normalizedValueA, normalizedValueB);
            }
            case "2Gram": {
                return NGram.computeDistance(normalizedValueA, normalizedValueB, 2);
            }

            default:
                logger.error("Similarity: \"" + distance + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }
    }
    
    /**
     * Computes the provided distance for the given custom normalized literals.
     *
     * @param pair the pair of custom normalized literals
     * @param distance the distance.
     * @return the given distance using normalized literals.
     */    
    public static double computeDistance(WeightedPairLiteral pair, String distance) {
        
        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();
        double baseWeight = pair.getBaseWeight();
        
        Set<LinkedTerm> terms = pair.getLinkedTerms();
        
        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();
        double mismatchWeight = pair.getMismatchWeight();
        
        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();
        double specialsWeight = pair.getSpecialTermsWeight();

        switch (distance) {
            case "cosine": {
                
                double base = Cosine.computeDistance(baseA, baseB);
                double specials = Cosine.computeDistance(specialsA, specialsB);
                double mismatch = Cosine.computeDistance(mismatchA, mismatchB);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }
            case "jaccard": {
                
                double base = Jaccard.computeDistance(baseA, baseB);
                double specials = Jaccard.computeDistance(specialsA, specialsB);
                double mismatch = Jaccard.computeDistance(mismatchA, mismatchB);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }
            case "levenshtein": {
                
                double base = Levenshtein.computeDistance(baseA, baseB, null);
                double specials = Levenshtein.computeDistance(specialsA, specialsB, null);
                double mismatch = Levenshtein.computeDistance(mismatchA, mismatchB, null);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }
            case "jaro": {
                
                double base = Jaro.computeDistance(baseA, baseB);
                double specials = Jaro.computeDistance(specialsA, specialsB);
                double mismatch = Jaro.computeDistance(mismatchA, mismatchB);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }
            case "jarowinkler": {
                
                double base = JaroWinkler.computeDistance(baseA, baseB);
                double specials = JaroWinkler.computeDistance(specialsA, specialsB);
                double mismatch = JaroWinkler.computeDistance(mismatchA, mismatchB);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }
            case "sortedjarowinkler": {
                
                double base = SortedJaroWinkler.computeDistance(baseA, baseB);
                double specials = SortedJaroWinkler.computeDistance(specialsA, specialsB);
                double mismatch = SortedJaroWinkler.computeDistance(mismatchA, mismatchB);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }
            case "longestcommonsubsequence": {
                
                double base = LongestCommonSubsequenceMetric.computeDistance(baseA, baseB);
                double specials = LongestCommonSubsequenceMetric.computeDistance(specialsA, specialsB);
                double mismatch = LongestCommonSubsequenceMetric.computeDistance(mismatchA, mismatchB);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }
            case "2Gram": {
                
                double base = NGram.computeDistance(baseA, baseB, 2);
                double specials = NGram.computeDistance(specialsA, specialsB, 2);
                double mismatch = NGram.computeDistance(mismatchA, mismatchB, 2);

                return base * baseWeight + mismatch * mismatchWeight + specials * specialsWeight;
            }

            default:
                logger.error("Similarity: \"" + distance + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }

    }
}
