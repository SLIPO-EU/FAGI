package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.core.Fuser;
import gr.athena.innovation.fagi.core.rule.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.core.specification.FusionSpecification;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import gr.athena.innovation.fagi.core.specification.SpecificationParser;
import gr.athena.innovation.fagi.fusers.MethodRegistry;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.repository.AbstractRepository;
import gr.athena.innovation.fagi.repository.GenericRDFRepository;
import gr.athena.innovation.fagi.utils.InputValidator;
import gr.athena.innovation.fagi.xml.RuleProcessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Main class of the application. 
 * 
 * @author nkarag
 */
public class Fagi {

    private static final Logger logger = LogManager.getRootLogger();
    
    /**
     * 
     * Entry point of FAGI. Parses arguments from command line, reads the fusion specification, validates input  
     * and initiates the fusion process.
     * 
     * @param args the command line arguments
     * @throws java.text.ParseException
     * @throws com.vividsolutions.jts.io.ParseException
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public static void main(String[] args) throws ParseException, com.vividsolutions.jts.io.ParseException, 
            FileNotFoundException, IOException, ParserConfigurationException, SAXException {

        long startTimeInput = System.currentTimeMillis();
        String rulesXsd = getResourceFilePath("rules.xsd");
        String specXsd = getResourceFilePath("spec.xsd");

        String specXml = null;
        String rulesXml = null;

        String arg;
        String value;
        
        int i = 0;
        
        while (i < args.length){
            arg = args[i];
            if(arg.startsWith("-")){
                if(arg.equals("-help")){
                    logger.info(SpecificationConstants.HELP);
                    System.exit(0);	   
                }
            }
            value = args[i+1];
            if(arg.equals("-spec")){
                logger.info("spec path: " + value);
                specXml = value;
            } else if(arg.equals("-rules")){
                logger.info("rules path: " + value);
                rulesXml = value;
                break;
            }
            i++;
        }

        SpecificationParser specificationParser = new SpecificationParser();
        FusionSpecification fusionSpecification = specificationParser.parse(specXml);

        MethodRegistry methodRegistry = new MethodRegistry();
        methodRegistry.init();

        HashSet<String> methodSet = methodRegistry.getMethodRegistryList();

        InputValidator inputValidator = new InputValidator(rulesXml, rulesXsd, specXml, specXsd, methodSet);

        if(!inputValidator.isValidInput()){
            logger.info(SpecificationConstants.HELP);
            System.exit(0);
        }

        RuleProcessor ruleProcessor = new RuleProcessor();
        RuleCatalog ruleCatalog = ruleProcessor.parseRules(rulesXml);
        ruleCatalog.setMethodRegistry(methodRegistry);
        
        long stopTimeInput = System.currentTimeMillis();
        
        long startTimeReadFiles = System.currentTimeMillis();
        
        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(fusionSpecification.getPathA());
        genericRDFRepository.parseRight(fusionSpecification.getPathB());
        genericRDFRepository.parseLinks(fusionSpecification.getPathLinks());
        
        long stopTimeReadFiles = System.currentTimeMillis();
        
        ArrayList<InterlinkedPair> interlinkedEntitiesList = new ArrayList<>();
        Fuser fuser = new Fuser(interlinkedEntitiesList);

        long startTimeFusion = System.currentTimeMillis();
        
        fuser.fuseAllWithRules(fusionSpecification, ruleCatalog, methodRegistry.getFunctionMap());
        
        long stopTimeFusion = System.currentTimeMillis();
        long startTimeWrite = System.currentTimeMillis();
        
        fuser.combineFusedAndWrite(fusionSpecification, interlinkedEntitiesList);
        
        long stopTimeWrite = System.currentTimeMillis();
        
        logger.info(fusionSpecification.toString());
        
        logger.info("####### ###### ##### #### ### ## # Results # ## ### #### ##### ###### #######");
        logger.info("Interlinked: " + interlinkedEntitiesList.size() + ", Fused: " + fuser.getFusedPairsCount() 
                + ", Linked Entities not found: " + fuser.getLinkedEntitiesNotFoundInDataset());        
        logger.info("Analyzing/validating input and configuration completed in " + (stopTimeInput-startTimeInput) + "ms.");
        logger.info("Datasets loaded in " + (stopTimeReadFiles-startTimeReadFiles) + "ms.");
        logger.info("Fusion completed in " + (stopTimeFusion - startTimeFusion) + "ms.");
        logger.info("Combining files and write to disk completed in " + (stopTimeWrite - startTimeWrite) + "ms.");
        logger.info("Total time {}ms.", stopTimeWrite - startTimeInput);
        logger.info("####### ###### ##### #### ### ## # # # # # # ## ### #### ##### ###### #######");  
    }
    
    private static String getResourceFilePath(String filename) throws FileNotFoundException, IOException{
        InputStream initialStream = new FileInputStream(new File("src/main/resources/" + filename));
        
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        File targetFile = new File("src/main/resources/targetFile.tmp");
        targetFile.deleteOnExit();
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        
        return targetFile.getAbsolutePath();
    }
}
