package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationAndAcronymResolver;
import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.model.CommonSpecialTerm;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class AdvancedGenericNormalizerTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AdvancedGenericNormalizerTest.class);
    
    public AdvancedGenericNormalizerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        
        Map<String, String> knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();
        Set<String> specialTerms = resourceFileLoader.getSpecialTerms();

        AbbreviationAndAcronymResolver.setKnownAbbreviationsAndAcronyms(knownAbbreviations);
        TermResolver.setTerms(specialTerms);        
    }

    /**
     * Test of getWeightedPair method, of class AdvancedGenericNormalizer.
     */
    @Test
    public void testGetWeightedPair() {
        logger.info("getWeightedPair");
        
        AdvancedGenericNormalizer normalizer = new AdvancedGenericNormalizer();
        
        Locale locale = Locale.ENGLISH;
        
        NormalizedLiteral normalizedLiteralA = new NormalizedLiteral();
        normalizedLiteralA.setIsNormalized(true);
        normalizedLiteralA.setLiteral("Aaa Bar mismatch Ccc  ");
        normalizedLiteralA.setNormalized("aaa bar ccc mismatch test");
        
        NormalizedLiteral normalizedLiteralB = new NormalizedLiteral();
        normalizedLiteralB.setIsNormalized(true);
        normalizedLiteralB.setLiteral("bar ccc test Eee");
        normalizedLiteralB.setNormalized("bar ccc eee test");
        
        //Set<CommonSpecialTerm> linkedTerms = new HashSet<>();
        
        //CommonSpecialTerm linkedTerm = new CommonSpecialTerm();
        //linkedTerm.setTerm("bar");
        //linkedTerms.add(linkedTerm);
        
        ArrayList<String> mismatchedA = new ArrayList<>();
        ArrayList<String> mismatchedB = new ArrayList<>();
        
        //TODO - check again mismatches with several examples
        mismatchedA.add("aaa");
        mismatchedA.add("mismatch");
        mismatchedB.add("eee");
        
        ArrayList<String> uniqueSpecialsA = new ArrayList<>();
        //ArrayList<String> uniqueSpecialsB = new ArrayList<>();        
        //uniqueSpecialsA.add("street");
        
        WeightedPairLiteral expResult = new WeightedPairLiteral();
        //expResult.setCommonSpecialTerms(linkedTerms);
        expResult.setBaseValueA("bar ccc test");
        expResult.setBaseValueB("bar ccc test");
        expResult.setMismatchTokensA(mismatchedA);
        expResult.setMismatchTokensB(mismatchedB);
        
        expResult.setSpecialTermsA(uniqueSpecialsA);
        
        WeightedPairLiteral result = normalizer.getWeightedPair(normalizedLiteralA, normalizedLiteralB, locale);
        
        assertEquals(expResult.getBaseValueA(), result.getBaseValueA());
        
        assertEquals(expResult.getBaseValueB(), result.getBaseValueB());

        //assertEquals(expResult.getCommonSpecialTerms(), result.getCommonSpecialTerms());
        
        assertEquals(expResult.getMismatchTokensA(), result.getMismatchTokensA());
        
        assertEquals(expResult.getMismatchTokensB(), result.getMismatchTokensB());
        
    }
    
}
