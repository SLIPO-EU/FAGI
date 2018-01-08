package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing a pair of two literals.
 * 
 * @author nkarag
 */
public class WeightedPairLiteral {

    private static final char CONNECTOR = SpecificationConstants.CONNECTOR;
    
    private String baseValueA;
    private String baseValueB;
    private double baseWeight;
    private Set<LinkedTerm> linkedTerms = new HashSet();
    
    private List<String> mismatchTokensA = new ArrayList();
    private List<String> mismatchTokensB = new ArrayList();
    private double mismatchWeight;
    
    private List<String> uniqueSpecialTermsA = new ArrayList();
    private List<String> uniqueSpecialTermsB = new ArrayList();
    private double specialTermsWeight;
    
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

    public Set<LinkedTerm> getLinkedTerms() {
        return linkedTerms;
    }

    public void setLinkedTerms(Set<LinkedTerm> linkedTerms) {
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

    public void setUniqueSpecialTermsA(List<String> uniqueSpecialTermsA) {
        this.uniqueSpecialTermsA = uniqueSpecialTermsA;
    }

    public void setUniqueSpecialTermsB(List<String> uniqueSpecialTermsB) {
        this.uniqueSpecialTermsB = uniqueSpecialTermsB;
    }
    
    public void addUniqueSpecialTermA(String specialA) {
        uniqueSpecialTermsA.add(specialA);
    }  
    
    public List<String> getUniqueSpecialTermsA() {
        return uniqueSpecialTermsA;
    }

    public void addUniqueSpecialTermB(String specialB) {
        uniqueSpecialTermsB.add(specialB);
    }  
    
    public List<String> getUniqueSpecialTermsB() {
        return uniqueSpecialTermsB;
    }

    public String mismatchToStringA(){

        StringBuilder sb = new StringBuilder().append(CONNECTOR);
        
        mismatchTokensA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString();
    }

    public String mismatchToStringB(){

        StringBuilder sb = new StringBuilder().append(CONNECTOR);
        
        mismatchTokensB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString();
    }

    public String specialTermsToStringA(){

        StringBuilder sb = new StringBuilder().append(CONNECTOR);
        
        uniqueSpecialTermsA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString();
    }

    public String specialTermsToStringB(){

        StringBuilder sb = new StringBuilder().append(CONNECTOR);
        
        uniqueSpecialTermsB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString();
    }

    public String commonTermsToString(){

        StringBuilder sb = new StringBuilder().append(CONNECTOR);
        
        linkedTerms.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString();
    }
    
    public String getCompleteA(){

        StringBuilder sb = new StringBuilder();
        
        sb.append(baseValueA).append(CONNECTOR);
        
        mismatchTokensA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });

        uniqueSpecialTermsA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });

        linkedTerms.stream().forEach((token) -> {
            sb.append(token.getTerm()).append(CONNECTOR);
        });
        
        return sb.toString();
    }
    
    public String getCompleteB(){
        StringBuilder sb = new StringBuilder();
        
        sb.append(baseValueB).append(CONNECTOR);
        
        mismatchTokensB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });

        uniqueSpecialTermsB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        linkedTerms.stream().forEach((token) -> {
            sb.append(token.getTerm()).append(CONNECTOR);
        });
        
        return sb.toString();        
    }

    public double getMismatchWeight() {
        return mismatchWeight;
    }

    public void setMismatchWeight(double mismatchWeight) {
        this.mismatchWeight = mismatchWeight;
    }

    public double getSpecialTermsWeight() {
        return specialTermsWeight;
    }

    public void setSpecialTermsWeight(double specialTermsWeight) {
        this.specialTermsWeight = specialTermsWeight;
    }
}
