package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
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
        
        AbbreviationResolver.setKnownAbbreviations(knownAbbreviations);
    }

    /**
     * Test of getNormalizedLiteral method, of class BasicGenericNormalizer.
     */
    @Test
    public void testGetNormalizedLiteral() {
        logger.info("getNormalizedLiteral");
        
        String literalA = "bbb aaa ccc Dr.";
        String literalB = "aaa bbb ddd";
        
        Locale locale = Locale.ENGLISH;
        
        BasicGenericNormalizer instance = new BasicGenericNormalizer();
        
        NormalizedLiteral expResult = new NormalizedLiteral();
        
        expResult.setLiteral(literalA);
        expResult.setNormalized("aaa bbb ccc doctor");
        expResult.setIsNormalized(true);
        
        NormalizedLiteral result = instance.getNormalizedLiteral(literalA, literalB, locale);
        
        assertEquals(expResult.getNormalized(), result.getNormalized());
        
        assertEquals(expResult.isIsNormalized(), result.isIsNormalized());
        
        assertEquals(expResult.getLiteral(), result.getLiteral());

    }
    
}
