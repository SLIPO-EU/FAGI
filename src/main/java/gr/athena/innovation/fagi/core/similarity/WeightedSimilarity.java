package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.model.CategoryWeight;
import gr.athena.innovation.fagi.model.CommonSpecialTerm;
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

    private static boolean useLengths = true;

    /**
     * Computes the provided distance for the given normalized literals.
     *
     * @param normA the first normalized literal
     * @param normB the second normalized literal
     * @param distance the distance.
     * @return the given distance using normalized literals.
     */
    public static double computeBDistance(NormalizedLiteral normA, NormalizedLiteral normB, String distance) {

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
    public static double computeBSimilarity(NormalizedLiteral normA, NormalizedLiteral normB, String similarity) {

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
     * Computes the provided distance for the given custom normalized literals. Uses the 'C' weights tuning.
     *
     * @param pair the pair of custom normalized literals
     * @param distance the distance.
     * @return the given distance using normalized literals.
     */
    public static double computeCDistance(String distance, WeightedPairLiteral pair) {

        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();

        Set<CommonSpecialTerm> terms = pair.getCommonSpecialTerms();

        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();

        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();

        CategoryWeight categoryWeight = new CategoryWeight(pair);

        double baseDis;
        double specialsDis;
        double mismatchDis;
        double termDis;

        if (categoryWeight.isZeroBaseSimilarity()) {
            baseDis = 1;
        } else {
            baseDis = computeBaseDistance(distance, baseA, baseB);
        }

        if (categoryWeight.isHalfEmptyMismatch() || categoryWeight.isFullEmptyMismatch()) {
            mismatchDis = 1;
        } else {
            mismatchDis = computeMismatchDistance(distance, mismatchA, mismatchB);
        }

        if (categoryWeight.isEmptySpecials()) {
            if (categoryWeight.isZeroBaseSimilarity()) {
                specialsDis = mismatchDis;
            } else {
                specialsDis = baseDis;
            }
        } else {
            specialsDis = computeBaseDistance(distance, specialsA, specialsB);
        }

        if (!terms.isEmpty()) {
            termDis = 0.0;
        } else if (categoryWeight.isZeroBaseSimilarity()) {
            termDis = mismatchDis;
        } else {
            termDis = baseDis;
        }

        return computeCWeights(pair, categoryWeight, baseDis, mismatchDis, specialsDis, termDis);
    }

    /**
     * Computes the provided distance for the given custom normalized literals. Uses the 'D' weights tuning.
     *
     * @param pair the pair of custom normalized literals
     * @param distance the distance.
     * @return the given distance using normalized literals.
     */
    public static double computeDDistance(String distance, WeightedPairLiteral pair) {

        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();

        Set<CommonSpecialTerm> terms = pair.getCommonSpecialTerms();

        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();

        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();

        CategoryWeight categoryWeight = new CategoryWeight(pair);

        double baseDis;
        double specialsDis;
        double mismatchDis;
        double termDis;

        if (categoryWeight.isZeroBaseSimilarity()) {
            baseDis = 1;
        } else {
            baseDis = computeBaseDistance(distance, baseA, baseB);
        }

        if (categoryWeight.isHalfEmptyMismatch() || categoryWeight.isFullEmptyMismatch()) {
            mismatchDis = 1;
        } else {
            mismatchDis = computeMismatchDistance(distance, mismatchA, mismatchB);
        }

        if (categoryWeight.isEmptySpecials()) {
            if (categoryWeight.isZeroBaseSimilarity()) {
                specialsDis = mismatchDis;
            } else {
                specialsDis = baseDis;
            }
        } else {
            specialsDis = computeBaseDistance(distance, specialsA, specialsB);
        }

        if (!terms.isEmpty()) {
            termDis = 0.0;
        } else if (categoryWeight.isZeroBaseSimilarity()) {
            termDis = mismatchDis;
        } else {
            termDis = baseDis;
        }

        return computeDWeights(pair, categoryWeight, baseDis, mismatchDis, specialsDis, termDis);
    }
    
    /**
     * Computes the provided similarity for the given custom normalized literals. Uses the 'C' weights tuning.
     *
     * @param pair the pair of custom normalized literals
     * @param similarity the similarity.
     * @return the given distance using normalized literals.
     */
    public static double computeCSimilarity(WeightedPairLiteral pair, String similarity) {

        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();

        Set<CommonSpecialTerm> terms = pair.getCommonSpecialTerms();

        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();

        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();

        CategoryWeight categoryWeight = new CategoryWeight(pair);

        double baseSim;
        double mismatchSim;
        double specialsSim;
        double termSim;

        if (categoryWeight.isZeroBaseSimilarity()) {
            baseSim = 0;
        } else {
            baseSim = computeBaseSimilarity(similarity, baseA, baseB);
        }

        if (categoryWeight.isHalfEmptyMismatch() || categoryWeight.isFullEmptyMismatch()) {
            mismatchSim = 0;
        } else {
            mismatchSim = computeMismatchSimilarity(similarity, mismatchA, mismatchB);
        }

        if (categoryWeight.isEmptySpecials()) {
            if (categoryWeight.isZeroBaseSimilarity()) {
                specialsSim = mismatchSim;
            } else {
                specialsSim = baseSim;
            }
        } else {
            specialsSim = computeBaseSimilarity(similarity, specialsA, specialsB);
        }

        if (!terms.isEmpty()) {
            termSim = 1.0;
        } else if (categoryWeight.isZeroBaseSimilarity()) {
            termSim = mismatchSim;
        } else {
            termSim = baseSim;
        }

        return computeCWeights(pair, categoryWeight, baseSim, mismatchSim, specialsSim, termSim);
    }

    /**
     * Computes the provided similarity for the given custom normalized literals.  Uses the 'D' weights tuning.
     *
     * @param pair the pair of custom normalized literals
     * @param similarity the similarity.
     * @return the given distance using normalized literals.
     */
    public static double computeDSimilarity(WeightedPairLiteral pair, String similarity) {

        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();

        Set<CommonSpecialTerm> terms = pair.getCommonSpecialTerms();

        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();

        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();

        CategoryWeight categoryWeight = new CategoryWeight(pair);

        double baseSim;
        double mismatchSim;
        double specialsSim;
        double termSim;

        if (categoryWeight.isZeroBaseSimilarity()) {
            baseSim = 0;
        } else {
            baseSim = computeBaseSimilarity(similarity, baseA, baseB);
        }

        if (categoryWeight.isHalfEmptyMismatch() || categoryWeight.isFullEmptyMismatch()) {
            mismatchSim = 0;
        } else {
            mismatchSim = computeMismatchSimilarity(similarity, mismatchA, mismatchB);
        }

        if (categoryWeight.isEmptySpecials()) {
            if (categoryWeight.isZeroBaseSimilarity()) {
                specialsSim = mismatchSim;
            } else {
                specialsSim = baseSim;
            }
        } else {
            specialsSim = computeBaseSimilarity(similarity, specialsA, specialsB);
        }

        if (!terms.isEmpty()) {
            termSim = 1.0;
        } else if (categoryWeight.isZeroBaseSimilarity()) {
            termSim = mismatchSim;
        } else {
            termSim = baseSim;
        }

        return computeDWeights(pair, categoryWeight, baseSim, mismatchSim, specialsSim, termSim);
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
                result = 1 - computeJaroSimilarityPerWord(a, b);
                break;
            }
            case "jarowinkler": {
                result = 1 - computeJaroWinklerSimilarityPerWord(a, b);
                break;
            }
            case "sortedjarowinkler": {
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
                result = computeJaroSimilarityPerWord(a, b);
                break;
            }
            case "jarowinkler": {
                result = computeJaroWinklerSimilarityPerWord(a, b);
                break;
            }
            case "sortedjarowinkler": {
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

    private static double computeCWeights(WeightedPairLiteral pair, CategoryWeight categorySimilarity,
            double baseSim, double mismatchSim, double specialsSim, double termSim) {

        double baseWeight = SpecificationConstants.BASE_WEIGHT;
        double mismatchWeight = SpecificationConstants.MISMATCH_WEIGHT;
        double mergedBaseMismatchWeight = SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT;
        double specialsWeight = SpecificationConstants.SPECIAL_TERMS_WEIGHT;
        double termWeight = SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT;

        if (useLengths) {

            int b1Length = pair.getBaseValueA().length();
            int b2Length = pair.getBaseValueB().length();

            int m1Length = pair.mismatchToStringA().length();
            int m2Length = pair.mismatchToStringB().length();

            int s1Length = pair.specialTermsToStringA().length();
            int s2Length = pair.specialTermsToStringB().length();

            int commonTermLength = pair.commonTermsToString().length();

            double baseWR = getWeightR(b1Length, b2Length, categorySimilarity.isZeroBaseSimilarity());
            double mismatchWR = getWeightR(m1Length, m2Length, categorySimilarity.isFullEmptyMismatch());
            double specialsWR = getWeightR(s1Length, s2Length, categorySimilarity.isEmptySpecials());
            double commonTermWR = getWeightR(commonTermLength, commonTermLength, categorySimilarity.isEmptyCommon());

            double result;

            if (categorySimilarity.isZeroBaseSimilarity()) {

                double merged = mismatchWR * mismatchSim * mergedBaseMismatchWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;        
                
                result = (merged + specialTerm + commonTerm) / (mergedBaseMismatchWeight * mismatchWR + specialsWR + commonTermWR);
                
            } else if (categorySimilarity.isFullEmptyMismatch()) {
                
                double base = baseWR * baseSim * mergedBaseMismatchWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;

                result = (base + specialTerm + commonTerm)
                        / (mergedBaseMismatchWeight * baseWR + specialsWeight * specialsWR + termWeight * commonTermWR);
                
            } else if (categorySimilarity.isHalfEmptyMismatch()) {
                
                double base = baseWR * baseSim * baseWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;
                
                result = (base + specialTerm + commonTerm)
                        / (baseWeight * baseWR + mismatchWeight * mismatchWR + specialsWeight * specialsWR + termWeight * commonTermWR);
            } else {
                
                double base = baseWR * baseSim * baseWeight;
                double mismatch = mismatchWR * mismatchSim * mismatchWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;
                
                result = (base + mismatch + specialTerm + commonTerm)
                        / (baseWeight * baseWR + mismatchWeight * mismatchWR + specialsWeight * specialsWR + termWeight * commonTermWR);
            }
            return result;

        } else if (categorySimilarity.isZeroBaseSimilarity()) {
            return mismatchSim * mergedBaseMismatchWeight + specialsSim * specialsWeight + termSim * termWeight;
        } else {
            return baseSim * baseWeight + mismatchSim * mismatchWeight + specialsSim * specialsWeight + termSim * termWeight;
        }
    }

    private static double computeDWeights(WeightedPairLiteral pair, CategoryWeight categorySimilarity,
            double baseSim, double mismatchSim, double specialsSim, double termSim) {

        double baseWeight = SpecificationConstants.BASE_WEIGHT;
        double mismatchWeight = SpecificationConstants.MISMATCH_WEIGHT;
        double mergedBaseMismatchWeight = SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT;
        double specialsWeight = SpecificationConstants.SPECIAL_TERMS_WEIGHT;
        double termWeight = SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT;
        
        if (useLengths) {
            
            int aMismatchSize = pair.getMismatchTokensA().size();
            int bMismatchSize = pair.getMismatchTokensB().size();

            int b1Length = pair.getBaseValueA().length();
            int b2Length = pair.getBaseValueB().length();

            int m1Length = pair.mismatchToStringA().length();
            int m2Length = pair.mismatchToStringB().length();

            int s1Length = pair.specialTermsToStringA().length();
            int s2Length = pair.specialTermsToStringB().length();

            int commonTermLength = pair.commonTermsToString().length();

            double baseWR = getWeightR(b1Length, b2Length, categorySimilarity.isZeroBaseSimilarity());
            double mismatchWR = getWeightR(m1Length, m2Length, categorySimilarity.isFullEmptyMismatch());
            double specialsWR = getWeightR(s1Length, s2Length, categorySimilarity.isEmptySpecials());
            double commonTermWR = getWeightR(commonTermLength, commonTermLength, categorySimilarity.isEmptyCommon());

            double result;

            if (categorySimilarity.isZeroBaseSimilarity()) {

                double merged = mismatchWR * mismatchSim * mergedBaseMismatchWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;        
                
                result = (merged + specialTerm + commonTerm) / (mergedBaseMismatchWeight * mismatchWR + specialsWR + commonTermWR);
                
            } else if (categorySimilarity.isFullEmptyMismatch()) {
                
                double base = baseWR * baseSim * mergedBaseMismatchWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;

                result = (base + specialTerm + commonTerm)
                        / (mergedBaseMismatchWeight * baseWR + specialsWeight * specialsWR + termWeight * commonTermWR);
                
            } else if (categorySimilarity.isHalfEmptyMismatch()) {
                
                int max;
                if(aMismatchSize > bMismatchSize){
                    max = aMismatchSize;
                } else {
                    max = bMismatchSize;
                }
                
                double base = baseWR * baseSim * baseWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;
                
                if(max == 1){
                    result = (base + specialTerm + commonTerm)
                            / (baseWeight * baseWR + mismatchWeight * mismatchWR + specialsWeight * specialsWR + termWeight * commonTermWR);                    
                } else {
                    result = (base + specialTerm + commonTerm)
                            / (baseWeight * baseWR + specialsWeight * specialsWR + termWeight * commonTermWR);                      
                }
            } else {
                
                double base = baseWR * baseSim * baseWeight;
                double mismatch = mismatchWR * mismatchSim * mismatchWeight;
                double specialTerm = specialsWR * specialsSim * specialsWeight;
                double commonTerm = commonTermWR * termSim * termWeight;
                
                result = (base + mismatch + specialTerm + commonTerm)
                        / (baseWeight * baseWR + mismatchWeight * mismatchWR + specialsWeight * specialsWR + termWeight * commonTermWR);
            }
            return result;

        } else if (categorySimilarity.isZeroBaseSimilarity()) {
            return mismatchSim * mergedBaseMismatchWeight + specialsSim * specialsWeight + termSim * termWeight;
        } else {
            return baseSim * baseWeight + mismatchSim * mismatchWeight + specialsSim * specialsWeight + termSim * termWeight;
        }
    }
    
    private static double getWeightR(int length1, int length2, boolean zeroSim) {
        if (zeroSim) {
            return 0;
        } else {
            return (length1 + length2);
            //return Math.sqrt(length1 + length2);
        }
    }

    private static double computeJaroSimilarityPerWord(String a, String b) {

        if (StringUtils.isBlank(a) || StringUtils.isBlank(b)) {
            return 0;
        }

        //compute per word and average. (The base category contains only matched words)
        String[] tokensA = tokenize(a);
        String[] tokensB = tokenize(b);

        double sum = 0;
        int minLen;
        int maxLen;
        boolean isAmax;

        if (tokensA.length > tokensB.length) {
            maxLen = tokensA.length;
            minLen = tokensB.length;
            isAmax = true;
        } else {
            isAmax = false;
            maxLen = tokensB.length;
            minLen = tokensA.length;
        }

        double denom = 0;

        for (int i = 0; i < maxLen; i++) {

            if (i < minLen) {

                int tempLenA = tokensA[i].length();
                int tempLenB = tokensB[i].length();
                double averageLen = (tempLenA + tempLenB) / 2;
                denom = denom + averageLen;

                sum = sum + averageLen * Jaro.computeSimilarity(tokensA[i], tokensB[i]);

            } else if (isAmax) {
                denom = denom + tokensA[i].length() / 2;
            } else {
                denom = denom + tokensB[i].length() / 2;
            }
        }

        return sum / (double) denom;
    }

    private static double computeJaroWinklerSimilarityPerWord(String a, String b) {

        if (StringUtils.isBlank(a) || StringUtils.isBlank(b)) {
            return 0;
        }

        //compute per word and average. (The base category contains only matched words)
        String[] tokensA = tokenize(a);
        String[] tokensB = tokenize(b);
        double sum = 0;
        int minLen;
        int maxLen;
        boolean isAmax;

        if (tokensA.length > tokensB.length) {
            maxLen = tokensA.length;
            minLen = tokensB.length;
            isAmax = true;
        } else {
            isAmax = false;
            maxLen = tokensB.length;
            minLen = tokensA.length;
        }

        double denom = 0;

        for (int i = 0; i < maxLen; i++) {

            if (i < minLen) {

                int tempLenA = tokensA[i].length();
                int tempLenB = tokensB[i].length();
                double averageLen = (tempLenA + tempLenB) / 2;
                denom = denom + averageLen;

                sum = sum + averageLen * JaroWinkler.computeSimilarity(tokensA[i], tokensB[i]);

            } else if (isAmax) {
                denom = denom + tokensA[i].length() / 2;
            } else {
                denom = denom + tokensB[i].length() / 2;
            }
        }

        return sum / (double) denom;
    }

    private static double computeSortedJaroWinklerSimilarityPerWord(String a, String b) {

        if (StringUtils.isBlank(a) || StringUtils.isBlank(b)) {
            return 0;
        }

        //compute per word and average. (The base category contains only matched words)
        String[] tokensA = tokenize(a);
        String[] tokensB = tokenize(b);

        double sum = 0;
        int minLen;
        int maxLen;
        boolean isAmax;

        if (tokensA.length > tokensB.length) {
            maxLen = tokensA.length;
            minLen = tokensB.length;
            isAmax = true;
        } else {
            isAmax = false;
            maxLen = tokensB.length;
            minLen = tokensA.length;
        }

        double denom = 0;

        for (int i = 0; i < maxLen; i++) {

            if (i < minLen) {

                int tempLenA = tokensA[i].length();
                int tempLenB = tokensB[i].length();
                double averageLen = (tempLenA + tempLenB) / 2;
                denom = denom + averageLen;

                sum = sum + averageLen * SortedJaroWinkler.computeSimilarity(tokensA[i], tokensB[i]);

            } else if (isAmax) {
                denom = denom + tokensA[i].length() / 2;
            } else {
                denom = denom + tokensB[i].length() / 2;
            }
        }

        return sum / (double) denom;
    }

    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }
}
