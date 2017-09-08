package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.model.InterlinkedPair;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import java.net.URL;
import org.xml.sax.SAXException;
//import java.io.File; // if you use File
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Validates the rules XML input against a preconfigured XSD
 * 
 * @author nkarag
 */
public class XmlValidator {
    
    private static final Logger logger = LogManager.getLogger(XmlValidator.class);
    
    
    public void validate(String xmlPath, String xsdPath) throws MalformedURLException{
        URL schemaFile = new URL(xsdPath);
        Source xmlFile = new StreamSource(new File(xmlPath));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {

            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            logger.info(xmlFile.getSystemId() + " is valid");

        } catch (SAXException e) {
            logger.error(xmlFile.getSystemId() + " is NOT valid, reason:" + e);
        } catch (IOException e) {
            logger.error("Error in XML against XSD validation" + e);
        }
    }
    
    public boolean validateAgainstXSD(String xmlPath, String xsdPath) throws FileNotFoundException {
        
        InputStream xml = new FileInputStream(xmlPath);
        InputStream xsd = new FileInputStream(xsdPath);
        
        try {

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            
            logger.debug("Validation against XSD success.");
            return true;
            
        } catch (SAXException e) {
            logger.error("Rules in XML are NOT valid reason:" + e);
            return false;
        } catch (IOException e) {
            logger.error(e);
            return false;
        }  
    }    
}
