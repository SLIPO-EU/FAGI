package gr.athena.innovation.fagi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(InputValidator.class);
    
    private final String rulesXmlPath;
    private final String rulesXsdPath;
    private final String specXmlPath;
    private final String specXsdPath;
    private final HashSet<String> methodSet;
    
    private final XmlValidator xmlValidator = new XmlValidator();
    
    /**
     * Validates the rules XML input against the corresponding XSD
     */
    private class XmlValidator {

        public boolean validateAgainstXSD(String xmlPath, String xsdPath) throws FileNotFoundException {

            InputStream xml = new FileInputStream(xmlPath);
            InputStream xsd = new FileInputStream(xsdPath);

            try {

                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(new StreamSource(xsd));
                Validator validator = schema.newValidator();
                validator.validate(new StreamSource(xml));

                logger.debug(xmlPath + " is valid");
                return true;

            } catch (SAXException e) {
                logger.error("Failed to validate " + xmlPath + " with " + xsdPath + ". Reason:\n" + e);
                return false;
            } catch (IOException e) {
                logger.error(e);
                return false;
            }  
        }    
    }
    
    public InputValidator(String rulesXmlPath, String rulesXsdPath, String specificationPath, 
            String specXsdPath, HashSet<String> methodSet){
        
        this.rulesXmlPath = rulesXmlPath;
        this.rulesXsdPath = rulesXsdPath;
        this.specXmlPath = specificationPath;
        this.specXsdPath = specXsdPath;
        this.methodSet = methodSet;
    }

    public boolean isValidInput() {
        boolean isValid;
        try {
            isValid = isValidSpecification() && isValidRulesXml() && isValidFunctions();
            
        } catch (FileNotFoundException ex) {
            logger.fatal("Input is not valid! " + ex);
            return false;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.fatal("Input is not valid! " + ex);
            //TODO: change return value after implementation is complete
            return true;
        }
        
        return true;
    }
    
    private boolean isValidRulesXml() throws FileNotFoundException{
        return xmlValidator.validateAgainstXSD(rulesXmlPath, rulesXsdPath);
    }
    
    private boolean isValidSpecification() throws FileNotFoundException{
        return xmlValidator.validateAgainstXSD(specXmlPath, specXsdPath);
    }
    
    private boolean isValidFunctions() throws ParserConfigurationException, SAXException, IOException{

        File fXmlFile = new File(rulesXmlPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();        
        NodeList nList = doc.getElementsByTagName("FUNCTION");
        for (int i = 0; i < nList.getLength(); i++) {
            
            Node functionNode = nList.item(i);
            if (functionNode.getNodeType() == Node.ELEMENT_NODE) {
                String function = functionNode.getTextContent();

                int index = function.indexOf("(");
                if (index != -1){
                    String functionNameBeforeParenthesis = function.substring(0, index);        
                    if(!methodSet.contains(functionNameBeforeParenthesis)){
                        return false;
                    }
                }
            }
        }        
        return true;
    }
}
