package gr.athena.innovation.fagi.preview.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author nkarag
 */
@JsonInclude(Include.NON_NULL)
public class StatisticResultPair implements Serializable{

    private final String a;
    private final String b;
    private final String both;
    private String label;

    public StatisticResultPair(String a, String b, String both) {
        this.a = a;
        this.b = b;
        this.both = both;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.a);
        hash = 11 * hash + Objects.hashCode(this.b);
        hash = 11 * hash + Objects.hashCode(this.both);
        hash = 11 * hash + Objects.hashCode(this.label);
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
        final StatisticResultPair other = (StatisticResultPair) obj;
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        if (!Objects.equals(this.b, other.b)) {
            return false;
        }
        if (!Objects.equals(this.both, other.both)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StatisticResultPair{" + "a=" + a + ", b=" + b + ", both=" + both + ", label=" + label + '}';
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBoth() {
        return both;
    }
}
