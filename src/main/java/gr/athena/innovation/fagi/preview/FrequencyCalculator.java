package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates frequencies of words extracted from the literal of a given property. 
 * Expects RDF N-triples but treats the input as simple text.
 * 
 * @author nkarag
 */
public class FrequencyCalculator {
    
    private static final Logger logger = LogManager.getLogger(FrequencyCalculator.class);
    
    private Locale locale;
    private StringBuilder property;
    
    public void countFrequencies(String inputFilename, String outputFilename) throws IOException{

        BufferedWriter writer = null;

        try {

            BufferedReader br = new BufferedReader(new FileReader(inputFilename));
            writer = new BufferedWriter(new FileWriter(outputFilename, true));

            File file = new File(outputFilename);

            int l = 0;
            String line;
            String splitBy = "\\s+";

            while ((line = br.readLine()) != null) {

                String[] spl = line.split(splitBy);

                if(spl[1].contentEquals(property)){
                    String[] tokens = Arrays.copyOfRange(spl, 2, spl.length);
                    String literal = String.join(" ", tokens);
                }

                l++;
            }

            if (file.exists()) {
                //clear contents
                PrintWriter pw = new PrintWriter(outputFilename);
                pw.close();
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            
        } catch(IOException | RuntimeException ex){
            
            if(writer != null){
                writer.close();
            }
            logger.error(ex);
            throw new ApplicationException(ex.getMessage());
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public StringBuilder getProperty() {
        return property;
    }

    public void setProperty(StringBuilder property) {
        this.property = property;
    }
}
