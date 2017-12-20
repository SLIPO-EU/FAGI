package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.model.LinkedTerm;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
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

        AbbreviationResolver.setKnownAbbreviations(knownAbbreviations);
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
        normalizedLiteralA.setLiteral("Aaa Bar Ccc Dr. St.");
        normalizedLiteralA.setNormalized("aaa bar ccc Doctor street");
        
        NormalizedLiteral normalizedLiteralB = new NormalizedLiteral();
        normalizedLiteralB.setIsNormalized(true);
        normalizedLiteralB.setLiteral("bar ccc Ddd Eee");
        normalizedLiteralB.setNormalized("bar ccc ddd eee");
        
        Set<LinkedTerm> linkedTerms = new HashSet<>();
        
        LinkedTerm linkedTerm = new LinkedTerm();
        linkedTerm.setTerm("bar");
        linkedTerm.setWeight(0.1);
        linkedTerms.add(linkedTerm);
        
        ArrayList<String> mismatchedA = new ArrayList<>();
        ArrayList<String> mismatchedB = new ArrayList<>();
        
        //TODO - check again mismatches with several examples
        mismatchedA.add("aaa");
        mismatchedA.add("Doctor");
        mismatchedB.add("ddd");
        
        ArrayList<String> uniqueSpecialsA = new ArrayList<>();
        //ArrayList<String> uniqueSpecialsB = new ArrayList<>();        
        uniqueSpecialsA.add("street");
        
        WeightedPairLiteral expResult = new WeightedPairLiteral();
        expResult.setLinkedTerms(linkedTerms);
        expResult.setBaseValueA("ccc");
        expResult.setBaseValueB("ccc");
        expResult.setMismatchTokensA(mismatchedA);
        expResult.setMismatchTokensB(mismatchedB);
        
        expResult.setMismatchWeight(0.5);
        expResult.setUniqueSpecialTermsA(uniqueSpecialsA);
        
        WeightedPairLiteral result = normalizer.getWeightedPair(normalizedLiteralA, normalizedLiteralB, locale);
        
        assertEquals(expResult.getBaseValueA(), result.getBaseValueA());
        
        assertEquals(expResult.getBaseValueB(), result.getBaseValueB());

        assertEquals(expResult.getLinkedTerms(), result.getLinkedTerms());
        
        assertEquals(expResult.getMismatchTokensA(), result.getMismatchTokensA());
        
        assertEquals(expResult.getMismatchTokensB(), result.getMismatchTokensB());
        
    }
    
}
