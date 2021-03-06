package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.core.normalizer.INormalizer;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for normalizing literals by removing special characters.
 * 
 * @author nkarag
 */
public class RemoveSpecialCharacters implements INormalizer{
    
    /**
     * Normalizes the given string literal by removing any special characters and replacing with a space character.
     * By v0.2 the special characters are - _ / @
     * 
     * @param literal the string literal
     * @return the normalized literal or an empty string if the given literal is blank.
     */
    public String normalize(String literal) {
        
        //v0.1
        //SpecificationConstants.Regex.NON_WORD_CHARACTERS_REGEX
        // \w : A word character, short for [a-zA-Z_0-9]
        // \W : A non-word character. Considers space also a non-word character, but the replace is also a space.
        
        //v0.2
        //SpecificationConstants.Regex.SPECIAL_CHARS
        //replace - _ / @ with spaces
        
        String normalizedLiteral = literal.replaceAll(SpecificationConstants.Regex.SPECIAL_CHARS, " ");

        if(StringUtils.isBlank(literal)){
            return "";
        } else {
            return normalizedLiteral;
        }
    }     

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
