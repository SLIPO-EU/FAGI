package gr.athena.innovation.fagi.core.similarity;

import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.apache.logging.log4j.LogManager;

/**
 * Class for computing the longest common subsequence.
 * 
 * @author nkarag
 */
public class LongestCommonSubsequenceMetric {

    private static final org.apache.logging.log4j.Logger LOG = 
            LogManager.getLogger(LongestCommonSubsequenceMetric.class);    
    /**
     * Computes the longest Common Subsequence similarity score of the two given strings.
     * Normalizes the output score between [0,1] by dividing the output with the max length of the two strings.
     * 
     * Two strings that are entirely different, return a value of 0, and two strings that return a value of 1
     * implies that the strings are completely the same.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the normalized similarity score.
     */
    public static double computeSimilarity(String a, String b){

        LongestCommonSubsequence longestCommonSubsequence = new LongestCommonSubsequence();

        //Get max length of the two strings.
        int maxLength = (a.length() > b.length()) ? a.length() : b.length();

        if(maxLength == 0){
            return 0.0;    
        }
        
        Integer result = longestCommonSubsequence.apply(a, b);

        double normalizedResult = (double) result/ (double) maxLength;

        return normalizedResult;
    }

    /**
     * Returns the complement of the {@link gr.athena.innovation.fagi.core.similarity.LongestCommonSubsequenceMetric#computeSimilarity(String,String) computeSimilarity} method.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the normalized distance.
     */
    public static double computeDistance(String a, String b){
        
        double similarity = computeSimilarity(a,b);
        double distance = 1 - similarity;
        
        return distance;
    }    
}
