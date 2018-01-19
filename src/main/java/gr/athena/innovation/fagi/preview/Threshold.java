package gr.athena.innovation.fagi.preview;

/**
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
        return "Threshold{" + "levenshtein=" + levenshtein + ", ngram2=" + ngram2 + ", cosine=" + cosine 
                + ", lqs=" + lqs + ", jaccard=" + jaccard + ", jaro=" + jaro + ", jaroWinkler=" + jaroWinkler 
                + ", sortedJaroWinkler=" + sortedJaroWinkler + '}';
    }

    public double getLevenshtein() {
        return levenshtein;
    }

    public void setLevenshtein(double levenshtein) {
        this.levenshtein = levenshtein;
    }

    public double getNgram2() {
        return ngram2;
    }

    public void setNgram2(double ngram2) {
        this.ngram2 = ngram2;
    }

    public double getCosine() {
        return cosine;
    }

    public void setCosine(double cosine) {
        this.cosine = cosine;
    }

    public double getLqs() {
        return lqs;
    }

    public void setLqs(double lqs) {
        this.lqs = lqs;
    }

    public double getJaccard() {
        return jaccard;
    }

    public void setJaccard(double jaccard) {
        this.jaccard = jaccard;
    }

    public double getJaro() {
        return jaro;
    }

    public void setJaro(double jaro) {
        this.jaro = jaro;
    }

    public double getJaroWinkler() {
        return jaroWinkler;
    }

    public void setJaroWinkler(double jaroWinkler) {
        this.jaroWinkler = jaroWinkler;
    }

    public double getSortedJaroWinkler() {
        return sortedJaroWinkler;
    }

    public void setSortedJaroWinkler(double sortedJaroWinkler) {
        this.sortedJaroWinkler = sortedJaroWinkler;
    }

}
