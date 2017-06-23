package gr.athena.innovation.fagi.core.specification;

import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import gr.athena.innovation.fagi.core.action.EnumMetadataActions;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class ConfigReader {
    
    private static final Logger logger = LogManager.getRootLogger();
    
    public FusionConfig loadConfiguration(String configPath){
        FusionConfig config = new FusionConfig();
        Properties properties = new Properties();
        
        try {
            properties.load(new FileInputStream(configPath));
        } catch (IOException ex) {
            logger.fatal("Could not read the configuration file! " + ex);
            throw new RuntimeException();
        }

        config.setPathA(properties.getProperty("pathA"));
        config.setPathB(properties.getProperty("pathB"));
        config.setPathLinks(properties.getProperty("pathLinks"));
        config.setPathOutput(properties.getProperty("pathOutput"));
        config.setInputRDFFormat(properties.getProperty("inputRDFFormat"));
        config.setOutputRDFFormat(properties.getProperty("outputRDFFormat"));
        
        EnumGeometricActions geoAction = EnumGeometricActions.fromString(properties.getProperty("geoAction"));
        EnumMetadataActions metaAction = EnumMetadataActions.fromString(properties.getProperty("metaAction"));
        EnumFuseIntoDataset finalDataset = EnumFuseIntoDataset.fromString(properties.getProperty("finalDataset"));
        
        config.setGeoAction(geoAction);
        config.setMetaAction(metaAction);
        config.setFinalDataset(finalDataset);
        
        int optionalDepth = 2;
        try {
            optionalDepth = Integer.parseInt(properties.getProperty("optionalDepth"));
        } catch (NumberFormatException ex) {
            logger.fatal("Optional depth in config file is not valid! The system will use the default value of 2." + ex);
        }
        
        config.setOptionalDepth(optionalDepth);
        
        return config;
    }
}
