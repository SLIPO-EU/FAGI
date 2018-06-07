package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class CosineTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(CosineTest.class);
    
    /**
     * Test of tokenize method, of class Cosine.
     */
    @Test
    public void testTokenize() {
        LOG.info("tokenize");
        
        CharSequence text = "word tokens&should be#five   %";

        CharSequence[] expResult = {"word", "tokens","should","be","five"};

        CharSequence[] result = Cosine.tokenize(text);

        assertArrayEquals(expResult, result);
    }
    
    /**
     * Test of computeSimilarity method, of class Cosine.
     */
    @Test
    public void testComputeSimilarity() {
        LOG.info("computeSimilarity");

        String a = "This is a foo bar string .";
        String b = "This string is similar to a foo bar string .";
        double expResult1 = 0.86164043;
        double result1 = Cosine.computeSimilarity(a, b);

        assertEquals(expResult1, result1, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);

        String c = "we are same and we should return 1.0 despite the floating point representation errors";
        String d = "we are same and we should return 1.0 despite the floating point representation errors";
        double expResult2 = 1.0;
        double result2 = Cosine.computeSimilarity(c, d);

        assertEquals(expResult2, result2, 0.0);
         
        String e = "similarity";
        String f = "0";
        double expResult3 = 0.0;
        double result3 = Cosine.computeSimilarity(e, f);

        assertEquals(expResult3, result3, 0.0);        
    }

    /**
     * Test of computeDistance method, of class Cosine.
     */
    @Test
    public void testComputeDistance() {
        LOG.info("computeDistance");
        
        String a = "This is a foo bar string .";
        String b = "This string is similar to a foo bar string .";
        double expResult1 = 1 - 0.86164043;
        double result1 = Cosine.computeDistance(a, b);

        assertEquals(expResult1, result1, SpecificationConstants.Similarity.SIMILARITY_ACCEPTED_ERROR);

        String c = "we are same and we should return 0.0 despite the floating point representation errors";
        String d = "we are same and we should return 0.0 despite the floating point representation errors";
        double expResult2 = 0.0;
        double result2 = Cosine.computeDistance(c, d);

        assertEquals(expResult2, result2, 0.0);
         
        String e = "distance";
        String f = "1";
        double expResult3 = 1.0;
        double result3 = Cosine.computeDistance(e, f);

        assertEquals(expResult3, result3, 0.0);  
    }
}
