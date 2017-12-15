package gr.athena.innovation.fagi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a pair of two literals.
 * 
 * @author nkarag
 */
public class WeightedPairLiteral {

    private String baseValueA;
    private String baseValueB;
    private double baseWeight;
    private String mismatchA;
    private String mismatchB;
    private List<LinkedTerm> linkedTerms = new ArrayList();
    private List<String> mismatchTokensA = new ArrayList();
    private List<String> mismatchTokensB = new ArrayList();
    
    public String getBaseValueA() {
        return baseValueA;
    }

    public void setBaseValueA(String baseValueA) {
        this.baseValueA = baseValueA;
    }

    public String getBaseValueB() {
        return baseValueB;
    }

    public void setBaseValueB(String baseValueB) {
        this.baseValueB = baseValueB;
    }

    public String getMismatchA() {
        return mismatchA;
    }

    public void setMismatchA(String mismatchA) {
        this.mismatchA = mismatchA;
    }

    public String getMismatchB() {
        return mismatchB;
    }

    public void setMismatchB(String mismatchB) {
        this.mismatchB = mismatchB;
    }

    public List<LinkedTerm> getLinkedTerms() {
        return linkedTerms;
    }

    public void setLinkedTerms(List<LinkedTerm> linkedTerms) {
        this.linkedTerms = linkedTerms;
    }
    
    public void addLinkedTerm(LinkedTerm linkedTerm) {
        linkedTerms.add(linkedTerm);
    }

    public List<String> getMismatchTokensA() {
        return mismatchTokensA;
    }

    public void setMismatchTokensA(List<String> mismatchTokensA) {
        this.mismatchTokensA = mismatchTokensA;
    }
    
    public void addMismatchTokenA(String mismatchTokenA) {
        mismatchTokensA.add(mismatchTokenA);
    }
    
    public List<String> getMismatchTokensB() {
        return mismatchTokensB;
    }

    public void setMismatchTokensB(List<String> mismatchTokensB) {
        this.mismatchTokensB = mismatchTokensB;
    }
    
    public void addMismatchTokenB(String mismatchTokenB) {
        mismatchTokensB.add(mismatchTokenB);
    }    

    public double getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(double baseWeight) {
        this.baseWeight = baseWeight;
    }
}
