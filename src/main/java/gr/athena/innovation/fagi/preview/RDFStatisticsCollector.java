package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for calculating statistics.
 * 
 * @author nkarag
 */
public class RDFStatisticsCollector implements StatisticsCollector{

    private static final Logger LOG = LogManager.getLogger(RDFStatisticsCollector.class);

    private int totalPOIsA;
    private int totalPOIsB;

    private int sumA = 0;
    private int sumB = 0;

    StatisticsContainer container;
    List<StatisticResultPair> stats = new ArrayList<>();

    @Override
    public StatisticsContainer collect(){

        /* IMPORTANT: The order of calculation is sensitive for avoiding re-calculations */
        
        container = new StatisticsContainer();

        /* POIs and triples count */
        StatisticResultPair totalEntities = countTotalEntities();
        container.setTotalPOIs(totalEntities);

        StatisticResultPair totalTriples = countTriples();
        container.setTotalTriples(totalTriples);
        
        /* Non empty properties */

        StatisticResultPair nonEmptyNames = countNonEmptyNames();
        container.setNonEmptyNames(nonEmptyNames);

        StatisticResultPair nonEmptyPhones = countNonEmptyPhones();
        container.setNonEmptyPhones(nonEmptyPhones);

        StatisticResultPair nonEmptyStreets = countNonEmptyStreets();
        container.setNonEmptyStreets(nonEmptyStreets);

        StatisticResultPair nonEmptyStreetNumbers = countNonEmptyStreetNumbers();
        container.setNonEmptyStreetNumbers(nonEmptyStreetNumbers);

        StatisticResultPair nonEmptyWebsites = countNonEmptyWebsites();
        container.setNonEmptyWebsites(nonEmptyWebsites);

        StatisticResultPair nonEmptyEmails = countNonEmptyEmails();
        container.setNonEmptyEmails(nonEmptyEmails);
        
        StatisticResultPair nonEmptyDates = countNonEmptyDates();
        container.setNonEmptyDates(nonEmptyDates);      
        
        /* Empty properties */
        
        StatisticResultPair emptyNames = countEmptyNames();
        container.setEmptyNames(emptyNames);

        StatisticResultPair emptyPhones = countEmptyPhones();
        container.setEmptyPhones(emptyPhones);

        StatisticResultPair emptyStreets = countEmptyStreets();
        container.setEmptyStreets(emptyStreets);

        StatisticResultPair emptyStreetNumbers = countEmptyStreetNumbers();
        container.setEmptyStreetNumbers(emptyStreetNumbers);

        StatisticResultPair emptyWebsites = countEmptyWebsites();
        container.setEmptyWebsites(emptyWebsites);

        StatisticResultPair emptyEmails = countEmptyEmails();
        container.setEmptyEmails(emptyEmails);
        
        StatisticResultPair emptyDates = countEmptyDates();
        container.setEmptyDates(emptyDates);
        
        /* Distinct properties */
        
        StatisticResultPair distinctProperties = countDistinctProperties();
        container.setDistinctProperties(distinctProperties);

        /* Percenteges */
        
        StatisticResultPair percentageOfKnownFormats = calculatePercentageOfPrimaryDateFormats();
        container.setPercentageOfDateKnownFormats(percentageOfKnownFormats);
        
        StatisticResultPair percentageOfNames = calculateNamePercentage();
        container.setNamePercentage(percentageOfNames);

        StatisticResultPair percentageOfWebsites = calculateWebsitePercentage();
        container.setWebsitePercentage(percentageOfWebsites);
        
        StatisticResultPair percentageOfPhones = calculatePhonePercentage();
        container.setPhonePercentage(percentageOfPhones);

        StatisticResultPair percentageOfStreets = calculateStreetPercentage();
        container.setStreetPercentage(percentageOfStreets);
        
        StatisticResultPair percentageOfStreetNumbers = calculateStreetNumberPercentage();
        container.setStreetNumberPercentage(percentageOfStreetNumbers);
        
        StatisticResultPair percentageOfLocalities = calculateLocalityPercentage();
        container.setLocalityPercentage(percentageOfLocalities);

        StatisticResultPair percentageOfDates = calculateDatePercentage();
        container.setDatePercentage(percentageOfDates);

        /* Statistics for linked POIs*/

        StatisticResultPair linkedVsUnlinked = countLinkedVsUnlinked();
        container.setLinkedVsUnlinked(linkedVsUnlinked);
        
        List<StatisticResultPair> linkStats = computeLinkStats();
        container.setLinkedNonEmptyNames(linkStats.get(0));

        /* Aggregate statistics */

        StatisticResultPair totalNonEmptyProperties = countTotalNonEmptyProperties();
        container.setTotalNonEmptyProperties(totalNonEmptyProperties);

        StatisticResultPair totalEmptyProperties = countTotalEmptyProperties();
        container.setTotalEmptyProperties(totalEmptyProperties); 

        StatisticResultPair percentageOfTotalProperties = calculateAllNonEmptyPropertiesPercentage();
        container.setPercentNonEmptyTotalProperties(percentageOfTotalProperties);

        if(totalPOIsA == 0 || totalPOIsB == 0){
            container.setValid(false);
        } else {
            container.setValid(true);
        }

        return container;
    }
    
