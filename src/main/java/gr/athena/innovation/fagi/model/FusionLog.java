package gr.athena.innovation.fagi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class FusionLog {

    private static final Logger LOG = LogManager.getLogger(FusionLog.class);
    
    //add model-selection score if case of ML action
    //keep previeous values to be fed to user feedback at a later time
    private String leftURI;
    private String rightURI;
    private final List<Action> actions = new ArrayList<>();
    private EnumDatasetAction defaultFusionAction;
    private EnumValidationAction validationAction;
    private String confidenceScore;

    @Override
    public String toString() {
        return "FusionInfo{" + "leftURI=" + leftURI + ", rightURI=" + rightURI 
                + ", actions=" + actions + ", defaultFusionAction=" + defaultFusionAction 
                + ", validationAction=" + validationAction + '}';
    }

    public String toJson() {
        try {
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
            
            String json = objectMapper.writeValueAsString(this);

            return json;

        } catch (IOException ex) {
            LOG.error(ex);
            throw new ApplicationException("Json serialization failed.");
        }
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

    public String getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}


