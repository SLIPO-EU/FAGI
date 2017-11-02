package gr.athena.innovation.fagi.core.normalizer.generic;

import gr.athena.innovation.fagi.core.normalizer.INormalizer;

/**
 * Class for removing special (common used) terms from a literal. 
 * 
 * @author nkarag
 */
public class RemoveSpecialTerms implements INormalizer{
    /**
     * Normalizes the given string literal by removing any special term and replacing with a space character.
     * Uses a text file lexicon containing these terms.
     * 
     * @param literal the string literal
     * @return the normalized literal or an empty string if the given literal is blank.
     */
    public String normalize(String literal) {
        
        throw new UnsupportedOperationException("Not implemented");
//        if(StringUtils.isBlank(literal)){
//            return "";
//        } else {
//            //return normalizedLiteral;
//            return "";
//        }
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
