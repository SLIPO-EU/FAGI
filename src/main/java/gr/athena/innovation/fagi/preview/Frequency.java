package gr.athena.innovation.fagi.preview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;

/**
 * Class representing term frequency.
 * 
 * @author nkarag
 */
public class Frequency {

    private Map<String, Integer> map = new HashMap<>();

    public Map<String, Integer> getMap() {
        return map;
    }

    public Map<String, Integer> getTopKFrequency(int topK) {

        List<Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        
        Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> left, Entry<String, Integer> right) {
                return right.getValue().compareTo(left.getValue());
            }
        });
        
        Map<String, Integer> sortedMap = new LinkedHashMap<>(entries.size());

        if(topK < 1){
            for (Entry<String, Integer> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }             
        } else {
            int count = 0;
            for (Entry<String, Integer> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
                if(count == topK){
                    break;
                }
                count++;
            }            
        }
        return sortedMap;
    }

    /**
     * Adds words to frequency map.
     *
     * @param tokens the string array containing the tokens
     */
    public void insert(String[] tokens) {
        for (String token : tokens) {
            if(!StringUtils.isBlank(token)){
                map.merge(token, 1, Integer::sum);
            }
        }
    }
}