    private StatisticResultPair countTotalEntities(){

        Integer totalA = SparqlRepository.countPOIs(LeftDataset.getLeftDataset().getModel());
        Integer totalB = SparqlRepository.countPOIs(RightDataset.getRightDataset().getModel());

        totalPOIsA = totalA;
        totalPOIsB = totalB;

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Total POIs");

        return pair;
    }
    
    private StatisticResultPair countDistinctProperties(){
        
        Integer distinctPropertiesA = SparqlRepository.countDistinctProperties(LeftDataset.getLeftDataset().getModel());
        Integer distinctPropertiesB = SparqlRepository.countDistinctProperties(RightDataset.getRightDataset().getModel());
        StatisticResultPair pair = new StatisticResultPair(distinctPropertiesA.toString(), distinctPropertiesB.toString());
        pair.setLabel("Distinct Properties");

        return pair;
    }

    private StatisticResultPair countNonEmptyNames(){

        Integer namesA = countNonEmptyProperty(Namespace.NAME_VALUE, EnumDataset.LEFT);
        Integer namesB = countNonEmptyProperty(Namespace.NAME_VALUE, EnumDataset.RIGHT);

        StatisticResultPair pair = new StatisticResultPair(namesA.toString(), namesB.toString());
        pair.setLabel("Non empty Names");
        
        return pair;
    }
    
    private StatisticResultPair countNonEmptyPhones(){

        Integer phonesA = countNonEmptyProperty(Namespace.PHONE, EnumDataset.LEFT);
        Integer phonesB = countNonEmptyProperty(Namespace.PHONE, EnumDataset.RIGHT);
        
        StatisticResultPair pair = new StatisticResultPair(phonesA.toString(), phonesB.toString());
        pair.setLabel("Non empty Phones");
        
        return pair;
    }
    
