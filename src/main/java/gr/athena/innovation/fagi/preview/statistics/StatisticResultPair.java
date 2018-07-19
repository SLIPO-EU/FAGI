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

    private EnumStatViewType type;

    private final String valueA;
    private final String valueB;
    private String valueTotal;
    private String legendA;
    private String legendB;
    private String legendTotal;
    private final String both;
    private String title;

    private EnumStatGroup group;

    public StatisticResultPair(String a, String b, String both) {
        this.valueA = a;
        this.valueB = b;
        this.both = both;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.valueA);
        hash = 11 * hash + Objects.hashCode(this.valueB);
        hash = 11 * hash + Objects.hashCode(this.both);
        hash = 11 * hash + Objects.hashCode(this.title);
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
        if (!Objects.equals(this.valueA, other.valueA)) {
            return false;
        }
        if (!Objects.equals(this.valueB, other.valueB)) {
            return false;
        }
        if (!Objects.equals(this.both, other.both)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StatisticResultPair{" + "a=" + valueA + ", b=" + valueB + ", both=" + both + ", label=" + title + '}';
    }

    public EnumStatGroup getGroup() {
        return group;
    }

    public void setGroup(EnumStatGroup group) {
        this.group = group;
    }
    
    public String getValueA() {
        return valueA;
    }

    public String getValueB() {
        return valueB;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBoth() {
        return both;
    }

    public EnumStatViewType getType() {
        return type;
    }

    public void setType(EnumStatViewType type) {
        this.type = type;
    }

    public String getLegendA() {
        return legendA;
    }

    public void setLegendA(String legendA) {
        this.legendA = legendA;
    }

    public String getLegendB() {
        return legendB;
    }

    public void setLegendB(String legendB) {
        this.legendB = legendB;
    }

    public String getLegendTotal() {
        return legendTotal;
    }

    public void setLegendTotal(String legendTotal) {
        this.legendTotal = legendTotal;
    }

    public String getValueTotal() {
        return valueTotal;
    }

    public void setValueTotal(String valueTotal) {
        this.valueTotal = valueTotal;
    }
}
