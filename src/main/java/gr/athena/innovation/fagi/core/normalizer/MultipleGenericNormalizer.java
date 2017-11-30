package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.function.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Uses a combination of the other available normalizations and similarity functions.
 * Produces normalized literals for matching purposes.
 * 
 * @author nkarag
 */
public class MultipleGenericNormalizer implements INormalizer{

    /**
     * Normalize literalA using the other available normalizations and optionally information from literalB.
     * 
     * @param literalA the literalA
     * @param literalB the literalB
     * @return the normalized literalA or an empty string if the initial literalA or the produced normalized value is blank.
     */
    public String normalize(String literalA, String literalB){
        
        String normalizedLiteral = literalA;
        
        //First recover abbreviations if possible.
        AbbreviationResolver resolver = AbbreviationResolver.getInstance();
        String possibleAbbreviation = resolver.getAbbreviation(literalA, literalB);
        
        //normalized literal has abbreviation replaced if it is known or can be recovered from literalB.

        //remove punctuation except parenthesis
        normalizedLiteral = literalA.replaceAll(SpecificationConstants.Regex.PUNCTUATION_EXCEPT_PARENTHESIS_REGEX, "");

        //transform to lowercase
        normalizedLiteral = normalizedLiteral.toLowerCase();

        //remove special character except parenthesis
        normalizedLiteral = normalizedLiteral.replaceAll(SpecificationConstants.Regex.NON_WORD_EXCEPT_PARENTHESIS_REGEX, " ");

        //for abbreviations:
        //tokenize with spaces. 
        //

        //try to replace abbreviation with full string if matches
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        

        

        if(!possibleAbbreviation.equals("")){ //getAbbreviation could not find abbreviation
            //recover full words of possible abbreviation from literalB.
            //replace possible abbreviation in literal A with full words
            String recoveredAbbreviation = recoverAbbreviation(possibleAbbreviation, literalB);
            
        }

        if(isLiteralAbbreviation.evaluate(literalA)){
            //TODO: update abbreviation recognition with the new rules

        }

        //sort string alphabetically
        //identify special/frequent terms:
        //-If both contain them -> map these terms to each other and produce an individual score for the final similarity.
        //-If only one contains them -> exclude it and assign a small weight for the mismatch
        //Optionally concatenate all words of each string for specific distance measures.

        if(StringUtils.isBlank(literalA)){
            return "";
        } else {
            return literalA;
        }
    }

    private String recoverAbbreviation(String abbreviation, String literalB){
        //TODO: update recory
        //logger.trace("Try to recover " + literalA + " abbreviation from " + literalB);
        String fullText = "";
        
        //firstCharsB will be the acronym representation of literalB containing only word characters in lowercase
        StringBuilder firstCharsB = new StringBuilder(24); //allocating 20 chars. TODO revise if needed
        //String[] abbrTokensA = tokenize(abbreviation);
        
        
        //transform abbreviation to contain only word characters lowercase 
        String abbreviationChars = abbreviation.
                replaceAll(SpecificationConstants.Regex.NON_WORD_CHARACTERS_REGEX, "").toLowerCase();
        

        String[] tokensB = literalB.split("\\s+");
        for(String tokenB : tokensB){
            firstCharsB.append(tokenB.charAt(0));
        }
        
        //check to see if the abbreviation characters A are contained in B acronym representation.
        if(firstCharsB.toString().toLowerCase().contains(abbreviationChars)){
            //found abbreviation. Recover and return the full text.
            
            for(int i=0; i<tokensB.length; i++){
                //tokensB[i].startsWith(abbreviationChars.charAt(i)
                Character firstCharacterB = tokensB[i].charAt(0);

            }

            return fullText;
        }
        

        return fullText;
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
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
