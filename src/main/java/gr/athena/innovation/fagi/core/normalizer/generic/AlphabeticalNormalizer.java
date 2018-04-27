package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.core.normalizer.INormalizer;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

/**
 * Class for normalizing words of literals in alphabetical order. 
 * 
 * @author nkarag
 */
public class AlphabeticalNormalizer implements INormalizer{
    
    private static final org.apache.logging.log4j.Logger LOG 
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
            LOG.trace("normalizedLiteral:" + normalizedLiteral);
            
            return normalizedLiteral;
        }
    }
    
    //tokenize on whitespaces
    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }
    
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
