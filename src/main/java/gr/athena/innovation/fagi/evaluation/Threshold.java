package gr.athena.innovation.fagi.evaluation;

/**
 * Class representing the threshold for all metrics used in the evaluation process. 
 * 
 * @author nkarag
 */
public class Threshold {
    
    private double levenshtein = 0;
    private double ngram2 = 0;
    private double cosine = 0;
    private double lqs = 0;
    private double jaccard = 0;
    private double jaro = 0;
    private double jaroWinkler = 0;
    private double sortedJaroWinkler = 0;

    @Override
    public String toString() {
        return "Threshold{" + "levenshtein=" + levenshtein + ", ngram2=" + ngram2 
                //+ ", cosine=" + cosine 
                + ", lqs=" + lqs 
                //+ ", jaccard=" + jaccard 
                + ", jaro=" + jaro + ", jaroWinkler=" + jaroWinkler +'}';
                //+ ", sortedJaroWinkler=" + sortedJaroWinkler + '}';
    }

    /**
     * Return the Levenshtein threshold.
     * 
     * @return the threshold.
     */
    public double getLevenshtein() {
        return levenshtein;
    }

    /**
     * Set the Levenshtein threshold.
     * 
     * @param levenshtein the threshold.
     */
    public void setLevenshtein(double levenshtein) {
        this.levenshtein = levenshtein;
    }

    /**
     * Return the N-gram threshold. N is 2 in this instance.
     * 
     * @return the threshold.
     */
    public double getNgram2() {
        return ngram2;
    }

    /**
     * Set the N-gram threshold. N is 2 in this instance.
     * 
     * @param ngram2 the threshold.
     */
    public void setNgram2(double ngram2) {
        this.ngram2 = ngram2;
    }

    /**
     * Return the Cosine threshold.
     * 
     * @return the threshold.
     */
    public double getCosine() {
        return cosine;
    }

    /**
     * Set the Cosine threshold.
     * 
     * @param cosine the threshold.
     */
    public void setCosine(double cosine) {
        this.cosine = cosine;
    }

    /**
     * Return the Longest Common Subsequence  threshold.
     * 
     * @return the threshold.
     */
    public double getLqs() {
        return lqs;
    }

    /**
     * Set the Longest Common Subsequence threshold.
     * 
     * @param lqs the threshold.
     */
    public void setLqs(double lqs) {
        this.lqs = lqs;
    }

    /**
     * Return the Jaccard threshold.
     * 
     * @return the threshold.
     */
    public double getJaccard() {
        return jaccard;
    }

    /**
     * Set the Jaccard threshold.
     * 
     * @param jaccard the threshold.
     */
    public void setJaccard(double jaccard) {
        this.jaccard = jaccard;
    }

    /**
     * Return the Jaro threshold.
     * 
     * @return the threshold.
     */
    public double getJaro() {
        return jaro;
    }

    /**
     * Set the Jaro threshold.
     * 
     * @param jaro the threshold.
     */
    public void setJaro(double jaro) {
        this.jaro = jaro;
    }

    /**
     * Return the Jaro-Winkler threshold.
     * 
     * @return the threshold.
     */
    public double getJaroWinkler() {
        return jaroWinkler;
    }

    /**
     * Set the Jaro-Winkler threshold.
     * 
     * @param jaroWinkler the threshold.
     */
    public void setJaroWinkler(double jaroWinkler) {
        this.jaroWinkler = jaroWinkler;
    }

    /**
     * Return the Sorted Jaro-Winkler threshold.
     * 
     * @return the threshold.
     */
    public double getSortedJaroWinkler() {
        return sortedJaroWinkler;
    }

    /**
     * Set the Sorted Jaro-Winkler threshold.
     * 
     * @param sortedJaroWinkler the threshold.
     */
    public void setSortedJaroWinkler(double sortedJaroWinkler) {
        this.sortedJaroWinkler = sortedJaroWinkler;
    }

}
