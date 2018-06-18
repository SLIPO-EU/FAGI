package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.preview.statistics.StatisticsContainer;
import gr.athena.innovation.fagi.preview.statistics.StatisticsCollector;
import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
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

    StatisticsContainer container = new StatisticsContainer();

    private final Map<String, StatisticResultPair> map = new HashMap<>();

    @Override
    public StatisticsContainer collect(){

        /* IMPORTANT: The order of calculation is sensitive for avoiding re-calculations */

        container.setComplete(false);

        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();

        /* POIs and triples count */
        countTotalEntities(leftModel, rightModel);
        countTriples(leftModel, rightModel);

        /* Non empty properties */

        countNonEmptyNames(leftModel, rightModel);
        countNonEmptyPhones(leftModel, rightModel);
        countNonEmptyStreets(leftModel, rightModel);
        countNonEmptyStreetNumbers(leftModel, rightModel);
        countNonEmptyWebsites(leftModel, rightModel);
        countNonEmptyEmails(leftModel, rightModel);
        countNonEmptyDates(leftModel, rightModel);

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

        computeLinkStats(totalPOIsA, totalPOIsB);

        /* Aggregate statistics */

        countTotalNonEmptyProperties();
        countTotalEmptyProperties();
        calculateTotalNonEmptyPropertiesPercentage();

        /* Average statistics */
        calculateAveragePropertiesPerPOI();
        calculateAveragePropertiesOfLinkedPOIs();
        calculateAverageEmptyPropertiesOfLinkedPOIs();

        if(totalPOIsA == 0 || totalPOIsB == 0){
            container.setValid(false);
        } else {
            container.setValid(true);
        }

        container.setMap(map);
        container.setComplete(true && container.isValid());

        return container;
    }

    public StatisticResultPair countTotalEntities(Model a, Model b){

        Integer totalA = SparqlRepository.countPOIs(a);
        Integer totalB = SparqlRepository.countPOIs(b);

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
    
    public StatisticResultPair countTriples(Model a, Model b){

        Long totalA = a.size();
        Long totalB = b.size();

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Total triples");

        map.put("totalTriples", pair);
        return pair;
    }
    
    public StatisticResultPair countLinkedVsTotalPOIs(Model links, int totalPOIsA, int totalPOIsB){

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

    public StatisticResultPair countLinkedPOIs(Model links){

        Integer linkedPOIsA = SparqlRepository.countDistinctSubjects(links);
        Integer linkedPOIsB = SparqlRepository.countDistinctObjects(links);

        StatisticResultPair pair = new StatisticResultPair(linkedPOIsA.toString(), linkedPOIsB.toString());
        pair.setLabel("Linked POIs");

        return pair;
    }

    public StatisticResultPair countTotalLinkedTriples(Model linkedA, Model linkedB){

        Integer linkedTriplesA = SparqlRepository.countLinkedTriplesA(linkedA);
        Integer linkedTriplesB = SparqlRepository.countLinkedTriplesB(linkedB);

        StatisticResultPair pair = new StatisticResultPair(linkedTriplesA.toString(), linkedTriplesB.toString());
        pair.setLabel("Linked Triples");

        return pair;
    }

    public StatisticResultPair countDistinctProperties(){

        Integer distinctPropertiesA = SparqlRepository.countDistinctProperties(LeftDataset.getLeftDataset().getModel());
        Integer distinctPropertiesB = SparqlRepository.countDistinctProperties(RightDataset.getRightDataset().getModel());
        StatisticResultPair pair = new StatisticResultPair(distinctPropertiesA.toString(), distinctPropertiesB.toString());
        pair.setLabel("Distinct Properties");

        map.put("distinctProperties", pair);
        return pair;
    }

    public StatisticResultPair countNonEmptyNames(Model a, Model b){

        Integer namesA = countNonEmptyProperty(Namespace.NAME_VALUE, a);
        Integer namesB = countNonEmptyProperty(Namespace.NAME_VALUE, b);

        StatisticResultPair pair = new StatisticResultPair(namesA.toString(), namesB.toString());
        pair.setLabel("Non empty Names");

        map.put("nonEmptyNames", pair);
        return pair;
    }

    public StatisticResultPair countNonEmptyPhones(Model a, Model b){

        Integer phonesA = countNonEmptyProperty(Namespace.PHONE, a);
        Integer phonesB = countNonEmptyProperty(Namespace.PHONE, b);

        StatisticResultPair pair = new StatisticResultPair(phonesA.toString(), phonesB.toString());
        pair.setLabel("Non empty Phones");

        map.put("nonEmptyPhones", pair);
        return pair;
    }

    public StatisticResultPair countNonEmptyStreets(Model a, Model b){

        Integer streetsA = countNonEmptyProperty(Namespace.STREET, a);
        Integer streetsB = countNonEmptyProperty(Namespace.STREET, b);
        StatisticResultPair pair = new StatisticResultPair(streetsA.toString(), streetsB.toString());
        pair.setLabel("Non empty Streets");
        
        map.put("nonEmptyStreets", pair);
        return pair;
    } 
    
    public StatisticResultPair countNonEmptyStreetNumbers(Model a, Model b){

        Integer stNumbersA = countNonEmptyProperty(Namespace.STREET_NUMBER, a);
        Integer stNumbersB = countNonEmptyProperty(Namespace.STREET_NUMBER, b);
        StatisticResultPair pair = new StatisticResultPair(stNumbersA.toString(), stNumbersB.toString());
        pair.setLabel("Non empty Street Numbers");
        
        map.put("nonEmptyStreetNumbers", pair);
        return pair;
    } 

    public StatisticResultPair countNonEmptyWebsites(Model a, Model b){

        Integer websitesA = countNonEmptyProperty(Namespace.WEBSITE, a);
        Integer websitesB = countNonEmptyProperty(Namespace.WEBSITE, b);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString());
        pair.setLabel("Non empty Websites");
        
        map.put("nonEmptyWebsites", pair);
        return pair;
    }     

    public StatisticResultPair countNonEmptyEmails(Model a, Model b){

        Integer websitesA = countNonEmptyProperty(Namespace.EMAIL, a);
        Integer websitesB = countNonEmptyProperty(Namespace.EMAIL, b);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString());
        pair.setLabel("Non empty Emails");
        
        map.put("nonEmptyEmails", pair);
        return pair;
    }  
    
    public StatisticResultPair countNonEmptyDates(Model a, Model b){
        Integer datesA = countNonEmptyProperty(Namespace.DATE, a);
        Integer datesB = countNonEmptyProperty(Namespace.DATE, b);
        StatisticResultPair pair = new StatisticResultPair(datesA.toString(), datesB.toString());
        pair.setLabel("Non empty Dates");
        
        map.put("nonEmptyDates", pair);
        return pair;
    }
    
    public StatisticResultPair countTotalNonEmptyProperties(){

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

    public StatisticResultPair countEmptyNames(){

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

    public StatisticResultPair countEmptyPhones(){

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
    
    public StatisticResultPair countEmptyStreets(){
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
    
    public StatisticResultPair countEmptyStreetNumbers(){
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

    public StatisticResultPair countEmptyWebsites(){
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

    public StatisticResultPair countEmptyEmails(){
        
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

    public StatisticResultPair countEmptyDates(){

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
    
    public StatisticResultPair countTotalEmptyProperties(){

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
            LOG.warn("Could not compute total empty properties due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Empty properties");

        map.put("emptyProperties", pair);
        return pair;
    }
    
    public StatisticResultPair calculatePercentageOfPrimaryDateFormats(){

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

    public StatisticResultPair calculateNamePercentage(){

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

    public StatisticResultPair calculateWebsitePercentage(){

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

    public StatisticResultPair calculatePhonePercentage(){

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
    
    public StatisticResultPair calculateStreetPercentage(){

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

    public StatisticResultPair calculateStreetNumberPercentage(){

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

    public StatisticResultPair calculateLocalityPercentage(){

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
    
    public StatisticResultPair calculateDatePercentage(){
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
    
    public StatisticResultPair calculateTotalNonEmptyPropertiesPercentage(){

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
    
    public Integer countNonEmptyProperty(String property, Model model){
        return SparqlRepository.countProperty(model, property);
    }

    public Integer countNonEmptyPropertyChain(String property1, String property2, EnumDataset dataset){
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

    public void computeLinkStats(int totalPOIsA, int totalPOIsB){

        Model linksModel = LinksModel.getLinksModel().getModel();

        Model modelA = LeftDataset.getLeftDataset().getModel();
        Model modelB = RightDataset.getRightDataset().getModel();

        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);

        StatisticResultPair linkedPOIs = countLinkedPOIs(linksModel);//already labeled
        StatisticResultPair linkedVsUnlinkedPOIs = countLinkedVsTotalPOIs(linksModel, totalPOIsA, totalPOIsB); //already labeled
        StatisticResultPair linkedTotalTriples = countTotalLinkedTriples(linkedA, linkedB); //already labeled

        map.put("linkedPOIs", linkedPOIs);
        map.put("linkedVsTotal", linkedVsUnlinkedPOIs);
        map.put("linkedTriples", linkedTotalTriples);

        StatisticResultPair pair1 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.NAME);
        StatisticResultPair pair2 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.PHONE);
        StatisticResultPair pair3 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.STREET);
        StatisticResultPair pair4 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.STREET_NUMBER);
        StatisticResultPair pair5 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.WEBSITE);
        StatisticResultPair pair6 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.EMAIL);
        StatisticResultPair pair7 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.DATE);

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
        
        Integer namesA = Integer.parseInt(pair1.getA());
        Integer namesB = Integer.parseInt(pair1.getB());
        Integer phonesA = Integer.parseInt(pair2.getA());
        Integer phonesB = Integer.parseInt(pair2.getB());
        Integer streetsA = Integer.parseInt(pair3.getA());
        Integer streetsB = Integer.parseInt(pair3.getB());
        Integer streetNumbersA = Integer.parseInt(pair4.getA());
        Integer streetNumbersB = Integer.parseInt(pair4.getB());
        Integer websitesA = Integer.parseInt(pair5.getA());
        Integer websitesB = Integer.parseInt(pair5.getB());
        Integer emailsA = Integer.parseInt(pair6.getA());
        Integer emailsB = Integer.parseInt(pair6.getB());
        Integer datesA = Integer.parseInt(pair7.getA());
        Integer datesB = Integer.parseInt(pair7.getB());
        
        StatisticResultPair linkedEmptyNames = computeEmptyLinkedProperty(namesA, namesB);
        StatisticResultPair linkedEmptyPhones = computeEmptyLinkedProperty(phonesA, phonesB);
        StatisticResultPair linkedEmptyStreets = computeEmptyLinkedProperty(streetsA, streetsB);
        StatisticResultPair linkedEmptyStreetNumbers = computeEmptyLinkedProperty(streetNumbersA, streetNumbersB);
        StatisticResultPair linkedEmptyWebsites = computeEmptyLinkedProperty(websitesA, websitesB);
        StatisticResultPair linkedEmptyEmails = computeEmptyLinkedProperty(emailsA, emailsB);
        StatisticResultPair linkedEmptyDates = computeEmptyLinkedProperty(datesA, datesB);

        linkedEmptyNames.setLabel("Linked Empty Names");
        linkedEmptyPhones.setLabel("Linked Empty Phones");
        linkedEmptyStreets.setLabel("Linked Empty Streets");
        linkedEmptyStreetNumbers.setLabel("Linked Empty Street Numbers");
        linkedEmptyWebsites.setLabel("Linked Empty Websites");
        linkedEmptyEmails.setLabel("Linked Empty Emails");
        linkedEmptyDates.setLabel("Linked Empty Dates");
        
        map.put("linkedEmptyNames", linkedEmptyNames);
        map.put("linkedEmptyPhones", linkedEmptyPhones);
        map.put("linkedEmptyStreets", linkedEmptyStreets);
        map.put("linkedEmptyStreetNumbers", linkedEmptyStreetNumbers);
        map.put("linkedEmptyWebsites", linkedEmptyWebsites);
        map.put("linkedEmptyEmails", linkedEmptyEmails);
        map.put("linkedEmptyDates", linkedEmptyDates);
   
        StatisticResultPair totalNonEmptyLinked = computeNonEmptyLinkedTotalProperties();
        map.put("linkedNonEmptyProperties", totalNonEmptyLinked); //already labeled

        StatisticResultPair totalEmptyLinked = computeEmptyLinkedTotalProperties();
        map.put("linkedEmptyProperties", totalEmptyLinked); //already labeled

    }

    public static StatisticResultPair computeNonEmptyLinkedProperty(Model linkedA, Model linkedB, String property){

        Integer nonEmptyCountA = SparqlRepository.countLinkedWithPropertyA(linkedA, property);
        Integer nonEmptyCountB = SparqlRepository.countLinkedWithPropertyB(linkedB, property);

        StatisticResultPair pair = new StatisticResultPair(nonEmptyCountA.toString(), nonEmptyCountB.toString());

        return pair;
    }

    public StatisticResultPair computeEmptyLinkedProperty(Integer nonEmptyA, Integer nonEmptyB){

        Integer emptyA = totalPOIsA - nonEmptyA;
        Integer emptyB = totalPOIsB - nonEmptyB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString());
        return pair;
    }
    
    public StatisticResultPair computeNonEmptyLinkedTotalProperties(){
        Integer totalA;
        Integer totalB;
                
        try{
            
            Integer a1 = Integer.parseInt(map.get("linkedNonEmptyNames").getA());
            Integer a2 = Integer.parseInt(map.get("linkedNonEmptyPhones").getA());
            Integer a3 = Integer.parseInt(map.get("linkedNonEmptyStreets").getA());
            Integer a4 = Integer.parseInt(map.get("linkedNonEmptyStreetNumbers").getA());
            Integer a5 = Integer.parseInt(map.get("linkedNonEmptyWebsites").getA());
            Integer a6 = Integer.parseInt(map.get("linkedNonEmptyEmails").getA());
            Integer a7 = Integer.parseInt(map.get("linkedNonEmptyDates").getA());

            Integer b1 = Integer.parseInt(map.get("linkedNonEmptyNames").getB());
            Integer b2 = Integer.parseInt(map.get("linkedNonEmptyPhones").getB());
            Integer b3 = Integer.parseInt(map.get("linkedNonEmptyStreets").getB());
            Integer b4 = Integer.parseInt(map.get("linkedNonEmptyStreetNumbers").getB());
            Integer b5 = Integer.parseInt(map.get("linkedNonEmptyWebsites").getB());
            Integer b6 = Integer.parseInt(map.get("linkedNonEmptyEmails").getB());
            Integer b7 = Integer.parseInt(map.get("linkedNonEmptyDates").getB());

            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4+ b5 + b6 + b7;
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute linked non empty properties due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString());
        pair.setLabel("Linked Non Empty properties");

        return pair;
    }

    public StatisticResultPair computeEmptyLinkedTotalProperties(){
        Integer totalNonEmptyA;
        Integer totalNonEmptyB;
        Integer totalA;
        Integer totalB;
        Integer totalEmptyA;
        Integer totalEmptyB;

        try{
            
            totalNonEmptyA = Integer.parseInt(map.get("linkedNonEmptyProperties").getA());
            totalNonEmptyB = Integer.parseInt(map.get("linkedNonEmptyProperties").getB());
            
            totalA = Integer.parseInt(map.get("linkedTriples").getA());
            totalB = Integer.parseInt(map.get("linkedTriples").getB());
            
            totalEmptyA = totalA - totalNonEmptyA;
            totalEmptyB = totalB - totalNonEmptyB;
            
           
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute linked empty properties due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0");
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalEmptyA.toString(), totalEmptyB.toString());
        pair.setLabel("Linked Empty properties");

        return pair;
    }

    public void calculateAveragePropertiesPerPOI() {

        StatisticResultPair distinctProperties = map.get("distinctProperties");

        int distinctPropertiesA = Integer.parseInt(distinctProperties.getA());
        int distinctPropertiesB = Integer.parseInt(distinctProperties.getB());

        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();

        Double averagePropertiesA = SparqlRepository.averagePropertiesPerPOI(leftModel, distinctPropertiesA)[0];
        Double averagePropertiesB = SparqlRepository.averagePropertiesPerPOI(rightModel, distinctPropertiesB)[0];

        Double averageEmptyPropertiesA = SparqlRepository.averagePropertiesPerPOI(leftModel, distinctPropertiesA)[1];
        Double averageEmptyPropertiesB = SparqlRepository.averagePropertiesPerPOI(rightModel, distinctPropertiesB)[1];
        
        StatisticResultPair averageProperties 
                = new StatisticResultPair(averagePropertiesA.toString(), averagePropertiesB.toString());

        averageProperties.setLabel("Average number of properties per POI");
        map.put("averageProperties", averageProperties);
        
        StatisticResultPair averageEmptyProperties 
                = new StatisticResultPair(averageEmptyPropertiesA.toString(), averageEmptyPropertiesB.toString());

        averageProperties.setLabel("Average number of empty properties per POI");
        map.put("averageEmptyProperties", averageEmptyProperties);        

    }

    public void calculateAveragePropertiesOfLinkedPOIs() {

        Model left = LeftDataset.getLeftDataset().getModel();
        Model right = RightDataset.getRightDataset().getModel();

        List<Link> links = LinksModel.getLinksModel().getLinks();

        int sumA = 0;
        int sumB = 0;

        for(Link link : links){

            String nodeA = link.getNodeA();
            String nodeB = link.getNodeB();

            int propertiesA = SparqlRepository.countDistinctPropertiesOfResource(left, nodeA);
            int propertiesB = SparqlRepository.countDistinctPropertiesOfResource(right, nodeB);

            sumA = sumA + propertiesA;
            sumB = sumB + propertiesB;

        }
        
        Double averageLinkedPropertiesA = sumA / (double)totalPOIsA;
        Double averageLinkedPropertiesB = sumB / (double)totalPOIsB;
        
        StatisticResultPair averageLinkedProperties 
                = new StatisticResultPair(averageLinkedPropertiesA.toString(), averageLinkedPropertiesB.toString());
        
        averageLinkedProperties.setLabel("Average number of properties of linked POI");
        map.put("averageLinkedProperties", averageLinkedProperties);        
    }

    public void calculateAverageEmptyPropertiesOfLinkedPOIs() {
        
        StatisticResultPair nonEmptyProperties = map.get("nonEmptyProperties");
        
        int a = Integer.parseInt(nonEmptyProperties.getA());
        int b = Integer.parseInt(nonEmptyProperties.getB());
        
        Double avgA = a /(double)totalPOIsA;
        Double avgB = b /(double)totalPOIsB;
        
        StatisticResultPair averageLinkedProperties = new StatisticResultPair(avgA.toString(), avgB.toString());
        
        averageLinkedProperties.setLabel("Average number of empty properties of linked POIs");
        map.put("averageLinkedEmptyProperties", averageLinkedProperties);            
        
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

    public void setTotalPOIsA(int totalPOIsA) {
        this.totalPOIsA = totalPOIsA;
    }

    public void setTotalPOIsB(int totalPOIsB) {
        this.totalPOIsB = totalPOIsB;
    }
}
