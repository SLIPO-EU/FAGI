package gr.athena.innovation.fagi.core.function.date;

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
public class IsValidDateTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsValidDateTest.class);

    /**
     * Test of evaluate method, of class IsValidDate.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        IsValidDate isValidDate = new IsValidDate();
        
        String date1 = "19/11/2015";
        Literal literal1 = ResourceFactory.createStringLiteral(date1);
        String format1 = "dd/mm/yyyy";
        boolean result1 = isValidDate.evaluate(literal1, format1);
        boolean expResult1 = true;
        assertEquals(expResult1, result1);
        
        String date2 = "19/11/2015";
        Literal literal2 = ResourceFactory.createStringLiteral(date2);
        String format2 = "dd-mm-yyyy";
        boolean result2 = isValidDate.evaluate(literal2, format2);
        boolean expResult2 = false;
        assertEquals(expResult2, result2);         
    }

    /**
     * Test of getName method, of class IsValidDate.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsValidDate isValidDate = new IsValidDate();

        String expResult = SpecificationConstants.Functions.IS_VALID_DATE;
        String result = isValidDate.getName();
        assertEquals(expResult, result);
    }
    
}
