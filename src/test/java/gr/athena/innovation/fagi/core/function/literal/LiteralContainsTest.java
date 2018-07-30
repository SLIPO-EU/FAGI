package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class LiteralContainsTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(LiteralContainsTest.class);

    /**
     * Test of evaluate method, of class LiteralContains.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        LiteralContains instance = new LiteralContains();
        
        String literal1 = "baseliteralexample";
        String value1 = "";
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(literal1, value1);
        assertEquals(expResult1, result1);
        
        String literal2 = "";
        String value2 = "test";
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(literal2, value2);
        assertEquals(expResult2, result2);
        
        String literal3 = "baseliteralexample";
        String value3 = "literal";
        boolean expResult3 = true;
        boolean result3 = instance.evaluate(literal3, value3);
        assertEquals(expResult3, result3);
    }

    /**
     * Test of getName method, of class LiteralContains.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        LiteralContains instance = new LiteralContains();
        String expResult = SpecificationConstants.Functions.LITERAL_CONTAINS;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
}
