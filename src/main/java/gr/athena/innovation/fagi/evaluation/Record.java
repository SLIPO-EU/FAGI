package gr.athena.innovation.fagi.evaluation;

/**
 * Class representing a record from a training file.
 * The record contains the two names of the pair (a and b) and the acceptance value.
 * 
 * @author nkarag
 */
public class Record {
    
    private int key;
    private String nameA;
    private String nameB;
    private String acceptance;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
    
    public String getNameA() {
        return nameA;
    }

    public void setNameA(String nameA) {
        this.nameA = nameA;
    }

    public String getNameB() {
        return nameB;
    }

    public void setNameB(String nameB) {
        this.nameB = nameB;
    }

    public String getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }
}
