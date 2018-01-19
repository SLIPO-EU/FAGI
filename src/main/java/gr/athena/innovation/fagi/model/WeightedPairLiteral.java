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
    private Set<LinkedTerm> linkedTerms = new HashSet();
    
    private List<String> mismatchTokensA = new ArrayList();
    private List<String> mismatchTokensB = new ArrayList();
    
    //unique means they exist only in one entity name for each pair.
    private List<String> uniqueSpecialTermsA = new ArrayList();
    private List<String> uniqueSpecialTermsB = new ArrayList();
    
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

        StringBuilder sb = new StringBuilder();
        
        mismatchTokensA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    public String mismatchToStringB(){

        StringBuilder sb = new StringBuilder();
        
        mismatchTokensB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    public String specialTermsToStringA(){

        StringBuilder sb = new StringBuilder();
        
        uniqueSpecialTermsA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    public String specialTermsToStringB(){

        StringBuilder sb = new StringBuilder();
        
        uniqueSpecialTermsB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    public String commonTermsToString(){

        StringBuilder sb = new StringBuilder();
        
        linkedTerms.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
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
        
        return sb.toString().trim();
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
        
        return sb.toString().trim();        
    }
}
