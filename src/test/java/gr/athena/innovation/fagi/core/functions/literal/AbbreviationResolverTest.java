package gr.athena.innovation.fagi.core.functions.literal;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author nkarag
 */
public class AbbreviationResolverTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AbbreviationResolverTest.class);

    @BeforeClass
    public static void setUpClass() throws IOException {
        
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        Map<String, String> knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();

        AbbreviationResolver.setKnownAbbreviations(knownAbbreviations);        
        
    }    

    /**
     * Test of getInstance method, of class AbbreviationResolver.
     */
    @Test
    public void testGetInstance() {
        logger.info("getInstance");
        try{
            AbbreviationResolver.getInstance();
        } catch(ApplicationException ex){
            logger.error(ex);
            fail("AbbreviationResolver is not initialized with knownAbbreviations.");
        }
    }
    
    /**
     * Test of getInstance method, of class AbbreviationResolver.
     */
    @Test
    public void testGetKnownAbbreviation() {
        logger.info("getInstance");

        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        String result = resolver.getKnownAbbreviation("Dr.");
        String expResult = "Doctor";
        assertEquals(expResult, result);
    }
    
    /**
     * Test of setKnownAbbreviations method, of class AbbreviationResolver.
     */
    @Test
    public void testSetKnownAbbreviations() {
        
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();

        try {
            Map<String, String> knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();
            assertFalse(knownAbbreviations.isEmpty());
        } catch (IOException | ApplicationException ex) {
            logger.error(ex);
            fail("AbbreviationResolver could not be initialized with knownAbbreviations");
        }
    }

    /**
     * Test of containsAbbreviation method, of class AbbreviationResolver.
     */
    @Test
    public void testContainsAbbreviation() {
        logger.info("containsAbbreviation");
        String literal = "this literal contains an abbreviation at the end: A.B.B.R.";
        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        boolean expResult = true;
        boolean result = resolver.containsAbbreviation(literal);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAbbreviation method, of class AbbreviationResolver.
     */
    @Test
    public void testGetAbbreviation() {
        logger.info("getAbbreviation");
        String literal1a = "this literal contains an abbreviation at the end: A.B.B.R.";
        String literal1b = "this literal is irrelevant";
        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        String expResult1 = "A.B.B.R.";
        String result1 = resolver.getAbbreviation(literal1a, literal1b);
        assertEquals(expResult1, result1);

        String literal2a = "This literal contains an abbreviation at the end: ABBR.";
        String literal2b = "This contains the expanded abbreviation aa bb bbb rrr of the above literal.";

        String expResult2 = "ABBR.";
        String result2 = resolver.getAbbreviation(literal2a, literal2b);
        logger.warn(result2);
        assertEquals(expResult2, result2);
        
    }

    /**
     * Test of recoverAbbreviation method, of class AbbreviationResolver.
     */
    @Test
    public void testRecoverAbbreviation() {
        logger.info("recoverAbbreviation");
        String abbreviation = "abbr";
        String text = "text";
        AbbreviationResolver instance = AbbreviationResolver.getInstance();
        String expResult = "";
        String result = instance.recoverAbbreviation(abbreviation, text);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of tokenize method, of class AbbreviationResolver.
     */
    @Test
    public void testTokenize() {
        logger.info("tokenize");
        CharSequence text = "tokenize on white-spaces and get&result of size 8.";
        String[] expResult = {"tokenize","on", "white-spaces", "and", "get&result", "of", "size", "8."};
        String[] result = AbbreviationResolver.tokenize(text);
        assertArrayEquals(expResult, result);
    }
    
}
