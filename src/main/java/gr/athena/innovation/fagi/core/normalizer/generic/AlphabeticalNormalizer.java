package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.core.normalizer.INormalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

/**
 * Class for normalizing words of literals in alphabetical order. 
 * 
 * @author nkarag
 */
public class AlphabeticalNormalizer implements INormalizer{
    
    private static final org.apache.logging.log4j.Logger logger 
            = LogManager.getLogger(AlphabeticalNormalizer.class);
    
    /**
     * Returns the string with its words are alphabetically sorted.
     * 
     * @param literal the string literal
     * @return the normalized literal string.
     */
    public String normalize(String literal) {

        if(StringUtils.isBlank(literal)){
            return "";
        } else {
            
            String[] parts = tokenize(literal);
            
            Arrays.sort(parts, String.CASE_INSENSITIVE_ORDER);

            StringBuilder sb = new StringBuilder();
            
            for(String s : parts){
               sb.append(s);
               sb.append(" ");
            }

            String normalizedLiteral = sb.toString().trim();
            logger.trace("normalizedLiteral:" + normalizedLiteral);
            
            return normalizedLiteral;
        }
    }
    
    /**
     * Returns an array of tokens. Utilizes regex to find words. It applies a regex
     * {@code}(\w)+{@code} over the input text to extract words from a given character
     * sequence. Implementation taken from org.apache.commons.text.similarity 
     * but changed the returned type to String[].
     *
     * @param text input text
     * @return array of tokens
     */
    private static String[] tokenize(final CharSequence text) {
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
