package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class PhoneHasMoreDigitsTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(PhoneHasMoreDigitsTest.class);

    /**
     * Test of evaluate method, of class PhoneHasMoreDigits.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        PhoneHasMoreDigits instance = new PhoneHasMoreDigits();
        String number1 = "0-123456789";
        String number2 = "0123456789";
        boolean expResult1 = false;
        boolean result1 = instance.evaluate(number1, number2);
        assertEquals(expResult1, result1);

        String number3 = "00123-445678999";
        String number4 = "(0123)-44 5678 999";
        boolean expResult2 = true;
        boolean result2 = instance.evaluate(number3, number4);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getName method, of class PhoneHasMoreDigits.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        PhoneHasMoreDigits instance = new PhoneHasMoreDigits();
        String expResult = SpecificationConstants.Functions.PHONE_HAS_MORE_DIGITS;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
