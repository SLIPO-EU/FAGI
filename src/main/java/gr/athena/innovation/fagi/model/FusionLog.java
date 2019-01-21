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
 * Class that keeps track of URIs, validation, confidence score and fusion actions applied on a pair of POIs.
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

    /**
     * Return the FusionLog object as a JSON String.
     * 
     * @return the Json String.
     */
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
    
    /**
     * Return the URI of the left POI.
     * 
     * @return the left URI.
     */
    public String getLeftURI() {
        return leftURI;
    }

    /**
     * Set the URI of the left POI.
     * 
     * @param leftURI the left URI.
     */
    public void setLeftURI(String leftURI) {
        this.leftURI = leftURI;
    }

    /**
     * Return the URI of the right POI.
     * 
     * @return the right URI.
     */
    public String getRightURI() {
        return rightURI;
    }

    /**
     * Set the URI of the right POI.
     * 
     * @param rightURI the right URI.
     */
    public void setRightURI(String rightURI) {
        this.rightURI = rightURI;
    }

    /**
     * Return the list of the actions applied on the pair.
     * 
     * @return the list of the actions.
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Add the given action to the current log.
     * 
     * @param action the action.
     */
    public void addAction(Action action) {
        actions.add(action);
    }

    /**
     * Return the default fusion action for this pair. 
     * 
     * The default action that applied to the current pair is the "EnumDatasetAction", which applied to all pairs as a default fusion action.
     * 
     * @return the default fusion action.
     */
    public EnumDatasetAction getDefaultFusionAction() {
        return defaultFusionAction;
    }

    /**
     * Set the default fusion action.
     * 
     * @param defaultFusionAction the default fusion action.
     */
    public void setDefaultFusionAction(EnumDatasetAction defaultFusionAction) {
        this.defaultFusionAction = defaultFusionAction;
    }

    /**
     * Return the validation action.
     * 
     * @return the validation action.
     */
    public EnumValidationAction getValidationAction() {
        return validationAction;
    }

    /**
     * Set the validation action.
     * 
     * @param validationAction the validation action.
     */
    public void setValidationAction(EnumValidationAction validationAction) {
        this.validationAction = validationAction;
    }

    /**
     * Return the confidence score of the fusion for this pair. This is a value between 0 and 1 as a string.
     * 
     * @return the confidence score.
     */
    public String getConfidenceScore() {
        return confidenceScore;
    }

    /**
     * Set the confidence score of the current pair.
     * 
     * @param confidenceScore the confidence score.
     */
    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}


