package gr.athena.innovation.fagi.core.similarity;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class JaroWinkler {
    private static final org.apache.logging.log4j.Logger logger = 
            LogManager.getLogger(JaroWinkler.class);

    /**
     * Computes the Jaro Winkler Distance which indicates the similarity score between two strings.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the distance. Range is between [0,1].
     */
    public static double compute(String a, String b){

        JaroWinklerDistance j = new JaroWinklerDistance();
        Double distance = j.apply(b, b);

        return distance;
    }    
}
