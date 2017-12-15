package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.model.LinkedTerm;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author nkarag
 */
public class AdvancedGenericNormalizer {
    
    
    private final Locale locale1 = Locale.ENGLISH;
    private final Locale locale2 = Locale.GERMANY;
    
    private final double baseWeight = 0.5;
    private final double linkedWeight = 0.8;
    private final double mismatchWeight = 0.2;

    
    /**
     *
     * @param normalizedLiteralA
     * @param normalizedLiteralB
     * @return 
     */
    public WeightedPairLiteral getWeightedPair(NormalizedLiteral normalizedLiteralA, NormalizedLiteral normalizedLiteralB) {

        WeightedPairLiteral weightedPairLiteral = new WeightedPairLiteral();

        String normalizedA = normalizedLiteralA.getNormalized();
        String normalizedB = normalizedLiteralB.getNormalized();
        
        List<String> tokensA = Arrays.asList(tokenize(normalizedA));
        List<String> tokensB = Arrays.asList(tokenize(normalizedB));

        Set<String> setA = new HashSet<>(tokensA);
        Set<String> setB = new HashSet<>(tokensB);

        Set<String> terms = TermResolver.getInstance().getTerms();

        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();
        
        
        //identify special/frequent terms:
        //-If both contain them -> map these terms to each other and produce an individual score for the final similarity.
        //-If only one contains them -> exclude it and assign a small weight for the mismatch
        setA.stream().forEach((token) -> {
            if (terms.contains(token) && setB.contains(token)) {

                LinkedTerm linkedTerm = new LinkedTerm();
                linkedTerm.setTerm(token);
                linkedTerm.setWeight(linkedWeight);

                tokensA.remove(token);
                weightedPairLiteral.addLinkedTerm(linkedTerm);
                
            } else if (terms.contains(token) && !setB.contains(token)) {
                tokensA.remove(token);
                weightedPairLiteral.addMismatchTokenA(token);
            } 
        });

        setB.stream().forEach((token) -> {
            if (terms.contains(token) && setA.contains(token)) {

                LinkedTerm linkedTerm = new LinkedTerm();
                linkedTerm.setTerm(token);
                linkedTerm.setWeight(linkedWeight);

                tokensB.remove(token);
                weightedPairLiteral.addLinkedTerm(linkedTerm);

            } else if (terms.contains(token) && !setA.contains(token)) {
                tokensB.remove(token);
                weightedPairLiteral.addMismatchTokenB(token);
            } 
        });
        
        tokensA.stream().forEach((tok) -> {
            a.append(tok).append(" ");
        });
        
        tokensB.stream().forEach((tok) -> {
            b.append(tok).append(" ");
        });
        
        //custom alphabetical normalization:
        String baseA = a.toString();
        String baseB = b.toString();
        
        weightedPairLiteral.setBaseWeight(0.5);
        
        return assignMismatch(weightedPairLiteral, tokenize(baseA), tokenize(baseB));
    }
    
    private WeightedPairLiteral assignMismatch(WeightedPairLiteral weightedPairLiteral, String[] tokensA, String[] tokensB){
        
        Collator enCollator = Collator.getInstance(Locale.ENGLISH);
        enCollator.setStrength(SpecificationConstants.COLLATOR_STRENGTH);
        
        List<String> mismatchA = new ArrayList<>();
        List<String> mismatchB = new ArrayList<>();
        
        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();
        
        int carret_i = 0;
        int carret_j = 0;

        for (int i = 0; i < tokensA.length; i++) {
            //TODO: add possible offsets to mismatch list
            String ta = tokensA[carret_i];
            String tb = tokensB[carret_j];
            
            int compareResult = enCollator.compare(ta, tb);
            
            if(compareResult == 0){
                
                a.append(ta).append(" ");
                b.append(tb).append(" ");
                
                carret_i++;
                carret_j++;
                
                
                if(carret_i > tokensA.length-1){
                    
                    weightedPairLiteral.setMismatchTokensA(mismatchA);
                    weightedPairLiteral.setMismatchTokensB(mismatchB);
                    weightedPairLiteral.setBaseValueA(a.toString());
                    weightedPairLiteral.setBaseValueB(b.toString());
                    
                    return weightedPairLiteral;
                    
                } else if(carret_j > tokensB.length-1){
                    weightedPairLiteral.setMismatchTokensA(mismatchA);
                    weightedPairLiteral.setMismatchTokensB(mismatchB);
                    weightedPairLiteral.setBaseValueA(a.toString());
                    weightedPairLiteral.setBaseValueB(b.toString());
                    
                    return weightedPairLiteral;
                }
            } else if(compareResult > 0){
                
                mismatchA.add(ta);
                
                carret_i++;
                
                if(carret_i > tokensA.length-1){
                    weightedPairLiteral.setMismatchTokensA(mismatchA);
                    weightedPairLiteral.setMismatchTokensB(mismatchB);
                    weightedPairLiteral.setBaseValueA(a.toString());
                    weightedPairLiteral.setBaseValueB(b.toString());
                    
                    return weightedPairLiteral;
                }                
                
            } else {
                
                mismatchB.add(tb);
                
                carret_j++;
                
                if(carret_j > tokensB.length-1){
                    weightedPairLiteral.setMismatchTokensA(mismatchA);
                    weightedPairLiteral.setMismatchTokensB(mismatchB);
                    weightedPairLiteral.setBaseValueA(a.toString());
                    weightedPairLiteral.setBaseValueB(b.toString());
                    
                    return weightedPairLiteral;
                }                
            }
        }

        weightedPairLiteral.setMismatchTokensA(mismatchA);
        weightedPairLiteral.setMismatchTokensB(mismatchB);
        weightedPairLiteral.setBaseValueA(a.toString());
        weightedPairLiteral.setBaseValueB(b.toString());
        
        return weightedPairLiteral;
    }
    
    /**
     * Returns an array of tokens extracted from the given text by whitespace.
     *
     * @param text input text
     * @return array of tokens
     */
    public static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }
    
}
