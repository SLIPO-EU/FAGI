package gr.athena.innovation.fagi.core.functions.phone;

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
public class IsSamePhoneNumberTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsSamePhoneNumberTest.class);
    
    public IsSamePhoneNumberTest() {
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
     * Test of evaluate method, of class IsSamePhoneNumber.
     */
    @Test
    public void testEvaluate() {
        
        logger.info("evaluate");
        
        IsSamePhoneNumber isSamePhoneNumber = new IsSamePhoneNumber();
        String number1 = "0123456789";
        String number2 = "0123456789";
        boolean expResult1 = true;
        boolean result1 = isSamePhoneNumber.evaluate(number1, number2);
        assertEquals(expResult1, result1);

        String number3 = "+00123-44 5678 999";
        String number4 = "+00123-44 5678 999";
        boolean expResult2 = true;
        boolean result2 = isSamePhoneNumber.evaluate(number3, number4);
        assertEquals(expResult2, result2);

    }

    /**
     * Test of getName method, of class IsSamePhoneNumber.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        IsSamePhoneNumber isSamePhoneNumber = new IsSamePhoneNumber();
        String expResult = SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER;
        String result = isSamePhoneNumber.getName();
        assertEquals(expResult, result);
    }
}
