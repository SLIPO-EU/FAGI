package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;

/**
 * 
 * @author nkarag
 */
public class StatisticsExporter {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(StatisticsExporter.class);
    
    public void exportStatistics(StatisticsContainer container, String outputDir){

        String statsPath = outputDir + "stats.txt";
            
        File outputFile = new File(statsPath);

        if (outputFile.exists()) {
            try (PrintWriter pw = new PrintWriter(outputFile)) {
                //clear contents
                pw.close();
                
                write(container, outputFile);
                
            } catch (FileNotFoundException ex) {
                logger.error(ex);
                throw new ApplicationException(ex.getMessage());
            } catch (IOException ex) {
                logger.error(ex);
                throw new ApplicationException(ex.getMessage());
            }
        } else {
            
            try {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
                
                write(container, outputFile);
                
            } catch (IOException ex) {
                logger.error(ex);
                throw new ApplicationException(ex.getMessage());
            }
        }        
    }
    
    private void write(StatisticsContainer container, File outputFile) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            //writer.append("#Statistics");
            //writer.newLine();
            writer.append(container.toJson());
            writer.newLine();
        }        
    }
}