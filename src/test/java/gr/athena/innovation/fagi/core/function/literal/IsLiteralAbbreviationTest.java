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
public class IsLiteralAbbreviationTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsLiteralAbbreviationTest.class);
    
    /**
     * Test of evaluate method, of class IsLiteralAbbreviation.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        
        String text1 = "ABBR";
        Literal literal1 = ResourceFactory.createStringLiteral(text1);
        boolean expResult1 = true;
        boolean result1 = isLiteralAbbreviation.evaluate(literal1);
        //LOG.debug(literal1);
        assertEquals(expResult1, result1);

        String text2 = "A.B.B.R.";
        Literal literal2 = ResourceFactory.createStringLiteral(text2);
        boolean expResult2 = true;
        boolean result2 = isLiteralAbbreviation.evaluate(literal2);
        //LOG.debug(literal2);
        assertEquals(expResult2, result2);

        String text3 = "Abbr";
        Literal literal3 = ResourceFactory.createStringLiteral(text3);
        boolean expResult3 = false;
        boolean result3 = isLiteralAbbreviation.evaluate(literal3);
        //LOG.debug(literal3);
        assertEquals(expResult3, result3);        
    }

    /**
     * Test of getName method, of class IsLiteralAbbreviation.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        String expResult = SpecificationConstants.Functions.IS_LITERAL_ABBREVIATION;
        String result = isLiteralAbbreviation.getName();
        assertEquals(expResult, result);
    }
    
}
