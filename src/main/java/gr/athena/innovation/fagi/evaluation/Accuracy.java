package gr.athena.innovation.fagi.evaluation;

/**
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

    public int getLevenshteinCount() {
        return levenshteinCount;
    }

    public void setLevenshteinCount(int levenshteinCount) {
        this.levenshteinCount = levenshteinCount;
    }

    public int getNgram2Count() {
        return ngram2Count;
    }

    public void setNgram2Count(int ngram2Count) {
        this.ngram2Count = ngram2Count;
    }

    public int getCosineCount() {
        return cosineCount;
    }

    public void setCosineCount(int cosineCount) {
        this.cosineCount = cosineCount;
    }

    public int getLqsCount() {
        return lqsCount;
    }

    public void setLqsCount(int lqsCount) {
        this.lqsCount = lqsCount;
    }

    public int getJaroCount() {
        return jaroCount;
    }

    public void setJaroCount(int jaroCount) {
        this.jaroCount = jaroCount;
    }

    public int getJaroWinklerCount() {
        return jaroWinklerCount;
    }

    public void setJaroWinklerCount(int jaroWinklerCount) {
        this.jaroWinklerCount = jaroWinklerCount;
    }

    public int getJaroWinklerSortedCount() {
        return jaroWinklerSortedCount;
    }

    public void setJaroWinklerSortedCount(int jaroWinklerSortedCount) {
        this.jaroWinklerSortedCount = jaroWinklerSortedCount;
    }

    public int getJaccardCount() {
        return jaccardCount;
    }

    public void setJaccardCount(int jaccardCount) {
        this.jaccardCount = jaccardCount;
    }
}
