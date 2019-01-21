package gr.athena.innovation.fagi.preview.statistics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 * Container of the statistics.
 * 
 * @author nkarag
 */
public class StatisticsContainer {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(StatisticsContainer.class);
    private boolean complete = false;

    @JsonIgnore
    private transient boolean valid;

    private Map<String, StatisticResultPair> map;

    /**
     * Return the JSON String with the values of this container.
     * @return the JSON String.
     */
    public String toJson() {
        
        String formattedJson = null;
        
        try {
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            
            String originalJson = objectMapper.writeValueAsString(this);

            JsonNode tree = objectMapper.readTree(originalJson);
            formattedJson = objectMapper.writeValueAsString(tree);

        } catch (IOException ex) {
            LOG.error(ex);
            throw new ApplicationException("Json serialization failed.");
        }
        
        return formattedJson;
    }
    
    /**
     * Return the JSON map of this container.
     * 
     * @return the JSON map.
     */
    public String toJsonMap() {
        
        if(map == null){
            throw new ApplicationException("Statistics container is not initialized properly.");
        }

        String formattedJson;

        if(valid && complete){
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

                String originalJson = objectMapper.writeValueAsString(map);

                JsonNode jsonNode = objectMapper.readTree(originalJson);
                formattedJson = objectMapper.writeValueAsString(jsonNode);

            } catch (IOException ex) {
                LOG.error(ex);
                throw new ApplicationException("Json serialization failed.");
            }
        } else {
            //TODO: some stats can be still be returned even when container is not complete.
            formattedJson = "{}";
        }

        return formattedJson;
    }

    /**
     * 
     * @return true if the container is valid.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     *
     * @param valid the validity of the container.
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     *
     * @return the map with stat-names as keys and corresponding results as values.
     */
    public Map<String, StatisticResultPair> getMap() {
        return map;
    }

    /**
     *
     * @param map the statistics map.
     */
    public void setMap(Map<String, StatisticResultPair> map) {
        this.map = map;
    }

    /**
     *
     * @return true if the container is complete.
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Sets this container as complete or not.
     * 
     * @param complete the complete value.
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
