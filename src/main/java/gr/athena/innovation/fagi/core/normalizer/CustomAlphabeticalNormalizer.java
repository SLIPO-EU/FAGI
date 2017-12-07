package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.normalizer.generic.AlphabeticalNormalizer;
import gr.athena.innovation.fagi.model.WeightedLiteral;
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
public class CustomAlphabeticalNormalizer {
    
    /**
     * Executes some custom steps in order to produce a normalized WightedLiteral.
     * 1) Alphabetical ordering of the two literals
     * 2) Compare word by word and recognize alphabetical mismatches. Move cursor accordingly.
     * 3) Exclude all mismatches from the base literals and treat them separately by assigning a weight.
     * 4) Apply similarity metric on both portions of the strings taking account the weights.
     * 
     * @param literalA the literalA
     * @param literalB the literalB
     * @return the normalized literalA or an empty string if the initial literalA or the produced normalized value is
     * blank.
     */
    public WeightedLiteral getWeightedLiteral(String literalA, String literalB) {

        WeightedLiteral weightedLiteral = new WeightedLiteral();
        
        AlphabeticalNormalizer alphabeticalNormalizer = new AlphabeticalNormalizer();
        
        String normalizedLiteralA = alphabeticalNormalizer.normalize(literalA);
        String normalizedLiteralB = alphabeticalNormalizer.normalize(literalB);

        String[] tokensA = tokenize(normalizedLiteralA);
        String[] tokensB = tokenize(normalizedLiteralB);

        Set<String> setA = new HashSet<>(Arrays.asList(tokensA));
        Set<String> setB = new HashSet<>(Arrays.asList(tokensB));

        //TODO: add the logic
        return weightedLiteral;
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
        normalizedLiteral = normalizedLiteral.replaceAll(SpecificationConstants.Regex.NON_WORD_EXCEPT_PARENTHESIS_REGEX, " ");

        //sort string alphabetically
        AlphabeticalNormalizer normalizer = new AlphabeticalNormalizer();

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
