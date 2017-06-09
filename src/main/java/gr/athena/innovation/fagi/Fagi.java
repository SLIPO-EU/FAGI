package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.configuration.ConfigReader;
import gr.athena.innovation.fagi.core.Fuser;
import gr.athena.innovation.fagi.configuration.FusionConfig;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.repository.AbstractRepository;
import gr.athena.innovation.fagi.repository.GenericRDFRepository;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class Fagi {

    private static final Logger logger = LogManager.getRootLogger();
    
    /**
     * @param args the command line arguments
     * @throws java.text.ParseException
     * @throws com.vividsolutions.jts.io.ParseException
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws ParseException, com.vividsolutions.jts.io.ParseException, FileNotFoundException, IOException {

        String configPath = "src/main/resources/config.properties";

        String arg;
        String value;
        int i = 0;
        while (i < args.length){
            arg = args[i];
            if(arg.startsWith("-")){
                if(arg.equals("-help")){
                    System.out.println("Usage:\n java -jar fagi-1.0-SNAPSHOT.jar -config <configFile> ");
                    System.out.println("-config requires a file path");
                    System.exit(0);	   
                }
            }    
            value = args[i+1];
            if(arg.equals("-config")){
             System.out.println("config file set, path is: " + value);
             configPath = value;
             break;
            } else {
                 System.out.println("Usage:\n java -jar fagi-1.0-SNAPSHOT.jar -config <configFile> ");
                 System.out.println("-config requires a file path");
                 System.exit(0);
            }
            i++;
        }

        ConfigReader configReader = new ConfigReader();
        FusionConfig config = configReader.loadConfiguration(configPath);

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(config.getPathA());
        genericRDFRepository.parseRight(config.getPathB());
        genericRDFRepository.parseLinks(config.getPathLinks());

        ArrayList<InterlinkedPair> interlinkedEntitiesList = new ArrayList<>();
        Fuser fuser = new Fuser(interlinkedEntitiesList);

        fuser.fuseAll(config);

        fuser.combineFusedAndWrite(config, interlinkedEntitiesList);

        logger.info(config.toString());
        logger.trace("interlinkedEntitiesList " + interlinkedEntitiesList.size());
        logger.info("Interlinked not found in datasets: " + fuser.getLinkedEntitiesNotFoundInDataset());
        logger.info("Number of fused pairs: " + fuser.getFusedPairsCount());        
    }
}
