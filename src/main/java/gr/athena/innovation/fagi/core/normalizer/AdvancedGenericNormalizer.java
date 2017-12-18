package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.model.EnumEntity;
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

    private final double baseWeight = 0.5;
    private final double linkedWeight = 0.8;
    private final double mismatchWeight = 0.2;

    /**
     *
     * @param normalizedLiteralA
     * @param normalizedLiteralB
     * @param locale
     * @return
     */
    public WeightedPairLiteral getWeightedPair(NormalizedLiteral normalizedLiteralA, 
            NormalizedLiteral normalizedLiteralB, Locale locale) {

        WeightedPairLiteral weightedPairLiteral = new WeightedPairLiteral();

        String normalizedA = normalizedLiteralA.getNormalized();
        String normalizedB = normalizedLiteralB.getNormalized();

        List<String> tokensA = getTokenList(normalizedA);
        List<String> tokensB = getTokenList(normalizedB);

        Set<String> setA = new HashSet<>(tokensA);
        Set<String> setB = new HashSet<>(tokensB);

        Set<String> terms = TermResolver.getInstance().getTerms();

        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();

        resolveTerms(weightedPairLiteral, setA, setB, terms, tokensA, EnumEntity.LEFT);
        resolveTerms(weightedPairLiteral, setB, setA, terms, tokensB, EnumEntity.RIGHT);

        appendTokens(tokensA, a);
        appendTokens(tokensB, b);
        
        String baseA = a.toString();
        String baseB = b.toString();

        weightedPairLiteral.setBaseWeight(0.5);

        //custom alphabetical re-ordering, assign mismatches
        WeightedPairLiteral weightedPair = assignMismatch(weightedPairLiteral, tokenize(baseA), tokenize(baseB), locale);
        
        return weightedPair;
    }

    private WeightedPairLiteral assignMismatch(WeightedPairLiteral weightedPairLiteral, String[] tokensA, 
            String[] tokensB, Locale locale) {

        if(locale == null){
            locale = Locale.ENGLISH;
        }
        
        Collator enCollator = Collator.getInstance(locale);
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

            if (compareResult == 0) {

                a.append(ta).append(" ");
                b.append(tb).append(" ");

                carret_i++;
                carret_j++;

                if (carret_i > tokensA.length - 1) {

                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);

                } else if (carret_j > tokensB.length - 1) {
                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
                }
            } else if (compareResult > 0) {

                mismatchA.add(ta);

                carret_i++;

                if (carret_i > tokensA.length - 1) {
                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
                }

            } else {

                mismatchB.add(tb);

                carret_j++;

                if (carret_j > tokensB.length - 1) {
                    return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
                }
            }
        }

        return getWeightedPairLiteral(weightedPairLiteral, mismatchA, mismatchB, a, b);
    }

    private List<String> getTokenList(String text) {
        return Arrays.asList(tokenize(text));
    }

    private WeightedPairLiteral getWeightedPairLiteral(WeightedPairLiteral weightedPairLiteral,
            List<String> mismatchA, List<String> mismatchB, StringBuilder a, StringBuilder b) {

        weightedPairLiteral.setMismatchTokensA(mismatchA);
        weightedPairLiteral.setMismatchTokensB(mismatchB);
        weightedPairLiteral.setBaseValueA(a.toString());
        weightedPairLiteral.setBaseValueB(b.toString());

        return weightedPairLiteral;
    }

    //tokenize on whitespaces
    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }

    private void addLinkedTerm(WeightedPairLiteral weightedPairLiteral, List<String> tokens, String token) {
        LinkedTerm linkedTerm = new LinkedTerm();
        linkedTerm.setTerm(token);
        linkedTerm.setWeight(linkedWeight);

        tokens.remove(token);

        weightedPairLiteral.addLinkedTerm(linkedTerm);
    }

    private void appendTokens(List<String> tokens, StringBuilder builder) {
        tokens.stream().forEach((tok) -> {
            builder.append(tok).append(" ");
        });        
    }

    //identify special/frequent terms:
    //-If both contain them -> map these terms to each other and produce an individual score for the final similarity.
    //-If only one contains them -> exclude it and assign a small weight for the mismatch
    private void resolveTerms(WeightedPairLiteral weightedPairLiteral, 
            Set<String> set, Set<String> helpSet, Set<String> terms, List<String> tokens, EnumEntity entity){
        
        set.stream().forEach((token) -> {
            if (terms.contains(token) && helpSet.contains(token)) {
                addLinkedTerm(weightedPairLiteral, tokens, token);
            } else if (terms.contains(token) && !helpSet.contains(token)) {
                tokens.remove(token);
                
                switch(entity) {
                    case LEFT:
                        weightedPairLiteral.addUniqueSpecialTermA(token);
                        break;
                    case RIGHT:
                        weightedPairLiteral.addUniqueSpecialTermB(token);
                        break;
                }
            }
        });
    }
}
