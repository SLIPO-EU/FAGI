package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class containing methods for loading resources.
 *
 * @author nkarag
 */
public class ResourceFileLoader {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ResourceFileLoader.class);
    
    public static Map<String, String> getProperties(String file) throws IOException, ApplicationException {
        final int left = 0;
        final int right = 1;

        TreeMap<String, String> map = new TreeMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("#") && !line.isEmpty()) {
                    String[] pair = line.trim().split("=");
                    if (!(pair.length == 2)) {
                        throw new ApplicationException("Malformed content in file: "
                                + file + ". Missing \"=\" between key-value");
                    }
                    map.put(pair[left].trim(), pair[right].trim());
                }
            }
        }
        return Collections.unmodifiableMap(map);
    }

    public Map<String, String> getKnownAbbreviationsMap() throws IOException, ApplicationException {
        InputStream inputStream = getClass().getResourceAsStream("/matching/name_abbreviations.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader reader = new BufferedReader(inputStreamReader);
        final int left = 0;
        final int right = 1;
        TreeMap<String, String> map = new TreeMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] pair = line.trim().split("=");
                if (!(pair.length == 2)) {
                    throw new ApplicationException("Malformed content in abbreviations.txt. Missing \"=\" between key-value");
                }
                map.put(pair[left].trim(), pair[right].trim());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    public List<String> getRDFProperties() throws IOException, ApplicationException {
        InputStream inputStream = getClass().getResourceAsStream("/matching/rdf_properties.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader reader = new BufferedReader(inputStreamReader);

        List<String> properties = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            properties.add(line);
        }
        return Collections.unmodifiableList(properties);
    }

    public Set<String> getSpecialTerms() throws IOException, ApplicationException {
        InputStream inputStream = getClass().getResourceAsStream("/matching/special_terms.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader reader = new BufferedReader(inputStreamReader);

        Set<String> specialTerms = new HashSet<>();

        String line;
        while ((line = reader.readLine()) != null) {
            specialTerms.add(line);
        }
        
        return Collections.unmodifiableSet(specialTerms);
    }

    public Set<String> getSpecialTermsFromPath(String filepath) throws IOException, ApplicationException {
        
        BufferedReader reader = new BufferedReader(new FileReader(filepath));

        Set<String> specialTerms = new HashSet<>();

        String line;
        while ((line = reader.readLine()) != null) {
            specialTerms.add(line);
        }
        
        return Collections.unmodifiableSet(specialTerms);
    }
    
    public Map<String, String> getExitCodes() throws IOException, ApplicationException, ParseException {
        InputStream inputStream = getClass().getResourceAsStream("/matching/country_codes.json");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader reader = new BufferedReader(inputStreamReader);
        
        JSONParser parser = new JSONParser();
        
        Map<String, String> codes = new HashMap<>();
        
        Object obj = parser.parse(reader);

        JSONArray codeArray = (JSONArray) obj;

        Iterator<JSONObject> iterator = codeArray.iterator();
        while (iterator.hasNext()) {
            JSONObject jsonObject = iterator.next();
            String countryCode = (String) jsonObject.get("code");
            String callingCode = (String) jsonObject.get("callingCode");
            codes.put(countryCode, callingCode);
        }
        return Collections.unmodifiableMap(codes);
    }    
}
