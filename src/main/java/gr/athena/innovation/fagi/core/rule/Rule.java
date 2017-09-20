package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import gr.athena.innovation.fagi.core.action.EnumMetadataActions;

/**
 * Class representing a rule for fusion. 
 * The rule is defined against a pair of properties and the method to apply for the fusion action selection. 
 * 
 * @author nkarag
 */
public class Rule {

    private String propertyA;
    private String propertyB;
    private ActionRuleSet actionRuleSet;
    private EnumGeometricActions defaultGeoAction;
    private EnumMetadataActions defaultMetaAction;

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

    public EnumGeometricActions getDefaultGeoAction() {
        return defaultGeoAction;
    }

    public void setDefaultGeoAction(EnumGeometricActions defaultGeoAction) {
        this.defaultGeoAction = defaultGeoAction;
    }

    public EnumMetadataActions getDefaultMetaAction() {
        return defaultMetaAction;
    }

    public void setDefaultMetaAction(EnumMetadataActions defaultMetaAction) {
        this.defaultMetaAction = defaultMetaAction;
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
                + "\ndefaultGeoAction=" + defaultGeoAction + ", "
                + "\ndefaultMetaAction=" + defaultMetaAction + ", "
                + "\nactionRuleSet=" + actionRuleSet + "}\n\n";
    }    
}
