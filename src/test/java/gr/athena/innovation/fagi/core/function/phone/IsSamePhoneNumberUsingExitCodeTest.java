package gr.athena.innovation.fagi.core.function.phone;

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
public class IsSamePhoneNumberUsingExitCodeTest {
    
    private static final org.apache.logging.log4j.Logger LOG 
            = LogManager.getLogger(IsSamePhoneNumberUsingExitCodeTest.class);

    /**
     * Test of evaluate method, of class IsSamePhoneNumberUsingExitCode.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        //+ symbol represents the exit code digits, so the below numbers should be considered the same
        String number1 = "00123-44 5678 999";
        Literal literal1 = ResourceFactory.createStringLiteral(number1);
        String number2 = "+123-44 5678 999";
        Literal literal2 = ResourceFactory.createStringLiteral(number2);
        String exitCodeDigits1 = "00";
        boolean expResult3 = true; 
        
        IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode = new IsSamePhoneNumberUsingExitCode();        
        boolean result3 = isSamePhoneNumberUsingExitCode.evaluate(literal1, literal2, exitCodeDigits1);
        assertEquals(expResult3, result3);

        String number3 = "00123-44 5678 999";
        Literal literal3 = ResourceFactory.createStringLiteral(number3);
        String number4 = "+123-44 5678 999";
        Literal literal4 = ResourceFactory.createStringLiteral(number4);
        String exitCodeDigits2 = "01";
        boolean expResult4 = false;
        
        boolean result4 = isSamePhoneNumberUsingExitCode.evaluate(literal3, literal4, exitCodeDigits2);
        assertEquals(expResult4, result4);   
    }

    /**
     * Test of getName method, of class IsSamePhoneNumberUsingExitCode.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode = new IsSamePhoneNumberUsingExitCode();
        String expResult = SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_EXIT_CODE;
        String result = isSamePhoneNumberUsingExitCode.getName();
        assertEquals(expResult, result);
    }
}
