package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.core.normalizer.INormalizer;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for lower case literal normalization. 
 * 
 * @author nkarag
 */
public class ToLowerCase implements INormalizer{
    
    /**
     * Normalizes the given string literal to lowercase. Returns an empty string if the literal is blank.
     * 
     * @param literal the string literal
     * @return the string literal in lower case.
     */
    public String normalize(String literal) {

        if(StringUtils.isBlank(literal)){
            return "";
        } else {
            return literal.toLowerCase();
        }
    }    

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
