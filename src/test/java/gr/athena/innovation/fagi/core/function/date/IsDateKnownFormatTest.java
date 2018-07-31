package gr.athena.innovation.fagi.core.function.date;

import gr.athena.innovation.fagi.core.function.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
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
public class IsDateKnownFormatTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsDateKnownFormatTest.class);

    /**
     * Test of evaluate method, of class IsDateKnownFormat.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();
        
        String dateString = "12/11/2016";
        Literal date = ResourceFactory.createStringLiteral(dateString);
        boolean expResult = true;
        boolean result = isDateKnownFormat.evaluate(date);
        assertEquals(expResult, result);

        String dateString1 = "12/05/2017";
        Literal date1 = ResourceFactory.createStringLiteral(dateString1);
        boolean expResult1 = true;
        boolean result1 = isDateKnownFormat.evaluate(date1);
        assertEquals(expResult1, result1);

        String dateString2 = "31-08-1982 10:20:56";
        Literal date2 = ResourceFactory.createStringLiteral(dateString2);
        boolean expResult2 = true;
        boolean result2 = isDateKnownFormat.evaluate(date2);
        assertEquals(expResult2, result2);        

        String dateString3 = "31/08-1982 10:20:56";
        Literal date3 = ResourceFactory.createStringLiteral(dateString3);
        boolean expResult3 = false;
        boolean result3 = isDateKnownFormat.evaluate(date3);
        assertEquals(expResult3, result3); 

        String dateString4 = "19-19-2017"; //invalid date, but known format should return true
        Literal date4 = ResourceFactory.createStringLiteral(dateString4);
        boolean expResult4 = true;
        boolean result4 = isDateKnownFormat.evaluate(date4);
        assertEquals(expResult4, result4);        
    }
    
    /**
     * Test of getName method, of class IsDateKnownFormatTest.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();

        String expResult = SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT;
        String result = isDateKnownFormat.getName();
        assertEquals(expResult, result);
    }    
}
