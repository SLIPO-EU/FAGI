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

//        XmlProcessor xmlProcessor = new XmlProcessor();
//        RuleCatalog ruleCatalog = xmlProcessor.parseRules(rulesXml);

        RuleProcessor ruleProcessor = new RuleProcessor();
        RuleCatalog ruleCatalog = ruleProcessor.parseRules(rulesXml);
        ruleCatalog.setMethodRegistry(methodRegistry);

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(fusionSpecification.getPathA());
        genericRDFRepository.parseRight(fusionSpecification.getPathB());
        genericRDFRepository.parseLinks(fusionSpecification.getPathLinks());

        List<Rule> rules = ruleCatalog.getRules();
        logger.info("\nRules size: " + rules.size());

        for (Rule rule : rules){
            logger.fatal(" ----------------------");
            logger.info(rule.toString());
            //String actionRuleString = rule.getActionRuleSet().getActionRuleList().get(0).toString();
            //logger.debug(actionRuleString);
        }

        ArrayList<InterlinkedPair> interlinkedEntitiesList = new ArrayList<>();
        Fuser fuser = new Fuser(interlinkedEntitiesList);

        //fuser.fuseAll(config);
        logger.trace("Start rule Fusion");
        
        fuser.fuseAllWithRules(fusionSpecification, ruleCatalog, methodRegistry.getFunctionMap());
        logger.trace("Rule Fusion complete.");
        
        fuser.combineFusedAndWrite(fusionSpecification, interlinkedEntitiesList);

        logger.info(fusionSpecification.toString());
        logger.trace("interlinkedEntitiesList " + interlinkedEntitiesList.size());
        logger.info("Interlinked not found in datasets: " + fuser.getLinkedEntitiesNotFoundInDataset());
        logger.info("Number of fused pairs: " + fuser.getFusedPairsCount());        
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
