package gr.athena.innovation.fagi.preview.statistics;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author nkarag
 */
public class StatisticResultPair implements Serializable{

    private final String a;
    private final String b;
    private String label;

    @Override
    public int hashCode() {
        
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.a);
        hash = 29 * hash + Objects.hashCode(this.b);
        hash = 29 * hash + Objects.hashCode(this.label);
        
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
        
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return label + "\na=" + a + ", b=" + b + "\n\n";
    }

    public StatisticResultPair(String a, String b) {
        this.a = a;
        this.b = b;
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
}
