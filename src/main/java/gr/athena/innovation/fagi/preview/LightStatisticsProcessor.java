package gr.athena.innovation.fagi.preview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import org.apache.logging.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Processor of the light version of statistics.
 * 
 * @author nkarag
 */
public class LightStatisticsProcessor {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(LightStatisticsProcessor.class);

    private static final String LABEL = "label";
    private static final String VALUE = "value";

    private final JSONObject statistics = new JSONObject();
    private final LightContainer lightContainer;
    private final DecimalFormat df2 = new DecimalFormat("#.##");

    /**
     * Constructor
     * 
     * @param lightContainer
     */
    public LightStatisticsProcessor(LightContainer lightContainer){
        this.lightContainer = lightContainer;
    }

    /**
     * Computes the light version of statistics.
     */
    public void compute(){

        try {

            LOG.info("Computing light statistics");

            //Initial POIS
            statistics.put("initialPOIs", countInitialPOIs());

            //Total POIs in fused
            statistics.put("poisInFinalDataset", countPoisInFinalDataset());

            //Fused POIs
            statistics.put("fusedPOIs", countFusedPois());

            //Initial Links
            statistics.put("initialLinks", countInitialLinks());

            //Unique Links
            statistics.put("uniqueLinks", countUniqueLinks());

            //Fused links, rejected links
            statistics.put("uniqueRejectedLinks", countUniqueRejectedLinks());
            
            //Average gain
            statistics.put("averageGain", setAverageGain());
            
            //Average Confidence
            statistics.put("averageConfidence", setConfidence());

            //Max Gain
            statistics.put("maxGain", setMaxGain());
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

            String originalJson = objectMapper.writeValueAsString(statistics);

            JsonNode tree = objectMapper.readTree(originalJson);
            String formattedJson = objectMapper.writeValueAsString(tree);
            LOG.info("\n" + formattedJson);

        } catch (JsonProcessingException ex) {
            LOG.error(ex);
            throw new ApplicationException(ex.getMessage());
        } catch (IOException ex) {
            LOG.error(ex);
            throw new ApplicationException(ex.getMessage());
        }
    }

    /**
     * Updates the JSON object with the execution times.
     * 
     * @param time1 dataset load time.
     * @param time2 fusion execution time.
     * @param time3 statistic computation time.
     */
    public void updateExecutionTimes(String time1, String time2, String time3){

        JSONObject json = new JSONObject();
        json.put(LABEL, "Execution time (ms)");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item1 = new JSONObject();
        item1.put(LABEL, "Dataset load");
        item1.put(VALUE, time1);

        JSONObject item2 = new JSONObject();
        item2.put(LABEL, "Fusion");
        item2.put(VALUE, time2);
        
        JSONObject item3 = new JSONObject();
        item3.put(LABEL, "Statistics");
        item3.put(VALUE, time3);

        jsonArray.add(item1);
        jsonArray.add(item2);
        jsonArray.add(item3);
        json.put("items", jsonArray);
        
        statistics.put("executionTime", json);
    }
    
    /**
     * Return the JSON statistics as a String.
     * 
     * @return the statistics.
     */
    public String getStats(){
        return statistics.toJSONString();
    }

    private JSONObject countInitialPOIs() {
        
        Integer poisA = countPOIs(lightContainer.getPathA());
        Integer poisB = countPOIs(lightContainer.getPathB());
        
        JSONObject initialPOIsStat = new JSONObject();
        initialPOIsStat.put(LABEL, "Initial POIs");
        JSONArray  initialPOIs = new JSONArray();

        JSONObject item1 = new JSONObject();
        item1.put(LABEL, "POIs in A");
        item1.put(VALUE, poisA.toString());

        JSONObject item2 = new JSONObject();
        item2.put(LABEL, "POIs in B");
        item2.put(VALUE, poisB.toString());

        initialPOIs.add(item1);
        initialPOIs.add(item2);
        initialPOIsStat.put("items", initialPOIs);

        return initialPOIsStat;
    }
    
    private JSONObject countPoisInFinalDataset() {
        
        Integer pois = countPOIs(lightContainer.getFusedPath());
        
        JSONObject json = new JSONObject();
        json.put(LABEL, "POIs in final dataset");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item = new JSONObject();
        item.put(LABEL, "POIs in final dataset");
        item.put(VALUE, pois.toString());

        jsonArray.add(item);
        json.put("items", jsonArray);
        return json;
    }

    private JSONObject countFusedPois() {
        JSONObject json = new JSONObject();
        json.put(LABEL, "Fused POIs");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item = new JSONObject();
        item.put(LABEL, "Fused POIs");
        item.put(VALUE, lightContainer.getFusedPOIs());

        jsonArray.add(item);
        json.put("items", jsonArray);
        return json;
    }

    private JSONObject countInitialLinks() {
        JSONObject json = new JSONObject();
        json.put(LABEL, "Initial links");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item = new JSONObject();
        item.put(LABEL, "Initial links");
        item.put(VALUE, lightContainer.getInitialLinks());

        jsonArray.add(item);
        json.put("items", jsonArray);
        return json;
    }

    private JSONObject countUniqueLinks() {
        JSONObject json = new JSONObject();
        json.put(LABEL, "Unique links");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item = new JSONObject();
        item.put(LABEL, "Unique links");
        item.put(VALUE, lightContainer.getUniqueLinks());

        jsonArray.add(item);
        json.put("items", jsonArray);
        return json;
    }

    private JSONObject countUniqueRejectedLinks() {
        JSONObject json = new JSONObject();
        json.put(LABEL, "Unique vs rejected links");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item1 = new JSONObject();
        item1.put(LABEL, "Unique links");
        item1.put(VALUE, lightContainer.getUniqueLinks());

        JSONObject item2 = new JSONObject();
        item2.put(LABEL, "Rejected links");
        item2.put(VALUE, lightContainer.getRejectedLinks());

        jsonArray.add(item1);
        jsonArray.add(item2);
        json.put("items", jsonArray);

        return json;
    }

    private JSONObject setConfidence() {
        JSONObject json = new JSONObject();
        json.put(LABEL, "Average Confidence");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item = new JSONObject();
        item.put(LABEL, "Average Confidence");
        item.put(VALUE, df2.format(lightContainer.getAverageConfidence()));

        jsonArray.add(item);
        json.put("items", jsonArray);
        return json;
    }

    private JSONObject setAverageGain() {
        JSONObject json = new JSONObject();
        json.put(LABEL, "Average Gain");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item = new JSONObject();
        item.put(LABEL, "Average Gain");
        item.put(VALUE, df2.format(lightContainer.getAverageGain()));

        jsonArray.add(item);
        json.put("items", jsonArray);
        return json;
    }

    private JSONObject setMaxGain() {
        JSONObject json = new JSONObject();
        json.put(LABEL, "Max Gain");
        JSONArray  jsonArray = new JSONArray();

        JSONObject item = new JSONObject();
        item.put(LABEL, "Max Gain");
        item.put(VALUE, df2.format(lightContainer.getMaxGain()));

        jsonArray.add(item);
        json.put("items", jsonArray);
        return json;
    }

    private Integer countPOIs(String path) {
        Integer count = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"))){
            for(String line; (line = br.readLine()) !=null;) {
                if(line.contains(Namespace.SOURCE)){
                    count++;
                }
            }
        } catch (ApplicationException | IOException ex) {
            LOG.fatal(ex);
            throw new ApplicationException(ex.getMessage());
        }

        return count;
    }
}
