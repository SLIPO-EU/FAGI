package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
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
public class IsLiteralAbbreviationTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsLiteralAbbreviationTest.class);
    
    /**
     * Test of evaluate method, of class IsLiteralAbbreviation.
     */
    @Test
    public void testEvaluate() {
        logger.info("evaluate");
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        String literal1 = "ABBR";
        boolean expResult1 = true;
        boolean result1 = isLiteralAbbreviation.evaluate(literal1);
        logger.debug(literal1);
        assertEquals(expResult1, result1);

        String literal2 = "A.B.B.R.";
        boolean expResult2 = true;
        boolean result2 = isLiteralAbbreviation.evaluate(literal2);
        logger.debug(literal2);
        assertEquals(expResult2, result2);

        String literal3 = "Abbr";
        boolean expResult3 = false;
        boolean result3 = isLiteralAbbreviation.evaluate(literal3);
        logger.debug(literal3);
        assertEquals(expResult3, result3);        
    }

    /**
     * Test of getName method, of class IsLiteralAbbreviation.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        String expResult = SpecificationConstants.Functions.IS_LITERAL_ABBREVIATION;
        String result = isLiteralAbbreviation.getName();
        assertEquals(expResult, result);
    }
    
}
