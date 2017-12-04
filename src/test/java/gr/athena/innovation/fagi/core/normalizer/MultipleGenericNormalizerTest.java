package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
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

        MultipleGenericNormalizer normalizer = new MultipleGenericNormalizer();

        String literal1a = "only alphabetical";
        String literal1b = "irrelevant literal";
        String expResult1 = "alphabetical only";
        String result1 = normalizer.normalize(literal1a, literal1b);
        assertEquals(expResult1, result1);

        String literal2a = "Sur-name Word.";
        String literal2b = "irrelevant literal";
        String expResult2 = "surname word";
        String result2 = normalizer.normalize(literal2a, literal2b);
        assertEquals(expResult2, result2); 
        

        String literal3a = "Sentence containing abbreviation A.B.B.R.";
        String literal3b = "literal with the full text of the abbreviation Aaa Bbb Bb rrr.";
        String expResult3 = "aaa abbreviation bb bbb containing rrr sentence";
        String result3 = normalizer.normalize(literal3a, literal3b);
        logger.warn(result3);
        assertEquals(expResult3, result3); 
                       
        
    }

    /**
     * Test of tokenize method, of class MultipleGenericNormalizer.
     */
    @Test
    public void testTokenize() {
        logger.info("tokenize");
        CharSequence text = "tokenize on non-character words     and get&result of size 11.- ";
        String[] expResult = {"tokenize", "on", "non","character", "words", "and", "get", "result", "of", "size", "11"};
        String[] result = MultipleGenericNormalizer.tokenize(text);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getName method, of class MultipleGenericNormalizer.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        MultipleGenericNormalizer normalizer = new MultipleGenericNormalizer();
        String expResult = SpecificationConstants.Normalize.MULTIPLE_NORMALIZER;
        String result = normalizer.getName();
        assertEquals(expResult, result);
    }
    
}
