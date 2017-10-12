package gr.athena.innovation.fagi.core.similarity;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class LevenshteinDistanceTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LevenshteinDistanceTest.class);
    
    public LevenshteinDistanceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of compute method, of class LevenshteinDistance.
     */
    @Test
    public void testCompute() {
        logger.info("compute");
        
        String a = "the first string for the test is longer than the second and all its characters are different.";
        String b = "###$$%%";
        double expResult1 = 1.0;
        double result1 = LevenshteinDistanceOptionalThreshold.compute(a, b, null);
        assertEquals(expResult1, result1, 0.0);

        String c = "we are same!";
        String d = "we are same!";
        double expResult2 = 0.0;
        double result2 = LevenshteinDistanceOptionalThreshold.compute(c, d, null);
        assertEquals(expResult2, result2, 0.0);
        
        String e = "one";
        String f = "one+";
        double expResult3 = 0.25; //1 levenshten distance / 4 max length of the two strings
        double result3 = LevenshteinDistanceOptionalThreshold.compute(e, f, null);
        assertEquals(expResult3, result3, 0.0);        
    }
}
