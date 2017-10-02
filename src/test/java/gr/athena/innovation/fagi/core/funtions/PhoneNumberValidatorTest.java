package gr.athena.innovation.fagi.core.funtions;

import gr.athena.innovation.fagi.core.functions.phone.PhoneNumberValidator;
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
public class PhoneNumberValidatorTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PhoneNumberValidatorTest.class);
    
    public PhoneNumberValidatorTest() {
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
     * Test of normalizePhoneNumber method, of class PhoneNumberValidator.
     */
    @Test
    public void testNormalizePhoneNumber() {
        logger.info("normalizePhoneNumber");
        String number1 = "+123-44 5678 999";
        String expResult1 = "123445678999";
        String result1 = PhoneNumberValidator.normalizePhoneNumber(number1, null);
        assertEquals(expResult1, result1);

        String number2 = "+123-44 5678 999";
        String expResult2 = "00123445678999";
        String exitCodeDigits = "00";
        String result2 = PhoneNumberValidator.normalizePhoneNumber(number2, exitCodeDigits);
        assertEquals(expResult2, result2);        
    }

    /**
     * Test of isPhoneNumberParsable method, of class PhoneNumberValidator.
     */
    @Test
    public void testIsPhoneNumberParsable() {
        
        logger.info("isPhoneNumberParsable");
        String number1 = "0123456789";
        boolean expResult1 = true;
        boolean result1 = PhoneNumberValidator.isPhoneNumberParsable(number1);
        assertEquals(expResult1, result1);

        
        String number2 = "+00123-44 5678 999";
        boolean expResult2 = false;
        boolean result2 = PhoneNumberValidator.isPhoneNumberParsable(number2);
        assertEquals(expResult2, result2);

        String number3 = "+00123445678999";
        boolean expResult3 = false;
        boolean result3 = PhoneNumberValidator.isPhoneNumberParsable(number3);
        assertEquals(expResult3, result3);
        
    }

    /**
     * Test of isSamePhoneNumber method, of class PhoneNumberValidator.
     */
    @Test
    public void testIsSamePhoneNumber() {
        

        logger.info("isSamePhoneNumber");
        String number1 = "0123456789";
        String number2 = "0123456789";
        boolean expResult1 = true;
        boolean result1 = PhoneNumberValidator.isSamePhoneNumber(number1, number2);
        assertEquals(expResult1, result1);

        String number3 = "+00123-44 5678 999";
        String number4 = "+00123-44 5678 999";
        boolean expResult2 = true;
        boolean result2 = PhoneNumberValidator.isSamePhoneNumber(number3, number4);
        assertEquals(expResult2, result2);

        //+ symbol represents the exit code digits, so the below numbers should be considered the same
        String number5 = "00123-44 5678 999";
        String number6 = "+123-44 5678 999";
        String exitCodeDigits1 = "00";
        boolean expResult3 = true;
        
        boolean result3 = PhoneNumberValidator.isSamePhoneNumber(number5, number6, exitCodeDigits1);
        assertEquals(expResult3, result3);

        String number7 = "00123-44 5678 999";
        String number8 = "+123-44 5678 999";
        String exitCodeDigits2 = "01";
        boolean expResult4 = false;
        
        boolean result4 = PhoneNumberValidator.isSamePhoneNumber(number7, number8, exitCodeDigits2);
        assertEquals(expResult4, result4);        
    }
    
}
