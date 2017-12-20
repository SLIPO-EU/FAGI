package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.normalizer.generic.AlphabeticalNormalizer;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.Locale;

/**
 *
 * @author nkarag
 */
public class BasicGenericNormalizer {

    public NormalizedLiteral getNormalizedLiteral(String literalA, String literalB, Locale locale) {

        String tempString;

        //1)
        String literalAabbr = getAbbreviation(literalA, literalB);

        //normalized literal has abbreviation replaced if it is known or can be recovered from literalB.
        //2)
        tempString = removePunctuation(literalAabbr);

        //3)lowercase
        tempString = toLowerCase(tempString, locale);

        //4)remove special character except parenthesis
        tempString = removeSpecialCharacters(tempString);

        //5)sort string alphabetically
        tempString = sortAlphabetically(tempString);

        return createNormalizedLiteral(literalA, tempString);
    }

    //1)Recover abbreviation if possible. Returns the whole literalA.
    private String getAbbreviation(String literalA, String literalB) {

        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        String possibleAbbreviation = resolver.getAbbreviation(literalA, literalB);

        String recoveredAbbr;
        if (possibleAbbreviation != null) {

            recoveredAbbr = resolver.recoverAbbreviation(possibleAbbreviation, literalB);

            if (recoveredAbbr != null) {
                literalA = literalA.replace(possibleAbbreviation, recoveredAbbr);
            }
        }

        return literalA;
    }

    //2) 
    //remove punctuation except parenthesis
    private String removePunctuation(String text) {
        return text.replaceAll(SpecificationConstants.Regex.PUNCTUATION_EXCEPT_PARENTHESIS_REGEX, "");
    }

    //3) 
    //transform to lowercase
    private String toLowerCase(String text, Locale locale) {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        return text.toLowerCase(locale);
    }

    //4) 
    //remove special character except parenthesis
    private String removeSpecialCharacters(String text) {
        return text.replaceAll(SpecificationConstants.Regex.NON_WORD_EXCEPT_PARENTHESIS_REGEX_2, " ");
    }

    //5) 
    //sort words alphabetically
    private String sortAlphabetically(String text) {
        AlphabeticalNormalizer normalizer = new AlphabeticalNormalizer();
        return normalizer.normalize(text);
    }

    private NormalizedLiteral createNormalizedLiteral(String original, String normalized) {
        NormalizedLiteral normalizedLiteral = new NormalizedLiteral();
        normalizedLiteral.setLiteral(original);
        normalizedLiteral.setNormalized(normalized);
        normalizedLiteral.setIsNormalized(true);

        return normalizedLiteral;
    }
}
