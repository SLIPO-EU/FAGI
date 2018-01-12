package gr.athena.innovation.fagi.preview;

/**
 *
 * @author nkarag
 */
public class Accuracy {
    private int levenAccur = 0;
    private int ngram2Accur = 0;
    private int cosineAccur = 0;
    private int lqsAccur = 0;
    private int jaroAccur = 0;
    private int jaroWinklerAccur = 0;
    private int jaroWinklerSortedAccur = 0;
    private int total = 0;

    public int getLevenAccur() {
        return levenAccur;
    }

    public void setLevenAccur(int levenAccur) {
        this.levenAccur = levenAccur;
    }

    public int getNgram2Accur() {
        return ngram2Accur;
    }

    public void setNgram2Accur(int ngram2Accur) {
        this.ngram2Accur = ngram2Accur;
    }

    public int getCosineAccur() {
        return cosineAccur;
    }

    public void setCosineAccur(int cosineAccur) {
        this.cosineAccur = cosineAccur;
    }

    public int getLqsAccur() {
        return lqsAccur;
    }

    public void setLqsAccur(int lqsAccur) {
        this.lqsAccur = lqsAccur;
    }

    public int getJaroAccur() {
        return jaroAccur;
    }

    public void setJaroAccur(int jaroAccur) {
        this.jaroAccur = jaroAccur;
    }

    public int getJaroWinklerAccur() {
        return jaroWinklerAccur;
    }

    public void setJaroWinklerAccur(int jaroWinklerAccur) {
        this.jaroWinklerAccur = jaroWinklerAccur;
    }

    public int getJaroWinklerSortedAccur() {
        return jaroWinklerSortedAccur;
    }

    public void setJaroWinklerSortedAccur(int jaroWinklerSortedAccur) {
        this.jaroWinklerSortedAccur = jaroWinklerSortedAccur;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
