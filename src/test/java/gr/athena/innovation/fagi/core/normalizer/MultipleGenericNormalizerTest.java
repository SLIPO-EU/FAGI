package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
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
public class MultipleGenericNormalizerTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MultipleGenericNormalizerTest.class);
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        Map<String, String> knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();

        AbbreviationResolver.setKnownAbbreviations(knownAbbreviations);        
        
    }
    
    /**
     * Test of normalize method, of class MultipleGenericNormalizer.
     */
    @Test
    public void testNormalize() {
        logger.info("normalize");
        String literalA = "";
        String literalB = "";
        MultipleGenericNormalizer instance = new MultipleGenericNormalizer();
        String expResult = "";
        //String result = instance.normalize(literalA, literalB);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of tokenize method, of class MultipleGenericNormalizer.
     */
    @Test
    public void testTokenize() {
        logger.info("tokenize");
        CharSequence text = null;
        String[] expResult = null;
        //String[] result = MultipleGenericNormalizer.tokenize(text);
        //assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class MultipleGenericNormalizer.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        MultipleGenericNormalizer instance = new MultipleGenericNormalizer();
        String expResult = "";
        String result = instance.getName();
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
