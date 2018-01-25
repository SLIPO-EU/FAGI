package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationAndAcronymResolver;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class BasicGenericNormalizerTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(BasicGenericNormalizerTest.class);
    
    public BasicGenericNormalizerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        
        Map<String, String> knownAbbreviations = null;
        
        try {
            knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();
        } catch (IOException | ApplicationException ex) {
            logger.error(ex);
        }
        
        AbbreviationAndAcronymResolver.setKnownAbbreviationsAndAcronyms(knownAbbreviations);
        AbbreviationAndAcronymResolver.setLocale(Locale.GERMAN);
    }

    /**
     * Test of getNormalizedLiteral method, of class BasicGenericNormalizer.
     */
    @Test
    public void testGetNormalizedLiteral() {
        logger.info("getNormalizedLiteral");
        
        String literalA = "bobab aaaba Dr. cacc";
        String literalB = "aaaba bbab Doktor";
        
        Locale locale = Locale.GERMAN;
        
        BasicGenericNormalizer instance = new BasicGenericNormalizer();
        
        NormalizedLiteral expResult = new NormalizedLiteral();
        
        expResult.setLiteral(literalA);
        expResult.setNormalized("aaaba bobab cacc doktor");
        expResult.setIsNormalized(true);
        
        NormalizedLiteral result = instance.getNormalizedLiteral(literalA, literalB, locale);

        assertEquals(expResult.getNormalized(), result.getNormalized());
        
        assertEquals(expResult.isIsNormalized(), result.isIsNormalized());
        
        assertEquals(expResult.getLiteral(), result.getLiteral());

    }
    /**
     * Test of normalize method, of class BasicGenericNormalizer.
     */
    @Test
    public void testNormalize() {
        logger.info("normalize");

        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();

        String literal1a = "only alphabetical";
        String literal1b = "irrelevant literal";
        String expResult1 = "alphabetical only";
        String result1 = normalizer.normalize(literal1a, literal1b);
        assertEquals(expResult1, result1);

        String literal2a = "Surname Word.";
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
     * Test of tokenize method, of class BasicGenericNormalizer.
     */
    @Test
    public void testTokenize() {
        logger.info("tokenize");
        CharSequence text = "tokenize on non-character words     and get&result of size 11.- ";
        String[] expResult = {"tokenize", "on", "non","character", "words", "and", "get", "result", "of", "size", "11"};
        String[] result = BasicGenericNormalizer.tokenize(text);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getName method, of class BasicGenericNormalizer.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        BasicGenericNormalizer normalizer = new BasicGenericNormalizer();
        String expResult = SpecificationConstants.Normalize.BASIC_NORMALIZER;
        String result = normalizer.getName();
        assertEquals(expResult, result);
    }
}
