package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.core.Fuser;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.specification.SpecificationParser;
import gr.athena.innovation.fagi.core.functions.FunctionRegistry;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.repository.AbstractRepository;
import gr.athena.innovation.fagi.repository.GenericRDFRepository;
import gr.athena.innovation.fagi.utils.InputValidator;
import gr.athena.innovation.fagi.rule.RuleProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
     * Entry point of FAGI. Parses arguments from command line, reads the specification and rules, validates input  
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

        String rulesXsd = getResourceFilePath(SpecificationConstants.RULES_XSD);
        String specXsd = getResourceFilePath(SpecificationConstants.SPEC_XSD);

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
                specXml = value;
            } else if(arg.equals("-rules")){
                rulesXml = value;
                break;
            }
            i++;
        }

        //Validate input
        FunctionRegistry functionRegistry = new FunctionRegistry();
        functionRegistry.init();
        Set<String> functionSet = functionRegistry.getFunctionMap().keySet();

        InputValidator validator = new InputValidator(rulesXml, rulesXsd, specXml, specXsd, functionSet);

        logger.info("Validating input..");

        if(!validator.isValidInput()){
            logger.info(SpecificationConstants.HELP);
            System.exit(0);
        }

        logger.info("XML files seem valid.");

        //Parse specification and rules
        SpecificationParser specificationParser = new SpecificationParser();
        FusionSpecification fusionSpecification = specificationParser.parse(specXml);

        RuleProcessor ruleProcessor = new RuleProcessor();
        RuleCatalog ruleCatalog = ruleProcessor.parseRules(rulesXml);
        ruleCatalog.setFunctionRegistry(functionRegistry);

        long stopTimeInput = System.currentTimeMillis();

        //Load datasets
        long startTimeReadFiles = System.currentTimeMillis();

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(fusionSpecification.getPathA());
        genericRDFRepository.parseRight(fusionSpecification.getPathB());
        genericRDFRepository.parseLinks(fusionSpecification.getPathLinks());

        long stopTimeReadFiles = System.currentTimeMillis();

        //Start fusion process
        long startTimeFusion = System.currentTimeMillis();
        List<InterlinkedPair> interlinkedEntitiesList = new ArrayList<>();

        Fuser fuser = new Fuser(interlinkedEntitiesList);
        fuser.fuseAllWithRules(fusionSpecification, ruleCatalog, functionRegistry.getFunctionMap());

        long stopTimeFusion = System.currentTimeMillis();

        //Combine result datasets and write to file
        long startTimeWrite = System.currentTimeMillis();

        fuser.combineFusedAndWrite(fusionSpecification, interlinkedEntitiesList, ruleCatalog.getDefaultDatasetAction());

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

        File targetFile = new File("src/main/resources/"+filename+".tmp");
        targetFile.deleteOnExit();
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        
        String path = targetFile.getAbsolutePath();
        logger.trace("path from resources for file: " + filename + " is " + path);
        return path;
    }
}
