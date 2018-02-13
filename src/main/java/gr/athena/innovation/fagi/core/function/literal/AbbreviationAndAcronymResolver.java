package gr.athena.innovation.fagi.core.function.literal;

import com.google.common.base.CharMatcher;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class AbbreviationAndAcronymResolver {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AbbreviationAndAcronymResolver.class);

    private static AbbreviationAndAcronymResolver abbreviationAcronymResolver;
    private static Map<String, String> abbreviationsAndAcronyms;
    private static Locale locale;

    private AbbreviationAndAcronymResolver() {
    }

    public static AbbreviationAndAcronymResolver getInstance() throws ApplicationException {
        //lazy init
        if (abbreviationAcronymResolver == null) {
            abbreviationAcronymResolver = new AbbreviationAndAcronymResolver();
        }

        if (abbreviationsAndAcronyms == null) {
            throw new ApplicationException("Known Abbreviations Map is not set.");
        }
        return abbreviationAcronymResolver;
    }

    public static void setKnownAbbreviationsAndAcronyms(Map<String, String> knownAbbreviationsAndAcronyms) {
        abbreviationsAndAcronyms = knownAbbreviationsAndAcronyms;
    }

    /**
     * Returns the full text of a known abbreviation or acronym. Returns null if the abbreviation/acronym is not found.
     *
     * @param word
     * @return returns the full text of the given abbreviation if exists in the known abbreviation or null otherwise.
     */
    public String getKnownAbbreviationOrAcronym(String word) {
        logger.trace("Get known abbreviation: " + word);
        return abbreviationsAndAcronyms.get(word);
    }

    /**
     * Checks if the given literal contains an abbreviation by using a regular expression from the
     * SpecificationConstants. Basically a modification of {@link IsLiteralAbbreviation} but the check is done against
     * all the words in the literal.
     *
     * @param literal
     * @return returns true if the literal matches the pattern of regular expression that represents an abbreviation
     */
    public boolean containsAbbreviationOrAcronym(String literal) {
        logger.trace("check if literal contains abbreviation/acronym: " + literal);
        List<String> recognized = getAbbreviationOrAcronym(literal, " - ");
        
        return !recognized.isEmpty();

    }

    /**
     * Return the abbreviation/acronym token within the given String if exists. Returns null otherwise.
     *
     * @param literalA the first literal that may contain abbreviation/acronym.
     * @param literalB the second literal which helps at the abbreviation/acronym discovery if anything else fails.
     * @return return the abbreviation/acronym token or null.
     */
    public List<String> getAbbreviationOrAcronym(String literalA, String literalB) {
        logger.trace("getAbbreviationOrAcronym of: " + literalA);

        String[] wordsA = tokenize(literalA);
        String[] wordsB = tokenize(literalB);

        List<String> possibleAbbreviations = new ArrayList<>();
        for (String word : wordsA) {

            if (StringUtils.isBlank(word)) {
                continue;
            }

            //a) Check if word exists in known abbreviations/acronym.
            String resolved = abbreviationsAndAcronyms.get(word);

            if (resolved != null) {
                logger.trace("\n\nabbreviation/acronym \"" + word + "\" is a known abbreviation/acronym. Full text is " + resolved);
                possibleAbbreviations.add(word);
                continue;
            }

            //b) Check if it is a non-standard abbreviation/acronym (single character capitalized token).
            char[] chars = word.toCharArray();
            if (chars.length == 1 && Character.isUpperCase(chars[0])) {
                logger.trace(word + " is a single capitalized character.");
                possibleAbbreviations.add(word);
                continue;
            }

            //c) Check if the word contain more than one capitalized characters.
            char[] upperCaseChars = CharMatcher.javaUpperCase().retainFrom(word).toCharArray();
            if (upperCaseChars.length > 1) {
                logger.trace(word + " uppercase characters more than 1.");
                possibleAbbreviations.add(word);
                continue;
            }

            //d) Check if the word contains two or more dots and has less than 8 characters
            if (word.indexOf(".", word.indexOf(".") + 1) != -1) {
                if (chars.length < 8) {
                    //return the word if it is less than 8 characters including dots.
                    logger.trace(word + " has less than 8 chars including dots.");
                    possibleAbbreviations.add(word);
                    continue;
                }
            }

            //e)
            //at this point we know that the word does not contain more than two dots, or it has more than 8 characters. 
            //We are interested for this word if it ends with a dot and has 4 chars or less (excluding the dot). 
            //If it does, we check against literalB in order to discover if the abbreviation/acronym candidate 
            //is indeed an abbreviation or acronym.
            String result = null;
            if((chars.length <= 4 && vowelsCount(chars) == 0) || word.endsWith(".") && chars.length <= 5){
                possibleAbbreviations.add(word);
                continue;
                //return recognizeAbbreviationFrom(wordsB, chars, word);
            } 
            
            if(result != null && word.endsWith(".") && chars.length <= 5){
                possibleAbbreviations.add(recognizeAcronymFrom(wordsB, chars, word));
            }
        }
        
        if(possibleAbbreviations.isEmpty()){
            return new ArrayList<>();
        } else {
            return possibleAbbreviations;
        }
    }

    private String recognizeAcronymFrom(String[] wordsB, char[] chars, String word) {
        int carret;
        for (int i = 0; i < wordsB.length; i++) {
            if (wordsB[i].startsWith(String.valueOf(chars[0]))) {
                carret = i;
                if ((wordsB.length - carret) > chars.length) {
                    for (int j = 1; j < chars.length; j++) {
                        if (!wordsB[carret + j].startsWith(String.valueOf(chars[j]))) {
                            break;
                        }
                    }
                } else {
                    return null;
                }
            }
        }
        logger.trace("recognized acronym from B: " + word);
        return word;
    }

    private String recognizeAbbreviationFrom(String[] wordsB, char[] chars, String word) {
        String tempWord = word.toLowerCase(locale);
        char[] tempChars;
        if(word.endsWith(".")){

            tempWord = tempWord.substring(0, tempWord.length() - 1);
            char[] lowerChars = tempWord.toLowerCase(locale).toCharArray();
            tempChars = Arrays.copyOfRange(lowerChars, 0, chars.length-1);

        } else {

            tempChars = tempWord.toCharArray();
        }
        

        for (String bWord : wordsB) {
            if(bWord.length() <= tempWord.length()){
                continue;
            }
            
            bWord = bWord.toLowerCase(locale);
            if(startsWithSameChar(bWord, tempWord)){

                char[] bChars = bWord.toCharArray();
                int i = 0;
                int j = 0;
                while(true){
                    if(i < tempChars.length && j < bChars.length){
                        if(tempChars[i] == bChars[j]){

                            i++;
                            j++;

                            if(i == tempChars.length){
                                //all characters matched. return word (returns the abbreviation not the recovered word)
                                return word;
                            }
                        } else {
                            j++;
                        }                          
                    } else {
                        break;
                    }
                }
            }
        }
        return null;
    }

    public String recoverAbbreviation(String word, String text) {
        String tempWord;
        char[] tempChars;
        
        if(word.endsWith(".")){

            tempWord = word.toLowerCase(locale);

            tempWord = tempWord.substring(0, word.length() - 1);
            char[] lowerChars = word.toLowerCase(locale).toCharArray();
            tempChars = Arrays.copyOfRange(lowerChars, 0, lowerChars.length-1);

        } else {
            tempWord = word.toLowerCase(locale);
            tempChars = tempWord.toLowerCase(locale).toCharArray();
        }     
        
        //char[] 
        
        String[] wordsB = tokenize(text);

        for (String bWord : wordsB) {
            if(bWord.length() <= tempWord.length()){
                continue;
            }
            
            String bTempWord = bWord.toLowerCase(locale);
            
            if(startsWithSameChar(bTempWord, tempWord)){
                char[] bChars = bTempWord.toCharArray();
                int i = 0;
                int j = 0;
                while(true){
                    if(i < tempChars.length && j < bChars.length){
                        if(tempChars[i] == bChars[j]){
                            i++;
                            j++;

                            if(i == tempChars.length){
                                //all characters matched. return the recovered word 
                                return bWord;
                            }
                        } else {
                            j++;
                        }                          
                    } else {
                        break;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Recovers the full text of the given acronym from the provided text or from known accronyms. Returns null
     * if it fails to find the match.
     *
     * The method does not check the validity of the acronym, as this is considered done in previous steps. If it
     * fails to match the known acronyms, it gets transformed to contain only word characters and the recovery is
     * tried upon the given text.
     *
     * @param acronym the acronym
     * @param text the text from which the acronym will get recovered.
     * @return return the full text of the given acronym or null on fail.
     */
    public String recoverAcronym(String acronym, String text) {
        logger.trace("recoverAcronym of: " + acronym + " from " + text);

        String[] wordsB = tokenize(text);

        if (StringUtils.isBlank(acronym)) {
            return null;
        }

        String resolved = abbreviationsAndAcronyms.get(acronym);

        if (resolved != null) {
            return resolved;
        }

        String normalizedAcronym = CharMatcher.inRange('a', 'z').retainFrom(acronym.toLowerCase());
        char[] acronymChars = normalizedAcronym.toCharArray();

        if (acronymChars.length == 0) {
            return null;
        }

        int carret;
        String[] full = new String[acronymChars.length];
        for (int i = 0; i < wordsB.length; i++) {
            if (wordsB[i].toLowerCase().startsWith(String.valueOf(acronymChars[0]))) {
                carret = i;
                if ((wordsB.length - carret) >= acronymChars.length) {
                    full[0] = wordsB[carret];
                    for (int j = 1; j < acronymChars.length; j++) {
                        if (wordsB[carret + j].toLowerCase().startsWith(String.valueOf(acronymChars[j]))) {
                            full[j] = wordsB[carret + j];
                            if (j == acronymChars.length - 1) {
                                boolean hasNull = Arrays.asList(full).contains(null);
                                if(!hasNull){
                                    return String.join(" ", full);
                                }
                            }
                        }
                    }
                } else {
                    return null;
                }
            }
        }
        
        boolean hasNull = Arrays.asList(full).contains(null);
        if(!hasNull){
            return String.join(" ", full);
        } else {
            return null;
        }
    }
    
    private static boolean startsWithSameChar(String wordA, String wordB){
        
        
        char[] charsA = wordA.toLowerCase(locale).toCharArray();
        char[] charsB = wordB.toLowerCase(locale).toCharArray();
        
        return charsA[0] == charsB[0];
    }
    
    private static int vowelsCount(char[] chars){
        String word = new String(chars);
        
        if(StringUtils.isBlank(word)){
            return 0;
        }
        int vowelCount = 0;

        String lowerCaseWord = word.toLowerCase(locale);
        
        for(int i=0; i< chars.length; i++){

            char c = lowerCaseWord.charAt(i);
            if (isVowel(c)){
                vowelCount++;
            }            
        }

        return vowelCount;
    }
    
    private static boolean isVowel(Character c){
        String vowels = SpecificationConstants.Evaluation.LOWERCASE_VOWELS;
        return vowels.indexOf(c) >= 0;
    }

    //tokenize on whitespace
    private static String[] tokenize(final CharSequence text) {
        if(text == null){
            return new String[]{};
        }
        
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }

    public static void setLocale(Locale loc) {
        locale = loc;
    }
}
