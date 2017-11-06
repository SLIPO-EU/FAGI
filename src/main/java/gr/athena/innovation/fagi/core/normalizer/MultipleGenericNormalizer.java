package gr.athena.innovation.fagi.core.normalizer;

import gr.athena.innovation.fagi.core.functions.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Uses a combination of the other available normalizations and similarity functions.
 * Produces normalized literals for matching purposes.
 * 
 * @author nkarag
 */
public class MultipleGenericNormalizer implements INormalizer{

    /**
     * Normalize literalA using the other available normalizations and optionally information from literalB.
     * 
     * @param literalA the literalA
     * @param literalB the literalB
     * @return the normalized literalA or an empty string if the initial literalA or the produced normalized value is blank.
     */
    public String normalize(String literalA, String literalB){
        //remove punctuation except parenthesis
        literalA = literalA.replaceAll(SpecificationConstants.Regex.PUNCTUATION_EXCEPT_PARENTHESIS_REGEX, "");

        //transform to lowercase
        literalA = literalA.toLowerCase();
        
        
        //remove special character except parenthesis
        literalA = literalA.replaceAll(SpecificationConstants.Regex.NON_WORD_EXCEPT_PARENTHESIS_REGEX, " ");

        //try to replace abbreviation with full string if matches
        IsLiteralAbbreviation abbr = new IsLiteralAbbreviation();
        if(abbr.evaluate(literalA)){
            
        }
        
        //sort string alphabetically
        //identify special/frequent terms:
        //-If both contain them -> map these terms to each other and produce an individual score for the final similarity.
        //-If only one contains them -> exclude it and assign a small weight for the mismatch
        //Optionally concatenate all words of each string for specific distance measures.

        
        if(StringUtils.isBlank(literalA)){
            return "";
        } else {
            return literalA;
        }        
    }
    
    private String recoverAbbreviation(String literalA, String literalB){
        //logger.trace("Try to recover " + literalA + " abbreviation from " + literalB);
        String[] possibleAbbreviations = literalA.split("\\s+");
        
        
        
        return "";
    }
        
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
    
}
