package gr.athena.innovation.fagi.core.functions.literal;

import com.google.common.base.CharMatcher;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class AbbreviationResolver{

    private static AbbreviationResolver abbreviationResolver;
    private static Map<String, String> abbreviations;

    private AbbreviationResolver(){}

    public static AbbreviationResolver getInstance() {
        //lazy init
        if(abbreviationResolver == null){
            abbreviationResolver= new AbbreviationResolver();
        }
        
        if(abbreviations == null){
            throw new ApplicationException("Known Abbreviations Map is not set.");
        }
        return abbreviationResolver;
    }

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AbbreviationResolver.class);

    public static void setKnownAbbreviations(Map<String, String> knownAbbreviations) {
        abbreviations = knownAbbreviations;
    }
    
    /**
     * Checks if the given literal contains an abbreviation by using a regular expression from the SpecificationConstants. 
     * Basically a modification of {@link IsLiteralAbbreviation} but the check is done against all the words in the literal.
     * 
     * @param literal
     * @return returns true if the literal matches the pattern of regular expression that represents an abbreviation
     */
    public boolean containsAbbreviation(String literal){
        logger.trace("Evaluating literal: " + literal);     

        String[] words = tokenize(literal);
        for (String word : words) {
            if (!StringUtils.isBlank(word)) {
                boolean matches = word.matches(SpecificationConstants.Regex.ABBR_REGEX2);
                if(matches){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the abbreviated token within the given String if exists. Returns null otherwise.  
     * 
     * @param literalA the first literal that may contain abbreviation.
     * @param literalB the second literal which helps at the abbreviation discovery.
     * @return return the abbreviation token or null.
     */
    public String getAbbreviation(String literalA, String literalB){
        logger.trace("getAbbreviation of: " + literalA);

        String[] wordsA = tokenize(literalA);
        String[] wordsB = tokenize(literalB);
        for (String word : wordsA) {
            if (!StringUtils.isBlank(word)) {

                String resolved = abbreviations.get(word);

                if(resolved != null){
                    return resolved;
                }

                char[] chars = word.toCharArray();
                
                //check if the word contains two or more dots.
                if(word.indexOf(".", word.indexOf(".") + 1) != -1){ 
                    if(chars.length < 8){
                        //return the word if it is less than 8 characters including dots.
                        return word;
                    }
                }
                
                //at this point we know tha t the word does not contain two or more dots. 
                //We are interested for this word if it ends with a dot and has 4 chars or less. 
                //If it does, we check against literalB in order to discover if the abbreviation candidate is indeed an abbreviation.
                if(word.endsWith(".") && chars.length <= 5){

                    char[] upperCaseChars = CharMatcher.javaUpperCase().retainFrom(word).toCharArray();
                    if(upperCaseChars.length>1){
                        return word;
                    }

                    //trying unique common prefix from word B
                    //http://www.geeksforgeeks.org/find-shortest-unique-prefix-every-word-given-list-set-2-using-sorting/
                    for(String wordB : wordsB){
                        if(wordB.startsWith(String.valueOf(chars[0]))){
                            return word;
                        }
                    }
                    
                    if(chars.length<=4 && upperCaseChars.length==chars.length-1){
                        return word;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns an array of tokens. Utilizes regex to find words. It applies a regex
     * {@code}(\s)+{@code} over the input text to extract words from a given character
     * sequence. Implementation modified from org.apache.commons.text.similarity
     *
     * @param text input text
     * @return array of tokens
     */
    public static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");
        final Pattern pattern = Pattern.compile("\\s+");
        final Matcher matcher = pattern.matcher(text.toString());
        final List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens.toArray(new String[0]);
    }  
}
