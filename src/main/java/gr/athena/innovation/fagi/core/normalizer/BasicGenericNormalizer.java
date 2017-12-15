package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.normalizer.generic.AlphabeticalNormalizer;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;

/**
 *
 * @author nkarag
 */
public class BasicGenericNormalizer {
    
    public NormalizedLiteral getNormalizedLiteral(String literalA, String literalB) {

        String tempNormalizedLiteral;

        //First recover abbreviations if possible.
        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        String possibleAbbreviation = resolver.getAbbreviation(literalA, literalB);

        String recoveredAbbr;
        if (possibleAbbreviation != null) {

            recoveredAbbr = resolver.recoverAbbreviation(possibleAbbreviation, literalB);

            if (recoveredAbbr != null) {
                literalA = literalA.replace(possibleAbbreviation, recoveredAbbr);
            }
        }

        //normalized literal has abbreviation replaced if it is known or can be recovered from literalB.
        //remove punctuation except parenthesis
        tempNormalizedLiteral = literalA.replaceAll(SpecificationConstants.Regex.PUNCTUATION_EXCEPT_PARENTHESIS_REGEX, "");

        //transform to lowercase
        tempNormalizedLiteral = tempNormalizedLiteral.toLowerCase();

        //remove special character except parenthesis
        //TODO: characters like ö are treated as non word. Change regex
        tempNormalizedLiteral = tempNormalizedLiteral.replaceAll(SpecificationConstants.Regex.NON_WORD_EXCEPT_PARENTHESIS_REGEX, " ");

        //sort string alphabetically
        AlphabeticalNormalizer normalizer = new AlphabeticalNormalizer();

        NormalizedLiteral normalized = new NormalizedLiteral();
        normalized.setLiteral(literalA);
        normalized.setNormalized(normalizer.normalize(tempNormalizedLiteral));
        normalized.setIsNormalized(true);        

        return normalized;
    }   
}
