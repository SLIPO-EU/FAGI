package gr.athena.innovation.fagi.rule.model;

import org.apache.jena.rdf.model.Literal;

/**
 * ExternalProperty represents an auxiliary (external) property of a rule to be used in a condition. The fusion action
 * does not affect the value of this property. The action always refers to the basic properties A and B.
 *
 * @author nkarag
 */
public class ExternalProperty {

    private String parameter;
    private String property;
    private Literal valueA;
    private Literal valueB;

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

    public Literal getValueA() {
        return valueA;
    }

    public void setValueA(Literal valueA) {
        this.valueA = valueA;
    }

    public Literal getValueB() {
        return valueB;
    }

    public void setValueB(Literal valueB) {
        this.valueB = valueB;
    }

}
