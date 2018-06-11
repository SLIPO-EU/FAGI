package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.preview.statistics.StatisticsContainer;
import gr.athena.innovation.fagi.preview.statistics.StatisticsCollector;
import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
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
import java.util.HashMap;
import java.util.Map;
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

    StatisticsContainer container;

    private final Map<String, StatisticResultPair> map = new HashMap<>();

    @Override
    public StatisticsContainer collect(){

        /* IMPORTANT: The order of calculation is sensitive for avoiding re-calculations */

        container = new StatisticsContainer();
        container.setComplete(false);

        /* POIs and triples count */
        countTotalEntities();
        countTriples();

        /* Non empty properties */

        countNonEmptyNames();
        countNonEmptyPhones();
        countNonEmptyStreets();
        countNonEmptyStreetNumbers();
        countNonEmptyWebsites();
        countNonEmptyEmails();
        countNonEmptyDates();

        /* Empty properties */

        countEmptyNames();
        countEmptyPhones();
        countEmptyStreets();
        countEmptyStreetNumbers();
        countEmptyWebsites();
        countEmptyEmails();
        countEmptyDates();

        /* Distinct properties */

        countDistinctProperties();

        /* Percenteges */

        calculatePercentageOfPrimaryDateFormats();
        calculateNamePercentage();
        calculateWebsitePercentage();
        calculatePhonePercentage();
        calculateStreetPercentage();
        calculateStreetNumberPercentage();
        calculateLocalityPercentage();
        calculateDatePercentage();

        /* Statistics for linked POIs*/

        computeLinkStats();

        /* Aggregate statistics */

        countTotalNonEmptyProperties();
        countTotalEmptyProperties();
        calculateAllNonEmptyPropertiesPercentage();

        if(totalPOIsA == 0 || totalPOIsB == 0){
            container.setValid(false);
        } else {
            container.setValid(true);
        }

        container.setMap(map);
        container.setComplete(true && container.isValid());

        return container;
    }
    
    private StatisticResultPair countTotalEntities(){

        Integer totalA = SparqlRepository.countPOIs(LeftDataset.getLeftDataset().getModel());
        Integer totalB = SparqlRepository.countPOIs(RightDataset.getRightDataset().getModel());

        totalPOIsA = totalA;
        totalPOIsB = totalB;

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            map.put("totalPOIs", pair);
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Total POIs");

        map.put("totalPOIs", pair);

        return pair;
    }

    private StatisticResultPair countLinkedVsTotalPOIs(Model links){

        Integer totalA = SparqlRepository.countLinkedPOIsA(links);
        Integer totalB = SparqlRepository.countLinkedPOIsB(links);

        Integer linked = (totalA + totalB);

        Integer total = totalPOIsA + totalPOIsB;

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            map.put("linkedVsTotal", pair);
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(linked.toString(), total.toString());
        pair.setLabel("Linked vs Total POIS");

        return pair;
    }

    private StatisticResultPair countLinkedPOIs(Model linkedA, Model linkedB){

        Integer linkedPOIsA = SparqlRepository.countDistinctSubjects(linkedA);
        Integer linkedPOIsB = SparqlRepository.countDistinctObjects(linkedB);

        StatisticResultPair pair = new StatisticResultPair(linkedPOIsA.toString(), linkedPOIsB.toString());
        pair.setLabel("Linked POIs");

        return pair;
    }

    private StatisticResultPair countTotalLinkedTriples(Model linkedA, Model linkedB){

        Integer linkedTriplesA = SparqlRepository.countLinkedTriplesA(linkedA);
        Integer linkedTriplesB = SparqlRepository.countLinkedTriplesB(linkedB);

        StatisticResultPair pair = new StatisticResultPair(linkedTriplesA.toString(), linkedTriplesB.toString());
        pair.setLabel("Linked Triples");

        return pair;
    }

    private StatisticResultPair countDistinctProperties(){

        Integer distinctPropertiesA = SparqlRepository.countDistinctProperties(LeftDataset.getLeftDataset().getModel());
        Integer distinctPropertiesB = SparqlRepository.countDistinctProperties(RightDataset.getRightDataset().getModel());
        StatisticResultPair pair = new StatisticResultPair(distinctPropertiesA.toString(), distinctPropertiesB.toString());
        pair.setLabel("Distinct Properties");

        map.put("distinctProperties", pair);
        return pair;
    }

    private StatisticResultPair countNonEmptyNames(){

        Integer namesA = countNonEmptyProperty(Namespace.NAME_VALUE, EnumDataset.LEFT);
        Integer namesB = countNonEmptyProperty(Namespace.NAME_VALUE, EnumDataset.RIGHT);

        StatisticResultPair pair = new StatisticResultPair(namesA.toString(), namesB.toString());
        pair.setLabel("Non empty Names");

        map.put("nonEmptyNames", pair);
        return pair;
    }

    private StatisticResultPair countNonEmptyPhones(){

        Integer phonesA = countNonEmptyProperty(Namespace.PHONE, EnumDataset.LEFT);
        Integer phonesB = countNonEmptyProperty(Namespace.PHONE, EnumDataset.RIGHT);

        StatisticResultPair pair = new StatisticResultPair(phonesA.toString(), phonesB.toString());
        pair.setLabel("Non empty Phones");

        map.put("nonEmptyPhones", pair);
        return pair;
    }

    private StatisticResultPair countNonEmptyStreets(){

        Integer streetsA = countNonEmptyProperty(Namespace.STREET, EnumDataset.LEFT);
        Integer streetsB = countNonEmptyProperty(Namespace.STREET, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(streetsA.toString(), streetsB.toString());
        pair.setLabel("Non empty Streets");
        
        map.put("nonEmptyStreets", pair);
        return pair;
    } 
    
    private StatisticResultPair countNonEmptyStreetNumbers(){

        Integer stNumbersA = countNonEmptyProperty(Namespace.STREET_NUMBER, EnumDataset.LEFT);
        Integer stNumbersB = countNonEmptyProperty(Namespace.STREET_NUMBER, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(stNumbersA.toString(), stNumbersB.toString());
        pair.setLabel("Non empty Street Numbers");
        
        map.put("nonEmptyStreetNumbers", pair);
        return pair;
    } 

    private StatisticResultPair countNonEmptyWebsites(){

        Integer websitesA = countNonEmptyProperty(Namespace.WEBSITE, EnumDataset.LEFT);
        Integer websitesB = countNonEmptyProperty(Namespace.WEBSITE, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString());
        pair.setLabel("Non empty Websites");
        
        map.put("nonEmptyWebsites", pair);
        return pair;
    }     

    private StatisticResultPair countNonEmptyEmails(){

        Integer websitesA = countNonEmptyProperty(Namespace.EMAIL, EnumDataset.LEFT);
        Integer websitesB = countNonEmptyProperty(Namespace.EMAIL, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString());
        pair.setLabel("Non empty Emails");
        
        map.put("nonEmptyEmails", pair);
        return pair;
    }  
    
    private StatisticResultPair countNonEmptyDates(){
        Integer datesA = countNonEmptyProperty(Namespace.DATE, EnumDataset.LEFT);
        Integer datesB = countNonEmptyProperty(Namespace.DATE, EnumDataset.RIGHT);
        StatisticResultPair pair = new StatisticResultPair(datesA.toString(), datesB.toString());
        pair.setLabel("Non empty Dates");
        
        map.put("nonEmptyDates", pair);
        return pair;
    }
    
    private StatisticResultPair countTotalNonEmptyProperties(){

        Integer totalA;
        Integer totalB;

        try {

            Integer a1 = Integer.parseInt(map.get("nonEmptyNames").getA());
            Integer a2 = Integer.parseInt(map.get("nonEmptyPhones").getA());
            Integer a3 = Integer.parseInt(map.get("nonEmptyStreets").getA());
            Integer a4 = Integer.parseInt(map.get("nonEmptyStreetNumbers").getA());
            Integer a5 = Integer.parseInt(map.get("nonEmptyWebsites").getA());
            Integer a6 = Integer.parseInt(map.get("nonEmptyEmails").getA());
            Integer a7 = Integer.parseInt(map.get("nonEmptyDates").getA());

            Integer b1 = Integer.parseInt(map.get("nonEmptyNames").getB());
            Integer b2 = Integer.parseInt(map.get("nonEmptyPhones").getB());
            Integer b3 = Integer.parseInt(map.get("nonEmptyStreets").getB());
            Integer b4 = Integer.parseInt(map.get("nonEmptyStreetNumbers").getB());
            Integer b5 = Integer.parseInt(map.get("nonEmptyWebsites").getB());
            Integer b6 = Integer.parseInt(map.get("nonEmptyEmails").getB());
            Integer b7 = Integer.parseInt(map.get("nonEmptyDates").getB()); 

            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4 + b5 + b6 + b7;

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute total non empty properties due to missing properties. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Non empty properties");
        
        map.put("nonEmptyProperties", pair);
        return pair;
    }    

    private StatisticResultPair countEmptyNames(){

        Integer nA;
        Integer nB;
        
        try {
            
            nA = Integer.parseInt(map.get("nonEmptyNames").getA());
            nB = Integer.parseInt(map.get("nonEmptyNames").getB());
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute empty names due to missing property. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        }

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Names");

        map.put("emptyNames", pair);

        return pair;
    }

    private StatisticResultPair countEmptyPhones(){

        Integer nA;
        Integer nB;
        
        try {
            
            nA = Integer.parseInt(map.get("nonEmptyPhones").getA());
            nB = Integer.parseInt(map.get("nonEmptyPhones").getB());
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute empty phones due to missing property. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        }
        
        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Phones");

        map.put("emptyPhones", pair);
        return pair;
    }
    
    private StatisticResultPair countEmptyStreets(){
        Integer nA;
        Integer nB;
        
        try {
            
            nA = Integer.parseInt(map.get("nonEmptyStreets").getA());
            nB = Integer.parseInt(map.get("nonEmptyStreets").getB());
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute empty streets due to missing property. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        }        

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Streets");
        
        map.put("emptyStreets", pair);
        return pair;
    } 
    
    private StatisticResultPair countEmptyStreetNumbers(){
        Integer nA;
        Integer nB;
        
        try {
            
            nA = Integer.parseInt(map.get("nonEmptyStreetNumbers").getA());
            nB = Integer.parseInt(map.get("nonEmptyStreetNumbers").getB());
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute empty street numbers due to missing property. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Street Numbers");
        
        map.put("emptyStreetNumbers", pair);
        return pair;
    } 

    private StatisticResultPair countEmptyWebsites(){
        Integer nA;
        Integer nB;
        
        try {
            
            nA = Integer.parseInt(map.get("nonEmptyWebsites").getA());
            nB = Integer.parseInt(map.get("nonEmptyWebsites").getB());
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute empty websites due to missing property. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Websites");
        
        map.put("emptyWebsites", pair);
        return pair;
    }     

    private StatisticResultPair countEmptyEmails(){
        
        Integer nA;
        Integer nB;
        
        try{
            
            nA = Integer.parseInt(map.get("nonEmptyEmails").getA());
            nB = Integer.parseInt(map.get("nonEmptyEmails").getB());
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute empty emails due to missing property. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 


        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Emails");
        
        map.put("emptyEmails", pair);
        return pair;
    }  

    private StatisticResultPair countEmptyDates(){

        Integer nA;
        Integer nB;
        
        try{
            nA = Integer.parseInt(map.get("nonEmptyDates").getA());
            nB = Integer.parseInt(map.get("nonEmptyDates").getB());

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute empty dates due to missing property. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");

            return pair;
        }

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        pair.setLabel("Empty Dates");

        map.put("emptyDates", pair);
        return pair;
    }
    
    private StatisticResultPair countTotalEmptyProperties(){

        Integer totalA;
        Integer totalB;
                
        try{
            
            Integer a1 = Integer.parseInt(map.get("emptyNames").getA());
            Integer a2 = Integer.parseInt(map.get("emptyPhones").getA());
            Integer a3 = Integer.parseInt(map.get("emptyStreets").getA());
            Integer a4 = Integer.parseInt(map.get("emptyStreetNumbers").getA());
            Integer a5 = Integer.parseInt(map.get("emptyWebsites").getA());
            Integer a6 = Integer.parseInt(map.get("emptyEmails").getA());
            Integer a7 = Integer.parseInt(map.get("emptyDates").getA());

            Integer b1 = Integer.parseInt(map.get("emptyNames").getB());
            Integer b2 = Integer.parseInt(map.get("emptyPhones").getB());
            Integer b3 = Integer.parseInt(map.get("emptyStreets").getB());
            Integer b4 = Integer.parseInt(map.get("emptyStreetNumbers").getB());
            Integer b5 = Integer.parseInt(map.get("emptyWebsites").getB());
            Integer b6 = Integer.parseInt(map.get("emptyEmails").getB());
            Integer b7 = Integer.parseInt(map.get("emptyDates").getB());
            
            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4+ b5 + b6 + b7;
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute total empty propertis due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Empty properties");

        map.put("emptyProperties", pair);
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
            map.put("datesPercent", pair);
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
        map.put("primaryDateFormatsPercent", pair);
        return pair;
    }

    private StatisticResultPair calculateNamePercentage(){

        int namesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.NAME);
        int namesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.NAME);
        
        if(warn(totalPOIsA, totalPOIsB, Namespace.NAME)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            map.put("namesPercent", pair);
            return pair;
        }

        Double percentageA = roundHalfDown((100 * namesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * namesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        pair.setLabel("Percentage of names in each dataset");

        map.put("namesPercent", pair);
        return pair;
    }

    private StatisticResultPair calculateWebsitePercentage(){

        int websiteA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.WEBSITE);
        int websiteB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.WEBSITE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.WEBSITE)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            map.put("websitesPercent", pair);
            return new StatisticResultPair("0","0");
        }
        
        Double percentageA = roundHalfDown((100 * websiteA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * websiteB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setLabel("Percentage of websites in each dataset");

        map.put("websitesPercent", pair);
        return pair;
    }

    private StatisticResultPair calculatePhonePercentage(){

        int phonesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.PHONE);
        int phonesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.PHONE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.PHONE)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            map.put("phonesPercent", pair);            
            return new StatisticResultPair("0","0");
        }
        
        Double percentageA = roundHalfDown((100 * phonesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * phonesB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setLabel("Percentage of phones in each dataset");

        map.put("phonesPercent", pair);
        return pair;
    }
    
    private StatisticResultPair calculateStreetPercentage(){

        int streetsA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.STREET);
        int streetsB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.STREET);

        if(warn(totalPOIsA, totalPOIsB, Namespace.STREET)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            map.put("streetsPercent", pair);            
            return new StatisticResultPair("0","0");
        }
        
        Double percentageA = roundHalfDown((100 * streetsA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * streetsB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());
        
        pair.setLabel("Percentage of streets in each dataset");

        map.put("streetsPercent", pair);
        return pair;
    }   

    private StatisticResultPair calculateStreetNumberPercentage(){

        int streetΝumbersA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.STREET_NUMBER);
        int streetNumbersB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.STREET_NUMBER);

        if(warn(totalPOIsA, totalPOIsB, Namespace.STREET_NUMBER)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            map.put("streetNumbersPercent", pair);            
            return new StatisticResultPair("0","0");
        }

        Double percentageA = roundHalfDown((100 * streetΝumbersA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * streetNumbersB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setLabel("Percentage of street Numbers in each dataset");

        map.put("streetNumbersPercent", pair);
        return pair;
    } 

    private StatisticResultPair calculateLocalityPercentage(){

        int localitiesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.LOCALITY);
        int localitiesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.LOCALITY);

        if(warn(totalPOIsA, totalPOIsB, Namespace.LOCALITY)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            map.put("localityPercent", pair);            
            return new StatisticResultPair("0","0");
        }

        Double percentageA = roundHalfDown((100 * localitiesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * localitiesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setLabel("Percentage of locality in each dataset");

        map.put("localityPercent", pair);
        return pair;
    }
    
    private StatisticResultPair calculateDatePercentage(){
        int datesA = SparqlRepository.countProperty(LeftDataset.getLeftDataset().getModel(), Namespace.DATE);
        int datesB = SparqlRepository.countProperty(RightDataset.getRightDataset().getModel(), Namespace.DATE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.DATE)){
            StatisticResultPair pair = new StatisticResultPair("0","0");
            map.put("datesPercent", pair);            
            return new StatisticResultPair("0","0");
        }

        Double percentageA = roundHalfDown((100 * datesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * datesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString());

        pair.setLabel("Percentage of dates in each dataset");

        map.put("datesPercent", pair);
        return pair;
    }
    
    private StatisticResultPair countTriples(){

        Long totalA = LeftDataset.getLeftDataset().getModel().size();
        Long totalB = RightDataset.getRightDataset().getModel().size();

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Total triples");

        map.put("totalTriples", pair);
        return pair;
    }
    
    private StatisticResultPair calculateAllNonEmptyPropertiesPercentage(){

        Double totalPropPercentageA;
        Double totalPropPercentageB;
        
        try{
            
            Double nA = Double.parseDouble(map.get("namesPercent").getA());
            Double nB = Double.parseDouble(map.get("namesPercent").getB());
            Double pA = Double.parseDouble(map.get("phonesPercent").getA());
            Double pB = Double.parseDouble(map.get("phonesPercent").getB());
            Double sA = Double.parseDouble(map.get("streetsPercent").getA());
            Double sB = Double.parseDouble(map.get("streetsPercent").getB());
            Double snA = Double.parseDouble(map.get("streetNumbersPercent").getA());
            Double snB = Double.parseDouble(map.get("streetNumbersPercent").getB());
            Double wA = Double.parseDouble(map.get("websitesPercent").getA());
            Double wB = Double.parseDouble(map.get("websitesPercent").getB());
            Double lA = Double.parseDouble(map.get("localityPercent").getA());
            Double lB = Double.parseDouble(map.get("localityPercent").getB());
            Double dA = Double.parseDouble(map.get("datesPercent").getA());
            Double dB = Double.parseDouble(map.get("datesPercent").getB());

            totalPropPercentageA = (nA + pA + sA + snA + wA +lA + dA) / 7;
            totalPropPercentageB = (nB + pB + sB + snB + wB +lB + dB) / 7;

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute total non empty percentages due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalPropPercentageA.toString(), totalPropPercentageB.toString());

        pair.setLabel("Percentage of total properties in each dataset");

        map.put("totalPropertiesPercent", pair);
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

    private Integer countNonEmptyPropertyChain(String property1, String property2, EnumDataset dataset){
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

    private void computeLinkStats(){

        Model linksModel = LinksModel.getLinksModel().getModel();

        Model modelA = LeftDataset.getLeftDataset().getModel();
        Model modelB = RightDataset.getRightDataset().getModel();

        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);

        StatisticResultPair linkedPOIs = countLinkedPOIs(linkedA, linkedB);//already labeled
        StatisticResultPair linkedVsUnlinkedPOIs = countLinkedVsTotalPOIs(linksModel); //already labeled
        StatisticResultPair linkedTotalTriples = countTotalLinkedTriples(linkedA, linkedB); //already labeled

        map.put("linkedPOIs", linkedPOIs);
        map.put("linkedVsTotal", linkedVsUnlinkedPOIs);
        map.put("linkedTriples", linkedTotalTriples);
        
        StatisticResultPair pair1 = computeNonEmptyLinked(linkedA, linkedB, Namespace.NAME);
        StatisticResultPair pair2 = computeNonEmptyLinked(linkedA, linkedB, Namespace.PHONE);
        StatisticResultPair pair3 = computeNonEmptyLinked(linkedA, linkedB, Namespace.STREET);
        StatisticResultPair pair4 = computeNonEmptyLinked(linkedA, linkedB, Namespace.STREET_NUMBER);
        StatisticResultPair pair5 = computeNonEmptyLinked(linkedA, linkedB, Namespace.WEBSITE);
        StatisticResultPair pair6 = computeNonEmptyLinked(linkedA, linkedB, Namespace.EMAIL);
        StatisticResultPair pair7 = computeNonEmptyLinked(linkedA, linkedB, Namespace.DATE);

        pair1.setLabel("Linked Non Empty Names");
        pair2.setLabel("Linked Non Empty Phones");
        pair3.setLabel("Linked Non Empty Streets");
        pair4.setLabel("Linked Non Empty Street Numbers");
        pair5.setLabel("Linked Non Empty Websites");
        pair6.setLabel("Linked Non Empty Emails");
        pair7.setLabel("Linked Non Empty Dates");

        map.put("linkedNonEmptyNames", pair1);
        map.put("linkedNonEmptyPhones", pair2);
        map.put("linkedNonEmptyStreets", pair3);
        map.put("linkedNonEmptyStreetNumbers", pair4);
        map.put("linkedNonEmptyWebsites", pair5);
        map.put("linkedNonEmptyEmails", pair6);
        map.put("linkedNonEmptyDates", pair7);

    }

    private static StatisticResultPair computeNonEmptyLinked(Model linkedA, Model linkedB, String property){

        Integer nonEmptyCountA = SparqlRepository.countLinkedWithProperty(linkedA, property);
        Integer nonEmptyCountB = SparqlRepository.countLinkedWithProperty(linkedB, property);

        StatisticResultPair pair = new StatisticResultPair(nonEmptyCountA.toString(), nonEmptyCountB.toString());

        return pair;
    }

    private double roundHalfDown(Double d){
        return new BigDecimal(d).setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_2, RoundingMode.DOWN).doubleValue();        
    }

    private boolean warn(int entitiesA, int entitiesB, String propertyName) {
        if(entitiesA == 0 && entitiesB == 0){
            LOG.warn("Could not count POIs in datasets. Check " + propertyName + " property.");
            return true;            
        } else if (entitiesA == 0) {
            LOG.warn("Could not count POIs in dataset A. Check " + propertyName + " property.");
            return true;
        } else if (entitiesB == 0) {
            LOG.warn("Could not count POIs in dataset B. Check " + propertyName + " property.");
            return true;
        }
        return false;
    }

    public Map<String, StatisticResultPair> getMap() {
        return map;
    }
}