    private StatisticResultPair countNonEmptyStreets(){

        Integer streetsA = countNonEmptyProperty(Namespace.STREET, EnumDataset.LEFT);
        Integer streetsB = countNonEmptyProperty(Namespace.STREET, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(streetsA.toString(), streetsB.toString());
        pair.setLabel("Non empty Streets");
        
        return pair;
    } 
    
    private StatisticResultPair countNonEmptyStreetNumbers(){

        Integer stNumbersA = countNonEmptyProperty(Namespace.STREET_NUMBER, EnumDataset.LEFT);
        Integer stNumbersB = countNonEmptyProperty(Namespace.STREET_NUMBER, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(stNumbersA.toString(), stNumbersB.toString());
        pair.setLabel("Non empty Street Numbers");
        
        return pair;
    } 

    private StatisticResultPair countNonEmptyWebsites(){

        Integer websitesA = countNonEmptyProperty(Namespace.WEBSITE, EnumDataset.LEFT);
        Integer websitesB = countNonEmptyProperty(Namespace.WEBSITE, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString());
        pair.setLabel("Non empty Websites");
        
        return pair;
    }     

    private StatisticResultPair countNonEmptyEmails(){

        Integer websitesA = countNonEmptyProperty(Namespace.EMAIL, EnumDataset.LEFT);
        Integer websitesB = countNonEmptyProperty(Namespace.EMAIL, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString());
        pair.setLabel("Non empty Websites");
        
        return pair;
    }  
    
    private StatisticResultPair countNonEmptyDates(){
        Integer datesA = countNonEmptyProperty(Namespace.DATE, EnumDataset.LEFT);
        Integer datesB = countNonEmptyProperty(Namespace.DATE, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(datesA.toString(), datesB.toString());
        pair.setLabel("Non empty Dates");
        
        return pair;
    }
    
    private StatisticResultPair countTotalNonEmptyProperties(){
        
        Integer a1 = Integer.parseInt(container.getNonEmptyNames().getA());
        Integer a2 = Integer.parseInt(container.getNonEmptyPhones().getA());
        Integer a3 = Integer.parseInt(container.getNonEmptyStreets().getA());
        Integer a4 = Integer.parseInt(container.getNonEmptyStreetNumbers().getA());
        Integer a5 = Integer.parseInt(container.getNonEmptyWebsites().getA());
        Integer a6 = Integer.parseInt(container.getNonEmptyEmails().getA());
        Integer a7 = Integer.parseInt(container.getNonEmptyDates().getA());
        
        Integer totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;

        Integer b1 = Integer.parseInt(container.getNonEmptyNames().getB());
        Integer b2 = Integer.parseInt(container.getNonEmptyPhones().getB());
        Integer b3 = Integer.parseInt(container.getNonEmptyStreets().getB());
        Integer b4 = Integer.parseInt(container.getNonEmptyStreetNumbers().getB());
        Integer b5 = Integer.parseInt(container.getNonEmptyWebsites().getB());
        Integer b6 = Integer.parseInt(container.getNonEmptyEmails().getB());
        Integer b7 = Integer.parseInt(container.getNonEmptyDates().getB());
        
        Integer totalB = b1 + b2 + b3 + b4 + b5 + b6 + b7;

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Non empty properties");
        
        return pair;
    }    

    private StatisticResultPair countEmptyNames(){

        Integer nA = Integer.parseInt(container.getNonEmptyNames().getA());
        Integer nB = Integer.parseInt(container.getNonEmptyNames().getB());

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Names");
        
        return pair;
    }
    
    private StatisticResultPair countEmptyPhones(){

        Integer nA = Integer.parseInt(container.getNonEmptyPhones().getA());
        Integer nB = Integer.parseInt(container.getNonEmptyPhones().getB());

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Phones");
        
        return pair;
    }
    
    private StatisticResultPair countEmptyStreets(){

        Integer nA = Integer.parseInt(container.getNonEmptyStreets().getA());
        Integer nB = Integer.parseInt(container.getNonEmptyStreets().getB());

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Streets");
        
        return pair;
    } 
    
    private StatisticResultPair countEmptyStreetNumbers(){

        Integer nA = Integer.parseInt(container.getNonEmptyStreetNumbers().getA());
        Integer nB = Integer.parseInt(container.getNonEmptyStreetNumbers().getB());

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Street Numbers");
        
        return pair;
    } 

    private StatisticResultPair countEmptyWebsites(){

        Integer nA = Integer.parseInt(container.getNonEmptyWebsites().getA());
        Integer nB = Integer.parseInt(container.getNonEmptyWebsites().getB());

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Websites");
        
        return pair;
    }     

    private StatisticResultPair countEmptyEmails(){

        Integer nA = Integer.parseInt(container.getNonEmptyEmails().getA());
        Integer nB = Integer.parseInt(container.getNonEmptyEmails().getB());

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Emails");
        
        return pair;
    }  
    
    private StatisticResultPair countEmptyDates(){
        
        Integer nA = Integer.parseInt(container.getNonEmptyDates().getA());
        Integer nB = Integer.parseInt(container.getNonEmptyDates().getB());

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Dates");
        
        return pair;
    }
    
    private StatisticResultPair countTotalEmptyProperties(){
        
        Integer a1 = Integer.parseInt(container.getEmptyNames().getA());
        Integer a2 = Integer.parseInt(container.getEmptyPhones().getA());
        Integer a3 = Integer.parseInt(container.getEmptyStreets().getA());
        Integer a4 = Integer.parseInt(container.getEmptyStreetNumbers().getA());
        Integer a5 = Integer.parseInt(container.getEmptyWebsites().getA());
        Integer a6 = Integer.parseInt(container.getEmptyEmails().getA());
        Integer a7 = Integer.parseInt(container.getEmptyDates().getA());
        
        Integer totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;

        Integer b1 = Integer.parseInt(container.getEmptyNames().getB());
        Integer b2 = Integer.parseInt(container.getEmptyPhones().getB());
        Integer b3 = Integer.parseInt(container.getEmptyStreets().getB());
        Integer b4 = Integer.parseInt(container.getEmptyStreetNumbers().getB());
        Integer b5 = Integer.parseInt(container.getEmptyWebsites().getB());
        Integer b6 = Integer.parseInt(container.getEmptyEmails().getB());
        Integer b7 = Integer.parseInt(container.getEmptyDates().getB());
        
        Integer totalB = b1 + b2 + b3 + b4+ b5 + b6 + b7;

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Empty properties");
        
        return pair;
    }
    
    private StatisticResultPair calculatePercentageOfPrimaryDateFormats(){

        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();
        String date = Namespace.DATE;

        int totalDatesA = SparqlRepository.countProperty(leftModel, date);
        int totalDatesB = SparqlRepository.countProperty(rightModel, date);
        
        if(totalDatesA == 0 || totalDatesB ==0){
            StatisticResultPair pair = new StatisticResultPair("0", "0");

            pair.setLabel("Percentage of primary date formats: 0 total Dates."); 
            return pair;
        }

        IsDatePrimaryFormat isDatePrimaryFormat = new IsDatePrimaryFormat();
        
        int knownFormatCounter = 0;
        NodeIterator objectsA = SparqlRepository.getObjectsOfProperty(date, leftModel);
        while(objectsA.hasNext()){
            RDFNode node = objectsA.next();
            if(node.isLiteral()){
                String literalDate = node.asLiteral().getString();
                if(isDatePrimaryFormat.evaluate(literalDate)){
                    knownFormatCounter++;
                }       
            }
        }
        
        Double percentA = roundHalfDown(knownFormatCounter / (double) totalDatesA);
        
        knownFormatCounter = 0;

        NodeIterator objectsB = SparqlRepository.getObjectsOfProperty(date, rightModel);
        while(objectsB.hasNext()){
            RDFNode node = objectsB.next();
            if(node.isLiteral()){
                String literalDate = node.asLiteral().getString();
                if(isDatePrimaryFormat.evaluate(literalDate)){
                    knownFormatCounter++;
                }       
            }
        }

        Double percentB = roundHalfDown(knownFormatCounter / (double) totalDatesB);

        StatisticResultPair pair = new StatisticResultPair(percentA.toString(), percentB.toString());

        pair.setLabel("Percentage of primary date formats");
        return pair;
    }

    private StatisticResultPair calculateNamePercentage(){

        int namesA = SparqlRepository.countPropertyWithObject(LeftDataset.getLeftDataset().getModel(), 
                Namespace.NAME_TYPE, Namespace.OFFICIAL_LITERAL);
        int namesB = SparqlRepository.countPropertyWithObject(RightDataset.getRightDataset().getModel(), 
                Namespace.NAME_TYPE, Namespace.OFFICIAL_LITERAL);

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }

        Double percentageA = roundHalfDown((100 * namesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * namesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        pair.setLabel("Percentage of names in each dataset");

        return pair;
    }

    private StatisticResultPair calculateWebsitePercentage(){

        int websiteA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.WEBSITE);
        int websiteB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.WEBSITE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }
        
        Double percentageA = roundHalfDown((100 * websiteA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * websiteB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setLabel("Percentage of websites in each dataset");

        return pair;
    }

    private StatisticResultPair calculatePhonePercentage(){

        int phonesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.PHONE);
        int phonesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.PHONE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }
        
        Double percentageA = roundHalfDown((100 * phonesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * phonesB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setLabel("Percentage of phones in each dataset");

        return pair;
    }
    
    private StatisticResultPair calculateStreetPercentage(){

        int streetsA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.STREET);
        int streetsB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.STREET);

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }
        
        Double percentageA = roundHalfDown((100 * streetsA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * streetsB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setLabel("Percentage of streets in each dataset");

        return pair;
    }   

    private StatisticResultPair calculateStreetNumberPercentage(){

        int streetΝumbersA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.STREET_NUMBER);
        int streetNumbersB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.STREET_NUMBER);

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }

        Double percentageA = roundHalfDown((100 * streetΝumbersA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * streetNumbersB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setLabel("Percentage of street Numbers in each dataset");

        return pair;
    } 

    private StatisticResultPair calculateLocalityPercentage(){

        int localitiesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.LOCALITY);
        int localitiesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.LOCALITY);

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }

