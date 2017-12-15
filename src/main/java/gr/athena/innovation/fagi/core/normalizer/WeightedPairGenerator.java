package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import static gr.athena.innovation.fagi.core.normalizer.MultipleGenericNormalizer.tokenize;
import gr.athena.innovation.fagi.core.normalizer.generic.AlphabeticalNormalizer;
import gr.athena.innovation.fagi.model.WeightedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author nkarag
 */
public class WeightedPairGenerator {
    
    /**
     *
     * @param literalA
     * @param literalB
     * @return 
     */
    public WeightedPairLiteral getWeightedPair(String literalA, String literalB) {

        WeightedLiteral weightedLiteral = new WeightedLiteral();
        WeightedPairLiteral weightedPairLiteral = new WeightedPairLiteral();
        //weightedPairLiteral.

        String normalizedA = getNormalizedLiteral(literalA, literalB);
        String normalizedB = getNormalizedLiteral(literalB, literalA);
        
        String[] tokensA = tokenize(normalizedA);
        String[] tokensB = tokenize(normalizedB);

        Set<String> setA = new HashSet<>(Arrays.asList(tokensA));
        Set<String> setB = new HashSet<>(Arrays.asList(tokensB));

        Set<String> terms = TermResolver.getInstance().getTerms();

        //identify special/frequent terms:
        //-If both contain them -> map these terms to each other and produce an individual score for the final similarity.
        //-If only one contains them -> exclude it and assign a small weight for the mismatch
        setA.stream().forEach((token) -> {
            if (terms.contains(token) && setB.contains(token)) {
                weightedLiteral.putTerm(token, 0.9);
                
                
            } else if (terms.contains(token)) {
                weightedLiteral.putTerm(token, 0.2);
            }
        });

        weightedPairLiteral.setBaseValueA(normalizedA);
        weightedPairLiteral.setBaseValueB(normalizedB);
        
        weightedLiteral.setBaseLiteral(normalizedA);
        weightedLiteral.setBaseWeight(0.5);

        //TODO:
        //Optionally concatenate all words of each string for specific distance measures.
        
        //concatenate mismatced to use with a single weight
        weightedLiteral.setMisMatched(weightedLiteral.getTermsLiteral());
        weightedLiteral.setMisMatchedWeight(0.5);
        
        return weightedPairLiteral;
    }    
    
    private String getNormalizedLiteral(String literalA, String literalB) {

        String normalizedLiteral = literalA;

        //First recover abbreviations if possible.
        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        String possibleAbbreviation = resolver.getAbbreviation(literalA, literalB);

        String recoveredAbbr = null;
        if (possibleAbbreviation != null) {

            recoveredAbbr = resolver.recoverAbbreviation(possibleAbbreviation, literalB);

            if (recoveredAbbr != null) {
                literalA = literalA.replace(possibleAbbreviation, recoveredAbbr);
            }
        }

        //normalized literal has abbreviation replaced if it is known or can be recovered from literalB.
        //remove punctuation except parenthesis
        normalizedLiteral = literalA.replaceAll(SpecificationConstants.Regex.PUNCTUATION_EXCEPT_PARENTHESIS_REGEX, "");

        //transform to lowercase
        normalizedLiteral = normalizedLiteral.toLowerCase();

        //remove special character except parenthesis
        //TODO: characters like ö are treated as non word. Change regex
        normalizedLiteral = normalizedLiteral.replaceAll(SpecificationConstants.Regex.NON_WORD_EXCEPT_PARENTHESIS_REGEX, " ");

        //sort string alphabetically
        AlphabeticalNormalizer normalizer = new AlphabeticalNormalizer();

        //Add step for computing similarity before the transformation to weigted Literals.
        return normalizer.normalize(normalizedLiteral);
    }

    //tokenize on all non word characters
    public static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");
        final Pattern pattern = Pattern.compile("(\\w)+");
        final Matcher matcher = pattern.matcher(text.toString());
        final List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens.toArray(new String[0]);
    }  
}
