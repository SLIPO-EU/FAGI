package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsLiteralNumericTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsLiteralNumericTest.class);
    
    /**
     * Test of evaluate method, of class IsLiteralNumeric.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        IsLiteralNumeric instance = new IsLiteralNumeric();
        String a = "1234";
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(a);
        assertEquals(expResult1, result1);

        String b = "1234 ";
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(b);
        assertEquals(expResult2, result2);

        String c = "12-14";
        boolean expResult3 = false;
        boolean result3 = instance.evaluate(c);
        assertEquals(expResult3, result3);
        
        String d = "-1";
        boolean expResult4 = false;
        boolean result4 = instance.evaluate(d);
        assertEquals(expResult4, result4);
    }

    /**
     * Test of getName method, of class IsLiteralNumeric.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsLiteralNumeric instance = new IsLiteralNumeric();
        String expResult = SpecificationConstants.Functions.IS_LITERAL_NUMERIC;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
