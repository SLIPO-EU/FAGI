package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.core.normalizer.generic.AlphabeticalNormalizer;

/**
 *
 * @author nkarag
 */
public class SortedJaroWinkler {
    
    /**
     * Computes a version of Jaro-Winkler Similarity by sorting the words of each string alphabetically. 
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the result score.
     */
    public static double computeSimilarity(String a, String b){

        AlphabeticalNormalizer alphabeticalNormalizer = new AlphabeticalNormalizer();
        
        String sortedA = alphabeticalNormalizer.normalize(a);
        String sortedB = alphabeticalNormalizer.normalize(b);

        double result = JaroWinkler.computeSimilarity(sortedA, sortedB);
        
        return result;
    }
    
    /**
     * Computes a version of Jaro-Winkler Distance by using the complement of
     * {@link #computeSimilarity(double) computeSimilarity}.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the result score.
     */
    public static double computeDistance(String a, String b){
        return 1 - computeSimilarity(a, b);
    }    
}
