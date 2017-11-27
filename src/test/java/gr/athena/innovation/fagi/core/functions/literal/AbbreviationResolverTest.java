package gr.athena.innovation.fagi.core.functions.literal;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import java.io.IOException;
import java.util.Map;
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
        
        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        
        //a)
        String literal0a = "this literal has the dr. abbreviation.";
        String literal0b = "this literal is irrelevant";
        String expResult0 = "dr.";
        String result0 = resolver.getAbbreviation(literal0a, literal0b);
        assertEquals(expResult0, result0);

        //b)
        String literal3a = "abbreviation is the H. in this sentence.";
        String literal3b = "this literal is irrelevant for this test.";
        String expResult3 = "H.";
        String result3 = resolver.getAbbreviation(literal3a, literal3b);
        assertEquals(expResult3, result3);

        //c)
        String literal2a = "This literal contains an abbreviation at the end: ABBR.";
        String literal2b = "this literal is irrelevant";
        String expResult2 = "ABBR.";
        String result2 = resolver.getAbbreviation(literal2a, literal2b);
        assertEquals(expResult2, result2);

        //d)
        String literal1a = "this literal contains an abbreviation at the end: A.B.B.R.";
        String literal1b = "this literal is irrelevant";
        String expResult1 = "A.B.B.R.";
        String result1 = resolver.getAbbreviation(literal1a, literal1b);
        assertEquals(expResult1, result1);

        //e)
        String literal4a = "This literal contains an abbreviation at the end: Abbr.";
        String literal4b = "This contains the expanded abbreviation aa bb bbb rrr of the above literal.";
        String expResult4 = "Abbr.";
        String result4 = resolver.getAbbreviation(literal4a, literal4b);
        assertEquals(expResult4, result4);        
    }

    /**
     * Test of recoverAbbreviation method, of class AbbreviationResolver.
     */
    @Test
    public void testRecoverAbbreviation() {
        logger.info("recoverAbbreviation");
        
        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        
        String abbreviation1 = "Dr.";
        String text1 = "irrelevant text";
        String expResult1 = "Doctor";
        String result1 = resolver.recoverAbbreviation(abbreviation1, text1);
        assertEquals(expResult1, result1);
        
        String abbreviation2 = "Abbr.";
        String text2 = "aa bb bbb rrr";
        String expResult2 = "aa bb bbb rrr";
        String result2 = resolver.recoverAbbreviation(abbreviation2, text2);
        assertEquals(expResult2, result2);   
        
        String abbreviation3 = "Abbr.";
        String text3 = "This contains the expanded abbreviation aa bb bbb rrr of the above literal.";
        String expResult3 = "aa bb bbb rrr";
        String result3 = resolver.recoverAbbreviation(abbreviation3, text3);
        assertEquals(expResult3, result3);
        
        String abbreviation4 = "A.B.B.R.";
        String text4 = "This contains the expanded abbreviation Aa   Bb Bbb Rrr rr of the above literal.";
        String expResult4 = "Aa Bb Bbb Rrr";
        String result4 = resolver.recoverAbbreviation(abbreviation4, text4);
        assertEquals(expResult4, result4);
    }

    /**
     * Test of tokenize method, of class AbbreviationResolver.
     */
    @Test
    public void testTokenize() {
        logger.info("tokenize");
        CharSequence text = "tokenize on white-spaces     and get&result of size 8.";
        String[] expResult = {"tokenize","on", "white-spaces", "and", "get&result", "of", "size", "8."};
        String[] result = AbbreviationResolver.tokenize(text);
        assertArrayEquals(expResult, result);
    }
}
