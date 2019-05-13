package gr.athena.innovation.fagi.preview.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class describing a statistic. Serialization of the object ignores null values.
 * 
 * @author nkarag
 */
@JsonInclude(Include.NON_NULL)
public class StatisticResultPair implements Serializable{

    
    private final String valueA;
    private final String valueB;
    private final String both;
    
    private EnumStatViewType type;
    private StatGroup group;
    private String valueTotal;
    private String legendA;
    private String legendB;
    private String legendTotal;
    private String title;

    /**
     * Constructor of statistic result pair object.
     * 
     * @param a the left value of the stat.
     * @param b the right value of the stat.
     * @param both the value that refers to both.
     */
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
        hash = 11 * hash + Objects.hashCode(this.group);
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
        if (!Objects.equals(this.group, other.group)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StatisticResultPair{" + "valueA=" + valueA + ", valueB=" + valueB + ", both=" + both + ", type=" + type 
                + ", group=" + group + ", valueTotal=" + valueTotal + ", legendA=" + legendA + ", legendB=" + legendB 
                + ", legendTotal=" + legendTotal + ", title=" + title + '}';
    }

    /**
     * 
     * @return the value of A.
     */
    public String getValueA() {
        return valueA;
    }

    /**
     *
     * @return the value of B.
     */
    public String getValueB() {
        return valueB;
    }

    /**
     *
     * @return the title of the statistic.
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title the title of the statistic.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return the value that refers to both.
     */
    public String getBoth() {
        return both;
    }

    /**
     * 
     * @return the enumeration of the type of the statistic view.
     */
    public EnumStatViewType getType() {
        return type;
    }

    /**
     * 
     * @param type the enumeration of the type of the statistic view.
     */
    public void setType(EnumStatViewType type) {
        this.type = type;
    }

    /**
     *
     * @return the legend of A.
     */
    public String getLegendA() {
        return legendA;
    }

    /**
     *
     * @param legendA the legend of A.
     */
    public void setLegendA(String legendA) {
        this.legendA = legendA;
    }

    /**
     *
     * @return the legend of B.
     */
    public String getLegendB() {
        return legendB;
    }

    /**
     *
     * @param legendB the legend of B.
     */
    public void setLegendB(String legendB) {
        this.legendB = legendB;
    }

    /**
     *
     * @return the legend that refers to the total value.
     */
    public String getLegendTotal() {
        return legendTotal;
    }

    /**
     *
     * @param legendTotal the legend that refers to the total value.
     */
    public void setLegendTotal(String legendTotal) {
        this.legendTotal = legendTotal;
    }

    /**
     *
     * @return the total value.
     */
    public String getValueTotal() {
        return valueTotal;
    }

    /**
     *
     * @param valueTotal the total value.
     */
    public void setValueTotal(String valueTotal) {
        this.valueTotal = valueTotal;
    }

    /**
     *
     * @return the statistic group object.
     */
    public StatGroup getGroup() {
        return group;
    }

    /**
     *
     * @param group the statistic group object.
     */
    public void setGroup(StatGroup group) {
        this.group = group;
    }
}
