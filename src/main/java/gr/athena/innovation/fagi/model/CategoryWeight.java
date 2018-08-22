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

    public boolean isHalfEmptyMismatch() {
        return halfEmptyMismatch;
    }

    public boolean isEmptySpecials() {
        return emptySpecials;
    }

    public boolean isZeroBaseSimilarity() {
        return zeroBaseSimilarity;
    }

    public boolean isEmptyCommon() {
        return emptyCommon;
    }

    public boolean isFullEmptyMismatch() {
        return fullEmptyMismatch;
    }
}
