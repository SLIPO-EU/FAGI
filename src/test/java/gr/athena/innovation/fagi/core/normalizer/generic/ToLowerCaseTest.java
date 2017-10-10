package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.core.normalizer.date.NormalizeDateToFormat;
import gr.athena.innovation.fagi.core.normalizer.date.NormalizeDateToFormatTest;
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
public class ToLowerCaseTest {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ToLowerCaseTest.class);
    
    public ToLowerCaseTest() {
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
     * Test of normalize method, of class ToLowerCase.
     */
    @Test
    public void testNormalize() {
        logger.info("normalize");
        
        ToLowerCase toLowerCase = new ToLowerCase();
        String literal = "FaGi";
        
        String expResult = "fagi";
        String result = toLowerCase.normalize(literal);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getName method, of class ToLowerCase.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        ToLowerCase toLowerCase = new ToLowerCase();
        String expResult = SpecificationConstants.Normalize.TO_LOWER_CASE;
        String result = toLowerCase.getName();
        assertEquals(expResult, result);
    }     
}
