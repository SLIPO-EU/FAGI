package gr.athena.innovation.fagi.evaluation;

/**
 * Container class of accuracy values for each similarity metric.
 * 
 * @author nkarag
 */
public class Accuracy {
    private int levenshteinCount = 0;
    private int ngram2Count = 0;
    private int cosineCount = 0;
    private int lqsCount = 0;
    private int jaroCount = 0;
    private int jaroWinklerCount = 0;
    private int jaroWinklerSortedCount = 0;
    private int jaccardCount = 0;

    /**
     * Return the levenshtein metric count.
     * 
     * @return the int value.
     */
    public int getLevenshteinCount() {
        return levenshteinCount;
    }

    /**
     * Set the levenshtein metric count.
     * 
     * @param levenshteinCount the int value.
     */
    public void setLevenshteinCount(int levenshteinCount) {
        this.levenshteinCount = levenshteinCount;
    }

    /**
     * Return the N-gram metric count. N is 2 in this instance.
     * 
     * @return the int value.
     */
    public int getNgram2Count() {
        return ngram2Count;
    }

    /**
     * Set the N-gram metric count. N is 2 in this instance.
     * 
     * @param ngram2Count the int value.
     */
    public void setNgram2Count(int ngram2Count) {
        this.ngram2Count = ngram2Count;
    }

    /**
     * Return the cosine metric count.
     * 
     * @return the int value.
     */
    public int getCosineCount() {
        return cosineCount;
    }

    /**
     * Set the cosine metric count.
     * 
     * @param cosineCount the int value.
     */
    public void setCosineCount(int cosineCount) {
        this.cosineCount = cosineCount;
    }

    /**
     * Return the longest common subsequence metric count.
     * 
     * @return the int value.
     */
    public int getLqsCount() {
        return lqsCount;
    }

    /**
     * Set the longest common subsequence metric count.
     * 
     * @param lqsCount the int value.
     */
    public void setLqsCount(int lqsCount) {
        this.lqsCount = lqsCount;
    }

    /**
     * Return the Jaro metric count.
     * 
     * @return the int value.
     */
    public int getJaroCount() {
        return jaroCount;
    }

    /**
     * Set the Jaro metric count.
     * 
     * @param jaroCount the int value.
     */
    public void setJaroCount(int jaroCount) {
        this.jaroCount = jaroCount;
    }

    /**
     * Return the Jaro-Winkler metric count.
     * 
     * @return the int value.
     */
    public int getJaroWinklerCount() {
        return jaroWinklerCount;
    }

    /**
     * Set the Jaro-Winkler metric count.
     * 
     * @param jaroWinklerCount the int value.
     */
    public void setJaroWinklerCount(int jaroWinklerCount) {
        this.jaroWinklerCount = jaroWinklerCount;
    }

    /**
     * Return the Sorted Jaro-Winkler metric count.
     * 
     * @return the int value.
     */
    public int getJaroWinklerSortedCount() {
        return jaroWinklerSortedCount;
    }

    /**
     * Set the Sorted Jaro-Winkler metric count.
     * 
     * @param jaroWinklerSortedCount the int value.
     */
    public void setJaroWinklerSortedCount(int jaroWinklerSortedCount) {
        this.jaroWinklerSortedCount = jaroWinklerSortedCount;
    }

    /**
     * Return the Jaccard metric count.
     * 
     * @return the int value.
     */
    public int getJaccardCount() {
        return jaccardCount;
    }

    /**
     * Set  the Jaccard metric count.
     * 
     * @param jaccardCount the int value.
     */
    public void setJaccardCount(int jaccardCount) {
        this.jaccardCount = jaccardCount;
    }
}
