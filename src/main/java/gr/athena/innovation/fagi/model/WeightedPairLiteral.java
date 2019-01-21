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

    private List<String> specialTermsA = new ArrayList();
    private List<String> specialTermsB = new ArrayList();
    
    /**
     * Return the base value of A.
     * 
     * @return the base value of A.
     */
    public String getBaseValueA() {
        return baseValueA;
    }

    /**
     * Set the base value of A.
     * 
     * @param baseValueA the base value of A.
     */
    public void setBaseValueA(String baseValueA) {
        this.baseValueA = baseValueA;
    }

    /**
     * Return the base value of B.
     * 
     * @return the base value of B.
     */
    public String getBaseValueB() {
        return baseValueB;
    }

    /**
     * Set the base value of B.
     * 
     * @param baseValueB the base value of B.
     */
    public void setBaseValueB(String baseValueB) {
        this.baseValueB = baseValueB;
    }

    /**
     * Return the set that contains the common special terms.
     * 
     * @return the set of the common special terms.
     */
    public Set<CommonSpecialTerm> getCommonSpecialTerms() {
        return commonSpecialTerms;
    }

    /**
     * Set the common special terms.
     * 
     * @param commonSpecialTerms the common special terms.
     */
    public void setCommonSpecialTerms(Set<CommonSpecialTerm> commonSpecialTerms) {
        this.commonSpecialTerms = commonSpecialTerms;
    }
    
    /**
     * Add a common special term.
     * 
     * @param commonSpecialTerm the common special term.
     */
    public void addLinkedTerm(CommonSpecialTerm commonSpecialTerm) {
        commonSpecialTerms.add(commonSpecialTerm);
    }

    /**
     * Return the mismatch tokens of A.
     * 
     * @return the list of the mismatch tokens of A.
     */
    public List<String> getMismatchTokensA() {
        return mismatchTokensA;
    }

    /**
     * Set the list with the mismatch tokens of A.
     * 
     * @param mismatchTokensA the mismatch tokens of A.
     */
    public void setMismatchTokensA(List<String> mismatchTokensA) {
        this.mismatchTokensA = mismatchTokensA;
    }
    
    /**
     * Add a mismatch token for A.
     * 
     * @param mismatchTokenA the mismatch token.
     */
    public void addMismatchTokenA(String mismatchTokenA) {
        mismatchTokensA.add(mismatchTokenA);
    }
    
    /**
     * Return the list with the mismatch tokens of B.
     * 
     * @return the mismatch tokens of B.
     */
    public List<String> getMismatchTokensB() {
        return mismatchTokensB;
    }

    /**
     * Set the list with the mismatch tokens of B.
     * 
     * @param mismatchTokensB the mismatch tokens of B.
     */
    public void setMismatchTokensB(List<String> mismatchTokensB) {
        this.mismatchTokensB = mismatchTokensB;
    }
    
    /**
     * Add a mismatch token for B.
     * 
     * @param mismatchTokenB the mismatch token.
     */
    public void addMismatchTokenB(String mismatchTokenB) {
        mismatchTokensB.add(mismatchTokenB);
    }

    /**
     * Set the special terms of A.
     * 
     * @param specialTermsA the special terms of A.
     */
    public void setSpecialTermsA(List<String> specialTermsA) {
        this.specialTermsA = specialTermsA;
    }

    /**
     * Set the special terms of B.
     * 
     * @param specialTermsB the special terms of B.
     */
    public void setSpecialTermsB(List<String> specialTermsB) {
        this.specialTermsB = specialTermsB;
    }
    
    /**
     * Add a special term for A.
     * 
     * @param specialA the special term.
     */
    public void addUniqueSpecialTermA(String specialA) {
        specialTermsA.add(specialA);
    }  
    
    /**
     * Get the special terms of A.
     * 
     * @return the list of the special terms of A.
     */
    public List<String> getSpecialTermsA() {
        return specialTermsA;
    }

    /**
     * Add a special term for B.
     * 
     * @param specialB the special term.
     */
    public void addUniqueSpecialTermB(String specialB) {
        specialTermsB.add(specialB);
    }  
    
    /**
     * Return the list of the special terms of B.
     * 
     * @return the special terms of B.
     */
    public List<String> getSpecialTermsB() {
        return specialTermsB;
    }

    /**
     * Return the mismatch tokens of A as a string concatenated with the connector.
     * 
     * @return the mismatch tokens as a concatenated string.
     */
    public String mismatchToStringA(){

        StringBuilder sb = new StringBuilder();
        
        mismatchTokensA.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    /**
     * Return the mismatch tokens of B as a string concatenated with the connector.
     * 
     * @return the mismatch tokens as a concatenated string.
     */
    public String mismatchToStringB(){

        StringBuilder sb = new StringBuilder();
        
        mismatchTokensB.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    /**
     * Return the special terms of A as a string concatenated with the connector.
     * 
     * @return the special terms as a concatenated string.
     */
    public String specialTermsToStringA(){

        StringBuilder sb = new StringBuilder();
        
        specialTermsA.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    /**
     * Return the special terms of B as a string concatenated with the connector.
     * 
     * @return the special terms as a concatenated string.
     */
    public String specialTermsToStringB(){

        StringBuilder sb = new StringBuilder();
        
        specialTermsB.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }

    /**
     * Return the common terms as a string concatenated with the connector.
     * 
     * @return the common terms as a concatenated string.
     */
    public String commonTermsToString(){

        StringBuilder sb = new StringBuilder();
        
        commonSpecialTerms.stream().forEach((CommonSpecialTerm token) -> {
            sb.append(token.getTerm()).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }
    
    /**
     * Return the complete value of A as a string. 
     * The method uses a StringBuilder and concatenates all tokens with the default connector. 
     * The order of the complete value is base - mismatch - special terms - common terms.
     * 
     * @return the complete value of A.
     */
    public String getCompleteA(){

        StringBuilder sb = new StringBuilder();
        
        sb.append(baseValueA).append(CONNECTOR);
        
        mismatchTokensA.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });

        specialTermsA.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });

        commonSpecialTerms.stream().forEach((CommonSpecialTerm token) -> {
            sb.append(token.getTerm()).append(CONNECTOR);
        });
        
        return sb.toString().trim();
    }
    
    /**
     * Return the complete value of B as a string. 
     * The method uses a StringBuilder and concatenates all tokens with the default connector. 
     * The order of the complete value is base - mismatch - special terms - common terms.
     * 
     * @return the complete value of A.
     */
    public String getCompleteB(){
        StringBuilder sb = new StringBuilder();
        
        sb.append(baseValueB).append(CONNECTOR);
        
        mismatchTokensB.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });

        specialTermsB.stream().forEach((String token) -> {
            sb.append(token).append(CONNECTOR);
        });
        
        commonSpecialTerms.stream().forEach((CommonSpecialTerm token) -> {
            sb.append(token.getTerm()).append(CONNECTOR);
        });
        
        return sb.toString().trim();        
    }
}
