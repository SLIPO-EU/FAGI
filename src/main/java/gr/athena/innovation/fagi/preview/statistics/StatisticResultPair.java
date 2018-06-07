package gr.athena.innovation.fagi.preview.statistics;

import java.io.Serializable;

/**
 *
 * @author nkarag
 */
public class StatisticResultPair implements Serializable{

    private final String a;
    private final String b;
    private String label;
    private EnumStatisticType type;

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
