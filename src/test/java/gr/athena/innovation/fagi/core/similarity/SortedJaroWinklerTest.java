package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author nkarag
 */
public class SortedJaroWinklerTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(SortedJaroWinklerTest.class);

    /**
     * Test of computeSimilarity method, of class SortedJaroWinkler.
     */
    @Test
    public void testComputeSimilarity() {
        logger.info("computeSimilarity");

        double expResult1 = 0.0; //jaro-winkler similarity is zero for any zero string length.
        double result1 = SortedJaroWinkler.computeSimilarity("", ""); 
        assertEquals(expResult1, result1, 0.0);

        double expResult2 = 0.0;
        double result2 = SortedJaroWinkler.computeSimilarity("","a");
        assertEquals(expResult2, result2, 0.0);      

        String a = "we should be same";
        String b = "same we should be";
        double expResult3 = 1.0;
        double result3 = SortedJaroWinkler.computeSimilarity(a, b);
        assertEquals(expResult3, result3, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);

        String c = "abcdm mjkl";
        String d = "efghm mnop";
        double expResult4 = 0.5;
        double result4 = SortedJaroWinkler.computeSimilarity(c, d);
        assertEquals(expResult4, result4, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);
    }
    
}
