package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.specification.SchemaDefinition;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author nkarag
 */
public class InputValidator {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(InputValidator.class);

    private final String configurationPath;
    private final Set<String> functionSet;
    
    private final XmlValidator xmlValidator = new XmlValidator();
    
    public InputValidator(String configurationPath, Set<String> functionSet){
        this.configurationPath = configurationPath;
        this.functionSet = functionSet;
    }
    
    /**
     * Helper class. Validates the rules XML input against the corresponding XSD
     */
    private class XmlValidator {

        public boolean validateAgainstXSD(String xmlPath, String xsd) throws FileNotFoundException {

            InputStream xml = new FileInputStream(xmlPath);
            Source inputStream = new StreamSource(new StringReader(xsd));

            try {

                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(inputStream);
                Validator validator = schema.newValidator();
                validator.validate(new StreamSource(xml));

                LOG.debug(xmlPath + " is valid");
                return true;

            } catch (SAXException e) {
                LOG.error("Failed to validate " + xmlPath + " with corresponding XSD. Reason:\n" + e);
                return false;
            } catch (IOException e) {
                LOG.error(e);
                return false;
            }  
        }    
    }

    public boolean isValidConfigurationXSD() {
        boolean isValid;
        try {
            isValid = isValidConfiguration();
            
        } catch (FileNotFoundException ex) {
            LOG.fatal("Input is not valid! " + ex);
            return false;
        }
        
        return isValid;
    }
    
    public boolean isValidRulesWithXSD(String rulesXmlPath) {
        boolean isValid;
        try {
            isValid = isValidRulesXml(rulesXmlPath);
            
        } catch (FileNotFoundException ex) {
            LOG.fatal("Input is not valid! " + ex);
            return false;
        }
        
        return isValid;
    }
    
    private boolean isValidRulesXml(String rulesXmlPath) throws FileNotFoundException{
        return xmlValidator.validateAgainstXSD(rulesXmlPath, SchemaDefinition.RULE_XSD);
    }
    
    private boolean isValidConfiguration() throws FileNotFoundException{
        return xmlValidator.validateAgainstXSD(configurationPath, SchemaDefinition.CONFIG_XSD);
    }
    
    public boolean isValidFunctions(String rulesXmlPath) throws ParserConfigurationException, SAXException, IOException{

        File fXmlFile = new File(rulesXmlPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();        
        NodeList nList = doc.getElementsByTagName(SpecificationConstants.Rule.FUNCTION);
        for (int i = 0; i < nList.getLength(); i++) {

            Node functionNode = nList.item(i);
            if (functionNode.getNodeType() == Node.ELEMENT_NODE) {
                String function = functionNode.getTextContent();

                int index = function.indexOf("(");
                if (index != -1){
                    String functionNameBeforeParenthesis = function.substring(0, index).toLowerCase();
                    if(!functionSet.contains(functionNameBeforeParenthesis)){
                        LOG.fatal("Functions defined in " + SpecificationConstants.Spec.RULES_XML + " is not valid. "
                                + functionNameBeforeParenthesis + " is malformed or not supported.");
                        return false;
                    }
                }
            }
        }        
        return true;
    }
    
    public boolean isValidOutputDirPath(String path){
        File file = new File(path);
        return file.isDirectory();
    }
}