        Double percentageA = roundHalfDown((100 * localitiesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * localitiesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setLabel("Percentage of locality in each dataset");

        return pair;
    }
    
    private StatisticResultPair calculateDatePercentage(){
        int datesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.DATE);
        int datesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.DATE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            return new StatisticResultPair("-","-");
        }

        Double percentageA = roundHalfDown((100 * datesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * datesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setLabel("Percentage of dates in each dataset");

        return pair;
    }
    
    private StatisticResultPair countTriples(){

        Long totalA = LeftDataset.getLeftDataset().getModel().size();
        Long totalB = LeftDataset.getLeftDataset().getModel().size();

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Total triples");

        return pair;
    }

    private StatisticResultPair countLinkedVsUnlinked(){

        Long totalA = LeftDataset.getLeftDataset().getModel().size();
        Long totalB = LeftDataset.getLeftDataset().getModel().size();
        Long totalLinks = LinksModel.getLinksModel().getModel().size();
        
        Long total = totalA + totalB;

        StatisticResultPair pair = new StatisticResultPair(totalLinks.toString(), total.toString());
        pair.setLabel("Linked vs Unlinked entities.");

        return pair;
    }
    
    private StatisticResultPair calculateAllNonEmptyPropertiesPercentage(){
        
        Double nA = Double.parseDouble(container.getNamePercentage().getA());
        Double nB = Double.parseDouble(container.getNamePercentage().getB());
        Double pA = Double.parseDouble(container.getPhonePercentage().getA());
        Double pB = Double.parseDouble(container.getPhonePercentage().getB());
        Double sA = Double.parseDouble(container.getStreetPercentage().getA());
        Double sB = Double.parseDouble(container.getStreetPercentage().getB());
        Double snA = Double.parseDouble(container.getStreetNumberPercentage().getA());
        Double snB = Double.parseDouble(container.getStreetNumberPercentage().getB());
        Double wA = Double.parseDouble(container.getWebsitePercentage().getA());
        Double wB = Double.parseDouble(container.getWebsitePercentage().getB());
        Double lA = Double.parseDouble(container.getLocalityPercentage().getA());
        Double lB = Double.parseDouble(container.getLocalityPercentage().getB());
        Double dA = Double.parseDouble(container.getDatePercentage().getA());
        Double dB = Double.parseDouble(container.getDatePercentage().getB());

        Double totalPropPercentageA = (nA + pA + sA + snA + wA +lA + dA) / 7;
        Double totalPropPercentageB = (nB + pB + sB + snB + wB +lB + dB) / 7;

        StatisticResultPair pair = new StatisticResultPair(totalPropPercentageA.toString(), totalPropPercentageB.toString());

        pair.setLabel("Percentage of total properties in each dataset");

        return pair;
    } 
    
    private Integer countNonEmptyProperty(String property, EnumDataset dataset){
        Integer count;
        switch (dataset){
            case LEFT:
                count = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), property);
                break;
            case RIGHT:
                count = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), property);
                break;
            default:
                throw new ApplicationException("Undefined dataset value.");
        }

        return count;
    }

    private List<StatisticResultPair> computeLinkStats(){
        
        List<StatisticResultPair> linkStats = new ArrayList<>();

        Model linksModel = LinksModel.getLinksModel().getModel();

        Model modelA = LeftDataset.getLeftDataset().getModel();
        Model modelB = LeftDataset.getLeftDataset().getModel();

        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);
        
        Integer namesA = SparqlRepository.countLinkedWithProperty(linkedA, Namespace.NAME);
        Integer namesB = SparqlRepository.countLinkedWithProperty(linkedA, Namespace.NAME);
        
        StatisticResultPair pair1 = new StatisticResultPair(namesA.toString(), namesB.toString());
        pair1.setLabel("Linked non empty names");
        linkStats.add(pair1);

        return linkStats;
    }

    private Integer countNonEmptyProperty(String property1, String property2, EnumDataset dataset){
        Integer count;
        switch (dataset){
            case LEFT:
                count = SparqlRepository.countPropertyChain(LeftDataset.getLeftDataset().getModel(), property1, property2);
                break;
            case RIGHT:
                count = SparqlRepository.countPropertyChain(RightDataset.getRightDataset().getModel(), property1, property2);
                break;
            default:
                throw new ApplicationException("Undefined dataset value.");
        }
        return count;
    }    
    
    private double roundHalfDown(Double d){
        return new BigDecimal(d).setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_2, RoundingMode.DOWN).doubleValue();        
    }
    
    private boolean warn(int entitiesA, int entitiesB, String propertyName) {
        if (entitiesA == 0) {
            LOG.warn("Zero entities in dataset A. Check " + propertyName + " property.");
            return true;
        } else if (entitiesB == 0) {
            LOG.warn("Zero entities in dataset B. Check " + propertyName + " property.");
            return true;
        }
        return false;
    }
}
