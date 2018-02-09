package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.specification.Namespace;
import com.vividsolutions.jts.index.strtree.STRtree;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class DataGridProcessor {

    private static final Logger logger = LogManager.getRootLogger();
    private final String filepath;

    public DataGridProcessor(String filepath){
        this.filepath = filepath;
    }

    public void splitDataset(){
        //todo
        //use jts spatial index:
        //split files at any line and for each file build on the same spatial index  
        if(canLoadToMemory(filepath)){
            STRtree tree = new STRtree();
            
        }
    }

    private int estimateGridCount() throws FileNotFoundException, IOException{
        
        FileInputStream inputStream = null;
        Scanner sc = null;

        try {
            inputStream = new FileInputStream(filepath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line.contains(Namespace.WKT)){
                    logger.trace("Found geometry");
                }
                // System.out.println(line);
            }
            //Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        return 1;
    }

    private boolean canLoadToMemory(String filepath) {
        
        File file = new File(filepath);
        
        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;
        
        return fileSizeInMB<500;
    }
}