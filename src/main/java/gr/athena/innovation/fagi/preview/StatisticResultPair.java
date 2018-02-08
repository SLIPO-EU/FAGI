package gr.athena.innovation.fagi.preview;

/**
 *
 * @author nkarag
 */
public class StatisticResultPair{

    private final int a;
    private final int b;
    private String name;

    @Override
    public String toString() {
        return name + "\na=" + a + ", b=" + b + "\n\n";
    }

    public StatisticResultPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
