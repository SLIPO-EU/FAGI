package gr.athena.innovation.fagi.model;

import java.util.Objects;

/**
 * Class representing a link between two terms of a corresponding pair of literals, along with their weight.
 * 
 * @author nkarag
 */
public class LinkedTerm {

    private String term;
    private double weight;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.term);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkedTerm other = (LinkedTerm) obj;
        if (!Objects.equals(this.term, other.term)) {
            return false;
        }
        return true;
    }

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
