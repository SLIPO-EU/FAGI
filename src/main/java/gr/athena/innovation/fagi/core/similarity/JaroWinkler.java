package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public final class JaroWinkler {
    private static final org.apache.logging.log4j.Logger logger = 
            LogManager.getLogger(JaroWinkler.class);

    /**
     * Computes the Jaro Winkler Distance which indicates the similarity score between two strings.
     * <a href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">
     * http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance</a>.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the distance. Range is between [0,1].
     */
    public static double computeSimilarity(String a, String b){

        //The class is named Distance, but it returns similarity score. 
        //TODO: check issue progress at https://issues.apache.org/jira/browse/TEXT-104 for any change
        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();

        double result = jaroWinkler.apply(a, b);

        if(result > SpecificationConstants.SIMILARITY_MAX){
            return 1;
        } else if(result < SpecificationConstants.SIMILARITY_MIN){
            return 0;
        } else {
            double roundedResult = new BigDecimal(result).
                    setScale(SpecificationConstants.ROUND_DECIMALS, RoundingMode.HALF_UP).doubleValue();

            return roundedResult;
        }
    }    
}
