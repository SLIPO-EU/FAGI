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
public class LiteralHasLanguageAnnotationTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(LiteralHasLanguageAnnotationTest.class);

    /**
     * Test of evaluate method, of class LiteralHasLanguageAnnotation.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        LiteralHasLanguageAnnotation instance = new LiteralHasLanguageAnnotation();
        
        String text1 = "\"Example of literal with language tag\"@en"; 
        Literal literal1 = ResourceFactory.createStringLiteral(text1);
        
        boolean expResult1 = false;
        boolean result1 = instance.evaluate(literal1);
        LOG.debug(literal1);
        assertEquals(expResult1, result1);
        
        String text2 = "Literal without language tag";
        Literal literal2 = ResourceFactory.createStringLiteral(text2);
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(literal2);
        LOG.debug(literal2);
        assertEquals(expResult2, result2);
        
        String text3 = "explicitly created lang tag.";
        String tag = "de";
        Literal literal3 = ResourceFactory.createLangLiteral(text3, tag);
        boolean expResult3 = true;
        boolean result3 = instance.evaluate(literal3);
        LOG.debug(literal3);
        assertEquals(expResult3, result3);
        
        String text4 = "\"Plain literal method\"@de";

        Literal literal4 = ResourceFactory.createPlainLiteral(text4); //another wrong way of creating lang literal
        boolean expResult4 = false;
        boolean result4 = instance.evaluate(literal4);
        LOG.debug(literal4);
        assertEquals(expResult4, result4);

    }

    /**
     * Test of getName method, of class LiteralHasLanguageAnnotation.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        LiteralHasLanguageAnnotation instance = new LiteralHasLanguageAnnotation();
        String expResult = SpecificationConstants.Functions.LITERAL_HAS_LANGUAGE_ANNOTATION;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
}
