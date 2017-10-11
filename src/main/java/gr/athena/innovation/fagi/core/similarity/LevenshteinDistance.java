package gr.athena.innovation.fagi.core.similarity;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * Class for computing a normalized value for the Levenshtein Distance of two strings.
 * 
 * @author nkarag
 */
public class LevenshteinDistance {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LevenshteinDistance.class);
    
    /**
     * Computes a normalized value of the Levenshtein Distance for the given strings. 
     * The result is normalized at [0,1] by dividing with the maximum possible distance for the given pair of strings.
     * The maximum possible distance is the length of the longer string (all the characters are different).
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the normalized distance result.
     */
    public static double compute(String a, String b){
        //https://stackoverflow.com/questions/6629712/levensteindistance-commons-lang-3-0-api
        //Get max length of the two strings.
        int maxLength = (a.length() > b.length()) ? a.length() : b.length();

        //divide result distance with the max length.
        int result = StringUtils.getLevenshteinDistance(a, b);

        logger.trace(result + ": Levenshtein Distance for strings: " + a + " <-> " + b);

        double normalizedResult = (double) result/ (double) maxLength;

        logger.trace("Normalized value: " + normalizedResult + " " + result +" / " + maxLength);
        
        if(normalizedResult > 1){
            return 1;
        } else if(normalizedResult < 0){
            return 0;
        }

        return normalizedResult;
    }
}
