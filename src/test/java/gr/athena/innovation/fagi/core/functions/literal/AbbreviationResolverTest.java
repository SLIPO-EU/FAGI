package gr.athena.innovation.fagi.core.functions.literal;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.utils.ResourceFileLoader;
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
        String literalA = "this literal contains an abbreviation at the end: A.B.B.R.";
        String literalB = "the literalB that has the expanded abbreviation aa bb bbb rrr";
        AbbreviationResolver instance = AbbreviationResolver.getInstance();
        String expResult = "A.B.B.R.";
        String result = instance.getAbbreviation(literalA, literalB);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
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
        CharSequence text = "text";
        String[] expResult = null;
        String[] result = AbbreviationResolver.tokenize(text);
        //assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
