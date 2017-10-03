package gr.athena.innovation.fagi.core.funtions;

import gr.athena.innovation.fagi.core.functions.literal.IsLiteralAbbreviation;
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
public class LiteralFuserTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LiteralFuserTest.class);
    
    public LiteralFuserTest() {
        
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
     * Test of isLiteralAbbreviation method, of class LiteralFuser.
     */
    @Test
    public void testIsLiteralAbbreviation() {
        
        logger.info("isLiteralAbbreviation");
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        String literal1 = "ABBR";
        boolean expResult1 = true;
        boolean result1 = isLiteralAbbreviation.evaluate(literal1);
        assertEquals(expResult1, result1);

        String literal2 = "A.B.B.R.";
        boolean expResult2 = true;
        boolean result2 = isLiteralAbbreviation.evaluate(literal2);
        assertEquals(expResult2, result2);

//        String literal3 = "Abbr";
//        boolean expResult3 = false;
//        boolean result3 = LiteralFuser.isLiteralAbbreviation(literal3);
//        assertEquals(expResult3, result3);
        
    }
    
}
