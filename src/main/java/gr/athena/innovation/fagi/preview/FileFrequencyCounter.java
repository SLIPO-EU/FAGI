package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.normalizer.SimpleLiteralNormalizer;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates frequencies of words extracted from the literal of a given property. Expects RDF N-triples but treats the
 * input as plain text.
 *
 * @author nkarag
 */
public class FileFrequencyCounter implements FrequencyCounter {

    private static final Logger LOG = LogManager.getLogger(FileFrequencyCounter.class);

    private Locale locale;
    private List<String> properties;
    private final Configuration configuration;
    private final int frequentTopK;

    public FileFrequencyCounter(int frequentTopK) {
        this.configuration = Configuration.getInstance();
        this.frequentTopK = frequentTopK;
    }

    @Override
    public void export(String inputFilename, EnumDataset dataset) {

        int index = 0;
        for (String property : properties) {

            StringBuilder prop = new StringBuilder(property);

            if (!property.startsWith("<")) {
                prop.insert(0, "<");
                prop.insert(prop.length(), ">");
            }

            String outputFilename = getFilename(property, index, inputFilename, dataset);

            if(property.equals(Namespace.NAME_VALUE_NO_BRACKETS)){
                //nameValue property: Set the output result path of this property to be used in learning process.
                switch(dataset){
                    case LEFT:
                        Configuration.getInstance().setPropertyFrequencyA(outputFilename);
                        break;
                    case RIGHT:
                        Configuration.getInstance().setPropertyFrequencyB(outputFilename);
                        break;
                }
            }

            File outputFile = new File(outputFilename);

            LOG.info("frequency file:" + outputFilename);
            PrintWriter pw = null;
            try {

                if (outputFile.exists()) {
                    //clear contents
                    pw = new PrintWriter(outputFile);
                    pw.close();

                } else {
                    outputFile.getParentFile().mkdirs();
                    outputFile.createNewFile();
                }

                writePropertyFrequency(prop, inputFilename, outputFilename);

            } catch (FileNotFoundException ex) {
                throw new ApplicationException(ex.getMessage());
            } catch (IOException ex) {
                throw new ApplicationException(ex.getMessage());
            } finally {
                if(pw != null){
                    pw.close();
                }
            }
        }
    }

    private String getFilename(String property, int index, String inputFilename, EnumDataset dataset) throws ApplicationException {
        //Create a user friendly filename based on each property and input dataset.
        String filenamePrefix;
        if (property.lastIndexOf("#") != -1) {
            filenamePrefix = property.substring(property.lastIndexOf("#") + 1);
        } else if (property.lastIndexOf("/") != -1) {
            filenamePrefix = property.substring(property.lastIndexOf("/") + 1);
        } else {
            filenamePrefix = "_" + index;
        }
        String filenameTemp = inputFilename.substring(inputFilename.lastIndexOf("/") + 1);
        String filename;
        if(filenameTemp.indexOf('.') > -1){
            filename = "_" + filenameTemp.substring(0, filenameTemp.lastIndexOf("."));
        } else {
            filename = "_" + filenameTemp;
        }

        String filenameSuffix;

        switch(dataset){
            case LEFT:
                filenameSuffix = SpecificationConstants.Config.FREQ_SUFFIX_A;
                break;
            case RIGHT:
                filenameSuffix = SpecificationConstants.Config.FREQ_SUFFIX_B;
                break;
            default:
                throw new ApplicationException("Wrong parameter for EnumDataset in Frequency export. "
                        + "Only LEFT or RIGHT allowed.");
                
        }
        String outputFilename = configuration.getOutputDir() + "frequencies/"
                + filenamePrefix + filename + filenameSuffix;

        return outputFilename;
    }

    private void writePropertyFrequency(StringBuilder property, String inputFilename, String outputFilename) throws IOException {
        BufferedWriter writer = null;
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilename));
            writer = new BufferedWriter(new FileWriter(outputFilename, true));
            String line;
            String splitBy = "\\s+";

            Frequency frequency = new Frequency();

            while ((line = bufferedReader.readLine()) != null) {
                if(StringUtils.isBlank(line)){
                    continue;
                }

                String[] spl = line.split(splitBy);

                if (spl[1].contentEquals(property)) {
                    String[] tokens = Arrays.copyOfRange(spl, 2, spl.length);

                    String literal = String.join(" ", tokens);

                    SimpleLiteralNormalizer normalizer = new SimpleLiteralNormalizer();
                    String bNorm = normalizer.normalize(literal, locale);
                    String[] toks = tokenize(bNorm);
                    
                    frequency.insert(toks);
                }
            }

            Map<String, Integer> frequencyMap = frequency.getTopKFrequency(frequentTopK);

            //title with the name of the property
            writer.append("# " + property);
            writer.newLine();
            for (String key : frequencyMap.keySet()) {
                String value = frequencyMap.get(key).toString();
                String pair = key + "=" + value;
                writer.append(pair);
                writer.newLine();
            }

            writer.close();

        } catch (IOException | RuntimeException ex) {
            if (writer != null) {
                writer.close();
            }
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

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
