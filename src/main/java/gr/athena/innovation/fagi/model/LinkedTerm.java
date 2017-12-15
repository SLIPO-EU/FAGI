package gr.athena.innovation.fagi.model;

/**
 * Class representing a link between two terms of a corresponding pair of literals, along with their weight.
 * 
 * @author nkarag
 */
public class LinkedTerm {

    private String term;
    private double weight;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
