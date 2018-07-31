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
public class LiteralsHaveSameLanguageAnnotationTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(LiteralsHaveSameLanguageAnnotationTest.class);

    /**
     * Test of evaluate method, of class LiteralsHaveSameLanguageAnnotation.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        LiteralsHaveSameLanguageAnnotation instance = new LiteralsHaveSameLanguageAnnotation();
        String text1 = "example literal of A.";
        String tag1 = "de";
        Literal literal1 = ResourceFactory.createLangLiteral(text1, tag1);
        
        String text2 = "example from B";
        String tag2 = "de";
        Literal literal2 = ResourceFactory.createLangLiteral(text2, tag2);
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(literal1, literal2);
        assertEquals(expResult1, result1);
        

        String text3 = "example literal of A.";
        String tag3 = "en";
        Literal literal3 = ResourceFactory.createLangLiteral(text3, tag3);
        
        String text4 = "example from B";
        String tag4 = "de";
        Literal literal4 = ResourceFactory.createLangLiteral(text4, tag4);
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(literal3, literal4);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getName method, of class LiteralsHaveSameLanguageAnnotation.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        LiteralsHaveSameLanguageAnnotation instance = new LiteralsHaveSameLanguageAnnotation();
        String expResult = SpecificationConstants.Functions.LITERALS_HAVE_SAME_LANG;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
}
