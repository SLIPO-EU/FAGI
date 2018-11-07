package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nkarag
 */
public class FusionLog {

    //add model-selection score if case of ML action
    //keep previeous values to be fed to user feedback at a later time
    private String leftURI;
    private String rightURI;
    //private final Map<String, String> actions = new HashMap<>();
    private final List<Action> actions = new ArrayList<>();
    private EnumDatasetAction defaultFusionAction;
    private EnumValidationAction validationAction;

    @Override
    public String toString() {
        return "FusionInfo{" + "leftURI=" + leftURI + ", rightURI=" + rightURI 
                + ", actions=" + actions + ", defaultFusionAction=" + defaultFusionAction 
                + ", validationAction=" + validationAction + '}';
    }

    public String getLeftURI() {
        return leftURI;
    }

    public void setLeftURI(String leftURI) {
        this.leftURI = leftURI;
    }

    public String getRightURI() {
        return rightURI;
    }

    public void setRightURI(String rightURI) {
        this.rightURI = rightURI;
    }

//    public Map<String, String> getActions() {
//        return actions;
//    }
//
//    public void addAction(String property, String action) {
//        actions.put(property, action);
//    }

    public List<Action> getActions() {
        return actions;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public EnumDatasetAction getDefaultFusionAction() {
        return defaultFusionAction;
    }

    public void setDefaultFusionAction(EnumDatasetAction defaultFusionAction) {
        this.defaultFusionAction = defaultFusionAction;
    }

    public EnumValidationAction getValidationAction() {
        return validationAction;
    }

    public void setValidationAction(EnumValidationAction validationAction) {
        this.validationAction = validationAction;
    }
}



