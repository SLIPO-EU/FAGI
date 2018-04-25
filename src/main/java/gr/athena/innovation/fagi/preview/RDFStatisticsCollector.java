package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class RDFStatisticsCollector implements StatisticsCollector{

    private static final Logger logger = LogManager.getLogger(RDFStatisticsCollector.class);
    
    private int totalEntitiesA;
    private int totalEntitiesB;
    
    private int sumA = 0;
    private int sumB = 0;
    
    StatisticsContainer container;

    @Override
    public StatisticsContainer collect(){

        container = new StatisticsContainer();

        StatisticResultPair totalEntities = countTotalEntities();
        container.setTotalEntities(totalEntities); 
        
        StatisticResultPair distinctProperties = countDistinctProperties();
        container.setDistinctProperties(distinctProperties);
        
        StatisticResultPair nonEmptyDates = countNonEmptyDates();
        container.setNonEmptyDates(nonEmptyDates);
        
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

        StatisticResultPair percentageOfTotalProperties = calculateAllNonEmptyPropertiesPercentage();
        container.setNonEmptyTotalProperties(percentageOfTotalProperties);

        if(totalEntitiesA == 0 || totalEntitiesB == 0){
            container.setValid(false);
        } else {
            container.setValid(true);
        }
        
        return container;
    }
    
    private StatisticResultPair countTotalEntities(){

        //count total entities using a the lat property. 
        Integer totalA = SparqlRepository.countPOIs(LeftDataset.getLeftDataset().getModel());
        Integer totalB = SparqlRepository.countPOIs(RightDataset.getRightDataset().getModel());

        totalEntitiesA = totalA;
        totalEntitiesB = totalB;

        if(warn(totalEntitiesA, totalEntitiesB, Namespace.SOURCE)){
            return new StatisticResultPair("0","0");
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setName("Total POIs");

        return pair;
    }
    
    private StatisticResultPair countDistinctProperties(){
        
        Integer distinctPropertiesA = SparqlRepository.countDistinctProperties(LeftDataset.getLeftDataset().getModel());
        Integer distinctPropertiesB = SparqlRepository.countDistinctProperties(RightDataset.getRightDataset().getModel());
        StatisticResultPair pair = new StatisticResultPair(distinctPropertiesA.toString(), distinctPropertiesB.toString());
        pair.setName("Distinct Properties");

        return pair;
    }

    private StatisticResultPair countNonEmptyDates(){

        Integer datesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.DATE);
        Integer datesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.DATE);
        StatisticResultPair pair = new StatisticResultPair(datesA.toString(), datesB.toString());
        pair.setName("Non empty Dates");
        
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

            pair.setName("Percentage of primary date formats: 0 total Dates."); 
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

        pair.setName("Percentage of primary date formats");
        return pair;
    }

    private StatisticResultPair calculateNamePercentage(){

        //count total entities using a the lat property. 
        int namesA = SparqlRepository.countPropertyWithObject(LeftDataset.getLeftDataset().getModel(), 
                Namespace.NAME_TYPE, Namespace.OFFICIAL_LITERAL);
        int namesB = SparqlRepository.countPropertyWithObject(RightDataset.getRightDataset().getModel(), 
                Namespace.NAME_TYPE, Namespace.OFFICIAL_LITERAL);

        if(warn(totalEntitiesA, totalEntitiesB, Namespace.SOURCE)){
            return new StatisticResultPair("0","0");
        }

        Double percentageA = roundHalfDown((100 * namesA) / (double) totalEntitiesA);
        Double percentageB = roundHalfDown((100 * namesB) / (double) totalEntitiesB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        pair.setName("Percentage of names in each dataset");

        return pair;
    }

    private StatisticResultPair calculateWebsitePercentage(){

        //count total entities using a the lat property. 
        int websiteA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.WEBSITE);
        int websiteB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.WEBSITE);

        if(warn(totalEntitiesA, totalEntitiesB, Namespace.SOURCE)){
            return new StatisticResultPair("0","0");
        }
        
        Double percentageA = roundHalfDown((100 * websiteA) / (double) totalEntitiesA);
        Double percentageB = roundHalfDown((100 * websiteB) / (double) totalEntitiesB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setName("Percentage of websites in each dataset");

        return pair;
    }

    private StatisticResultPair calculatePhonePercentage(){

        //count total entities using a the lat property. 
        int phonesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.PHONE);
        int phonesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.PHONE);

        if(warn(totalEntitiesA, totalEntitiesB, Namespace.SOURCE)){
            return new StatisticResultPair("0","0");
        }
        
        Double percentageA = roundHalfDown((100 * phonesA) / (double) totalEntitiesA);
        Double percentageB = roundHalfDown((100 * phonesB) / (double) totalEntitiesB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setName("Percentage of phones in each dataset");

        return pair;
    }
    
    private StatisticResultPair calculateStreetPercentage(){

        //count total entities using a the lat property. 
        int streetsA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.STREET);
        int streetsB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.STREET);

        if(warn(totalEntitiesA, totalEntitiesB, Namespace.SOURCE)){
            return new StatisticResultPair("0","0");
        }
        
        Double percentageA = roundHalfDown((100 * streetsA) / (double) totalEntitiesA);
        Double percentageB = roundHalfDown((100 * streetsB) / (double) totalEntitiesB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setName("Percentage of streets in each dataset");

        return pair;
    }   

    private StatisticResultPair calculateStreetNumberPercentage(){

        //count total entities using a the lat property. 
        int streetΝumbersA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.STREET_NUMBER);
        int streetNumbersB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.STREET_NUMBER);

        if(warn(totalEntitiesA, totalEntitiesB, Namespace.SOURCE)){
            return new StatisticResultPair("0","0");
        }

        Double percentageA = roundHalfDown((100 * streetΝumbersA) / (double) totalEntitiesA);
        Double percentageB = roundHalfDown((100 * streetNumbersB) / (double) totalEntitiesB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setName("Percentage of street Numbers in each dataset");

        return pair;
    } 

    private StatisticResultPair calculateLocalityPercentage(){

        //count total entities using a the lat property. 
        int localitiesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.LOCALITY);
        int localitiesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.LOCALITY);

        if(warn(totalEntitiesA, totalEntitiesB, Namespace.SOURCE)){
            return new StatisticResultPair("0","0");
        }

        Double percentageA = roundHalfDown((100 * localitiesA) / (double) totalEntitiesA);
        Double percentageB = roundHalfDown((100 * localitiesB) / (double) totalEntitiesB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setName("Percentage of locality in each dataset");

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
        Double dA = Double.parseDouble(container.getNonEmptyDates().getA());
        Double dB = Double.parseDouble(container.getNonEmptyDates().getB());
        
        Double totalPropPercentageA = (nA + pA + sA + snA + wA +lA + dA) / 7;
        Double totalPropPercentageB = (nB + pB + sB + snB + wB +lB + dB) / 7;
        
        StatisticResultPair pair = new StatisticResultPair(totalPropPercentageA.toString(), totalPropPercentageB.toString());

        pair.setName("Percentage of total properties in each dataset");
        
        return pair;
    } 
    
    private double roundHalfDown(Double d){
        return new BigDecimal(d).setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_2, RoundingMode.DOWN).doubleValue();        
    }
    
    private boolean warn(int entitiesA, int entitiesB, String propertyName) {
        if (entitiesA == 0) {
            logger.warn("Zero entities in dataset A. Check " + propertyName + " property.");
            return true;
        } else if (entitiesB == 0) {
            logger.warn("Zero entities in dataset B. Check " + propertyName + " property.");
            return true;
        }
        return false;
    }
}
