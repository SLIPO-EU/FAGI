package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsPhoneNumberParsableTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsPhoneNumberParsableTest.class);
    
    public IsPhoneNumberParsableTest() {
    }

    /**
     * Test of evaluate method, of class IsPhoneNumberParsable.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        IsPhoneNumberParsable isPhoneNumberParsable = new IsPhoneNumberParsable();
        String number1 = "0123456789";
        boolean expResult1 = true;
        boolean result1 = isPhoneNumberParsable.evaluate(number1);
        assertEquals(expResult1, result1);

        
        String number2 = "+00123-44 5678 999";
        boolean expResult2 = false;
        boolean result2 = isPhoneNumberParsable.evaluate(number2);
        assertEquals(expResult2, result2);

        String number3 = "+00123445678999";
        boolean expResult3 = false;
        boolean result3 = isPhoneNumberParsable.evaluate(number3);
        assertEquals(expResult3, result3);
    }

    /**
     * Test of getName method, of class IsPhoneNumberParsable.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsPhoneNumberParsable isPhoneNumberParsable = new IsPhoneNumberParsable();
        String expResult = SpecificationConstants.Functions.IS_PHONE_NUMBER_PARSABLE;
        String result = isPhoneNumberParsable.getName();
        assertEquals(expResult, result);
    }
}
