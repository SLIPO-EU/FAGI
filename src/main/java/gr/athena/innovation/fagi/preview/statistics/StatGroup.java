package gr.athena.innovation.fagi.preview.statistics;

import java.util.Objects;

/**
 * Class representing a group of statistics.
 * 
 * @author nkarag
 */
public class StatGroup {
    private EnumStatGroup enumGroup;
    private String title;
    private String legendA;
    private String legendB;
    private String legendTotal;
    
    /**
     * Constructor of a statistic group object.
     * 
     * @param enumGroup the group enumeration.
     */
    public StatGroup(EnumStatGroup enumGroup){

        switch(enumGroup) {
            case UNDEFINED: 
                break;
            case PERCENT: 
                this.enumGroup = enumGroup;
                this.title = "Percentages of property values";
                this.legendA = "Non empty fields A(%)";
                this.legendB = "Non empty fields B(%)";
                this.legendTotal = "Total(%)";
                
                break;
            case PROPERTY: 
                this.enumGroup = enumGroup;
                this.title = "Number of property values";
                this.legendA = "Properties of dataset A";
                this.legendB = "Properties of dataset B";
                this.legendTotal = "Total Properties";

                break;
            case TRIPLE_BASED: 
                this.enumGroup = enumGroup;
                this.title = "Number of Triples";
                this.legendA = "Triples in A";
                this.legendB = "Triples in B";
                this.legendTotal = "Total Triples";
                
                break;
            case POI_BASED: 
                this.enumGroup = enumGroup;
                this.title = "Number of POIs";
                this.legendA = "Number of POIs ";
                this.legendB = "Number of POIs";
                this.legendTotal = "Total";
                
                break;
            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "StatGroup{" + "enumGroup=" + enumGroup + ", title=" + title 
                + ", legendA=" + legendA + ", legendB=" + legendB + ", legendTotal=" + legendTotal + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.enumGroup);
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + Objects.hashCode(this.legendA);
        hash = 97 * hash + Objects.hashCode(this.legendB);
        hash = 97 * hash + Objects.hashCode(this.legendTotal);
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
        final StatGroup other = (StatGroup) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.legendA, other.legendA)) {
            return false;
        }
        if (!Objects.equals(this.legendB, other.legendB)) {
            return false;
        }
        if (!Objects.equals(this.legendTotal, other.legendTotal)) {
            return false;
        }
        return this.enumGroup == other.enumGroup;
    }

    /**
     * Return the enumeration of the statistic group.
     * 
     * @return the statistic group enumeration.
     */
    public EnumStatGroup getEnumGroup() {
        return enumGroup;
    }

    /**
     * Return the title.
     * 
     * @return the title.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Return the legend of A.
     * 
     * @return the legend.
     */
    public String getLegendA() {
        return legendA;
    }

    /**
     * Return the legend of B.
     * 
     * @return the legend.
     */
    public String getLegendB() {
        return legendB;
    }

    /**
     * The legend that refers to the total value.
     * 
     * @return the legend of the total value.
     */
    public String getLegendTotal() {
        return legendTotal;
    }
}
