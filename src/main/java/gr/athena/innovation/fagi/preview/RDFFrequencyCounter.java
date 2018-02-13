package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class RDFFrequencyCounter{
    
    private static final Logger logger = LogManager.getLogger(RDFFrequencyCounter.class);
    
    public Map<String, String> getCategoryMap(String categoriesPath){
        Map<String, String> categoryMap = new HashMap<>();
        
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(categoriesPath));
            String line;
            String splitBy = "\\s+";

            while ((line = bufferedReader.readLine()) != null) {
                
                String[] spl = line.split(splitBy);

                String[] literalTokens = Arrays.copyOfRange(spl, 2, spl.length);
                String literal = String.join(" ", literalTokens);

                if (spl[1].contentEquals(Namespace.CATEGORY_VALUE)) {
                    categoryMap.put(unWrapResource(spl[0]), literal);
                }
            }
        } catch (FileNotFoundException ex) {
            throw new ApplicationException(ex.getMessage());
        } catch (IOException ex) {
            throw new ApplicationException(ex.getMessage());
        }
        return categoryMap;
    }
    
    public Frequency exportCategoryFrequency(String category, Model model) {

        Frequency frequency = SparqlRepository.selectCategories(model, category);

        return frequency;
    }

    private String unWrapResource(String resource){

        return resource.substring(1, resource.length()-1);
    }
}
