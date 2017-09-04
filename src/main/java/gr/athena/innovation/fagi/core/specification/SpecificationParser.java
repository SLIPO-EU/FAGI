/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.athena.innovation.fagi.core.specification;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author nkarag
 */
public class SpecificationParser {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(SpecificationParser.class);
    
    public FusionSpecification parse(String fusionSpecificationPath){
        
        logger.info("Parsing Fusion Specification: " + fusionSpecificationPath);
        FusionSpecification fusionSpecification = new FusionSpecification();
        
        try {

            File fXmlFile = new File(fusionSpecificationPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList inputNodeList = doc.getElementsByTagName(SpecificationConstants.INPUT_FORMAT);
            String inputFormat = inputNodeList.item(0).getTextContent();
            fusionSpecification.setInputRDFFormat(inputFormat);
            logger.debug("INPUT FORMAT " + inputFormat);
            
            NodeList outputNodeList = doc.getElementsByTagName(SpecificationConstants.OUTPUT_FORMAT);
            String outputFormat = outputNodeList.item(0).getTextContent();
            fusionSpecification.setOutputRDFFormat(outputFormat);
            logger.debug("OUTPUT FORMAT " + outputFormat);

            NodeList leftNodeList = doc.getElementsByTagName(SpecificationConstants.LEFT_DATASET);
            Node leftNode = leftNodeList.item(0);
            NodeList leftChilds = leftNode.getChildNodes();
            for (int i = 0; i < leftChilds.getLength(); i++) {
 
                Node n = leftChilds.item(i);                            

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println("\nCurrent Element :" + n.getNodeName());
                    System.out.println("\ntext :" + n.getTextContent());

                    if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.FILE)){
                       fusionSpecification.setPathA(n.getTextContent());
                    } else if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.ID)){
                        fusionSpecification.setIdA(n.getTextContent());
                    } else if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.ENDPOINT)){
                        fusionSpecification.setEndpointA(n.getTextContent());
                    }

                }
                n.getNextSibling();
            }
            

            NodeList rightNodeList = doc.getElementsByTagName(SpecificationConstants.RIGHT_DATASET);
            Node rightNode = rightNodeList.item(0);
            NodeList rightChilds = rightNode.getChildNodes();
            for (int i = 0; i < rightChilds.getLength(); i++) {
                
                Node n = rightChilds.item(i);                            

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println("\nCurrent Element :" + n.getNodeName());
                    System.out.println("\ntext :" + n.getTextContent());

                    if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.FILE)){
                       fusionSpecification.setPathB(n.getTextContent());
                    } else if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.ID)){
                        fusionSpecification.setIdB(n.getTextContent());
                    } else if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.ENDPOINT)){
                        fusionSpecification.setEndpointB(n.getTextContent());
                    }
                }
                n.getNextSibling();
            }

            NodeList linksNodeList = doc.getElementsByTagName(SpecificationConstants.LINKS);
            Node linksNode = linksNodeList.item(0);
            NodeList linksChilds = linksNode.getChildNodes();
            for (int i = 0; i < linksChilds.getLength(); i++) {
 
                Node n = linksChilds.item(i);                            

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println("\nCurrent Element :" + n.getNodeName());
                    System.out.println("\ntext :" + n.getTextContent());

                    if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.FILE)){
                       fusionSpecification.setPathLinks(n.getTextContent());
                    } else if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.ID)){
                        fusionSpecification.setIdLinks(n.getTextContent());
                    } else if(n.getNodeName().equalsIgnoreCase(SpecificationConstants.ENDPOINT)){
                        fusionSpecification.setEndpointLinks(n.getTextContent());
                    }

                }
                n.getNextSibling();
            }

        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            logger.fatal("Exception occured while parsing the fusion specification: " 
                    + fusionSpecificationPath + "\n" + e);
        }
        
        return fusionSpecification;
    }
}
