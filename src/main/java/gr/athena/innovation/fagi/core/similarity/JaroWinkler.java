package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.text.similarity.JaroWinklerDistance;

/**
 * Class for computing the Jaro-Winkler Distance.
 * 
 * @author nkarag
 */
public final class JaroWinkler {

    /**
     * Computes the Jaro Winkler Distance which indicates the similarity score between two strings.
     * <a href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">
     * http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance</a>.
     * 
     * @param a The first string.
     * @param b The second string.
     * @return The distance. Range is between [0,1].
     */
    public static double computeSimilarity(String a, String b){

        //The class is named Distance, but it returns similarity score. 
        //TODO: check issue progress at https://issues.apache.org/jira/browse/TEXT-104 for any change
        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();

        double result = jaroWinkler.apply(a, b);

        if(result > SpecificationConstants.Similarity.SIMILARITY_MAX){
            return 1;
        } else if(result < SpecificationConstants.Similarity.SIMILARITY_MIN){
            return 0;
        } else {
            double roundedResult = new BigDecimal(result).
                    setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_3, RoundingMode.HALF_UP).doubleValue();

            return roundedResult;
        }
    }
    
    /**
     * Computes the Jaro Winkler Distance using the complement of 
     * {@link gr.athena.innovation.fagi.core.similarity.JaroWinkler#computeSimilarity(String, String) computeSimilarity}.
     * <a href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">
     * http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance</a>.
     * 
     * @param a The first string.
     * @param b The second string.
     * @return The distance. Range is between [0,1].
     */
    public static double computeDistance(String a, String b){
        return 1 - computeSimilarity(a,b);
    }    
}
