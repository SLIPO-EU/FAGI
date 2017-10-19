package gr.athena.innovation.fagi.core.similarity;

import org.apache.lucene.search.spell.NGramDistance;

/**
 * Class for computing n-gram distance of two strings using the org.apache.lucene.search.spell implementation.
 * 
 * @author nkarag
 */
public class NGram {

    /**
     * Creates a 2-Gram distance measure using n-grams of size 2.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the computed 2-gram distance.
     */
    public static float computeDistance(String a, String b){
        NGramDistance n = new NGramDistance();
        float result = n.getDistance(a, b);
        
        return result;
    }
    
    /**
     * Creates an N-Gram distance measure using n-grams of the given size.
     * 
     * @param a the first string.
     * @param b the second string.
     * @param size the size of the n-grams distance measure.
     * @return the computed n-gram distance.
     */
    public static float computeDistance(String a, String b, int size){
        NGramDistance n = new NGramDistance(size);
        float result = n.getDistance(a, b);
        
        return result;
    }    
}
