package gr.athena.innovation.fagi.core.normalizer.generic;

import org.apache.commons.lang3.StringUtils;

/**
 * Class for lower case literal normalization. 
 * 
 * @author nkarag
 */
public class ToLowerCase {
    
    /**
     * Normalizes the given string literal to lowercase. Returns an empty string if the literal is blank.
     * 
     * @param literal the string literal
     * @return 
     */
    public String normalize(String literal) {

        if(StringUtils.isBlank(literal)){
            return "";
        } else {
            return literal.toLowerCase();
        }
    }    
}
