package gr.athena.innovation.fagi.rule.model;

import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a rule for fusion. 
 * The rule is defined against a pair of properties and the method to apply for the fusion action selection. 
 * 
 * @author nkarag
 */
public class Rule {

    private String parentPropertyA = null;
    private String propertyA;
    private String parentPropertyB = null;    
    private String propertyB;

    private Map<String, ExternalProperty> externalProperties = new HashMap<>();
    
    private ActionRuleSet actionRuleSet;
    private EnumFusionAction defaultFusionAction;
    private EnumValidationAction defaultValidationAction;

    //this property is always the end of a chain and points to a literal.
    public String getPropertyA() {
        return propertyA;
    }

    public void setPropertyA(String propertyA) {
        if(propertyA.contains(" ")){
            String[] chains = propertyA.split(" ");
            this.parentPropertyA = chains[0];
            this.propertyA = chains[1];
        } else {
            this.propertyA = propertyA;
        }
    }
    
    //this property is always the end of a chain and points to a literal.
    public String getPropertyB() {
        return propertyB;
    }

    public void setPropertyB(String propertyB) {
        if(propertyB.contains(" ")){
            String[] chains = propertyB.split(" ");
            this.parentPropertyB = chains[0];
            this.propertyB = chains[1];
        } else {
            this.propertyB = propertyB;
        }
    }

    public String getParentPropertyA() {
        return parentPropertyA;
    }

    public String getParentPropertyB() {
        return parentPropertyB;
    }
    
    public EnumFusionAction getDefaultFusionAction() {
        return defaultFusionAction;
    }

    public void setDefaultFusionAction(EnumFusionAction defaultFusionAction) {
        this.defaultFusionAction = defaultFusionAction;
    }

    public ActionRuleSet getActionRuleSet() {
        return actionRuleSet;
    }

    public void setActionRuleSet(ActionRuleSet actionRuleSet) {
        this.actionRuleSet = actionRuleSet;
    }

    public Map<String, ExternalProperty> getExternalProperties() {
        return externalProperties;
    }

    public void putExternalProperty(String parameter, ExternalProperty externalProperty) {
        externalProperties.put(parameter, externalProperty);
    }
    
    @Override
    public String toString() {
        return "\n\nRule{" + "\npropertyA=" + propertyA + "\npropertyB=" + propertyB 
                + "\nexternalProperties=" + externalProperties + "\nactionRuleSet=" + actionRuleSet 
                + "\ndefaultAction=" + defaultFusionAction + "}\n\n";
    }

    public EnumValidationAction getDefaultValidationAction() {
        return defaultValidationAction;
    }

    public void setDefaultValidationAction(EnumValidationAction defaultValidationAction) {
        this.defaultValidationAction = defaultValidationAction;
    }
}
