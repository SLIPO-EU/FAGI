package gr.athena.innovation.fagi.core.normalizer.generic;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author nkarag
 */
public class AlphabeticalNormalizer {
    
    /**
     * Returns the string with its words alphabetically sorted.
     * 
     * @param literal the string literal
     * @return the normalized literal string.
     */
    public String normalize(String literal) {

        if(StringUtils.isBlank(literal)){
            return "";
        } else {
            
            String[] parts = literal.split("\\s+");   
            Arrays.sort(parts);  
            StringBuilder sb = new StringBuilder();  
            for(String s:parts){  
               sb.append(s);
               sb.append(" ");
            }

            String normalizedLiteral = sb.toString().trim();
            return normalizedLiteral;
        }
    }     
}
