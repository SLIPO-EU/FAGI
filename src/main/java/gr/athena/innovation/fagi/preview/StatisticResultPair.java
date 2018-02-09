package gr.athena.innovation.fagi.preview;

/**
 *
 * @author nkarag
 */
public class StatisticResultPair{

    private final String a;
    private final String b;
    private String name;

    @Override
    public String toString() {
        return name + "\na=" + a + ", b=" + b + "\n\n";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
