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

    public String getAttribute() {
        return attribute;
    }

    public String getFusionAction() {
        return fusionAction;
    }

    public String getValueA() {
        return valueA;
    }

    public String getValueB() {
        return valueB;
    }

    public String getFusedValue() {
        return fusedValue;
    }
}
