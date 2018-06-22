package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.preview.statistics.StatisticsContainer;
import gr.athena.innovation.fagi.preview.statistics.StatisticsCollector;
import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.EnumEntity;
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
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Literal;
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
        List<Link> links = LinksModel.getLinksModel().getLinks();

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

        countDistinctProperties(leftModel, rightModel);

        /* Percenteges */

        calculatePercentageOfPrimaryDateFormats(leftModel, rightModel);
        calculateNamePercentage(leftModel, rightModel);
        calculateWebsitePercentage(leftModel, rightModel);
        calculatePhonePercentage(leftModel, rightModel);
        calculateStreetPercentage(leftModel, rightModel);
        calculateStreetNumberPercentage(leftModel, rightModel);
        calculateLocalityPercentage(leftModel, rightModel);
        calculateDatePercentage(leftModel, rightModel);

        /* Statistics for linked POIs*/

        computeLinkStats(totalPOIsA, totalPOIsB);

        /* Aggregate statistics */

        countTotalNonEmptyProperties();
        countTotalEmptyProperties();
        calculateTotalNonEmptyPropertiesPercentage();

        /* Average statistics */
        calculateAveragePropertiesPerPOI(leftModel, rightModel);
        calculateStatsPerLink(leftModel, rightModel, links);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            map.put("totalPOIs", pair);
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setLabel("Total POIs");

        map.put("totalPOIs", pair);

        return pair;
    }
    
    public StatisticResultPair countTriples(Model a, Model b){

        Long totalA = a.size();
        Long totalB = b.size();

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            map.put("linkedVsTotal", pair);
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(linked.toString(), total.toString(), null);
        pair.setLabel("Linked vs Total POIS");

        return pair;
    }

    public StatisticResultPair countLinkedPOIs(Model links){

        Integer linkedPOIsA = SparqlRepository.countDistinctSubjects(links);
        Integer linkedPOIsB = SparqlRepository.countDistinctObjects(links);

        StatisticResultPair pair = new StatisticResultPair(linkedPOIsA.toString(), linkedPOIsB.toString(), null);
        pair.setLabel("Linked POIs");

        return pair;
    }

    public StatisticResultPair countTotalLinkedTriples(Model linkedA, Model linkedB){

        Integer linkedTriplesA = SparqlRepository.countLinkedTriplesA(linkedA);
        Integer linkedTriplesB = SparqlRepository.countLinkedTriplesB(linkedB);

        StatisticResultPair pair = new StatisticResultPair(linkedTriplesA.toString(), linkedTriplesB.toString(), null);
        pair.setLabel("Linked Triples");

        return pair;
    }

    public StatisticResultPair countDistinctProperties(Model a, Model b){

        Integer distinctPropertiesA = SparqlRepository.countDistinctProperties(a);
        Integer distinctPropertiesB = SparqlRepository.countDistinctProperties(b);
        StatisticResultPair pair = new StatisticResultPair(distinctPropertiesA.toString(), distinctPropertiesB.toString(), null);
        pair.setLabel("Distinct Properties");

        map.put("distinctProperties", pair);
        return pair;
    }

    public StatisticResultPair countNonEmptyNames(Model a, Model b){

        Integer namesA = countNonEmptyProperty(Namespace.NAME_VALUE, a);
        Integer namesB = countNonEmptyProperty(Namespace.NAME_VALUE, b);

        StatisticResultPair pair = new StatisticResultPair(namesA.toString(), namesB.toString(), null);
        pair.setLabel("Non empty Names");

        map.put("nonEmptyNames", pair);
        return pair;
    }

    public StatisticResultPair countNonEmptyPhones(Model a, Model b){

        Integer phonesA = countNonEmptyProperty(Namespace.PHONE, a);
        Integer phonesB = countNonEmptyProperty(Namespace.PHONE, b);

        StatisticResultPair pair = new StatisticResultPair(phonesA.toString(), phonesB.toString(), null);
        pair.setLabel("Non empty Phones");

        map.put("nonEmptyPhones", pair);
        return pair;
    }

    public StatisticResultPair countNonEmptyStreets(Model a, Model b){

        Integer streetsA = countNonEmptyProperty(Namespace.STREET, a);
        Integer streetsB = countNonEmptyProperty(Namespace.STREET, b);
        StatisticResultPair pair = new StatisticResultPair(streetsA.toString(), streetsB.toString(), null);
        pair.setLabel("Non empty Streets");
        
        map.put("nonEmptyStreets", pair);
        return pair;
    } 
    
    public StatisticResultPair countNonEmptyStreetNumbers(Model a, Model b){

        Integer stNumbersA = countNonEmptyProperty(Namespace.STREET_NUMBER, a);
        Integer stNumbersB = countNonEmptyProperty(Namespace.STREET_NUMBER, b);
        StatisticResultPair pair = new StatisticResultPair(stNumbersA.toString(), stNumbersB.toString(), null);
        pair.setLabel("Non empty Street Numbers");
        
        map.put("nonEmptyStreetNumbers", pair);
        return pair;
    } 

    public StatisticResultPair countNonEmptyWebsites(Model a, Model b){

        Integer websitesA = countNonEmptyProperty(Namespace.HOMEPAGE, a);
        Integer websitesB = countNonEmptyProperty(Namespace.HOMEPAGE, b);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString(), null);
        pair.setLabel("Non empty Websites");
        
        map.put("nonEmptyWebsites", pair);
        return pair;
    }     

    public StatisticResultPair countNonEmptyEmails(Model a, Model b){

        Integer websitesA = countNonEmptyProperty(Namespace.EMAIL, a);
        Integer websitesB = countNonEmptyProperty(Namespace.EMAIL, b);
        StatisticResultPair pair = new StatisticResultPair(websitesA.toString(), websitesB.toString(), null);
        pair.setLabel("Non empty Emails");
        
        map.put("nonEmptyEmails", pair);
        return pair;
    }  
    
    public StatisticResultPair countNonEmptyDates(Model a, Model b){
        Integer datesA = countNonEmptyProperty(Namespace.DATE, a);
        Integer datesB = countNonEmptyProperty(Namespace.DATE, b);
        StatisticResultPair pair = new StatisticResultPair(datesA.toString(), datesB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        }

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        }
        
        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        }        

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        } 


        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");

            return pair;
        }

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setLabel("Empty properties");

        map.put("emptyProperties", pair);
        return pair;
    }
    
    public StatisticResultPair calculatePercentageOfPrimaryDateFormats(Model leftModel, Model rightModel){

        String date = Namespace.DATE;

        int totalDatesA = SparqlRepository.countProperty(leftModel, date);
        int totalDatesB = SparqlRepository.countProperty(rightModel, date);
        
        if(totalDatesA == 0 || totalDatesB ==0){
            StatisticResultPair pair = new StatisticResultPair("0", "0", null);

            pair.setLabel("Percentage of primary date formats");
            map.put("datesPercent", pair);
            return pair;
        }

        IsDatePrimaryFormat isDatePrimaryFormat = new IsDatePrimaryFormat();
        
        int primaryFormatCounter = 0;
        NodeIterator objectsA = SparqlRepository.getObjectsOfProperty(date, leftModel);
        while(objectsA.hasNext()){
            RDFNode node = objectsA.next();
            if(node.isLiteral()){
                String literalDate = node.asLiteral().getString();
                if(isDatePrimaryFormat.evaluate(literalDate)){
                    primaryFormatCounter++;
                }       
            }
        }
        
        Double percentA = roundHalfDown(primaryFormatCounter / (double) totalDatesA);
        
        primaryFormatCounter = 0;

        NodeIterator objectsB = SparqlRepository.getObjectsOfProperty(date, rightModel);
        while(objectsB.hasNext()){
            RDFNode node = objectsB.next();
            if(node.isLiteral()){
                String literalDate = node.asLiteral().getString();
                if(isDatePrimaryFormat.evaluate(literalDate)){
                    primaryFormatCounter++;
                }       
            }
        }

        Double percentB = roundHalfDown(primaryFormatCounter / (double) totalDatesB);

        StatisticResultPair pair = new StatisticResultPair(percentA.toString(), percentB.toString(), null);

        pair.setLabel("Percentage of primary date formats");
        map.put("primaryDateFormatsPercent", pair);
        return pair;
    }

    public StatisticResultPair calculateNamePercentage(Model a, Model b){

        int namesA = SparqlRepository.countProperty(a, Namespace.NAME);
        int namesB = SparqlRepository.countProperty(b, Namespace.NAME);
        
        if(warn(totalPOIsA, totalPOIsB, Namespace.NAME)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            map.put("namesPercent", pair);
            return pair;
        }

        Double percentageA = roundHalfDown((100 * namesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * namesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);
        pair.setLabel("Percentage of names in each dataset");

        map.put("namesPercent", pair);
        return pair;
    }

    public StatisticResultPair calculateWebsitePercentage(Model a, Model b){

        int websiteA = SparqlRepository.countProperty(a, Namespace.HOMEPAGE);
        int websiteB = SparqlRepository.countProperty(b, Namespace.HOMEPAGE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.HOMEPAGE)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            map.put("websitesPercent", pair);
            return pair;
        }
        
        Double percentageA = roundHalfDown((100 * websiteA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * websiteB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);
        
        pair.setLabel("Percentage of websites in each dataset");

        map.put("websitesPercent", pair);
        return pair;
    }

    public StatisticResultPair calculatePhonePercentage(Model a, Model b){

        int phonesA = SparqlRepository.countProperty(a, Namespace.PHONE);
        int phonesB = SparqlRepository.countProperty(b, Namespace.PHONE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.PHONE)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            map.put("phonesPercent", pair);            
            return pair;
        }
        
        Double percentageA = roundHalfDown((100 * phonesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * phonesB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);
        
        pair.setLabel("Percentage of phones in each dataset");

        map.put("phonesPercent", pair);
        return pair;
    }
    
    public StatisticResultPair calculateStreetPercentage(Model a, Model b){

        int streetsA = SparqlRepository.countProperty(a, Namespace.STREET);
        int streetsB = SparqlRepository.countProperty(b, Namespace.STREET);

        if(warn(totalPOIsA, totalPOIsB, Namespace.STREET)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            map.put("streetsPercent", pair);            
            return new StatisticResultPair("0","0", null);
        }
        
        Double percentageA = roundHalfDown((100 * streetsA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * streetsB) / (double) totalPOIsB);
        
        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);
        
        pair.setLabel("Percentage of streets in each dataset");

        map.put("streetsPercent", pair);
        return pair;
    }   

    public StatisticResultPair calculateStreetNumberPercentage(Model a, Model b){

        int streetΝumbersA = SparqlRepository.countProperty(a, Namespace.STREET_NUMBER);
        int streetNumbersB = SparqlRepository.countProperty(b, Namespace.STREET_NUMBER);

        if(warn(totalPOIsA, totalPOIsB, Namespace.STREET_NUMBER)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            map.put("streetNumbersPercent", pair);            
            return new StatisticResultPair("0","0", null);
        }

        Double percentageA = roundHalfDown((100 * streetΝumbersA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * streetNumbersB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);

        pair.setLabel("Percentage of street numbers in each dataset");

        map.put("streetNumbersPercent", pair);
        return pair;
    } 

    public StatisticResultPair calculateLocalityPercentage(Model a, Model b){

        int localitiesA = SparqlRepository.countProperty(a, Namespace.LOCALITY);
        int localitiesB = SparqlRepository.countProperty(b, Namespace.LOCALITY);

        if(warn(totalPOIsA, totalPOIsB, Namespace.LOCALITY)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            map.put("localityPercent", pair);            
            return pair;
        }

        Double percentageA = roundHalfDown((100 * localitiesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * localitiesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);

        pair.setLabel("Percentage of locality in each dataset");

        map.put("localityPercent", pair);
        return pair;
    }
    
    public StatisticResultPair calculateDatePercentage(Model a, Model b){
        int datesA = SparqlRepository.countProperty(a, Namespace.DATE);
        int datesB = SparqlRepository.countProperty(b, Namespace.DATE);

        if(warn(totalPOIsA, totalPOIsB, Namespace.DATE)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            map.put("datesPercent", pair);            
            return new StatisticResultPair("0","0", null);
        }

        Double percentageA = roundHalfDown((100 * datesA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * datesB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);

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

            totalPropPercentageA = roundHalfDown((nA + pA + sA + snA + wA +lA + dA) / 7);
            totalPropPercentageB = roundHalfDown((nB + pB + sB + snB + wB +lB + dB) / 7);

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute total non empty percentages due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");

            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalPropPercentageA.toString(), totalPropPercentageB.toString(), null);

        pair.setLabel("Percentage of total properties in each dataset");

        map.put("totalPropertiesPercent", pair);

        return pair;
    }

    private Integer countNonEmptyProperty(String property, Model model){
        return SparqlRepository.countProperty(model, property);
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

        StatisticResultPair pair1 = computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.NAME, Namespace.NAME_VALUE);
        StatisticResultPair pair2 = computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.PHONE, Namespace.CONTACT_VALUE);
        StatisticResultPair pair3 = computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET);
        StatisticResultPair pair4 = computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET_NUMBER);
        StatisticResultPair pair5 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.HOMEPAGE);
        StatisticResultPair pair6 = computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.EMAIL, Namespace.CONTACT_VALUE);
        StatisticResultPair pair7 = computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.DATE);

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

    public static StatisticResultPair computeNonEmptyLinkedPropertyChain(Model linkedA, Model linkedB, String property1, String property2){

        Integer nonEmptyCountA = SparqlRepository.countLinkedWithPropertyA(linkedA, property1, property2);
        Integer nonEmptyCountB = SparqlRepository.countLinkedWithPropertyB(linkedB, property1, property2);

        StatisticResultPair pair = new StatisticResultPair(nonEmptyCountA.toString(), nonEmptyCountB.toString(), null);
        
        switch(property1){
            case Namespace.NAME:
                pair.setLabel("Linked Non Empty Names");
                break;
            case Namespace.PHONE:
                pair.setLabel("Linked Non Empty Phones");
                break;    
            case Namespace.ADDRESS:
                if(property2.equals(Namespace.STREET)){
                    pair.setLabel("Linked Non Empty Streets");
                } else if(property2.equals(Namespace.STREET_NUMBER)){
                    pair.setLabel("Linked Non Empty Street Numbers");
                } else {
                    throw new ApplicationException("Wrong property parameters. " + Namespace.ADDRESS 
                        + " parent does not have " + Namespace.STREET + " or " + Namespace.STREET_NUMBER + " child.");
                }
                break; 
            case Namespace.HOMEPAGE:
                pair.setLabel("Linked Non Empty Websites");
                break; 
            case Namespace.EMAIL:
                pair.setLabel("Linked Non Empty Emails");
                break; 
            case Namespace.DATE:
                pair.setLabel("Linked Non Empty Dates");
                break;
            default:
                throw new ApplicationException("Property not supported for stats. " + property1);
        }
        
        return pair;
    }

    public static StatisticResultPair computeNonEmptyLinkedProperty(Model linkedA, Model linkedB, String property){

        Integer nonEmptyCountA = SparqlRepository.countLinkedWithPropertyA(linkedA, property);
        Integer nonEmptyCountB = SparqlRepository.countLinkedWithPropertyB(linkedB, property);

        StatisticResultPair pair = new StatisticResultPair(nonEmptyCountA.toString(), nonEmptyCountB.toString(), null);
        
        switch(property){
            case Namespace.HOMEPAGE:
                pair.setLabel("Linked Non Empty Websites");
                break;
            case Namespace.DATE:
                pair.setLabel("Linked Non Empty Dates");
                break;                
            default:
                throw new ApplicationException("Property not supported for stats. " + property);
        }
        
        return pair;
    }
    
    public StatisticResultPair computeEmptyLinkedProperty(Integer nonEmptyA, Integer nonEmptyB){

        StatisticResultPair linkedPOIs = map.get("linkedPOIs");
        Integer emptyA = Integer.parseInt(linkedPOIs.getA()) - nonEmptyA;
        Integer emptyB = Integer.parseInt(linkedPOIs.getB()) - nonEmptyB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
        
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");

            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
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
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setLabel("Could not compute");
            
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalEmptyA.toString(), totalEmptyB.toString(), null);
        pair.setLabel("Linked Empty properties");

        return pair;
    }

    public StatisticResultPair[] calculateAveragePropertiesPerPOI(Model leftModel, Model rightModel) {

        StatisticResultPair[] results = new StatisticResultPair[2];
        
        StatisticResultPair distinctProperties = map.get("distinctProperties");

        int distinctPropertiesA = Integer.parseInt(distinctProperties.getA());
        int distinctPropertiesB = Integer.parseInt(distinctProperties.getB());

        Double averagePropertiesA = SparqlRepository.averagePropertiesPerPOI(leftModel, distinctPropertiesA)[0];
        Double averagePropertiesB = SparqlRepository.averagePropertiesPerPOI(rightModel, distinctPropertiesB)[0];

        Double averageEmptyPropertiesA = SparqlRepository.averagePropertiesPerPOI(leftModel, distinctPropertiesA)[1];
        Double averageEmptyPropertiesB = SparqlRepository.averagePropertiesPerPOI(rightModel, distinctPropertiesB)[1];
        
        StatisticResultPair averageProperties 
                = new StatisticResultPair(averagePropertiesA.toString(), averagePropertiesB.toString(), null);
        StatisticResultPair averageEmptyProperties 
                = new StatisticResultPair(averageEmptyPropertiesA.toString(), averageEmptyPropertiesB.toString(), null);
        
        averageProperties.setLabel("Average number of properties per POI");
        averageEmptyProperties.setLabel("Average number of empty properties per POI");
        map.put("averageProperties", averageProperties);
        map.put("averageEmptyProperties", averageEmptyProperties);
        
        results[0] = averageProperties;
        results[1] = averageEmptyProperties;
        
        return results;
    }

    public StatisticResultPair[] calculateStatsPerLink(Model leftModel, Model rightModel, List<Link> links) {

        int sumA = 0;
        int sumB = 0;

        Integer countLongerNamesA = 0;
        Integer countLongerNamesB = 0;
        
        Integer countLongerPhonesA = 0;
        Integer countLongerPhonesB = 0;    
        
        Integer fullMatchStreets = 0;
        Integer fullMatchStreetNumbers = 0;

        //Integer fullMatchStreetsB =0;

        //using the same loop for several statistics to improve performance
        for(Link link : links){

            //average properties
            sumA = getAverageProperties(leftModel, sumA, link.getNodeA());
            sumB = getAverageProperties(leftModel, sumA, link.getNodeB());
            
            //longer name values
            EnumEntity longerNameEntity = countLongerProperty(link, Namespace.NAME, Namespace.NAME_VALUE, leftModel, rightModel);
            switch(longerNameEntity){
                case LEFT:
                    countLongerNamesA++;
                    break;
                case RIGHT:
                    countLongerNamesB++;
                    break;
                case UNDEFINED:
                    //donnot count
                    break;                    
            }

            //longer phone values
            EnumEntity longerPhoneEntity = countLongerProperty(link, Namespace.PHONE, Namespace.CONTACT_VALUE, leftModel, rightModel);
            switch(longerPhoneEntity){
                case LEFT:
                    countLongerPhonesA++;
                    break;
                case RIGHT:
                    countLongerPhonesB++;
                    break;
                case UNDEFINED:
                    //donnot count
                    break;                     
            }
            
            //fully matching address street values
            boolean fullMatchStreet = countFullMatch(link, Namespace.ADDRESS, Namespace.STREET, leftModel, rightModel);
            if(fullMatchStreet){
                fullMatchStreets++;
            }
            
            //fully matching street number values
            boolean fullMatchStreetNumber = countFullMatch(link, Namespace.ADDRESS, Namespace.STREET, leftModel, rightModel);
            if(fullMatchStreetNumber){
                fullMatchStreetNumbers++;
            }
        }
        
        Double averageLinkedPropertiesA = sumA / (double)totalPOIsA;
        Double averageLinkedPropertiesB = sumB / (double)totalPOIsB;
        
        StatisticResultPair[] results = new StatisticResultPair[5];
        
        StatisticResultPair averageLinkedProperties 
                = new StatisticResultPair(averageLinkedPropertiesA.toString(), averageLinkedPropertiesB.toString(), null);

        StatisticResultPair longerNames 
                = new StatisticResultPair(countLongerNamesA.toString(), countLongerNamesB.toString(), null);

        StatisticResultPair longerPhones 
                = new StatisticResultPair(countLongerPhonesA.toString(), countLongerPhonesB.toString(), null);

        StatisticResultPair fullyMatchingStreets 
                = new StatisticResultPair(null, null, fullMatchStreets.toString());

        StatisticResultPair fullyMatchingStreetNumbers 
                = new StatisticResultPair(null, null, fullMatchStreetNumbers.toString());
        
        averageLinkedProperties.setLabel("Average number of properties of linked POI");
        longerNames.setLabel("Number of POI name properties that are longer than the names of the corresponding (linked) POIs of the two datasets.");
        longerPhones.setLabel("Number of POI phone properties that are longer than the phones of the corresponding (linked) POIs of the two datasets.");
        fullyMatchingStreets.setLabel("Number of fully matching address streets between linked POIs in the two datasets.");
        fullyMatchingStreetNumbers.setLabel("Number of fully matching street numbers between linked POIs in the two datasets.");
        
        map.put("averageLinkedProperties", averageLinkedProperties);
        map.put("longerNames", longerNames);
        map.put("longerPhones", longerPhones);
        map.put("fullyMatchingStreets", fullyMatchingStreets);
        map.put("fullyMatchingStreetNumbers", fullyMatchingStreetNumbers);

        results[0] = averageLinkedProperties;
        results[1] = longerNames;
        results[2] = longerPhones;
        results[3] = fullyMatchingStreets;
        results[4] = fullyMatchingStreetNumbers;

        return results;
    }

    private int getAverageProperties(Model leftModel, int sum, String node) {
        int properties = SparqlRepository.countDistinctPropertiesOfResource(leftModel, node);
        sum = sum + properties;
        
        return sum;
    }

    public StatisticResultPair calculateAverageEmptyPropertiesOfLinkedPOIs() {
        
        StatisticResultPair nonEmptyProperties = map.get("nonEmptyProperties");
        
        int a = Integer.parseInt(nonEmptyProperties.getA());
        int b = Integer.parseInt(nonEmptyProperties.getB());
        
        Double avgA = a /(double)totalPOIsA;
        Double avgB = b /(double)totalPOIsB;
        
        StatisticResultPair averageLinkedProperties = new StatisticResultPair(avgA.toString(), avgB.toString(), null);
        
        averageLinkedProperties.setLabel("Average number of empty properties of linked POIs");
        map.put("averageLinkedEmptyProperties", averageLinkedProperties);
        
        return averageLinkedProperties;
        
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

    private EnumEntity countLongerProperty(Link link, String prop1, String prop2, Model modelA, Model modelB) {

        Literal literalA = SparqlRepository.getObjectOfProperty(link.getNodeA(), prop1, prop2, modelA);
        Literal literalB = SparqlRepository.getObjectOfProperty(link.getNodeB(), prop1,prop2, modelB);
        
        if(literalA == null && literalB == null){
            return EnumEntity.UNDEFINED;
        } else if(literalA != null && literalB == null){
            return EnumEntity.LEFT;
        } else if(literalB != null && literalA == null){
            return EnumEntity.RIGHT;
        }
        
        String propertyValueA = literalA.getString();
        String propertyValueB = literalB.getString();
        
        String sA = Normalizer.normalize(propertyValueA, Normalizer.Form.NFC);
        String sB = Normalizer.normalize(propertyValueB, Normalizer.Form.NFC);

        if (sA.length() >= sB.length()) {
            return EnumEntity.LEFT;
        } else {
            return EnumEntity.RIGHT;
        }
    }
    
    private boolean countFullMatch(Link link, String prop1, String prop2, Model modelA, Model modelB) {

        Literal literalA = SparqlRepository.getObjectOfProperty(link.getNodeA(), prop1, prop2, modelA);
        Literal literalB = SparqlRepository.getObjectOfProperty(link.getNodeB(), prop1,prop2, modelB);

        if(literalA == null || literalB == null){
            return false;
        }

        String nameA = literalA.getString();
        String nameB = literalB.getString();

        return nameA.equals(nameB);

    }
}
