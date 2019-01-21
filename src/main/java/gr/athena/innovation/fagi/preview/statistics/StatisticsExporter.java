package gr.athena.innovation.fagi.preview.statistics;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;

/**
 * Class for exporting statistics to file.
 * 
 * @author nkarag
 */
public class StatisticsExporter {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(StatisticsExporter.class);
    
    /**
     * Exports the statistics to the given file-path in JSON format.
     * @param statsJsonString the JSON String that contains the statistics.
     * @param filepath the output path.
     */
    public void exportStatistics(String statsJsonString, String filepath){

        File outputFile = new File(filepath);

        if (outputFile.exists()) {
            try (PrintWriter pw = new PrintWriter(outputFile)) {
                //clear contents
                pw.close();
                
                write(statsJsonString, outputFile);
                
            } catch (FileNotFoundException ex) {
                LOG.error(ex);
                throw new ApplicationException(ex.getMessage());
            } catch (IOException ex) {
                LOG.error(ex);
                throw new ApplicationException(ex.getMessage());
            }
        } else {

            try {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();

                write(statsJsonString, outputFile);

            } catch (IOException ex) {
                LOG.error(ex);
                throw new ApplicationException(ex.getMessage());
            }
        }
    }

    private void write(String statsJsonString, File outputFile) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            writer.append(statsJsonString);
            writer.newLine();
        }        
    }   
}