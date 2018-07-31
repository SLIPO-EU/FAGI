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
public class LiteralContainsTheOtherTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(LiteralContainsTheOtherTest.class);

    /**
     * Test of evaluate method, of class LiteralContainsTheOther.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        LiteralContainsTheOther instance = new LiteralContainsTheOther();
        
        String text1 = "baseliteralexample";
        Literal literal1 = ResourceFactory.createStringLiteral(text1);
        String text2 = "";
        Literal literal2 = ResourceFactory.createStringLiteral(text2);
        boolean expResult1 = false;
        boolean result1 = instance.evaluate(literal1, literal2);
        assertEquals(expResult1, result1);
        
        String text3 = "";
        Literal literal3 = ResourceFactory.createStringLiteral(text3);
        String text4 = "otherLiteral";
        Literal literal4 = ResourceFactory.createStringLiteral(text4);
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(literal3, literal4);
        assertEquals(expResult2, result2);
        
        String text5 = "a Literal example";
        Literal literal5 = ResourceFactory.createStringLiteral(text5);
        String text6 = "a Literal";
        Literal literal6 = ResourceFactory.createStringLiteral(text6);
        boolean expResult3 = true;
        boolean result3 = instance.evaluate(literal5, literal6);
        assertEquals(expResult3, result3);
    }

    /**
     * Test of getName method, of class LiteralContainsTheOther.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        LiteralContainsTheOther instance = new LiteralContainsTheOther();
        String expResult = SpecificationConstants.Functions.LITERAL_CONTAINS_THE_OTHER;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
