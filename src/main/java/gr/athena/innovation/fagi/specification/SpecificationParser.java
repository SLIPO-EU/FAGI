package gr.athena.innovation.fagi.specification;

import gr.athena.innovation.fagi.exception.WrongInputException;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
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

    public FusionSpecification parse(String fusionSpecificationPath) throws WrongInputException {

        logger.info("Parsing Fusion Specification: " + fusionSpecificationPath);
        FusionSpecification fusionSpecification = FusionSpecification.getInstance();

        try {

            File fXmlFile = new File(fusionSpecificationPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList inputNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.INPUT_FORMAT);
            String inputFormat = inputNodeList.item(0).getTextContent();
            fusionSpecification.setInputRDFFormat(inputFormat);

            NodeList outputNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.OUTPUT_FORMAT);
            String outputFormat = outputNodeList.item(0).getTextContent();
            fusionSpecification.setOutputRDFFormat(outputFormat);

            NodeList localeNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.LOCALE);
            
            String localeText = "";
            Locale locale;
            
            if (localeNodeList.getLength() > 0) {
                localeText = localeNodeList.item(0).getTextContent();
            }

            switch (localeText) {
                case "":
                case "EN-GB":
                    locale = Locale.forLanguageTag("en-GB");
                    break;
                case "EN-US":
                    locale = Locale.forLanguageTag("en-US");
                    break;                    
                case "DE":
                case "de":
                case "de-DE":
                case "GERMAN":
                case "german":
                    locale = Locale.forLanguageTag("de-DE");
                    break;
                case "DE-AT":
                case "de-at":
                    locale = Locale.forLanguageTag("de-AT");
                    break;
                case "EL":
                case "el":   
                case "greek":
                    locale = Locale.forLanguageTag("el-GR");
                    break;
                default:
                    locale = Locale.ENGLISH;
            }

            fusionSpecification.setLocale(locale);

            NodeList similarityNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.SIMILARITY);
            String similarityText = "";
            String similarity;
            
            if (similarityNodeList.getLength() > 0) {
                similarityText = similarityNodeList.item(0).getTextContent();
            }

            switch (similarityText.toUpperCase()) {
                case "SORTEDJAROWINKLER":
                    similarity = SpecificationConstants.Similarity.SORTED_JARO_WINKLER;
                    break;
                case "JAROWINKLER":
                    similarity = SpecificationConstants.Similarity.JARO_WINKLER;
                    break;                    
                case "COSINE":
                    similarity = SpecificationConstants.Similarity.COSINE;
                    break;                       
                case "LEVENSHTEIN":
                    similarity = SpecificationConstants.Similarity.LEVENSHTEIN;
                    break;  
                case "JARO":
                    similarity = SpecificationConstants.Similarity.JARO;
                    break;  
                case "2GRAM":
                    similarity = SpecificationConstants.Similarity.GRAM_2;
                    break;  
                case "LONGESTCOMMONSUBSEQUENCE":
                    similarity = SpecificationConstants.Similarity.LCS;
                    break;  
                case "":
                default:
                    similarity = SpecificationConstants.Similarity.JARO_WINKLER;
            }

            fusionSpecification.setSimilarity(similarity);

            NodeList leftNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.LEFT_DATASET);
            Node leftNode = leftNodeList.item(0);
            NodeList leftChilds = leftNode.getChildNodes();
            for (int i = 0; i < leftChilds.getLength(); i++) {
                Node n = leftChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FILE)) {
                        fusionSpecification.setPathDatasetA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        fusionSpecification.setIdA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        fusionSpecification.setEndpointA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.CATEGORIES)) {
                        fusionSpecification.setCategoriesA(n.getTextContent());
                    }
                }
                n.getNextSibling();
            }

            NodeList rightNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.RIGHT_DATASET);
            Node rightNode = rightNodeList.item(0);
            NodeList rightChilds = rightNode.getChildNodes();
            for (int i = 0; i < rightChilds.getLength(); i++) {

                Node n = rightChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FILE)) {
                        fusionSpecification.setPathDatasetB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        fusionSpecification.setIdB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        fusionSpecification.setEndpointB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.CATEGORIES)) {
                        fusionSpecification.setCategoriesB(n.getTextContent());
                    }
                }
                n.getNextSibling();
            }

            NodeList linksNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.LINKS);
            Node linksNode = linksNodeList.item(0);
            NodeList linksChilds = linksNode.getChildNodes();
            for (int i = 0; i < linksChilds.getLength(); i++) {

                Node n = linksChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FILE)) {
                        fusionSpecification.setPathLinks(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        fusionSpecification.setIdLinks(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        fusionSpecification.setEndpointLinks(n.getTextContent());
                    }
                }
                n.getNextSibling();
            }

            NodeList targetNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.TARGET_DATASET);
            Node targetNode = targetNodeList.item(0);
            NodeList targetChilds = targetNode.getChildNodes();
            for (int i = 0; i < targetChilds.getLength(); i++) {
                Node n = targetChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.OUTPUT_DIR)) {
                        fusionSpecification.setOutputDir(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FILE_A)) {
                        fusionSpecification.setFileA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FILE_B)) {
                        fusionSpecification.setFileB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FILE_C)) {
                        fusionSpecification.setFileC(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.MODE)) {
                        fusionSpecification.setOutputMode(EnumOutputMode.fromString(n.getTextContent()));
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        fusionSpecification.setIdOutput(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        fusionSpecification.setEndpointOutput(n.getTextContent());
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
