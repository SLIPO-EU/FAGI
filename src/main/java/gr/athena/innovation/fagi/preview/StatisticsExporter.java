package gr.athena.innovation.fagi.preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nkarag
 */
public class StatisticsExporter {
    
    public void exportStatistics(StatisticsContainer statisticsContainer, String filepath){
        
            File outputFile = new File(filepath);

            if (outputFile.exists()) {
                PrintWriter pw = null;
                try {
                    //clear contents
                    pw = new PrintWriter(outputFile);
                    pw.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(StatisticsExporter.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    pw.close();
                }
            } else {
                try {
                    outputFile.getParentFile().mkdirs();
                    outputFile.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(StatisticsExporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }        
    }
}