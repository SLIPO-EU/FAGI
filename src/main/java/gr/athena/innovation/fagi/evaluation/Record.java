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

    /**
     * The key of the record (int value). 
     * 
     * @return the key.
     */
    public int getKey() {
        return key;
    }

    /**
     * Set the key of the record.
     * 
     * @param key the key.
     */
    public void setKey(int key) {
        this.key = key;
    }
    
    /**
     * Return the name A (left) of the record. 
     * 
     * @return the name A.
     */
    public String getNameA() {
        return nameA;
    }

    /**
     * Set the name A (left) of the record. 
     * 
     * @param nameA the name A.
     */
    public void setNameA(String nameA) {
        this.nameA = nameA;
    }

    /**
     * Return the name B (right) of the record. 
     * 
     * @return the name B.
     */
    public String getNameB() {
        return nameB;
    }

    /**
     * Set the name B (right) of the record. 
     * 
     * @param nameB the name B.
     */
    public void setNameB(String nameB) {
        this.nameB = nameB;
    }

    /**
     * The acceptance value as a string.
     * 
     * @return the acceptance value.
     */
    public String getAcceptance() {
        return acceptance;
    }

    /**
     * Set the acceptance value as a string.
     * 
     * @param acceptance the acceptance value.
     */
    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }
}
