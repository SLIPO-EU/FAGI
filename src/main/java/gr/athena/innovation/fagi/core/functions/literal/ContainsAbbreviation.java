package gr.athena.innovation.fagi.core.functions.literal;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.functions.IFunctionSingleParameter;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class ContainsAbbreviation implements IFunction, IFunctionSingleParameter{
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ContainsAbbreviation.class);
    
    /**
     * Checks if the given literal contains an abbreviation by using a regular expression from the SpecificationConstants. 
     * Basically a modification of {@link IsLiteralAbbreviation} but the check is done against all the words in the literal.
     * 
     * @param literal
     * @return returns true if the literal matches the pattern of regular expression that represents an abbreviation
     */
    @Override
    public boolean evaluate(String literal){
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
     * @param literal
     * @return returns true if the literal matches the pattern of regular expression that represents an abbreviation
     */
    public String getAbbreviation(String literal){
        logger.trace("getAbbreviation of: " + literal);     

        String[] words = tokenize(literal);
        for (String word : words) {
            if (!StringUtils.isBlank(word)) {
                boolean matches = word.matches(SpecificationConstants.Regex.ABBR_REGEX2);
                if(matches){
                    return word;
                }
            }
        }
        return "";
    }
    
    /**
     * Returns an array of tokens. Utilizes regex to find words. It applies a regex
     * {@code}(\w)+{@code} over the input text to extract words from a given character
     * sequence. Implementation taken from org.apache.commons.text.similarity
     *
     * @param text input text
     * @return array of tokens
     */
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
