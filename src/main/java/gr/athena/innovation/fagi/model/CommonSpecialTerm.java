package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.Objects;

/**
 * Class representing a link between two terms of a corresponding pair of literals, along with their weight.
 * 
 * @author nkarag
 */
public class CommonSpecialTerm {

    private String term;
    private final double weight = SpecificationConstants.Evaluation.COMMON_SPECIAL_TERM_WEIGHT;

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
        final CommonSpecialTerm other = (CommonSpecialTerm) obj;
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
}
