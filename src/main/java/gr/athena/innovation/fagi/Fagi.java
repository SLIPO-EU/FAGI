package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.core.specification.ConfigReader;
import gr.athena.innovation.fagi.core.Fuser;
import gr.athena.innovation.fagi.core.rule.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.core.rule.XmlValidator;
import gr.athena.innovation.fagi.core.specification.FusionConfig;
import gr.athena.innovation.fagi.core.specification.FusionSpecification;
import gr.athena.innovation.fagi.core.specification.SpecificationParser;
import gr.athena.innovation.fagi.fusers.MethodRegistry;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.repository.AbstractRepository;
import gr.athena.innovation.fagi.repository.GenericRDFRepository;
import gr.athena.innovation.fagi.utils.InputValidator;
import gr.athena.innovation.fagi.xml.XmlProcessor2;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        
        String configPath = "src/main/resources/config.properties";
        String rulesXsd = "/home/nkarag/SLIPO/FAGI-gis/src/main/resources/rules.xsd";
        
        //These values (fusionSpec and rulesXml) will come from the cli arguments
        String fusionSpec = "/home/nkarag/SLIPO/FAGI-gis/src/main/resources/fusion.spec";
        String rulesXml = "/home/nkarag/SLIPO/FAGI-gis/src/main/resources/rules.xml";

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
            if(arg.equals("-spec")){
                System.out.println("spec path: " + value);
                fusionSpec = value;
                //break;
            } else if(arg.equals("-rules")){
                System.out.println("rules path: " + value);
                rulesXml = value;
                break;
            } 
//            else {
//                 System.out.println("Usage:\n java -jar fagi-1.0-SNAPSHOT.jar -config <configFile> ");
//                 System.out.println("-config requires a file path");
//                 System.exit(0);
//            }
            i++;
        }

        InputValidator inputValidator = new InputValidator(rulesXml, rulesXsd, fusionSpec);
        
        if(!inputValidator.isValidInput()){
            System.out.println("Wrong input! Check input files");
            System.out.println("Usage:\n java -jar fagi-1.0-SNAPSHOT.jar -config <configFile> ");
            System.out.println("-config requires a file path");
            System.exit(0);
        }

        XmlValidator validator = new XmlValidator();
        validator.validateAgainstXSD(rulesXml, rulesXsd);

        XmlProcessor2 xm2 = new XmlProcessor2();
        RuleCatalog ruleCatalog = xm2.parseRules(rulesXml);

        List<Rule> rules = ruleCatalog.getRules();
        logger.info("\n\n\n\nRules size: " + rules.size());
        for (Rule rule : rules){
            logger.fatal(rule.toString());
            //String actionRuleString = rule.getActionRuleSet().getActionRuleList().get(0).toString();
            //logger.debug(actionRuleString);

        }

        //TODO - replace config reader with Specification parser. Pass parameters to abstractRepository from SpecParser
        SpecificationParser specificationParser = new SpecificationParser();
        FusionSpecification fusionSpecification = specificationParser.parse();
        ConfigReader configReader = new ConfigReader();
        FusionConfig config = configReader.loadConfiguration(configPath);

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(config.getPathA());
        genericRDFRepository.parseRight(config.getPathB());
        genericRDFRepository.parseLinks(config.getPathLinks());

        MethodRegistry methodRegistry = new MethodRegistry();
        methodRegistry.init();
        methodRegistry.validateRules(ruleCatalog.getRules());
        HashSet<String> methodSet = methodRegistry.getMethodRegistryList();
        
        
        
        
        ArrayList<InterlinkedPair> interlinkedEntitiesList = new ArrayList<>();
        Fuser fuser = new Fuser(interlinkedEntitiesList);

        //fuser.fuseAll(config);
        logger.trace("Start rule Fusion");
        
        fuser.fuseAllWithRules(config, ruleCatalog);
        logger.trace("Rule Fusion complete.");
        
        fuser.combineFusedAndWrite(config, interlinkedEntitiesList);

        logger.info(config.toString());
        logger.trace("interlinkedEntitiesList " + interlinkedEntitiesList.size());
        logger.info("Interlinked not found in datasets: " + fuser.getLinkedEntitiesNotFoundInDataset());
        logger.info("Number of fused pairs: " + fuser.getFusedPairsCount());        
    }
}
