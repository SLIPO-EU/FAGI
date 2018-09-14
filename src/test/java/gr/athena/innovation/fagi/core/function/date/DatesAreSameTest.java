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
public class DatesAreSameTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(DatesAreSameTest.class);

    /**
     * Test of evaluate method, of class DatesAreSame.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        DatesAreSame instance = new DatesAreSame();
        
        //3 days difference, 4 tolerance
        String dateAString1 = "2018/08/10";
        Literal literalA1 = ResourceFactory.createStringLiteral(dateAString1);
        String dateFormatA1 = "yyyy/MM/dd";
        String dateBString1 = "2018/08/07";
        Literal literalB1 = ResourceFactory.createStringLiteral(dateBString1);
        String dateFormatB1 = "yyyy/MM/dd";
        String tolerance1 = "4";
        
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(literalA1, dateFormatA1, literalB1, dateFormatB1, tolerance1);
        assertEquals(expResult1, result1);
        
        //3 days difference, 1 tolerance
        String dateAString2 = "2018/08/10";
        Literal literalA2 = ResourceFactory.createStringLiteral(dateAString2);
        String dateFormatA2 = "yyyy/MM/dd";
        String dateBString2 = "2018/08/07";
        Literal literalB2 = ResourceFactory.createStringLiteral(dateBString2);
        String dateFormatB2 = "yyyy/MM/dd";
        String tolerance2 = "1";
        
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(literalA2, dateFormatA2, literalB2, dateFormatB2, tolerance2);
        assertEquals(expResult2, result2);
        
        //same date, null tolerance
        String dateAString3 = "2018/08/10";
        Literal literalA3 = ResourceFactory.createStringLiteral(dateAString3);
        String dateFormatA3 = "yyyy/MM/dd";
        String dateBString3 = "2018/08/10";
        Literal literalB3 = ResourceFactory.createStringLiteral(dateBString3);
        String dateFormatB3 = "yyyy/MM/dd";
        String tolerance3 = null;
        
        boolean expResult3 = true;
        boolean result3 = instance.evaluate(literalA3, dateFormatA3, literalB3, dateFormatB3, tolerance3);
        assertEquals(expResult3, result3);
    }

    /**
     * Test of getName method, of class DatesAreSame.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        DatesAreSame instance = new DatesAreSame();
        String expResult = SpecificationConstants.Functions.DATES_ARE_SAME;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
