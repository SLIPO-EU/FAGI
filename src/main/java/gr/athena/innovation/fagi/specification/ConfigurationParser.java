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
public class ConfigurationParser {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(ConfigurationParser.class);

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

            NodeList inputNodeList = doc.getElementsByTagName(SpecificationConstants.Config.INPUT_FORMAT);
            String inputFormat = inputNodeList.item(0).getTextContent();
            configuration.setInputRDFFormat(inputFormat);

            NodeList outputNodeList = doc.getElementsByTagName(SpecificationConstants.Config.OUTPUT_FORMAT);
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

            NodeList localeNodeList = doc.getElementsByTagName(SpecificationConstants.Config.LOCALE);

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

            NodeList similarityNodeList = doc.getElementsByTagName(SpecificationConstants.Config.SIMILARITY);
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

            NodeList statsNodeList = doc.getElementsByTagName(SpecificationConstants.Config.STATS);
            String statsModeText = "";
            String stats;
            
            if (statsNodeList.getLength() > 0) {
                statsModeText = statsNodeList.item(0).getTextContent();
            }
            
            switch (statsModeText.toUpperCase()) {
                case "LIGHT":
                    stats = SpecificationConstants.Config.LIGHT_STATS;
                    break;
                case "DETAILED":
                    stats = SpecificationConstants.Config.DETAILED_STATS;
                    break;
                case "":
                default:
                    stats = "";
            }

            configuration.setStats(stats);

            NodeList rulesNodeList = doc.getElementsByTagName(SpecificationConstants.Config.RULES);
            String rules = rulesNodeList.item(0).getTextContent();
            configuration.setRulesPath(rules);

            NodeList verboseNodeList = doc.getElementsByTagName(SpecificationConstants.Config.VERBOSE);
            String verbose = verboseNodeList.item(0).getTextContent();
            configuration.setVerbose(Boolean.parseBoolean(verbose));
            
