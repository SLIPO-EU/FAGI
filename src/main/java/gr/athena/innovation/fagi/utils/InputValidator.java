package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.core.rule.XmlValidator;
import java.io.FileNotFoundException;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class InputValidator {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(InputValidator.class);
    
    private final String rulesXmlPath;
    private final String rulesXsdPath;
    private final String specXmlPath;
    private final String specXsdPath;
    
    private final XmlValidator xmlValidator = new XmlValidator();

    public InputValidator(String rulesXmlPath, String rulesXsdPath, String specificationPath, String specXsdPath){
        
        this.rulesXmlPath = rulesXmlPath;
        this.rulesXsdPath = rulesXsdPath;
        this.specXmlPath = specificationPath;
        this.specXsdPath = specXsdPath;
    }

    public boolean isValidInput() throws FileNotFoundException{
        return isValidRulesXml() && isValidSpecification() && isValidRules();
    }
    
    private boolean isValidRulesXml() throws FileNotFoundException{
        xmlValidator.validateAgainstXSD(rulesXmlPath, rulesXsdPath);
        
        logger.info("Input seems valid!");
        return true;
    }
    
    private boolean isValidSpecification() throws FileNotFoundException{
        xmlValidator.validateAgainstXSD(specXmlPath, specXsdPath);
        return true;
    }
    
    private boolean isValidRules(){
        return true;
    }
}
