package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class PermutedJaroWinklerTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(PermutedJaroWinklerTest.class);

    /**
     * Test of computeSimilarity method, of class PermutedJaroWinkler.
     */
    @Test
    public void testComputeSimilarity() {
        LOG.info("computeSimilarity");
        
        PermutedJaroWinkler permutedJaroWinkler = new PermutedJaroWinkler();

        double expResult1 = 0.0; //jaro-winkler similarity is zero for any zero string length.
        double result1 = permutedJaroWinkler.computeSimilarity("", ""); 
        assertEquals(expResult1, result1, 0.0);

        double expResult2 = 0.0;
        double result2 = permutedJaroWinkler.computeSimilarity("","a");
        assertEquals(expResult2, result2, 0.0);      

        String a = "we should be same";
        String b = "same we should be";
        double expResult3 = 1.0;
        double result3 = permutedJaroWinkler.computeSimilarity(a, b);
        assertEquals(expResult3, result3, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);

        String c = "abcdm mjkl";
        String d = "efghm mnop";
        double expResult4 = 0.5;
        double result4 = permutedJaroWinkler.computeSimilarity(c, d);
        assertEquals(expResult4, result4, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);        
    }
}