            NodeList leftNodeList = doc.getElementsByTagName(SpecificationConstants.Config.LEFT_DATASET);
            Node leftNode = leftNodeList.item(0);
            NodeList leftChilds = leftNode.getChildNodes();
            for (int i = 0; i < leftChilds.getLength(); i++) {
                Node n = leftChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.FILE)) {
                        configuration.setPathDatasetA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ID)) {
                        configuration.setIdA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }
                        configuration.setEndpointA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.CATEGORIES)) {
                        configuration.setCategoriesA(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.DATE)) {
                        String dateString = n.getTextContent();
                        if(StringUtils.isBlank(dateString)){
                            configuration.setDateB(null);
                        } else {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SpecificationConstants.Config.DATE_FORMAT);
                            simpleDateFormat.setLenient(false);
                            try {
                                Date dateA = simpleDateFormat.parse(dateString);
                                configuration.setDateA(dateA);
                            } catch (ParseException ex) {
                                LOG.error(ex);
                                throw new WrongInputException("Date in \"left\" dataset does not have the expected format. "
                                        + "\nSupported format is " + SpecificationConstants.Config.DATE_FORMAT);
                            }                            
                        }
                    }
                }
                n.getNextSibling();
            }

            NodeList rightNodeList = doc.getElementsByTagName(SpecificationConstants.Config.RIGHT_DATASET);
            Node rightNode = rightNodeList.item(0);
            NodeList rightChilds = rightNode.getChildNodes();
            for (int i = 0; i < rightChilds.getLength(); i++) {

                Node n = rightChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.FILE)) {
                        configuration.setPathDatasetB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ID)) {
                        configuration.setIdB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }
                        configuration.setEndpointB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.CATEGORIES)) {
                        configuration.setCategoriesB(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.DATE)) {
                        String dateString = n.getTextContent();
                        if(StringUtils.isBlank(dateString)){
                            configuration.setDateB(null);
                        } else {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SpecificationConstants.Config.DATE_FORMAT);
                            simpleDateFormat.setLenient(false);
                            try {
                                Date dateB = simpleDateFormat.parse(dateString);
                                configuration.setDateB(dateB);
                            } catch (ParseException ex) {
                                LOG.error(ex);
                                throw new WrongInputException("Date in \"right\" dataset does not have the expected format. "
                                        + "\nSupported format is " + SpecificationConstants.Config.DATE_FORMAT);
                            }                            
                        }
                    }
                }
                n.getNextSibling();
            }

            NodeList linksNodeList = doc.getElementsByTagName(SpecificationConstants.Config.LINKS);
            Node linksNode = linksNodeList.item(0);
            NodeList linksChilds = linksNode.getChildNodes();
            for (int i = 0; i < linksChilds.getLength(); i++) {

                Node n = linksChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.FILE)) {
                        configuration.setPathLinks(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ID)) {
                        configuration.setIdLinks(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }                        
                        configuration.setEndpointLinks(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.LINKS_FORMAT)) {
                        String linksFormatText = n.getTextContent();
                        String linksFormat;
                        
                        switch (linksFormatText.toLowerCase()) {
                            case "nt":
                                linksFormat = SpecificationConstants.Config.NT;
                                break;
                            case "csv":
                                linksFormat = SpecificationConstants.Config.CSV;
                                break;                    
                            case "csv-unique-links":
                                linksFormat = SpecificationConstants.Config.CSV_UNIQUE_LINKS;
                                break;  
                            case "csv-ensembles":
                                linksFormat = SpecificationConstants.Config.CSV_ENSEMBLES;
                                break; 
                            default:
                                throw new WrongInputException("Wrong links format. Define between " 
                                        + SpecificationConstants.Config.NT + " " 
                                        + SpecificationConstants.Config.CSV + " " 
                                        + SpecificationConstants.Config.CSV_UNIQUE_LINKS);
                        }
                        configuration.setLinksFormat(linksFormat);
                    }
                }
                n.getNextSibling();
            }

            NodeList targetNodeList = doc.getElementsByTagName(SpecificationConstants.Config.TARGET);
            Node targetNode = targetNodeList.item(0);
            NodeList targetChilds = targetNode.getChildNodes();
            for (int i = 0; i < targetChilds.getLength(); i++) {
                Node n = targetChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.OUTPUT_DIR)) {
                        configuration.setOutputDir(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.FUSED)) {
                        configuration.setFused(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.REMAINING)) {
                        configuration.setRemaining(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.AMBIGUOUS)) {
                        configuration.setAmbiguousDatasetFilepath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.STATISTICS)) {
                        configuration.setStatsFilepath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.FUSION_LOG)) {
                        configuration.setFusionLog(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.MODE)) {
                        configuration.setOutputMode(EnumOutputMode.fromString(n.getTextContent()));
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ID)) {
                        configuration.setIdOutput(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ENDPOINT)) {
                        if(!StringUtils.isBlank(n.getTextContent())){
                            throw new UnsupportedOperationException("Endpoints are not supported yet.");
                        }                        
                        configuration.setEndpointOutput(n.getTextContent());
                    }
                }
                n.getNextSibling();
            }

            NodeList mlNodeList = doc.getElementsByTagName(SpecificationConstants.Config.ML);
            Node mlNode = mlNodeList.item(0);
            NodeList mlChilds = mlNode.getChildNodes();

            for (int i = 0; i < mlChilds.getLength(); i++) {

                Node n = mlChilds.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.NAME)) {
                        configuration.setNameModelPath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.ADDRESS)) {
                        configuration.setAddressModelPath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.WEBSITE)) {                      
                        configuration.setWebsiteModelPath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.PHONE)) {                      
                        configuration.setPhoneModelPath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.EMAIL)) {                      
                        configuration.setEmailModelPath(n.getTextContent());
                    } else if (n.getNodeName().equalsIgnoreCase(SpecificationConstants.Config.VALIDATION)) {                      
                        configuration.setValidationModelPath(n.getTextContent());
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
