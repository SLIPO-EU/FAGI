package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.Locale;

/**
 * Normalized the given literal with some basic steps. Namely, removes punctuation and transforms to lower case using
 * the given or default locale.
 * 
 * @author nkarag
 */
public class SimpleLiteralNormalizer implements INormalizer {

    private Locale locale = null;

    /**
     * Normalize a literal with basic steps. (remove punctuation, lower casing)
     *
     * @param literal the literal
     * @return the normalized literal
     */
    public String normalize(String literal) {
        return getNormalizedLiteral(literal);
    }

    /**
     * Normalize a literal with basic steps. (remove punctuation, lower casing)
     *
     * @param literal the literal
     * @param locale the locale
     * @return the normalized literal
     */
    public String normalize(String literal, Locale locale) {
        return getNormalizedLiteral(literal, locale);
    }

    private String getNormalizedLiteral(String literal) {

        //1)
        String tempString = removePunctuation(literal);

        //2)to lowercase using locale
        tempString = tempString.toLowerCase(getLocale());

        //3)remove special character except parenthesis
        return removeSpecialCharacters(tempString);

    }

    private String getNormalizedLiteral(String literal, Locale locale) {

        //1)to lowercase using given locale
        String tempString = literal.toLowerCase(locale);

        //2)remove special character except parenthesis
        return removeSpecialCharacters(tempString);

    }
    
    //remove punctuation except parenthesis
    private String removePunctuation(String text) {
        String result = text.replaceAll(SpecificationConstants.Regex.PUNCTUATION_EXCEPT_PARENTHESIS_REGEX, " ");
        return result;
    }

    //remove special character except parenthesis
    private String removeSpecialCharacters(String text) {
        String result = text.replaceAll(SpecificationConstants.Regex.SIMPLE_SPECIAL_CHARS, " ");
        return result;
    }
    
    public Locale getLocale() {
        if(locale == null){
            return Locale.ENGLISH;
        } else {
            return locale;
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
