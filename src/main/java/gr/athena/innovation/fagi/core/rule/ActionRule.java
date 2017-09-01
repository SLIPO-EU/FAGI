package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import gr.athena.innovation.fagi.core.action.EnumMetadataActions;

/**
 * ActionRule represents a fusion action to be executed when a condition is fulfilled. 
 * The order of appearance inside the general Rule, indicates the priority of the condition-action of the ActionRule. 
 * 
 * @author nkarag
 */
public class ActionRule {

    private ConditionTag condition;
    private EnumGeometricActions geoAction;
    private EnumMetadataActions metaAction;
            
    public void setConditionTag(ConditionTag condition){
        this.condition = condition;
    }
    
    public ConditionTag getConditionTag(){
        return condition;
    }

    public EnumGeometricActions getGeoAction() {
        return geoAction;
    }

    public void setGeoAction(EnumGeometricActions geoAction) {
        this.geoAction = geoAction;
    }

    public EnumMetadataActions getMetaAction() {
        return metaAction;
    }

    public void setMetaAction(EnumMetadataActions metaAction) {
        this.metaAction = metaAction;
    }

    @Override
    public String toString() {
        return "ActionRule{" + "condition=" + condition + ", geoAction=" + geoAction + ", metaAction=" + metaAction + '}';
    }
}
