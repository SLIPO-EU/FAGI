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
    private Set<CommonSpecialTerm> commonSpecialTerms = new HashSet();
    
    private List<String> mismatchTokensA = new ArrayList();
    private List<String> mismatchTokensB = new ArrayList();
    
    //unique means they exist only in one entity name for each pair.
    private List<String> specialTermsA = new ArrayList();
    private List<String> specialTermsB = new ArrayList();
    
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

    public Set<CommonSpecialTerm> getCommonSpecialTerms() {
        return commonSpecialTerms;
    }

    public void setCommonSpecialTerms(Set<CommonSpecialTerm> commonSpecialTerms) {
        this.commonSpecialTerms = commonSpecialTerms;
    }
    
    public void addLinkedTerm(CommonSpecialTerm linkedTerm) {
        commonSpecialTerms.add(linkedTerm);
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

    public void setSpecialTermsA(List<String> specialTermsA) {
        this.specialTermsA = specialTermsA;
    }

    public void setSpecialTermsB(List<String> specialTermsB) {
        this.specialTermsB = specialTermsB;
    }
    
    public void addUniqueSpecialTermA(String specialA) {
        specialTermsA.add(specialA);
    }  
    
    public List<String> getSpecialTermsA() {
        return specialTermsA;
    }

    public void addUniqueSpecialTermB(String specialB) {
        specialTermsB.add(specialB);
    }  
    
    public List<String> getSpecialTermsB() {
        return specialTermsB;
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
        
        specialTermsA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    public String specialTermsToStringB(){

        StringBuilder sb = new StringBuilder();
        
        specialTermsB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    public String commonTermsToString(){

        StringBuilder sb = new StringBuilder();
        
        commonSpecialTerms.stream().forEach((token) -> {
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

        specialTermsA.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });

        commonSpecialTerms.stream().forEach((token) -> {
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

        specialTermsB.stream().forEach((token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        commonSpecialTerms.stream().forEach((token) -> {
            sb.append(token.getTerm()).append(CONNECTOR);
        });
        
        return sb.toString().trim();        
    }
}
