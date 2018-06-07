package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.logging.log4j.LogManager;

/**
 * Class providing methods for computing Jaro Similarity and Jaro Distance.
 *
 * @author nkarag
 */
public class Jaro {

    private static final org.apache.logging.log4j.Logger LOG
            = LogManager.getLogger(Jaro.class);

    /**
     * Computes the Jaro Distance which indicates the similarity score between two strings.
     * <a href="https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance#Jaro_distance">
     * https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance#Jaro_distance</a>.
     *
     * @param a the first string.
     * @param b the second string.
     * @return the distance. Range is between [0,1].
     */
    public static double computeSimilarity(String a, String b) {

        Jaro jaro = new Jaro();

        double result = jaro.apply(a, b);

        if (result > SpecificationConstants.Similarity.SIMILARITY_MAX) {
            return 1;
        } else if (result < SpecificationConstants.Similarity.SIMILARITY_MIN) {
            return 0;
        } else {
            double roundedResult = new BigDecimal(result).
                    setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_3, RoundingMode.HALF_UP).doubleValue();

            return roundedResult;
        }
    }

    /**
     * Computes the Jaro Distance using the complement of
     * <a href="https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance#Jaro_distance">
     * https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance#Jaro_distance</a>.
     *
     * @param a the first string.
     * @param b the second string.
     * @return the distance. Range is between [0,1].
     */
    public static double computeDistance(String a, String b) {
        return 1 - computeSimilarity(a, b);
    }

    private double apply(String a, String b) {
        int aLength = a.length();
        int bLength = b.length();

        if (aLength == 0 || bLength == 0) {
            return 1;
        }

        int matchDistance = Integer.max(aLength, bLength) / 2 - 1;

        boolean[] aMatches = new boolean[aLength];
        boolean[] bMatches = new boolean[bLength];

        int matches = 0;
        int transpositions = 0;

        for (int i = 0; i < aLength; i++) {
            int start = Integer.max(0, i - matchDistance);
            int end = Integer.min(i + matchDistance + 1, bLength);

            for (int j = start; j < end; j++) {
                if (bMatches[j]) {
                    continue;
                }
                if (a.charAt(i) != b.charAt(j)) {
                    continue;
                }
                aMatches[i] = true;
                bMatches[j] = true;
                matches++;
                break;
            }
        }

        if (matches == 0) {
            return 0;
        }

        int k = 0;
        for (int i = 0; i < aLength; i++) {
            if (!aMatches[i]) {
                continue;
            }
            while (!bMatches[k]) {
                k++;
            }
            if (a.charAt(i) != b.charAt(k)) {
                transpositions++;
            }
            k++;
        }

        double aFraction = (double) matches / aLength;
        double bFraction = (double) matches / bLength;
        double transp = ((double) matches - transpositions / 2.0);

        return (aFraction + bFraction + (transp / matches)) / 3.0;
    }

}
