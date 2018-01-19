package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.model.CategoryWeight;
import gr.athena.innovation.fagi.model.LinkedTerm;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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

        termSim = 1;

        if (categorySimilarity.isZeroBaseSimilarity()) {
            baseSim = 1;
        } else {
            baseSim = computeBaseDistance(distance, baseA, baseB);
        }

        if (categorySimilarity.isEmptyMismatch()) {
            mismatchSim = baseSim;
        } else {
            mismatchSim = computeMismatchDistance(distance, mismatchA, mismatchB);
        }

        if (categorySimilarity.isEmptySpecials()) {
            if (categorySimilarity.isZeroBaseSimilarity()) {
                specialsSim = mismatchSim;
            } else {
                specialsSim = baseSim;
            }
        } else {
            specialsSim = computeBaseDistance(distance, specialsA, specialsB);
        }

        if (!terms.isEmpty()) {
            termSim = 0.0;
        } else {
            if (categorySimilarity.isZeroBaseSimilarity()) {
                termSim = mismatchSim;
            } else {
                termSim = baseSim;
            }            
        }

        return computeWeights(categorySimilarity,baseSim, mismatchSim, specialsSim, termSim);
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
        double mismatchSim;
        double specialsSim;
        double termSim;

//        baseSim = computeBaseSimilarity(similarity, baseA, baseB);
//        mismatchSim = computeMismatchSimilarity(similarity, mismatchA, mismatchB);
//        specialsSim = computeBaseSimilarity(similarity, specialsA, specialsB);

        if (categorySimilarity.isZeroBaseSimilarity()) {
            baseSim = 0;
        } else {
            baseSim = computeBaseSimilarity(similarity, baseA, baseB);
        }

        if (categorySimilarity.isEmptyMismatch()) {
            mismatchSim = baseSim;
        } else {
            mismatchSim = computeMismatchSimilarity(similarity, mismatchA, mismatchB);
        }

        if (categorySimilarity.isEmptySpecials()) {
            if (categorySimilarity.isZeroBaseSimilarity()) {
                specialsSim = mismatchSim;
            } else {
                specialsSim = baseSim;
            }
        } else {
            specialsSim = computeBaseSimilarity(similarity, specialsA, specialsB);
        }

        if (!terms.isEmpty()) {
            termSim = 1.0;
        } else {
            if (categorySimilarity.isZeroBaseSimilarity()) {
                termSim = mismatchSim;
            } else {
                termSim = baseSim;
            }            
        }

        return computeWeights(categorySimilarity, baseSim, mismatchSim, specialsSim, termSim);
    }

    private static double computeBaseSimilarity(String similarity, String a, String b) {

        double result;

        switch (similarity) {
            case "cosine": {
                result = Cosine.computeSimilarity(a, b);
                break;
            }
            case "jaccard": {
                result = Jaccard.computeSimilarity(a, b);
                break;
            }
            case "levenshtein": {
                result = Levenshtein.computeSimilarity(a, b, null);
                break;
            }
            case "jaro": {
                //compute per word and average. (The base category contains only matched words)
                result = computeJaroSimilarityPerWord(a, b);
                break;
            }
            case "jarowinkler": {
                //compute per word and average. (The base category contains only matched words)
                result = computeJaroWinklerSimilarityPerWord(a, b);
                break;
            }
            case "sortedjarowinkler": {
                //compute per word and average. (The base category contains only matched words)
                result = computeSortedJaroWinklerSimilarityPerWord(a, b);
                break;
            }
            case "longestcommonsubsequence": {
                result = LongestCommonSubsequenceMetric.computeSimilarity(a, b);
                break;
            }
            case "2Gram": {
                result = NGram.computeSimilarity(a, b, 2);
                break;
            }
            default:
                logger.error("Similarity: \"" + similarity + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }
        return result;
    }

    private static double computeBaseDistance(String similarity, String a, String b) {

        double result;

        switch (similarity) {
            case "cosine": {
                result = Cosine.computeDistance(a, b);
                break;
            }
            case "jaccard": {
                result = Jaccard.computeDistance(a, b);
                break;
            }
            case "levenshtein": {
                result = Levenshtein.computeDistance(a, b, null);
                break;
            }
            case "jaro": {
                //compute per word and average. (The base category contains only matched words)
                result = 1 - computeJaroSimilarityPerWord(a, b);
                break;
            }
            case "jarowinkler": {
                //compute per word and average. (The base category contains only matched words)
                result = 1 - computeJaroWinklerSimilarityPerWord(a, b);
                break;
            }
            case "sortedjarowinkler": {
                //compute per word and average. (The base category contains only matched words)
                result = 1 - computeSortedJaroWinklerSimilarityPerWord(a, b);
                break;
            }
            case "longestcommonsubsequence": {
                result = LongestCommonSubsequenceMetric.computeDistance(a, b);
                break;
            }
            case "2Gram": {
                result = NGram.computeDistance(a, b, 2);
                break;
            }
            default:
                logger.error("Similarity: \"" + similarity + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }
        return result;
    }

    private static double computeMismatchDistance(String similarity, String a, String b) {

        double result;

        switch (similarity) {
            case "cosine": {
                result = Cosine.computeDistance(a, b);
                break;
            }
            case "jaccard": {
                result = Jaccard.computeDistance(a, b);
                break;
            }
            case "levenshtein": {
                result = Levenshtein.computeDistance(a, b, null);
                break;
            }
            case "jaro": {
                //using levenshtein metric for mismatch terms for jaro
                result = Levenshtein.computeDistance(a, b, null);
                break;
            }
            case "jarowinkler": {
                //using levenshtein metric for mismatch terms for jaro-winkler
                result = Levenshtein.computeDistance(a, b, null);
                break;
            }
            case "sortedjarowinkler": {
                //using levenshtein metric for mismatch terms for sorted jaro-winkler
                result = Levenshtein.computeDistance(a, b, null);
                break;
            }
            case "longestcommonsubsequence": {
                result = LongestCommonSubsequenceMetric.computeDistance(a, b);
                break;
            }
            case "2Gram": {
                result = NGram.computeDistance(a, b, 2);
                break;
            }
            default:
                logger.error("Similarity: \"" + similarity + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }
        return result;
    }

    private static double computeMismatchSimilarity(String similarity, String a, String b) {

        double result;

        switch (similarity) {
            case "cosine": {
                result = Cosine.computeSimilarity(a, b);
                break;
            }
            case "jaccard": {
                result = Jaccard.computeSimilarity(a, b);
                break;
            }
            case "levenshtein": {
                result = Levenshtein.computeSimilarity(a, b, null);
                break;
            }
            case "jaro": {
                //using levenshtein metric for mismatch terms for jaro
                result = Levenshtein.computeSimilarity(a, b, null);
                break;
            }
            case "jarowinkler": {
                //using levenshtein metric for mismatch terms for jaro-winkler
                result = Levenshtein.computeSimilarity(a, b, null);
                break;
            }
            case "sortedjarowinkler": {
                //using levenshtein metric for mismatch terms for sorted jaro-winkler
                result = Levenshtein.computeSimilarity(a, b, null);
                break;
            }
            case "longestcommonsubsequence": {
                result = LongestCommonSubsequenceMetric.computeSimilarity(a, b);
                break;
            }
            case "2Gram": {
                result = NGram.computeSimilarity(a, b, 2);
                break;
            }
            default:
                logger.error("Similarity: \"" + similarity + "\" does not exist for weighted literals.");
                throw new RuntimeException();
        }
        return result;
    }

    private static double computeWeights(CategoryWeight categorySimilarity, double baseSim, double mismatchSim, double specialsSim, double termSim) {

        double baseWeight = SpecificationConstants.BASE_WEIGHT;
        double mismatchWeight = SpecificationConstants.MISMATCH_WEIGHT;
        double mergedBaseMismatchWeight = SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT;
        double specialsWeight = SpecificationConstants.SPECIAL_WEIGHT;
        double termWeight = SpecificationConstants.LINKED_TERM_WEIGHT;

        if (categorySimilarity.isZeroBaseSimilarity()) {
            return mismatchSim * mergedBaseMismatchWeight + specialsSim * specialsWeight + termSim * termWeight;
        } else {
            return baseSim * baseWeight + mismatchSim * mismatchWeight + specialsSim * specialsWeight + termSim * termWeight;
        }
    }
    
    private static double computeJaroSimilarityPerWord(String a, String b) {
        
        if(StringUtils.isBlank(a) ||  StringUtils.isBlank(b)){
            return 0;
        }
        
        double result;
        //compute per word and average. (The base category contains only matched words)
        String[] tokensA = tokenize(a);
        String[] tokensB = tokenize(b);
        if ((tokensA.length == tokensB.length) && tokensA.length != 0) {
            double sum = 0;
            
            for (int i = 0; i < tokensA.length; i++) {
                sum = sum + Jaro.computeSimilarity(tokensA[i], tokensB[i]);
            }
            
            result = sum / (double) tokensA.length;
            
        } else {
            result = Jaro.computeSimilarity(a, b);
        }
        return result;
    }

    private static double computeJaroWinklerSimilarityPerWord(String a, String b) {
        
        if(StringUtils.isBlank(a) ||  StringUtils.isBlank(b)){
            return 0;
        }
        
        double result;
        //compute per word and average. (The base category contains only matched words)
        String[] tokensA = tokenize(a);
        String[] tokensB = tokenize(b);
        if ((tokensA.length == tokensB.length) && tokensA.length != 0) {
            double sum = 0;
            
            for (int i = 0; i < tokensA.length; i++) {
                sum = sum + JaroWinkler.computeSimilarity(tokensA[i], tokensB[i]);
            }
            
            result = sum / (double) tokensA.length;
            
        } else {
            result = JaroWinkler.computeSimilarity(a, b);
        }
        return result;
    }

    private static double computeSortedJaroWinklerSimilarityPerWord(String a, String b) {
        
        if(StringUtils.isBlank(a) ||  StringUtils.isBlank(b)){
            return 0;
        }
        
        double result;
        //compute per word and average. (The base category contains only matched words)
        String[] tokensA = tokenize(a);
        String[] tokensB = tokenize(b);
        if ((tokensA.length == tokensB.length) && tokensA.length != 0) {
            double sum = 0;
            
            for (int i = 0; i < tokensA.length; i++) {
                sum = sum + SortedJaroWinkler.computeSimilarity(tokensA[i], tokensB[i]);
            }
            
            result = sum / (double) tokensA.length;
            
        } else {
            result = SortedJaroWinkler.computeSimilarity(a, b);
        }
        return result;
    }
    
    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }
}
