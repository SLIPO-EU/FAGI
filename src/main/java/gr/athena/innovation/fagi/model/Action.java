package gr.athena.innovation.fagi.model;

/**
 * Action class models a fusion action that applies to a specific attribute for fusion logging.
 * 
 * @author nkarag
 */
public class Action {

    private final String attribute;
    private final String fusionAction;
    private final String valueA;
    private final String valueB;
    private final String fusedValue;

    Action(String attribute, String fusionAction, String valueA, String valueB, String fusedValue) {
        this.attribute = attribute;
        this.fusionAction = fusionAction;
        this.valueA = valueA;
        this.valueB = valueB;
        this.fusedValue = fusedValue;
    }
    
    @Override
    public String toString() {
        return "Action{" + "attribute=" + attribute + ", fusionAction=" + fusionAction 
                + ", valueA=" + valueA + ", valueB=" + valueB + ", fusedValue=" + fusedValue + '}';
    }

    /**
     * Return the attribute.
     * 
     * @return the attribute.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Return the fusion action as a string.
     * 
     * @return the fusion action.
     */
    public String getFusionAction() {
        return fusionAction;
    }

    /**
     * Return the value of A (left).
     * 
     * @return the left value.
     */
    public String getValueA() {
        return valueA;
    }

    /**
     * Return the value of B (right).
     * 
     * @return the right value.
     */
    public String getValueB() {
        return valueB;
    }

    /**
     * Return the fused value as a String.
     * 
     * @return the fused value.
     */
    public String getFusedValue() {
        return fusedValue;
    }
}
