package gr.athena.innovation.fagi.core.functions.date;

import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
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
public class IsDateKnownFormatTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsDateKnownFormatTest.class);
    
    public IsDateKnownFormatTest() {
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
     * Test of evaluate method, of class IsDateKnownFormat.
     */
    @Test
    public void testEvaluate() {
        logger.info("evaluate isDateKnownFormat");
        String dateString = "12/11/2016";
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();

        boolean expResult = true;
        boolean result = isDateKnownFormat.evaluate(dateString);
        assertEquals(expResult, result);

        String dateString1 = "12/05/2017";

        boolean expResult1 = true;
        boolean result1 = isDateKnownFormat.evaluate(dateString1);
        assertEquals(expResult1, result1);

        String dateString2 = "31-08-1982 10:20:56";
        boolean expResult2 = true;
        boolean result2 = isDateKnownFormat.evaluate(dateString2);
        assertEquals(expResult2, result2);        

        String dateString3 = "31/08-1982 10:20:56";
        boolean expResult3 = false;
        boolean result3 = isDateKnownFormat.evaluate(dateString3);
        assertEquals(expResult3, result3); 

        String dateString4 = "19-19-2017"; //invalid date, but known format should return true
        boolean expResult4 = true;
        boolean result4 = isDateKnownFormat.evaluate(dateString4);
        assertEquals(expResult4, result4);        
    }
    
    /**
     * Test of getName method, of class IsDateKnownFormatTest.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();

        String expResult = SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT;
        String result = isDateKnownFormat.getName();
        assertEquals(expResult, result);
    }    
}
