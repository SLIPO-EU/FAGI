package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;

/**
 * 
 * @author nkarag
 */
public class StatisticsExporter {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(StatisticsExporter.class);
    
    public void exportStatistics(StatisticsContainer statisticsContainer, String outputPath){
        
        File outputFile = new File(outputPath);

        if (outputFile.exists()) {
            try (PrintWriter pw = new PrintWriter(outputFile)) {
                //clear contents
                pw.close();
            } catch (FileNotFoundException ex) {
                logger.error(ex);
                throw new ApplicationException(ex.getMessage());
            }
        } else {
            try {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            } catch (IOException ex) {
                logger.error(ex);
                throw new ApplicationException(ex.getMessage());
            }
        }        
    }
}