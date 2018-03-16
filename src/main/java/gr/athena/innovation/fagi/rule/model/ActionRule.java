package gr.athena.innovation.fagi.rule.model;

import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;

/**
 * ActionRule represents a fusion action to be executed when a condition is fulfilled. 
 * The order of appearance inside the general Rule, indicates the priority of the condition-action of the ActionRule. 
 * 
 * @author nkarag
 */
public class ActionRule {

    private Condition condition;
    private EnumFusionAction fusionAction;
    private EnumValidationAction validationAction;

    public EnumFusionAction getFusionAction() {
        return fusionAction;
    }

    public void setFusionAction(EnumFusionAction fusionAction) {
        this.fusionAction = fusionAction;
    }
    
    public EnumValidationAction getValidationAction() {
        return validationAction;
    }

    public void setValidationAction(EnumValidationAction validationAction) {
        this.validationAction = validationAction;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    
    @Override
    public String toString() {
        return "\nActionRule{" + "condition=" + condition + ", action=" + fusionAction + "\n}";
    }    
}
