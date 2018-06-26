package gr.athena.innovation.fagi.specification;

import gr.athena.innovation.fagi.exception.WrongInputException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for parsing the specification XML. 
 * 
 * @author nkarag
 */
public class SpecificationParser {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(SpecificationParser.class);

    /**
     * Parses the specification XML and produces a configuration object.
     * 
     * @param configurationPath the configuration file path.
     * @return the configuration object.
     * @throws WrongInputException Indicates that something is wrong with the input.
     */
    public Configuration parse(String configurationPath) throws WrongInputException {

        LOG.info("Parsing configuration: " + configurationPath);
        Configuration configuration = Configuration.getInstance();

        try {

            File fXmlFile = new File(configurationPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList inputNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.INPUT_FORMAT);
            String inputFormat = inputNodeList.item(0).getTextContent();
            configuration.setInputRDFFormat(inputFormat);

            NodeList outputNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.OUTPUT_FORMAT);
            String outputFormatString = outputNodeList.item(0).getTextContent();

            String outputFormat;
            //for future use. Only NT is supported.
            switch (outputFormatString) {
                case "":
                case "TTL":
                case "RDF":
                case "OWL":
                case "JSONLD":
                case "RJ":
                case "TRIG":    
                case "TRIX":
                case "NQ":
                case "NT":
                    outputFormat = "NT";
                    break;
                default:
                    outputFormat = "NT";
            }            

            configuration.setOutputRDFFormat(outputFormat);

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

            configuration.setLocale(locale);

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

            configuration.setSimilarity(similarity);

            NodeList rulesNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.RULES);
            String rules = rulesNodeList.item(0).getTextContent();
            configuration.setRulesPath(rules);
            
            NodeList leftNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.LEFT_DATASET);
            Node leftNode = leftNodeList.item(0);
            NodeList leftChilds = leftNode.getChildNodes();
            for (int i = 0; i < leftChilds.getLength(); i++) {
                Node n = leftChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FILE)) {
                        configuration.setPathDatasetA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        configuration.setIdA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }
                        configuration.setEndpointA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.CATEGORIES)) {
                        configuration.setCategoriesA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.DATE)) {
                        String dateString = n.getTextContent();
                        if(StringUtils.isBlank(dateString)){
                            configuration.setDateB(null);
                        } else {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SpecificationConstants.Spec.DATE_FORMAT);
                            simpleDateFormat.setLenient(false);
                            try {
                                Date dateA = simpleDateFormat.parse(dateString);
                                configuration.setDateA(dateA);
                            } catch (ParseException ex) {
                                LOG.error(ex);
                                throw new WrongInputException("Date in \"left\" dataset does not have the expected format. "
                                        + "\nSupported format is " + SpecificationConstants.Spec.DATE_FORMAT);
                            }                            
                        }
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
                        configuration.setPathDatasetB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        configuration.setIdB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }
                        configuration.setEndpointB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.CATEGORIES)) {
                        configuration.setCategoriesB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.DATE)) {
                        String dateString = n.getTextContent();
                        if(StringUtils.isBlank(dateString)){
                            configuration.setDateB(null);
                        } else {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SpecificationConstants.Spec.DATE_FORMAT);
                            simpleDateFormat.setLenient(false);
                            try {
                                Date dateB = simpleDateFormat.parse(dateString);
                                configuration.setDateB(dateB);
                            } catch (ParseException ex) {
                                LOG.error(ex);
                                throw new WrongInputException("Date in \"right\" dataset does not have the expected format. "
                                        + "\nSupported format is " + SpecificationConstants.Spec.DATE_FORMAT);
                            }                            
                        }
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
                        configuration.setPathLinks(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        configuration.setIdLinks(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }                        
                        configuration.setEndpointLinks(n.getTextContent());
                    }
                }
                n.getNextSibling();
            }

            NodeList targetNodeList = doc.getElementsByTagName(SpecificationConstants.Spec.TARGET);
            Node targetNode = targetNodeList.item(0);
            NodeList targetChilds = targetNode.getChildNodes();
            for (int i = 0; i < targetChilds.getLength(); i++) {
                Node n = targetChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.OUTPUT_DIR)) {
                        configuration.setOutputDir(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.FUSED)) {
                        configuration.setFused(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.REMAINING)) {
                        configuration.setRemaining(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.AMBIGUOUS)) {
                        configuration.setAmbiguousDatasetFilepath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.STATISTICS)) {
                        configuration.setStatsFilepath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.MODE)) {
                        configuration.setOutputMode(EnumOutputMode.fromString(n.getTextContent()));
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ID)) {
                        configuration.setIdOutput(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Spec.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }                        
                        configuration.setEndpointOutput(n.getTextContent());
                    }
                }
                n.getNextSibling();
            }

        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            LOG.fatal("Exception occured while parsing the configuration: "
                    + configurationPath + "\n" + e);
        }

        return configuration;
    }
}
