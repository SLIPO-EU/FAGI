package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.function.literal.TermResolver;
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
import org.apache.logging.log4j.LogManager;

/**
 * Uses a combination of the other available normalizations and similarity functions. Produces normalized literals for
 * matching purposes.
 *
 * @author nkarag
 */
public class MultipleGenericNormalizer implements INormalizer {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MultipleGenericNormalizer.class);
    
    /**
     * Normalize literalA using the other available normalizations and optionally information from literalB.
     *
     * @param literalA the literalA
     * @param literalB the literalB
     * @return the normalized literalA or an empty string if the initial literalA or the produced normalized value is
     * blank.
     */
    public String normalize(String literalA, String literalB) {
        return getNormalizedLiteral(literalA, literalB);
    }

    /**
     * Normalize literalA using the other available normalizations and optionally information from literalB. Returns a
     * "WeightedLiteral" which is a tokenized weighted version of the source literal (literalA).
     *
     * @param literalA the literalA
     * @param literalB the literalB
     * @return the normalized literalA or an empty string if the initial literalA or the produced normalized value is
     * blank.
     */
    public WeightedLiteral getWeightedLiteral(String literalA, String literalB) {

        WeightedLiteral weightedLiteral = new WeightedLiteral();

        String normalizedLiteralA = getNormalizedLiteral(literalA, literalB);
        String normalizedLiteralB = getNormalizedLiteral(literalB, literalA);

        String[] tokensA = tokenize(normalizedLiteralA);
        String[] tokensB = tokenize(normalizedLiteralB);

        Set<String> setA = new HashSet<>(Arrays.asList(tokensA));
        Set<String> setB = new HashSet<>(Arrays.asList(tokensB));

        Set<String> terms = TermResolver.getInstance().getTerms();

        //identify special/frequent terms:
        //-If both contain them -> map these terms to each other and produce an individual score for the final similarity.
        //-If only one contains them -> exclude it and assign a small weight for the mismatch
        setA.stream().forEach((token) -> {
            if (terms.contains(token) && setB.contains(token)) {
                weightedLiteral.putTerm(token, 1.0);
            } else if (terms.contains(token)) {
                weightedLiteral.putTerm(token, 0.1);
            }
        });

        weightedLiteral.setBaseLiteral(normalizedLiteralA);
        weightedLiteral.setBaseWeight(0.5);

        //TODO:
        //Optionally concatenate all words of each string for specific distance measures.
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

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
