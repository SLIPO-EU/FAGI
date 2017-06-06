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

        String configPath = "/home/nkarag/fagi/src/main/resources/config.properties";
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
        logger.info("not found entities: " + fuser.getLinkedEntitiesNotFoundInDataset());
        logger.info("Number of fused pairs: " + fuser.getFusedPairsCount());        
    }
}
