package gr.athena.innovation.fagi.core.similarity;

import org.apache.commons.text.similarity.LevenshteinDetailedDistance;
import org.apache.logging.log4j.LogManager;

/**
 * Class for computing a normalized value for the Levenshtein Distance of two strings.
 * 
 * Provides methods for computing the number of insertions, deletions or substitutions needed to get from one string
 * to the other.
 * 
 * @author nkarag
 */
public final class Levenshtein {

    private static final org.apache.logging.log4j.Logger logger = 
            LogManager.getLogger(Levenshtein.class);

    /**
     * Computes a normalized value of the Levenshtein Distance for the given strings. 
     * The result is normalized at [0,1] by dividing with the maximum possible distance for the given pair of strings.
     * The maximum possible distance is the length of the longer string (all the characters are different).
     * The method returns 1.0, if the distance is greater than the threshold.
     * 
     * @param a the first string.
     * @param b the second string.
     * @param threshold the threshold, must not be negative, 
     * may be null if the desired distance should be computed without a threshold.
     * @return the normalized distance result. Returns 1.0 if the distance is greater than the threshold.
     */
    public static double computeDistance(String a, String b, Integer threshold){
        //TODO: add weights for insertions, deletions, subs and return 1 if the distance is greater than the max lengths.
        
        //https://stackoverflow.com/questions/6629712/levensteindistance-commons-lang-3-0-api
        try {

            //Get max length of the two strings.
            int maxLength = (a.length() > b.length()) ? a.length() : b.length();

            //divide result distance with the max length.
            LevenshteinDetailedDistance ld = new LevenshteinDetailedDistance(threshold);
            int result = ld.apply(a, b).getDistance();

            logger.trace(result + ": Levenshtein Distance for strings: " + a + " <-> " + b);

            double normalizedResult = (double) result/ (double) maxLength;

            logger.trace("Normalized value: " + normalizedResult + " " + result +" / " + maxLength);

            if(result == -1){
                return 1;
            }
            
            if(normalizedResult > 1){
                return 1;
            } else if(normalizedResult < 0){
                return 0;
            }

            return normalizedResult;            
         
        } catch (IllegalArgumentException ex){
            logger.error("Threshold must not be negative", ex);
            return 1;
        }
    }
    
    /**
     * Compute the number of insertions needed to change one string into another.
     * The method returns -1, if the distance is greater than the threshold.
     * 
     * @param a the first string.
     * @param b the second string.
     * @param threshold the threshold, must not be negative, 
     * may be null if the desired distance should be computed without a threshold.
     * @return the distance result. Returns -1 if the distance is greater than the threshold.
     */
    public static int computeInsertions(String a, String b, Integer threshold){

        try {

            LevenshteinDetailedDistance ld = new LevenshteinDetailedDistance(threshold);
            int result = ld.apply(a, b).getDistance();

            return result;            
         
        } catch (IllegalArgumentException ex){
            logger.error("Threshold must not be negative", ex);
            return -1;
        }
    }

    /**
     * Compute the number of character deletion needed to change one string to the other.
     * The method returns -1, if the distance is greater than the threshold.
     * 
     * @param a the first string.
     * @param b the second string.
     * @param threshold the threshold, must not be negative, 
     * may be null if the desired distance should be computed without a threshold.
     * @return the distance result. Returns -1 if the distance is greater than the threshold.
     */
    public static int computeDeletes(String a, String b, Integer threshold){

        try {
            
            LevenshteinDetailedDistance ld = new LevenshteinDetailedDistance(threshold);
            int result = ld.apply(a, b).getDeleteCount();

            return result;            
         
        } catch (IllegalArgumentException ex){
            logger.error("Threshold must not be negative", ex);
            return -1;
        }
    }  

    /**
     * Compute  the number of character substitution needed to change one string to the other.
     * The method returns -1, if the distance is greater than the threshold.
     * 
     * @param a the first string.
     * @param b the second string.
     * @param threshold the threshold, must not be negative, 
     * may be null if the desired distance should be computed without a threshold.
     * @return the distance result. Returns -1 if the distance is greater than the threshold.
     */
    public static int computeSubstitutes(String a, String b, Integer threshold){

        try {
            
            LevenshteinDetailedDistance ld = new LevenshteinDetailedDistance(threshold);
            int result = ld.apply(a, b).getSubstituteCount();

            return result;            
         
        } catch (IllegalArgumentException ex){
            logger.error("Threshold must not be negative", ex);
            return -1;
        }
    }     
}
