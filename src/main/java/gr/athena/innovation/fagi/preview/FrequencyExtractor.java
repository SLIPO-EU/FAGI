package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.utils.Namespace;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class FrequencyExtractor {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FrequencyExtractor.class);
    
    public void extract(int frequentTopK, List<String> rdfProperties, String categoryMappingsNTPath, 
            FusionSpecification fusionSpecification, Locale locale){

            //Frequent terms
            FileFrequencyCounter termFrequency = new FileFrequencyCounter(fusionSpecification, frequentTopK);
            termFrequency.setLocale(locale);

            //rdf properties from file
            termFrequency.setProperties(rdfProperties);

            //n-triples input
            termFrequency.export(fusionSpecification.getPathA());

            
            //Category frequencies
            RDFFrequencyCounter categoryCounter = new RDFFrequencyCounter();

            Map<String, String> categoryMap = categoryCounter.getCategoryMap(categoryMappingsNTPath);

            Frequency categoryFrequencies = categoryCounter.exportCategoryFrequency(Namespace.CATEGORY);

            File propertyFile = new File(fusionSpecification.getPathOutput());
            File parentDir = propertyFile.getParentFile();

            String outputFilename = parentDir.getPath() + "/frequencies/category.freq.txt";
            File outputFile = new File(outputFilename);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
                writer.append("# category frequencies");    
                writer.newLine();
                for (Map.Entry<String, Integer> f : categoryFrequencies.getTopKFrequency(frequentTopK).entrySet()){
                    String catLiteral = categoryMap.get(f.getKey());
                    
                    String pair = catLiteral + "=" + f.getValue();
                    writer.append(pair);
                    writer.newLine();
                    
                }
            } catch (IOException ex) {
                logger.error(ex);
                throw new ApplicationException(ex.getMessage());
        }        
    }
}
