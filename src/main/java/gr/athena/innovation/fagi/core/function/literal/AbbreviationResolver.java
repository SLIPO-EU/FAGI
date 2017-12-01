package gr.athena.innovation.fagi.core.function.literal;

import com.google.common.base.CharMatcher;
import gr.athena.innovation.fagi.exception.ApplicationException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class AbbreviationResolver {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AbbreviationResolver.class);

    private static AbbreviationResolver abbreviationResolver;
    private static Map<String, String> abbreviations;

    private AbbreviationResolver() {
    }

    public static AbbreviationResolver getInstance() throws ApplicationException {
        //lazy init
        if (abbreviationResolver == null) {
            abbreviationResolver = new AbbreviationResolver();
        }

        if (abbreviations == null) {
            throw new ApplicationException("Known Abbreviations Map is not set.");
        }
        return abbreviationResolver;
    }

    public static void setKnownAbbreviations(Map<String, String> knownAbbreviations) {
        abbreviations = knownAbbreviations;
    }

    /**
     * Returns the full text of a known abbreviation. Returns null if the abbreviation is not found.
     *
     * @param abbreviation
     * @return returns the full text of the given abbreviation if exists in the known abbreviation or null otherwise.
     */
    public String getKnownAbbreviation(String abbreviation) {
        logger.trace("Get known abbreviation: " + abbreviation);
        return abbreviations.get(abbreviation);
    }

    /**
     * Checks if the given literal contains an abbreviation by using a regular expression from the
     * SpecificationConstants. Basically a modification of {@link IsLiteralAbbreviation} but the check is done against
     * all the words in the literal.
     *
     * @param literal
     * @return returns true if the literal matches the pattern of regular expression that represents an abbreviation
     */
    public boolean containsAbbreviation(String literal) {
        logger.trace("check if literal contains abbreviation: " + literal);

        String recognized = getAbbreviation(literal, " - ");
        return recognized != null;
    }

    /**
     * Return the abbreviation token within the given String if exists. Returns null otherwise.
     *
     * @param literalA the first literal that may contain abbreviation.
     * @param literalB the second literal which helps at the abbreviation discovery if anything else fails.
     * @return return the abbreviation token or null.
     */
    public String getAbbreviation(String literalA, String literalB) {
        logger.trace("getAbbreviation of: " + literalA);

        String[] wordsA = tokenize(literalA);
        String[] wordsB = tokenize(literalB);

        for (String word : wordsA) {
            logger.trace("word: " + word);
            if (StringUtils.isBlank(word)) {
                continue;
            }

            //a) Check if word exists in known abbreviations.
            String resolved = abbreviations.get(word);

            if (resolved != null) {
                logger.trace("\n\nabbreviation \"" + word + "\" is a known abbreviation. Full text is " + resolved);
                return word;
            }

            //b) Check if it is a non-standard abbreviation (single character capitalized token).
            char[] chars = word.toCharArray();
            if (chars.length == 1 && Character.isUpperCase(chars[0])) {
                logger.trace(word + " is a single capitalized character.");
                return word;
            }

            //c) Check if the word contain more than one capitalized characters.
            char[] upperCaseChars = CharMatcher.javaUpperCase().retainFrom(word).toCharArray();
            if (upperCaseChars.length > 1) {
                logger.trace(word + " uppercase characters more than 1.");
                return word;
            }

            //d) Check if the word contains two or more dots and has less than 8 characters
            if (word.indexOf(".", word.indexOf(".") + 1) != -1) {
                if (chars.length < 8) {
                    //return the word if it is less than 8 characters including dots.
                    logger.trace(word + " has less than 8 chars including dots.");
                    return word;
                }
            }

            //e)
            //at this point we know that the word does not contain more than two dots, or it has more than 8 characters. 
            //We are interested for this word if it ends with a dot and has 4 chars or less (excluding the dot). 
            //If it does, we check against literalB in order to discover if the abbreviation candidate is indeed an abbreviation.
            if (word.endsWith(".") && chars.length <= 5) {
                return recognizeAbbreviationFrom(wordsB, chars, word);
            }
        }
        return null;
    }

    private String recognizeAbbreviationFrom(String[] wordsB, char[] chars, String word) {
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
        logger.trace("recognized abbreviation from B: " + word);
        return word;
    }

    /**
     * Recovers the full text of the given abbreviation from the provided text or from known abbreviations. Returns null
     * if it fails to find the match.
     *
     * The method does not check the validity of the abbreviation, as this is considered done in previous steps. If it
     * fails to match the known abbreviations, it gets transformed to contain only word characters and the recovery is
     * tried upon the given text.
     *
     * @param abbreviation the abbreviation
     * @param text the text from which the abbreviation will get recovered.
     * @return return the full text of the given abbreviation or null on fail.
     */
    public String recoverAbbreviation(String abbreviation, String text) {
        logger.trace("recoverAbbreviation of: " + abbreviation + " from " + text);

        String[] wordsB = tokenize(text);

        if (StringUtils.isBlank(abbreviation)) {
            return null;
        }

        String resolved = abbreviations.get(abbreviation);

        if (resolved != null) {
            return resolved;
        }

        String normalizedAbbreviation = CharMatcher.inRange('a', 'z').retainFrom(abbreviation.toLowerCase());
        char[] abbreviationChars = normalizedAbbreviation.toCharArray();

        if (abbreviationChars.length == 0) {
            return null;
        }

        int carret;
        String[] full = new String[abbreviationChars.length];
        for (int i = 0; i < wordsB.length; i++) {
            if (wordsB[i].toLowerCase().startsWith(String.valueOf(abbreviationChars[0]))) {
                carret = i;
                if ((wordsB.length - carret) >= abbreviationChars.length) {
                    full[0] = wordsB[carret];
                    for (int j = 1; j < abbreviationChars.length; j++) {
                        if (wordsB[carret + j].toLowerCase().startsWith(String.valueOf(abbreviationChars[j]))) {
                            full[j] = wordsB[carret + j];
                            if (j == abbreviationChars.length - 1) {
                                return String.join(" ", full);
                            }
                        }
                    }
                } else {
                    return null;
                }
            }
        }
        return String.join(" ", full);
    }

    /**
     * Returns an array of tokens extracted from the given text by whitespace.
     *
     * @param text input text
     * @return array of tokens
     */
    public static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
    }
}
