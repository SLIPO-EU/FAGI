package gr.athena.innovation.fagi.rule.model;

/**
 * ExternalProperty represents an auxiliary (external) property of a rule to be used in a condition. The fusion action
 * does not affect the value of this property. The action always refers to the basic properties A and B.
 *
 * @author nkarag
 */
public class ExternalProperty {

    private String parameter;
    private String property;
    private String valueA;
    private String valueB;

    @Override
    public String toString() {
        return "{parameter: " + parameter + ", property=" + property + '}';
    }

    public String getParameter() {
        return parameter;

    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {

        this.property = property;
    }

    public String getValueA() {
        return valueA;
    }

    public void setValueA(String valueA) {
        this.valueA = valueA;
    }

    public String getValueB() {
        return valueB;
    }

    public void setValueB(String valueB) {
        this.valueB = valueB;
    }

}
