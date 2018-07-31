package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsSamePhoneNumberCustomNormalizeTest {
    
    private static final org.apache.logging.log4j.Logger LOG 
            = LogManager.getLogger(IsSamePhoneNumberCustomNormalizeTest.class);
    
    /**
     * Test of evaluate method, of class IsSamePhoneNumberCustomNormalize.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        IsSamePhoneNumberCustomNormalize instance = new IsSamePhoneNumberCustomNormalize();

        //considered same
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(ResourceFactory.createStringLiteral("+(30)-(1234)-1230"), 
                ResourceFactory.createStringLiteral("01234/123-0"));
        
        assertEquals(expResult1, result1);

        boolean expResult2 = true;
        boolean result2 = instance.evaluate(ResourceFactory.createStringLiteral("01234/123-0"), 
                ResourceFactory.createStringLiteral("+(30)-(1234)-1230"));
        assertEquals(expResult2, result2);
        
        boolean expResult3 = true;
        boolean result3 = instance.evaluate(ResourceFactory.createStringLiteral("+(30)-(1234)-56789"), 
                ResourceFactory.createStringLiteral("01234/56789-0"));
        assertEquals(expResult3, result3);

        boolean expResult4 = true;
        boolean result4 = instance.evaluate(ResourceFactory.createStringLiteral("01234/56789-0"), 
                ResourceFactory.createStringLiteral("+(30)-(1234)-56789"));
        assertEquals(expResult4, result4);

        boolean expResult5 = true;
        boolean result5 = instance.evaluate(ResourceFactory.createStringLiteral("+(30)-(1234)-567890"), 
                ResourceFactory.createStringLiteral("01234/56789-0"));
        assertEquals(expResult5, result5);
        
        boolean expResult6 = true;
        boolean result6 = instance.evaluate(ResourceFactory.createStringLiteral("01234/56789-0"), 
                ResourceFactory.createStringLiteral("+(30)-(1234)-567890"));
        assertEquals(expResult6, result6);   

        boolean expResult7 = true;
        boolean result7 = instance.evaluate(ResourceFactory.createStringLiteral("+(30)-(1234)-1230"), 
                ResourceFactory.createStringLiteral("01234/123-0"));
        assertEquals(expResult7, result7);   

        boolean expResult8 = true;
        boolean result8 = instance.evaluate(ResourceFactory.createStringLiteral("01234/123-0"), 
                ResourceFactory.createStringLiteral("+(30)-(1234)-1230"));
        assertEquals(expResult8, result8);  

        boolean expResult9 = true;
        boolean result9 = instance.evaluate(ResourceFactory.createStringLiteral("+(30)-(123)-4500"), 
                ResourceFactory.createStringLiteral("0123/450-0"));
        assertEquals(expResult9, result9);   

        boolean expResult10 = true;
        boolean result10 = instance.evaluate(ResourceFactory.createStringLiteral("0123/450-0"), 
                ResourceFactory.createStringLiteral("+(30)-(123)-4500"));
        assertEquals(expResult10, result10);  
        
        
        //considered different
        
        boolean expResult11 = false;
        boolean result11 = instance.evaluate(ResourceFactory.createStringLiteral("+(30)-(1234)-5670"), 
                ResourceFactory.createStringLiteral("01234/567-00"));
        assertEquals(expResult11, result11);
        
        boolean expResult12 = false;
        boolean result12 = instance.evaluate(ResourceFactory.createStringLiteral("01234/567-89"), 
                ResourceFactory.createStringLiteral("+(30)-(1234)-5670"));
        assertEquals(expResult12, result12);
        
        boolean expResult13 = false;
        boolean result13 = instance.evaluate(ResourceFactory.createStringLiteral("+(30)-(123)-4500"), 
                ResourceFactory.createStringLiteral("0123/450-1"));
        assertEquals(expResult13, result13);           
    }

    /**
     * Test of getName method, of class IsSamePhoneNumberCustomNormalize.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsSamePhoneNumberCustomNormalize isSamePhoneNumberCustomNormalize = new IsSamePhoneNumberCustomNormalize();
        String expResult = SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_CUSTOM_NORMALIZE;
        String result = isSamePhoneNumberCustomNormalize.getName();
        assertEquals(expResult, result);
    }
    
}
