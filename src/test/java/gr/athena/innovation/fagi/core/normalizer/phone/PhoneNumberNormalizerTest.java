package gr.athena.innovation.fagi.core.normalizer.phone;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class PhoneNumberNormalizerTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(PhoneNumberNormalizerTest.class);

    /**
     * Test of normalizePhoneNumber method, of class PhoneNumberNormalizer.
     */
    @Test
    public void testNormalizePhoneNumber() {
        LOG.info("normalize");
        
        PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
        
        String number1 = "+123-44 5678 999";
        String expResult1 = "123445678999";
        String result1 = phoneNumberNormalizer.normalize(number1, null);
        assertEquals(expResult1, result1);

        String number2 = "+123-44 5678 999";
        String expResult2 = "00123445678999";
        String exitCodeDigits = "00";
        String result2 = phoneNumberNormalizer.normalize(number2, exitCodeDigits);
        assertEquals(expResult2, result2);   
    }
    
    /**
     * Test of getName method, of class PhoneNumberNormalizer.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
        String expResult = SpecificationConstants.Normalize.PHONE_NUMBER_NORMALIZER;
        String result = phoneNumberNormalizer.getName();
        assertEquals(expResult, result);
    }    
}
