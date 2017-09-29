package gr.athena.innovation.fagi.core.rule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Validates the rules XML input against the corresponding XSD
 * 
 * @author nkarag
 */
public class XmlValidator {
    
    private static final Logger logger = LogManager.getLogger(XmlValidator.class);
    
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
