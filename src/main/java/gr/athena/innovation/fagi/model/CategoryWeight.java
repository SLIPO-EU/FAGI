package gr.athena.innovation.fagi.model;

import java.util.List;
import java.util.Set;

/**
 * Class configuring category weights.
 * 
 * @author nkarag
 */
public class CategoryWeight {
    
    private boolean halfEmptyMismatch = false;
    private boolean fullEmptyMismatch = false;
    private boolean emptySpecials = false;
    private boolean emptyCommon = false;
    private boolean zeroBaseSimilarity = false;
    private boolean fullZeroBaseSimilarity = false;
    
    /**
     * Constructor of a category weight.
     * 
     * @param pair the WeightedPairLiteral object.
     */
    public CategoryWeight(WeightedPairLiteral pair) {
        
        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();
        
        List<String> mismatchA = pair.getMismatchTokensA();
        List<String> mismatchB = pair.getMismatchTokensB();
        
        List<String> specialsA = pair.getSpecialTermsA();
        List<String> specialsB = pair.getSpecialTermsB();
        
        Set<CommonSpecialTerm> common = pair.getCommonSpecialTerms();
        
        // giann: added the fullZeroBaseSimilarity check  /        
        if(baseA.isEmpty() && baseB.isEmpty()){
            fullZeroBaseSimilarity = true;
        }
        
        /*giann: replaced with XOR*/
        if(baseA.isEmpty() ^ baseB.isEmpty()){
            zeroBaseSimilarity = true;
        }

        if((mismatchA.isEmpty() && !mismatchB.isEmpty()) || (!mismatchA.isEmpty() && mismatchB.isEmpty())){
            halfEmptyMismatch = true;
        }

        if(mismatchA.isEmpty() && mismatchB.isEmpty()){
            fullEmptyMismatch = true;
        }
        
        if(specialsA.isEmpty() && specialsB.isEmpty()){
            emptySpecials = true;
        } 
        
        if(common.isEmpty()){
            emptyCommon = true;
        }        
    }

    /**
     * Return the halfEmptyMismatch value.
     * 
     * @return true if there is a "half-empty" mismatch.
     */
    public boolean isHalfEmptyMismatch() {
        return halfEmptyMismatch;
    }

    /**
     * Return the emptySpecials value.
     * 
     * @return true if the special terms are empty.
     */
    public boolean isEmptySpecials() {
        return emptySpecials;
    }

    /**
     * Return the zeroBaseSimilarity.
     * 
     * @return true if the base values have zero similarity.
     */
    public boolean isZeroBaseSimilarity() {
        return zeroBaseSimilarity;
    }

    /**
     * Return the emptyCommon value.
     * 
     * @return true if the common terms list is empty.
     */
    public boolean isEmptyCommon() {
        return emptyCommon;
    }

    /**
     * Return the fullEmptyMismatch value.
     * 
     * @return true if there is a "full-empty" mismatch.
     */
    public boolean isFullEmptyMismatch() {
        return fullEmptyMismatch;
    }
}
