package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class JaroWinklerTest {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(JaroWinklerTest.class);
    
    /**
     * Test of compute method, of class JaroWinkler.
     */
    @Test
    public void testComputeDistance() {
        logger.info("computeDistance");

        double expResult1 = 0.0; //jaro-winkler similarity is zero for any zero length string
        double result1 = JaroWinkler.computeSimilarity("", ""); 
        assertEquals(expResult1, result1, 0.0);

        double expResult2 = 0.0;
        double result2 = JaroWinkler.computeSimilarity("","a");
        assertEquals(expResult2, result2, 0.0);      

        double expResult3 = 0.0;
        double result3 = JaroWinkler.computeSimilarity("aaapppp", "");
        assertEquals(expResult3, result3, 0.0);
        
        double expResult4 = 0.93;
        double result4 = JaroWinkler.computeSimilarity("frog", "fog");
        assertEquals(expResult4, result4, SpecificationConstants.SIMILARITY_ACCEPTED_ERROR);

        double expResult5 = 0.88;
        double result5 = JaroWinkler.computeSimilarity("hello", "hallo");
        assertEquals(expResult5, result5, SpecificationConstants.SIMILARITY_ACCEPTED_ERROR);
        
        double expResult6 = 1.0;
        double result6 = JaroWinkler.computeSimilarity("fog", "fog");
        assertEquals(expResult6, result6, SpecificationConstants.SIMILARITY_ACCEPTED_ERROR);
    }
    
}
