package gr.athena.innovation.fagi.model;

/**
 *
 * @author nkarag
 */
public class CategoryWeight {
    
    private boolean emptyMismatch = false;
    private boolean emptySpecials = false;
    private boolean zeroBaseSimilarity = false;

    public CategoryWeight(WeightedPairLiteral pair) {
        
        String baseA = pair.getBaseValueA();
        String baseB = pair.getBaseValueB();
        
        String mismatchA = pair.mismatchToStringA();
        String mismatchB = pair.mismatchToStringB();
        
        String specialsA = pair.specialTermsToStringA();
        String specialsB = pair.specialTermsToStringB();
        
        if(baseA.isEmpty() || baseB.isEmpty()){
            zeroBaseSimilarity = true;
        }

        if(mismatchA.isEmpty() && mismatchB.isEmpty()){
            emptyMismatch = true;
        }

        if(specialsA.isEmpty() && specialsB.isEmpty()){
            emptySpecials = true;
        }        
    }

    public boolean isEmptyMismatch() {
        return emptyMismatch;
    }

    public boolean isEmptySpecials() {
        return emptySpecials;
    }

    public boolean isZeroBaseSimilarity() {
        return zeroBaseSimilarity;
    }
}
