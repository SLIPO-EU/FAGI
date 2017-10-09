package gr.athena.innovation.fagi.core.rule.model;

import gr.athena.innovation.fagi.core.action.EnumFusionAction;

/**
 * ActionRule represents a fusion action to be executed when a condition is fulfilled. 
 * The order of appearance inside the general Rule, indicates the priority of the condition-action of the ActionRule. 
 * 
 * @author nkarag
 */
public class ActionRule {

    private Condition condition;
    //private EnumGeometricActions geoAction;
    //private EnumMetadataActions metaAction;
    private EnumFusionAction action;

//    public EnumGeometricActions getGeoAction() {
//        return geoAction;
//    }
//
//    public void setGeoAction(EnumGeometricActions geoAction) {
//        this.geoAction = geoAction;
//    }
//
//    public EnumMetadataActions getMetaAction() {
//        return metaAction;
//    }
//
//    public void setMetaAction(EnumMetadataActions metaAction) {
//        this.metaAction = metaAction;
//    }

    public EnumFusionAction getAction() {
        return action;
    }

    public void setAction(EnumFusionAction action) {
        this.action = action;
    }
    
    public boolean isConditionFulfilled(){
        return true;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    
    @Override
    public String toString() {
        return "\nActionRule{" + "condition=" + condition + ", action=" + action + "\n}";
    }    
}
