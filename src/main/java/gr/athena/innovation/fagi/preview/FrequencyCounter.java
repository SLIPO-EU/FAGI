package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.normalizer.SimpleLiteralNormalizer;
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
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates frequencies of words extracted from the literal of a given property. 
 * Expects RDF N-triples but treats the input as simple text.
 * 
 * @author nkarag
 */
public class FrequencyCounter {
    
    private static final Logger logger = LogManager.getLogger(FrequencyCounter.class);
    
    private Locale locale;
    private StringBuilder property;
    
    public void extractFrequencyToFile(String inputFilename, String outputFilename) throws IOException{

        BufferedWriter writer = null;

        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilename));
            writer = new BufferedWriter(new FileWriter(outputFilename, true));

            File outputFile = new File(outputFilename);

            String line;
            String splitBy = "\\s+";

            Frequency freq = new Frequency();

            while ((line = bufferedReader.readLine()) != null) {

                String[] spl = line.split(splitBy);

                if(spl[1].contentEquals(property)){
                    String[] tokens = Arrays.copyOfRange(spl, 2, spl.length);

                    String literal = String.join(" ", tokens);
                    SimpleLiteralNormalizer normalizer = new SimpleLiteralNormalizer();
                    String bNorm = normalizer.normalize(literal, locale);
                    String[] toks = tokenize(bNorm);
                    freq.insertWords(toks);
                }
            }

            if (outputFile.exists()) {
                //clear contents
                PrintWriter pw = new PrintWriter(outputFilename);
                pw.close();
            } else {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            }
            
            Map<String, Integer> frequency = freq.getTopKFrequency(100);

            for (String key : frequency.keySet()){
                String value = frequency.get(key).toString();
                String pair = key + "=" + value;
                writer.append(pair);
                writer.newLine();
            }
            
            writer.close();
            
        } catch(IOException | RuntimeException ex){

            if(writer != null){
                writer.close();
            }
            logger.error(ex);
            throw new ApplicationException(ex.getMessage());
        }
    }

    //tokenize on whitespaces
    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");

        String[] split = text.toString().split("\\s+");
        return split;
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
