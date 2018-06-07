package gr.athena.innovation.fagi.core.similarity;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.logging.log4j.LogManager;

/**
 * Class for computing the cosine similarity and cosine distance of two strings.
 * 
 * @author nkarag
 */
public final class Cosine {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(Cosine.class);

    /**
     * Computes a normalized value of the Cosine similarity for the given strings. 
     * It constructs two vectors with the word frequency of each string by tokenizing it to words.
     * The similarity range is already normalized between [0,1] 
     * because there cannot be negative frequencies of a word occurrences.
     * 
     * See: <a href="https://en.wikipedia.org/wiki/Cosine_similarity">
     * https://en.wikipedia.org/wiki/Cosine_similarity</a>.
     * 
     * Note that if the computed value is not close to the SIMILARITY_MAX or SIMILARITY_MIN 
     * the method returns a rounded number using the RoundingMode.HALF_UP strategy 
     * on the <code>SpecificationConstants.Similarity.ROUND_DECIMALS</code> decimal digits.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the normalized similarity result. 
     */
    public static double computeSimilarity(String a, String b){

        if(StringUtils.isBlank(a) || StringUtils.isBlank(b)){
            return 0.0;
        }
        
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

        //split string to words:
        CharSequence[] wordsA = tokenize(a);

        //create vectors
        Map<CharSequence, Integer> aVector = new HashMap<>();
        for (CharSequence w : wordsA) {
            Integer n = aVector.get(w);
            n = (n == null) ? 1 : ++n;
            aVector.put(w, n);
        } 

        CharSequence[] wordsB = tokenize(b);

        Map<CharSequence, Integer> bVector = new HashMap<>();
        for (CharSequence w : wordsB) {
            Integer n = bVector.get(w);
            n = (n == null) ? 1 : ++n;
            bVector.put(w, n);
        }

        Double result = cosineSimilarity.cosineSimilarity(aVector, bVector);

        if(result > SpecificationConstants.Similarity.SIMILARITY_MAX){
            return 1.0;
        } else if(result < SpecificationConstants.Similarity.SIMILARITY_MIN){
            return 0.0;
        } else {
            double roundedResult = new BigDecimal(result).
                    setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_3, RoundingMode.HALF_UP).doubleValue();

            return roundedResult;
        }
    }

    /**
     * Computes a normalized value of the Cosine similarity for the given strings. 
     * Cosine similarity is in the range of [-1,1] so we normalize the result by mapping it to [0,1].
     * See: <a href="https://en.wikipedia.org/wiki/Cosine_similarity">
     * https://en.wikipedia.org/wiki/Cosine_similarity</a>.
     * 
     * Note that if the computed value is not close to the SIMILARITY_MAX or SIMILARITY_MIN 
     * the method returns a rounded number using the RoundingMode.HALF_UP strategy 
     * on the <code>SpecificationConstants.Similarity.ROUND_DECIMALS</code> decimal digits.
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the normalized similarity result. 
     */
    public static double computeDistance(String a, String b){

        CosineDistance cosDist = new CosineDistance();

        Double result = cosDist.apply(a, b);
        
        //check the returned result 
        if(result > SpecificationConstants.Similarity.SIMILARITY_MAX){
            return 1;
        } else if(result < SpecificationConstants.Similarity.SIMILARITY_MIN){
            return 0;
        } else {
            double resultRounded = new BigDecimal(result).
                    setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_3, RoundingMode.HALF_UP).doubleValue();

            return resultRounded;
        }
    }

    /**
     * Returns an array of tokens. Utilizes regex to find words. It applies a regex
     * {@code}(\w)+{@code} over the input text to extract words from a given character
     * sequence. Implementation taken from org.apache.commons.text.similarity
     *
     * @param text input text
     * @return array of tokens
     */
    public static CharSequence[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");
        final Pattern pattern = Pattern.compile("(\\w)+");
        final Matcher matcher = pattern.matcher(text.toString());
        final List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens.toArray(new String[0]);
    }    
}
