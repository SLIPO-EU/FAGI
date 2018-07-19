package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class LongestCommonSubsequenceMetricTest {
    
private static final org.apache.logging.log4j.Logger LOG 
        = LogManager.getLogger(LongestCommonSubsequenceMetricTest.class);

    /**
     * Test of computeSimilarity method, of class LongestCommonSubsequenceMetric.
     */
    @Test
    public void testComputeSimilarity() {
        LOG.info("computeSimilarity");
        
        double expResult1 = 0.0;
        double result1 = LongestCommonSubsequenceMetric.computeSimilarity("", "");
        //LOG.debug(result1);
        assertEquals(expResult1, result1, 0.0);

        double expResult2 = 1.0;
        double result2 = LongestCommonSubsequenceMetric.computeSimilarity("same", "same");
        //LOG.debug(result2);
        assertEquals(expResult2, result2, 0.0);

        double expResult3 = 0.0;
        double result3 = LongestCommonSubsequenceMetric.computeSimilarity("foo", "bar");
        //LOG.debug(result3);
        assertEquals(expResult3, result3, 0.0);
        
        String a = "This is a foo bar string.";
        String b = "This string is similar to a foo bar string.";
        double expResult4 = 0.58;
        double result4 = LongestCommonSubsequenceMetric.computeSimilarity(a, b);
        //LOG.debug(result4);
        assertEquals(expResult4, result4, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);

    }

    /**
     * Test of computeDistance method, of class LongestCommonSubsequenceMetric.
     */
    @Test
    public void testComputeDistance() {
        LOG.info("computeDistance");
        
        double expResult1 = 1.0 - 0.0;
        double result1 = LongestCommonSubsequenceMetric.computeDistance("", "");
        //LOG.debug(result1);
        assertEquals(expResult1, result1, 0.0);

        double expResult2 = 1.0 - 1.0;
        double result2 = LongestCommonSubsequenceMetric.computeDistance("same", "same");
        //LOG.debug(result2);
        assertEquals(expResult2, result2, 0.0);

        double expResult3 = 1.0 - 0.0;
        double result3 = LongestCommonSubsequenceMetric.computeDistance("foo", "bar");
        //LOG.debug(result3);
        assertEquals(expResult3, result3, 0.0);
        
        String a = "This is a foo bar string.";
        String b = "This string is similar to a foo bar string.";
        double expResult4 = 1.0 - 0.58;
        double result4 = LongestCommonSubsequenceMetric.computeDistance(a, b);
        //LOG.debug(result4);
        assertEquals(expResult4, result4, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);
    }
    
}
