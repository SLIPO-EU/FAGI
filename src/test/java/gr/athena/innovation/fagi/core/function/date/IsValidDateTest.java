package gr.athena.innovation.fagi.core.function.date;

import gr.athena.innovation.fagi.core.function.date.IsValidDate;
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
public class IsValidDateTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsValidDateTest.class);
    
    public IsValidDateTest() {
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
     * Test of evaluate method, of class IsValidDate.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        IsValidDate isValidDate = new IsValidDate();
        
        String date1 = "19/11/2015";
        String format1 = "dd/mm/yyyy";
        boolean result1 = isValidDate.evaluate(date1, format1);
        boolean expResult1 = true;
        assertEquals(expResult1, result1);
        
        String date2 = "19/11/2015";
        String format2 = "dd-mm-yyyy";
        boolean result2 = isValidDate.evaluate(date2, format2);
        boolean expResult2 = false;
        assertEquals(expResult2, result2);         
    }

    /**
     * Test of getName method, of class IsValidDate.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsValidDate isValidDate = new IsValidDate();

        String expResult = SpecificationConstants.Functions.IS_VALID_DATE;
        String result = isValidDate.getName();
        assertEquals(expResult, result);
    }
    
}
