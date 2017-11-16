package gr.athena.innovation.fagi.core.functions.literal;

import gr.athena.innovation.fagi.utils.ResourceFileLoader;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author nkarag
 */
public class AbbreviationResolverTest {
    
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
        System.out.println("getInstance");
        AbbreviationResolver expResult = null;
        AbbreviationResolver result = AbbreviationResolver.getInstance();
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setKnownAbbreviations method, of class AbbreviationResolver.
     */
    @Test
    public void testSetKnownAbbreviations() {
        //System.out.println("setKnownAbbreviations");
        //Map<String, String> knownAbbreviations = null;
        //AbbreviationResolver.setKnownAbbreviations(knownAbbreviations);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of containsAbbreviation method, of class AbbreviationResolver.
     */
    @Test
    public void testContainsAbbreviation() {
        System.out.println("containsAbbreviation");
        String literal = "literal";
        AbbreviationResolver instance = AbbreviationResolver.getInstance();
        boolean expResult = false;
        boolean result = instance.containsAbbreviation(literal);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getAbbreviation method, of class AbbreviationResolver.
     */
    @Test
    public void testGetAbbreviation() {
        System.out.println("getAbbreviation");
        String literalA = "literalA";
        String literalB = "literalB";
        AbbreviationResolver instance = AbbreviationResolver.getInstance();
        String expResult = "result";
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
        System.out.println("recoverAbbreviation");
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
        System.out.println("tokenize");
        CharSequence text = "text";
        String[] expResult = null;
        String[] result = AbbreviationResolver.tokenize(text);
        //assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
