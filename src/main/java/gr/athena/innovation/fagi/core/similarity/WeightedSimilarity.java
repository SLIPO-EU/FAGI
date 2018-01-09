package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.model.CategoryWeight;
import gr.athena.innovation.fagi.model.LinkedTerm;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
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
    public static double computeNormalizedDistance(NormalizedLiteral normA, NormalizedLiteral normB, String distance) {

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
     * Computes the provided similarity for the given normalized literals.
     *
     * @param normA the first normalized literal
     * @param normB the second normalized literal
     * @param similarity the similarity.
     * @return the given distance using normalized literals.
     */
    public static double computeNormalizedSimilarity(NormalizedLiteral normA, NormalizedLiteral normB, String similarity) {

        String normalizedValueA = normA.getNormalized();
        String normalizedValueB = normB.getNormalized();
        switch (similarity) {
            case "cosine": {
                return Cosine.computeSimilarity(normalizedValueA, normalizedValueB);
            }
            case "jaccard": {
                return Jaccard.computeSimilarity(normalizedValueA, normalizedValueB);
            }
            case "levenshtein": {
                return Levenshtein.computeSimilarity(normalizedValueA, normalizedValueB, null);
            }
            case "jaro": {
                return Jaro.computeSimilarity(normalizedValueA, normalizedValueB);
            }
            case "jarowinkler": {
                return JaroWinkler.computeSimilarity(normalizedValueA, normalizedValueB);
            }
            case "sortedjarowinkler": {
                return SortedJaroWinkler.computeSimilarity(normalizedValueA, normalizedValueB);
            }
            case "longestcommonsubsequence": {
                return LongestCommonSubsequenceMetric.computeSimilarity(normalizedValueA, normalizedValueB);
            }
            case "2Gram": {
                return NGram.computeSimilarity(normalizedValueA, normalizedValueB, 2);
            }

            default:
                logger.error("Similarity: \"" + similarity + "\" does not exist for weighted literals.");
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
    public static double computeAdvancedNormarizedDistance(String distance, WeightedPairLiteral pair) {
        
        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();
        
        Set<LinkedTerm> terms = pair.getLinkedTerms();
        
        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();
        
        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();

        CategoryWeight categorySimilarity = new CategoryWeight(pair);

        double baseSim;
        double specialsSim;
        double mismatchSim;
        double termSim;
        
        baseSim = computeDistance(distance, baseA, baseB);
        mismatchSim = computeDistance(distance, mismatchA, mismatchB);
        specialsSim = computeDistance(distance, specialsA, specialsB);
        termSim = 0;
        
        if(categorySimilarity.isZeroBaseSimilarity()){
            baseSim = 0;
        }
        
        if(categorySimilarity.isEmptyMismatch()){
            mismatchSim = baseSim;
        }

        if(categorySimilarity.isEmptySpecials()){
            specialsSim = baseSim;
        }
        
        if(!terms.isEmpty()){
            termSim = 1.0;
        }
        
        return computeWeights(baseSim, mismatchSim, specialsSim, termSim);
    }
    
    /**
     * Computes the provided similarity for the given custom normalized literals.
     *
     * @param pair the pair of custom normalized literals
     * @param similarity the similarity.
     * @return the given distance using normalized literals.
     */    
    public static double computeAdvancedNormarizedSimilarity(WeightedPairLiteral pair, String similarity) {

        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();
        
        Set<LinkedTerm> terms = pair.getLinkedTerms();
        
        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();
        
        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();

        CategoryWeight categorySimilarity = new CategoryWeight(pair);

        double baseSim;
        double specialsSim;
        double mismatchSim;
        double termSim;
        
        baseSim = computeSimilarity(similarity, baseA, baseB);
        mismatchSim = computeSimilarity(similarity, mismatchA, mismatchB);
        specialsSim = computeSimilarity(similarity, specialsA, specialsB);
        termSim = 0;
        
        if(categorySimilarity.isZeroBaseSimilarity()){
            baseSim = 0;
        }
        
        if(categorySimilarity.isEmptyMismatch()){
            mismatchSim = baseSim;
        }

        if(categorySimilarity.isEmptySpecials()){
            specialsSim = baseSim;
        }
        
        if(!terms.isEmpty()){
            termSim = 1.0;
        }
        
        return computeWeights(baseSim, mismatchSim, specialsSim, termSim);
    }

    private static double computeSimilarity(String similarity, String a, String b){
        
        double result;
        
        switch (similarity) {
            case "cosine": {
                result = Cosine.computeSimilarity(a, b);
                break;
            }
            case "jaccard": {
                result =  Jaccard.computeSimilarity(a, b);
                break;
            }
            case "levenshtein": {
                result =  Levenshtein.computeSimilarity(a, b, null);
                break;
            }
            case "jaro": {
                result =  Jaro.computeSimilarity(a, b);
                break;
            }
            case "jarowinkler": {
                result =  JaroWinkler.computeSimilarity(a, b);
                break;
            }
            case "sortedjarowinkler": {
                result =  SortedJaroWinkler.computeSimilarity(a, b);
                break;
            }
            case "longestcommonsubsequence": {
                result =  LongestCommonSubsequenceMetric.computeSimilarity(a, b);
                break;
            }
            case "2Gram": {
                result =  NGram.computeSimilarity(a, b, 2);
                break;
            }
            default:
                logger.error("Similarity: \"" + similarity + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }
        return result;
    }

    private static double computeDistance(String similarity, String a, String b){
        
        double result;
        
        switch (similarity) {
            case "cosine": {
                result = Cosine.computeDistance(a, b);
                break;
            }
            case "jaccard": {
                result =  Jaccard.computeDistance(a, b);
                break;
            }
            case "levenshtein": {
                result =  Levenshtein.computeDistance(a, b, null);
                break;
            }
            case "jaro": {
                result =  Jaro.computeDistance(a, b);
                break;
            }
            case "jarowinkler": {
                result =  JaroWinkler.computeDistance(a, b);
                break;
            }
            case "sortedjarowinkler": {
                result =  SortedJaroWinkler.computeDistance(a, b);
                break;
            }
            case "longestcommonsubsequence": {
                result =  LongestCommonSubsequenceMetric.computeDistance(a, b);
                break;
            }
            case "2Gram": {
                result =  NGram.computeDistance(a, b, 2);
                break;
            }
            default:
                logger.error("Similarity: \"" + similarity + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }
        return result;
    }
    
    private static double computeWeights(double baseSim, double mismatchSim, double specialsSim, double termSim) {
        
        double baseWeight = SpecificationConstants.BASE_WEIGHT;
        double mismatchWeight = SpecificationConstants.MISMATCH_WEIGHT;
        double specialsWeight = SpecificationConstants.SPECIAL_WEIGHT;
        double termWeight = SpecificationConstants.LINKED_TERM_WEIGHT;
        
        return baseSim * baseWeight + mismatchSim * mismatchWeight + specialsSim * specialsWeight + termSim*termWeight;
    }
}
