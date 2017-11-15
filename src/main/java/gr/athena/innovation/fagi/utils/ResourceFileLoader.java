package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class containing methods for loading resources.
 * 
 * @author nkarag
 */
public class ResourceFileLoader {
    
    public static TreeMap<String, String> getProperties(String file) throws IOException, ApplicationException{
        final int left = 0;
        final int right = 1;

        TreeMap<String, String> map = new TreeMap<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(file)))) {
            String line;
            while ((line = bfr.readLine()) != null) {
                if (!line.startsWith("#") && !line.isEmpty()) {
                    String[] pair = line.trim().split("=");
                    if(!(pair.length == 2)){
                        throw new ApplicationException("Malformed content in file: "
                                + file + ". Missing \"=\" between key-value");
                    }
                    map.put(pair[left].trim(), pair[right].trim());
                }
            }
        }
        return map;
    }
    
    public Map<String, String> getKnownAbbreviationsMap() throws IOException, ApplicationException{
        InputStream inputStream = getClass().getResourceAsStream("/abbreviations.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        
        BufferedReader reader = new BufferedReader(inputStreamReader);
        final int left = 0;
        final int right = 1;        
        TreeMap<String, String> map = new TreeMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] pair = line.trim().split("=");
                if(!(pair.length == 2)){
                    throw new ApplicationException("Malformed content in abbreviations.txt. Missing \"=\" between key-value");
                }
                map.put(pair[left].trim(), pair[right].trim());
            }
        }
        return map;
    }
}
