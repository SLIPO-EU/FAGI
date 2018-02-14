package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumberCustomNormalizeTest {
    
    private static final org.apache.logging.log4j.Logger logger 
            = LogManager.getLogger(IsSamePhoneNumberCustomNormalizeTest.class);
    
    /**
     * Test of evaluate method, of class IsSamePhoneNumberCustomNormalize.
     */
    @Test
    public void testEvaluate() {
        logger.info("evaluate");
        IsSamePhoneNumberCustomNormalize instance = new IsSamePhoneNumberCustomNormalize();
        
        String number1 = "+(30)-(1234)-1230";
        String number2 = "01234/123-0";
        
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(number1, number2);
        assertEquals(expResult1, result1);
        
        String number3 = "01234/123-0";
        String number4 = "+(30)-(1234)-1230";
        
        boolean expResult2 = true;
        boolean result2 = instance.evaluate(number3, number4);
        assertEquals(expResult2, result2);
        
        String number5 = "+(30)-(1324)-56789";
        String number6 = "01234/56789-0";
        
        boolean expResult3 = false;
        boolean result3 = instance.evaluate(number5, number6);
        assertEquals(expResult3, result3);        

    }

    /**
     * Test of getName method, of class IsSamePhoneNumberCustomNormalize.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        IsSamePhoneNumberCustomNormalize isSamePhoneNumberCustomNormalize = new IsSamePhoneNumberCustomNormalize();
        String expResult = SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_CUSTOM_NORMALIZE;
        String result = isSamePhoneNumberCustomNormalize.getName();
        assertEquals(expResult, result);
    }
    
}
