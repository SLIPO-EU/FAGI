package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import gr.athena.innovation.fagi.core.action.EnumMetadataActions;

/**
 *
 * @author nkarag
 */
public class ActionRule {
    
    private Condition condition;
    private EnumGeometricActions geoAction;
    private EnumMetadataActions metaAction;
            
    public void setCondition(Condition condition){
        this.condition = condition;
    }
    
    public Condition getCondition(){
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
