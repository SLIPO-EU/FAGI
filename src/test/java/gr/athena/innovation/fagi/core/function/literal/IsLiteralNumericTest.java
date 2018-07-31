package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
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
        Literal literal1 = ResourceFactory.createStringLiteral(a);
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(literal1);
        assertEquals(expResult1, result1);

        String b = "1234 ";
        Literal literal2 = ResourceFactory.createStringLiteral(b);
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(literal2);
        assertEquals(expResult2, result2);

        String c = "12-14";
        Literal literal3 = ResourceFactory.createStringLiteral(c);
        boolean expResult3 = false;
        boolean result3 = instance.evaluate(literal3);
        assertEquals(expResult3, result3);
        
        String d = "-1";
        Literal literal4 = ResourceFactory.createStringLiteral(d);
        boolean expResult4 = false;
        boolean result4 = instance.evaluate(literal4);
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
