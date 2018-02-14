package gr.athena.innovation.fagi.core.function.phone;

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
public class IsSamePhoneNumberUsingExitCodeTest {
    
    private static final org.apache.logging.log4j.Logger logger 
            = LogManager.getLogger(IsSamePhoneNumberUsingExitCodeTest.class);
    
    public IsSamePhoneNumberUsingExitCodeTest() {
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
     * Test of evaluate method, of class IsSamePhoneNumberUsingExitCode.
     */
    @Test
    public void testEvaluate() {
        logger.info("evaluate");
        
        //+ symbol represents the exit code digits, so the below numbers should be considered the same
        String number5 = "00123-44 5678 999";
        String number6 = "+123-44 5678 999";
        String exitCodeDigits1 = "00";
        boolean expResult3 = true; 
        
        IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode = new IsSamePhoneNumberUsingExitCode();        
        boolean result3 = isSamePhoneNumberUsingExitCode.evaluate(number5, number6, exitCodeDigits1);
        assertEquals(expResult3, result3);

        String number7 = "00123-44 5678 999";
        String number8 = "+123-44 5678 999";
        String exitCodeDigits2 = "01";
        boolean expResult4 = false;
        
        boolean result4 = isSamePhoneNumberUsingExitCode.evaluate(number7, number8, exitCodeDigits2);
        assertEquals(expResult4, result4);   
    }

    /**
     * Test of getName method, of class IsSamePhoneNumberUsingExitCode.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode = new IsSamePhoneNumberUsingExitCode();
        String expResult = SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_EXIT_CODE;
        String result = isSamePhoneNumberUsingExitCode.getName();
        assertEquals(expResult, result);
    }
}
