package gr.athena.innovation.fagi.rule.model;

import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import java.util.List;

/**
 * Class representing a rule for fusion. 
 * The rule is defined against a pair of properties and the method to apply for the fusion action selection. 
 * 
 * @author nkarag
 */
public class Rule {

    private String propertyA;
    private String propertyB;
    
    //TODO: external properties implementation
    private List<String> externalProperties;
    
    private ActionRuleSet actionRuleSet;
    private EnumFusionAction defaultAction;

    public String getPropertyA() {
        return propertyA;
    }

    public void setPropertyA(String propertyA) {
        this.propertyA = propertyA;
    }

    public String getPropertyB() {
        return propertyB;
    }

    public void setPropertyB(String propertyB) {
        this.propertyB = propertyB;
    }

    public EnumFusionAction getDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(EnumFusionAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public ActionRuleSet getActionRuleSet() {
        return actionRuleSet;
    }

    public void setActionRuleSet(ActionRuleSet actionRuleSet) {
        this.actionRuleSet = actionRuleSet;
    }

    @Override
    public String toString() {
        return "\n\nRule{" + "\npropertyA=" + propertyA + ", \npropertyB=" + propertyB + ", "
                + "\ndefaultAction=" + defaultAction + ", "
                + "\nactionRuleSet=" + actionRuleSet + "}\n\n";
    }
}
