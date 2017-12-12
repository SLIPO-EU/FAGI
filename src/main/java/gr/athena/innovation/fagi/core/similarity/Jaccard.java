package gr.athena.innovation.fagi.core.similarity;

import org.apache.commons.text.similarity.JaccardDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;

/**
 * Class providing methods for computing Jaccard Distance and Jaccard Similarity.
 * 
 * @author nkarag
 */
public class Jaccard {

    /**
     * Computes the Jaccard distance for the given Strings.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the computed Jaccard distance.
     */
    public static double computeDistance(String a, String b){
        JaccardDistance jaccardDistance = new JaccardDistance();
        
        double result = jaccardDistance.apply(a, b);
        
        return result;
    }

    /**
     * Computes the Jaccard distance for the given Strings.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the computed Jaccard similarity.
     */
    public static double computeSimilarity(String a, String b){
        
        JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
        
        double result = jaccardSimilarity.apply(a, b);
        
        return result;
    }
    
}
