package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.function.literal.AbbreviationAndAcronymResolver;
import gr.athena.innovation.fagi.core.normalizer.generic.AlphabeticalNormalizer;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.Locale;
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
public class BasicGenericNormalizer implements INormalizer {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(BasicGenericNormalizer.class);
    private Locale locale = null;
    
    /**
     * Normalize literalA using some basic steps information from literalB (in case of abbreviation recovery).
     *
     * @param literalA the literalA
     * @param literalB the literalB
     * @return the normalized literalA or an empty string if the initial literalA or the produced normalized value is
     * blank.
     */
    public String normalize(String literalA, String literalB) {
        return getNormalizedLiteral(literalA, literalB);
    }
    
    public NormalizedLiteral getNormalizedLiteral(String literalA, String literalB, Locale locale) {

        String tempString;

        //1)
        String literalAabbr = getAbbreviation(literalA, literalB);

        //normalized literal has abbreviation replaced if it is known or can be recovered from literalB.
        //2)
        tempString = removePunctuation(literalAabbr);

        //3)lowercase
        tempString = toLowerCase(tempString, locale);

        //4)remove special character except parenthesis
        tempString = removeSpecialCharacters(tempString);

        //5)sort string alphabetically
        tempString = sortAlphabetically(tempString);

        return createNormalizedLiteral(literalA, tempString);
    }

    //Same steps with the getNormalizedLiteral, returns a string instead. 
    //This method is used by the 'normalize' method that is called from a rule function.
    //The above method returns a 'NormalizedLiteral' to be used for the custom normalization steps.
    private String getNormalizedLiteral(String literalA, String literalB) {

        String tempString;

        //1)
        String literalAabbr = getAbbreviation(literalA, literalB);

        //normalized literal has abbreviation replaced if it is known or can be recovered from literalB.
        //2)
        tempString = removePunctuation(literalAabbr);

        //3)to lowercase using locale
        tempString = tempString.toLowerCase(getLocale());

        //4)remove special character except parenthesis
        tempString = removeSpecialCharacters(tempString);

        //5)sort string alphabetically
        return sortAlphabetically(tempString);
    }
    
    //1)Recover abbreviation if possible. Returns the whole literalA.
    private String getAbbreviation(String literalA, String literalB) {

        AbbreviationAndAcronymResolver resolver = AbbreviationAndAcronymResolver.getInstance();
        List<String> possibleAbbreviations = resolver.getAbbreviationOrAcronym(literalA, literalB);
        
        if(possibleAbbreviations.isEmpty()){
            return literalA;
        }
        
        for(String possibleAbbreviation : possibleAbbreviations){
            String recoveredAcronym;
            String recoveredAbbreviation;
            if (possibleAbbreviation != null) {

                recoveredAbbreviation = resolver.recoverAbbreviation(possibleAbbreviation, literalB);
                
                if (recoveredAbbreviation != null) {
                    literalA = literalA.replace(possibleAbbreviation, recoveredAbbreviation);
                }
                
                if(recoveredAbbreviation == null){
                    recoveredAcronym = resolver.recoverAcronym(possibleAbbreviation, literalB);
                    
                    if (recoveredAcronym != null) {
                        literalA = literalA.replace(possibleAbbreviation, recoveredAcronym);
                    }                    
                }
            }
        }
        return literalA;
    }

    //2) 
    //remove punctuation except parenthesis
    private String removePunctuation(String text) {
        String result = text.replaceAll(SpecificationConstants.Regex.PUNCTUATION_EXCEPT_PARENTHESIS_REGEX, " ");
        return result;
    }

    //3) 
    //transform to lowercase
    private String toLowerCase(String text, Locale locale) {
        return text.toLowerCase(locale);
    }

    //4) 
    //remove special character except parenthesis
    private String removeSpecialCharacters(String text) {
        //String result = text.replaceAll(SpecificationConstants.Regex.NON_WORD_EXCEPT_PARENTHESIS_REGEX_2, " ");
        String result = text.replaceAll(SpecificationConstants.Regex.SPECIAL_CHARS, " ");
        return result;
    }

    //5) 
    //sort words alphabetically
    private String sortAlphabetically(String text) {
        AlphabeticalNormalizer normalizer = new AlphabeticalNormalizer();
        String result= normalizer.normalize(text);
        return result;
    }

    private NormalizedLiteral createNormalizedLiteral(String original, String normalized) {
        NormalizedLiteral normalizedLiteral = new NormalizedLiteral();
        normalizedLiteral.setLiteral(original);
        normalizedLiteral.setNormalized(normalized);
        normalizedLiteral.setIsNormalized(true);

        return normalizedLiteral;
    }
    
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
