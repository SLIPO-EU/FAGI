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
public class RemoveSpecialCharactersTest {
    
    private static final org.apache.logging.log4j.Logger LOG 
            = LogManager.getLogger(RemoveSpecialCharactersTest.class);
    
    public RemoveSpecialCharactersTest() {
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
     * Test of normalize method, of class RemoveSpecialCharacters.
     */
    @Test
    public void testNormalize() {
        LOG.info("normalize");
        
        RemoveSpecialCharacters removeSpecialCharacters = new RemoveSpecialCharacters();
        
        String literal = "-_/@ contain m@any special characTers-";
        
        String expResult = "     contain m any special characTers ";
        String result = removeSpecialCharacters.normalize(literal);
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class RemoveSpecialCharacters.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        RemoveSpecialCharacters removeSpecialCharacters = new RemoveSpecialCharacters();
        String expResult = SpecificationConstants.Normalize.REMOVE_SPECIAL_CHARACTERS;
        String result = removeSpecialCharacters.getName();
        assertEquals(expResult, result);
    }
}
