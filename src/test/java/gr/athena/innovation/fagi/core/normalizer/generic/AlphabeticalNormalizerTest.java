package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
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
public class AlphabeticalNormalizerTest {
    
    private static final org.apache.logging.log4j.Logger logger 
            = LogManager.getLogger(AlphabeticalNormalizerTest.class);
    
    public AlphabeticalNormalizerTest() {
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
     * Test of normalize method, of class AlphabeticalNormalizer.
     */
    @Test
    public void testNormalize() {
        logger.info("normalize");
        
        AlphabeticalNormalizer alphabeticalNormalizer = new AlphabeticalNormalizer();
        
        String literal = "I am fagi";
        String expResult1 = "am fagi I";
        
        String result1 = alphabeticalNormalizer.normalize(literal);
        assertEquals(expResult1, result1);
    }

    /**
     * Test of getName method, of class AlphabeticalNormalizer.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        AlphabeticalNormalizer alphabeticalNormalizer = new AlphabeticalNormalizer();
        String expResult = SpecificationConstants.Normalize.NORMALIZE_ALPHABETICALLY;
        String result = alphabeticalNormalizer.getName();
        assertEquals(expResult, result);
    }
}
