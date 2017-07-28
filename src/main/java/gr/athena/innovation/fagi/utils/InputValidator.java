package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.core.rule.XmlValidator;
import java.io.FileNotFoundException;

/**
 *
 * @author nkarag
 */
public class InputValidator {

    private final String rulesXmlPath;
    private final String rulesXsdPath;
    private final String specificationPath;
    
    public InputValidator(String rulesXmlPath, String rulesXsdPath, String specificationPath){
        this.rulesXmlPath = rulesXmlPath;
        this.rulesXsdPath = rulesXsdPath;
        this.specificationPath = specificationPath;
    }
    
    public boolean isValidInput() throws FileNotFoundException{
        return isValidXml() && isValidSpecification() && isValidRules();
    }
    
    private boolean isValidXml() throws FileNotFoundException{
        XmlValidator xmlValidator = new XmlValidator();
        xmlValidator.validateAgainstXSD(rulesXmlPath, rulesXsdPath);
        
        return true;
    }
    
    private boolean isValidSpecification(){
        return true;
    }
    
    private boolean isValidRules(){
        return true;
    }
}
