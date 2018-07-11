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
import gr.athena.innovation.fagi.preview.statistics.EnumStatViewType;
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

    private Integer totalPOIsA;
    private Integer totalPOIsB;

    StatisticsContainer container = new StatisticsContainer();

    private final Map<String, StatisticResultPair> map = new HashMap<>();

    @Override
    public StatisticsContainer collect(){

        /* IMPORTANT: The order of calculation is sensitive for avoiding re-calculations */

        container.setComplete(false);

        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();
        Model linksModel = LinksModel.getLinksModel().getModel();
        List<Link> links = LinksModel.getLinksModel().getLinks();

        /* POIs and triples count */
        
        map.put(EnumStat.TOTAL_POIS.getKey(), countTotalEntities(leftModel, rightModel));
        map.put(EnumStat.TOTAL_TRIPLES.getKey(), countTriples(leftModel, rightModel));

        /* Non empty properties */

        map.put(EnumStat.NON_EMPTY_NAMES.getKey(), countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_NAMES, Namespace.NAME_VALUE));
        map.put(EnumStat.NON_EMPTY_PHONES.getKey(), countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_PHONES, Namespace.PHONE));
        map.put(EnumStat.NON_EMPTY_STREETS.getKey(), countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREETS, Namespace.STREET));
        map.put(EnumStat.NON_EMPTY_STREET_NUMBERS.getKey(), countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER));
        map.put(EnumStat.NON_EMPTY_WEBSITES.getKey(), countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_WEBSITES, Namespace.HOMEPAGE));
        map.put(EnumStat.NON_EMPTY_EMAILS.getKey(), countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_EMAILS, Namespace.EMAIL));
        map.put(EnumStat.NON_EMPTY_DATES.getKey(), countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_DATES, Namespace.DATE));

        /* Empty properties */
        
        map.put(EnumStat.EMPTY_NAMES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_NAMES, Namespace.NAME_VALUE));
        map.put(EnumStat.EMPTY_PHONES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_PHONES, Namespace.PHONE));
        map.put(EnumStat.EMPTY_STREETS.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_STREETS, Namespace.STREET));
        map.put(EnumStat.EMPTY_STREET_NUMBERS.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER));
        map.put(EnumStat.EMPTY_WEBSITES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_WEBSITES, Namespace.HOMEPAGE));
        map.put(EnumStat.EMPTY_EMAILS.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_EMAILS, Namespace.EMAIL));
        map.put(EnumStat.EMPTY_DATES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_DATES, Namespace.DATE));

        /* Distinct properties */

        map.put(EnumStat.EMPTY_DATES.getKey(), countDistinctProperties(leftModel, rightModel));

        /* Percentages */

        map.put(EnumStat.PRIMARY_DATE_FORMATS_PERCENT.getKey(), calculatePercentageOfPrimaryDateFormats(leftModel, rightModel));

        map.put(EnumStat.NAMES_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.NAMES_PERCENT, Namespace.NAME_VALUE));
        map.put(EnumStat.PHONES_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.PHONES_PERCENT, Namespace.PHONE));
        map.put(EnumStat.STREETS_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREETS_PERCENT, Namespace.STREET));
        map.put(EnumStat.STREET_NUMBERS_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREET_NUMBERS_PERCENT, Namespace.STREET_NUMBER));
        map.put(EnumStat.WEBSITE_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.WEBSITE_PERCENT, Namespace.HOMEPAGE));
        map.put(EnumStat.EMAIL_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.EMAIL_PERCENT, Namespace.EMAIL));
        map.put(EnumStat.LOCALITY_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.LOCALITY_PERCENT, Namespace.LOCALITY));
        map.put(EnumStat.DATES_PERCENT.getKey(), calculatePropertyPercentage(leftModel, rightModel, EnumStat.DATES_PERCENT, Namespace.DATE));

        /* Statistics for linked POIs*/

        map.put(EnumStat.LINKED_POIS.getKey(), countLinkedPOIs(linksModel));
        map.put(EnumStat.LINKED_VS_TOTAL.getKey(), countLinkedVsTotalPOIs(leftModel, rightModel, linksModel));
        map.put(EnumStat.LINKED_TRIPLES.getKey(), countLinkedTriples(leftModel, rightModel, linksModel));

        //map.put(EnumStat.LINKED_TRIPLES.getKey(), computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.NAME, Namespace.NAME_VALUE));
//        computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.PHONE, Namespace.CONTACT_VALUE);
//        computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET);
//        computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET_NUMBER);
//        computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.HOMEPAGE);
//        computeNonEmptyLinkedPropertyChain(linkedA, linkedB, Namespace.EMAIL, Namespace.CONTACT_VALUE);
//        computeNonEmptyLinkedProperty(linkedA, linkedB, Namespace.DATE);

//        computeLinkStats(totalPOIsA, totalPOIsB);

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

    @Override
    public StatisticsContainer collect(List<String> selected){

        container.setComplete(false);

        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();
        Model linksModel = LinksModel.getLinksModel().getModel();
        //List<Link> links = LinksModel.getLinksModel().getLinks();
        
        Map<String, EnumStat> statMap = EnumStat.getMap();
        for(String statKey : selected){
            EnumStat stat = statMap.get(statKey);
            switch(stat){
                case TOTAL_POIS:
                    map.put(EnumStat.TOTAL_POIS.getKey(), countTotalEntities(leftModel, rightModel));
                    break;
                case TOTAL_TRIPLES:
                    map.put(EnumStat.TOTAL_TRIPLES.getKey(), countTriples(leftModel, rightModel));
                    break;
                case NON_EMPTY_NAMES:
                    map.put(EnumStat.NON_EMPTY_NAMES.getKey(), 
                            countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_NAMES, Namespace.NAME_VALUE));
                    break;
                case NON_EMPTY_PHONES:
                    map.put(EnumStat.NON_EMPTY_PHONES.getKey(), 
                            countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_PHONES, Namespace.PHONE));
                    break;   
                case NON_EMPTY_STREETS:
                    map.put(EnumStat.NON_EMPTY_STREETS.getKey(), 
                            countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREETS, Namespace.STREET));
                    break;
                case NON_EMPTY_STREET_NUMBERS:
                    map.put(EnumStat.NON_EMPTY_STREET_NUMBERS.getKey(), 
                            countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER));
                    break;
                case NON_EMPTY_WEBSITES:
                    map.put(EnumStat.NON_EMPTY_WEBSITES.getKey(), 
                            countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_WEBSITES, Namespace.HOMEPAGE));
                    break; 
                case NON_EMPTY_EMAILS:
                    map.put(EnumStat.NON_EMPTY_EMAILS.getKey(), 
                            countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_EMAILS, Namespace.EMAIL));
                    break; 
                case NON_EMPTY_DATES:
                    map.put(EnumStat.NON_EMPTY_DATES.getKey(), 
                            countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_DATES, Namespace.DATE));
                    break;    
                case EMPTY_NAMES:
                    map.put(EnumStat.EMPTY_NAMES.getKey(), 
                            countEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_NAMES, Namespace.NAME));
                    break;
                case EMPTY_PHONES:
                    map.put(EnumStat.EMPTY_PHONES.getKey(), 
                            countEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_PHONES, Namespace.PHONE));
                    break;
                case EMPTY_STREETS:
                    map.put(EnumStat.EMPTY_STREETS.getKey(), 
                            countEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREETS, Namespace.STREET));
                    break;
                case EMPTY_STREET_NUMBERS:
                    map.put(EnumStat.EMPTY_STREET_NUMBERS.getKey(), 
                            countEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER));
                    break;
                case EMPTY_WEBSITES:
                    map.put(EnumStat.EMPTY_WEBSITES.getKey(), 
                            countEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_WEBSITES, Namespace.HOMEPAGE));
                    break;
                case EMPTY_EMAILS:
                    map.put(EnumStat.EMPTY_EMAILS.getKey(), 
                            countEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_EMAILS, Namespace.EMAIL));
                    break;
                case EMPTY_DATES:
                    map.put(EnumStat.EMPTY_DATES.getKey(), 
                            countEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_DATES, Namespace.DATE));
                    break;
                case DISTINCT_PROPERTIES:
                    map.put(EnumStat.DISTINCT_PROPERTIES.getKey(), countDistinctProperties(leftModel, rightModel));
                    break;
                case PRIMARY_DATE_FORMATS_PERCENT:
                    map.put(EnumStat.PRIMARY_DATE_FORMATS_PERCENT.getKey(), calculatePercentageOfPrimaryDateFormats(leftModel, rightModel));
                    break;  
                case NAMES_PERCENT:
                    map.put(EnumStat.NAMES_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.NAMES_PERCENT, Namespace.NAME_VALUE));
                    break;
                case WEBSITE_PERCENT:
                    map.put(EnumStat.WEBSITE_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.WEBSITE_PERCENT, Namespace.HOMEPAGE));
                    break;
                case EMAIL_PERCENT:
                    map.put(EnumStat.EMAIL_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.EMAIL_PERCENT, Namespace.EMAIL));
                    break;
                case PHONES_PERCENT:
                    map.put(EnumStat.PHONES_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.PHONES_PERCENT, Namespace.PHONE));
                    break;
                case STREETS_PERCENT:
                    map.put(EnumStat.STREETS_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREETS_PERCENT, Namespace.STREET));
                    break;
                case STREET_NUMBERS_PERCENT:
                    map.put(EnumStat.STREET_NUMBERS_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREET_NUMBERS_PERCENT, Namespace.STREET_NUMBER));
                    break;
                case LOCALITY_PERCENT:
                    map.put(EnumStat.LOCALITY_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.LOCALITY_PERCENT, Namespace.LOCALITY));
                    break;
                case DATES_PERCENT:
                    map.put(EnumStat.DATES_PERCENT.getKey(), 
                            calculatePropertyPercentage(leftModel, rightModel, EnumStat.DATES_PERCENT, Namespace.DATE));
                    break;
                case LINKED_POIS:
                    map.put(EnumStat.LINKED_POIS.getKey(), countLinkedPOIs(linksModel));
                    break;
                case LINKED_VS_TOTAL:
                    map.put(EnumStat.LINKED_VS_TOTAL.getKey(), countLinkedVsTotalPOIs(leftModel, rightModel, linksModel));
                    break;
                case LINKED_TRIPLES:
                    map.put(EnumStat.LINKED_TRIPLES.getKey(), countLinkedTriples(leftModel, rightModel, linksModel));
                    break;
            }
        }

        container.setMap(map);
        container.setValid(true);
        container.setComplete(true);

        return container;
    }

    public StatisticResultPair countTotalEntities(Model a, Model b){

        Integer totalA = SparqlRepository.countPOIs(a);
        Integer totalB = SparqlRepository.countPOIs(b);
        Integer total = totalA + totalB;

        totalPOIsA = totalA;
        totalPOIsB = totalB;

        if(warn(totalPOIsA, totalPOIsB, Namespace.SOURCE)){
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setTitle("Could not compute");
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setValueTotal(total.toString());
        pair.setTitle(EnumStat.TOTAL_POIS.toString());
        pair.setLegendTotal(EnumStat.TOTAL_POIS.getLegendTotal());
        pair.setLegendA(EnumStat.TOTAL_POIS.getLegendA());
        pair.setLegendB(EnumStat.TOTAL_POIS.getLegendB());

        return pair;
    }

    public StatisticResultPair countTriples(Model a, Model b){

        Long totalA = a.size();
        Long totalB = b.size();
        Long total = totalA + totalB;

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setValueTotal(total.toString());
        pair.setTitle(EnumStat.TOTAL_TRIPLES.toString());
        pair.setLegendTotal(EnumStat.TOTAL_TRIPLES.getLegendTotal());
        pair.setLegendA(EnumStat.TOTAL_TRIPLES.getLegendA());
        pair.setLegendB(EnumStat.TOTAL_TRIPLES.getLegendB());        

        return pair;
    }

    public StatisticResultPair countLinkedVsTotalPOIs(Model a, Model b, Model links){

        Integer linkedA = SparqlRepository.countLinkedPOIsA(links);
        Integer linkedB = SparqlRepository.countLinkedPOIsB(links);

        Integer linked = (linkedA + linkedB);

        if(totalPOIsA == null || totalPOIsB == null){
            StatisticResultPair totalEntities = countTotalEntities(a, b);
            totalPOIsA = Integer.parseInt(totalEntities.getValueA());
            totalPOIsB = Integer.parseInt(totalEntities.getValueB());
        }

        Integer total = totalPOIsA + totalPOIsB;

        StatisticResultPair pair = new StatisticResultPair(linked.toString(), total.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setTitle(EnumStat.LINKED_VS_TOTAL.toString());
        pair.setLegendTotal(EnumStat.LINKED_VS_TOTAL.getLegendTotal());
        pair.setLegendA(EnumStat.LINKED_VS_TOTAL.getLegendA());
        pair.setLegendB(EnumStat.LINKED_VS_TOTAL.getLegendB());
        return pair;
    }

    public StatisticResultPair countLinkedPOIs(Model links){

        Integer linkedPOIsA = SparqlRepository.countDistinctSubjects(links);
        Integer linkedPOIsB = SparqlRepository.countDistinctObjects(links);

        StatisticResultPair pair = new StatisticResultPair(linkedPOIsA.toString(), linkedPOIsB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setTitle(EnumStat.LINKED_POIS.toString());
        pair.setLegendTotal(EnumStat.LINKED_POIS.getLegendTotal());
        pair.setLegendA(EnumStat.LINKED_POIS.getLegendA());
        pair.setLegendB(EnumStat.LINKED_POIS.getLegendB());

        return pair;
    }

    public StatisticResultPair countLinkedTriples(Model modelA, Model modelB, Model linksModel){
        
        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);
        
        Integer linkedTriplesA = SparqlRepository.countLinkedTriplesA(linkedA);
        Integer linkedTriplesB = SparqlRepository.countLinkedTriplesB(linkedB);

        StatisticResultPair pair = new StatisticResultPair(linkedTriplesA.toString(), linkedTriplesB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setTitle(EnumStat.LINKED_TRIPLES.toString());
        pair.setLegendTotal(EnumStat.LINKED_TRIPLES.getLegendTotal());
        pair.setLegendA(EnumStat.LINKED_TRIPLES.getLegendA());
        pair.setLegendB(EnumStat.LINKED_TRIPLES.getLegendB());

        return pair;
    }

    public StatisticResultPair countDistinctProperties(Model a, Model b){

        Integer distinctPropertiesA = SparqlRepository.countDistinctProperties(a);
        Integer distinctPropertiesB = SparqlRepository.countDistinctProperties(b);
        Integer total = distinctPropertiesA + distinctPropertiesB;
        
        StatisticResultPair pair = new StatisticResultPair(distinctPropertiesA.toString(), distinctPropertiesB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setValueTotal(total.toString());
        pair.setTitle(EnumStat.DISTINCT_PROPERTIES.toString());
        pair.setLegendTotal(EnumStat.DISTINCT_PROPERTIES.getLegendTotal());
        pair.setLegendA(EnumStat.DISTINCT_PROPERTIES.getLegendA());
        pair.setLegendB(EnumStat.DISTINCT_PROPERTIES.getLegendB());
        
        return pair;
    }

    public StatisticResultPair countNonEmptyProperty(Model a, Model b, EnumStat stat, String property){

        Integer propertyA = countNonEmptyProperty(property, a);
        Integer propertyB = countNonEmptyProperty(property, b);
        Integer total = propertyA + propertyB;

        StatisticResultPair pair = new StatisticResultPair(propertyA.toString(), propertyB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair countEmptyProperty(Model a, Model b, EnumStat stat, String property){
        
        Integer nA;
        Integer nB;
        Integer total;
        
        if(map.get(stat.getKey()) != null){
            try {

                nA = Integer.parseInt(map.get(stat.getKey()).getValueA());
                nB = Integer.parseInt(map.get(stat.getKey()).getValueB());
                total = nA + nB;

            } catch(NumberFormatException ex){
                LOG.warn("Could not compute empty names. ", ex);
                StatisticResultPair pair = new StatisticResultPair("0","0", null);
                pair.setTitle("Could not compute");

                return pair;
            }
        } else {
            StatisticResultPair nonEmptyProperty = countNonEmptyProperty(a, b, stat, property);
            nA = Integer.parseInt(nonEmptyProperty.getValueA());
            nB = Integer.parseInt(nonEmptyProperty.getValueB());
            total = nA + nB;
        }
        
        if(totalPOIsA == null || totalPOIsB == null){
            StatisticResultPair totalEntities = countTotalEntities(a, b);
            totalPOIsA = Integer.parseInt(totalEntities.getValueA());
            totalPOIsB = Integer.parseInt(totalEntities.getValueB());
        }
        
        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;
        
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }
    
    public StatisticResultPair countTotalNonEmptyProperties(){

        Integer totalA;
        Integer totalB;

        try {

            Integer a1 = Integer.parseInt(map.get("nonEmptyNames").getValueA());
            Integer a2 = Integer.parseInt(map.get("nonEmptyPhones").getValueA());
            Integer a3 = Integer.parseInt(map.get("nonEmptyStreets").getValueA());
            Integer a4 = Integer.parseInt(map.get("nonEmptyStreetNumbers").getValueA());
            Integer a5 = Integer.parseInt(map.get("nonEmptyWebsites").getValueA());
            Integer a6 = Integer.parseInt(map.get("nonEmptyEmails").getValueA());
            Integer a7 = Integer.parseInt(map.get("nonEmptyDates").getValueA());

            Integer b1 = Integer.parseInt(map.get("nonEmptyNames").getValueB());
            Integer b2 = Integer.parseInt(map.get("nonEmptyPhones").getValueB());
            Integer b3 = Integer.parseInt(map.get("nonEmptyStreets").getValueB());
            Integer b4 = Integer.parseInt(map.get("nonEmptyStreetNumbers").getValueB());
            Integer b5 = Integer.parseInt(map.get("nonEmptyWebsites").getValueB());
            Integer b6 = Integer.parseInt(map.get("nonEmptyEmails").getValueB());
            Integer b7 = Integer.parseInt(map.get("nonEmptyDates").getValueB()); 

            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4 + b5 + b6 + b7;

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute total non empty properties due to missing properties. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setTitle("Could not compute");
            
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setTitle("Non empty properties");
        
        map.put("nonEmptyProperties", pair);
        return pair;
    }

    public StatisticResultPair countTotalEmptyProperties(){

        Integer totalA;
        Integer totalB;
                
        try{
            
            Integer a1 = Integer.parseInt(map.get("emptyNames").getValueA());
            Integer a2 = Integer.parseInt(map.get("emptyPhones").getValueA());
            Integer a3 = Integer.parseInt(map.get("emptyStreets").getValueA());
            Integer a4 = Integer.parseInt(map.get("emptyStreetNumbers").getValueA());
            Integer a5 = Integer.parseInt(map.get("emptyWebsites").getValueA());
            Integer a6 = Integer.parseInt(map.get("emptyEmails").getValueA());
            Integer a7 = Integer.parseInt(map.get("emptyDates").getValueA());

            Integer b1 = Integer.parseInt(map.get("emptyNames").getValueB());
            Integer b2 = Integer.parseInt(map.get("emptyPhones").getValueB());
            Integer b3 = Integer.parseInt(map.get("emptyStreets").getValueB());
            Integer b4 = Integer.parseInt(map.get("emptyStreetNumbers").getValueB());
            Integer b5 = Integer.parseInt(map.get("emptyWebsites").getValueB());
            Integer b6 = Integer.parseInt(map.get("emptyEmails").getValueB());
            Integer b7 = Integer.parseInt(map.get("emptyDates").getValueB());
            
            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4+ b5 + b6 + b7;
            
        } catch(NumberFormatException ex){
            LOG.warn("Could not compute total empty properties due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setTitle("Could not compute");
            
            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setTitle("Empty properties");

        map.put("emptyProperties", pair);
        return pair;
    }
    
    public StatisticResultPair calculatePercentageOfPrimaryDateFormats(Model leftModel, Model rightModel){

        String date = Namespace.DATE;

        int totalDatesA = SparqlRepository.countProperty(leftModel, date);
        int totalDatesB = SparqlRepository.countProperty(rightModel, date);
        
        if(totalDatesA == 0 || totalDatesB ==0){
            StatisticResultPair pair = new StatisticResultPair("0", "0", null);

            pair.setTitle("Percentage of primary date formats");
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

        pair.setTitle("Percentage of primary date formats");
        map.put("primaryDateFormatsPercent", pair);
        return pair;
    }

    public StatisticResultPair calculatePropertyPercentage(Model a, Model b, EnumStat stat, String property){

        int propertyA = SparqlRepository.countProperty(a, property);
        int propertyB = SparqlRepository.countProperty(b, property);

        if(totalPOIsA == null || totalPOIsB == null){
            StatisticResultPair totalPois = countTotalEntities(a, b);
            totalPOIsA = Integer.parseInt(totalPois.getValueA());
            totalPOIsB = Integer.parseInt(totalPois.getValueB());
        }
        
        Double percentageA = roundHalfDown((100 * propertyA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * propertyB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setValueTotal("100");
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());        

        return pair;
    }

    public StatisticResultPair calculateTotalNonEmptyPropertiesPercentage(){

        Double totalPropPercentageA;
        Double totalPropPercentageB;

        try{

            Double nA = Double.parseDouble(map.get("namesPercent").getValueA());
            Double nB = Double.parseDouble(map.get("namesPercent").getValueB());
            Double pA = Double.parseDouble(map.get("phonesPercent").getValueA());
            Double pB = Double.parseDouble(map.get("phonesPercent").getValueB());
            Double sA = Double.parseDouble(map.get("streetsPercent").getValueA());
            Double sB = Double.parseDouble(map.get("streetsPercent").getValueB());
            Double snA = Double.parseDouble(map.get("streetNumbersPercent").getValueA());
            Double snB = Double.parseDouble(map.get("streetNumbersPercent").getValueB());
            Double wA = Double.parseDouble(map.get("websitesPercent").getValueA());
            Double wB = Double.parseDouble(map.get("websitesPercent").getValueB());
            Double lA = Double.parseDouble(map.get("localityPercent").getValueA());
            Double lB = Double.parseDouble(map.get("localityPercent").getValueB());
            Double dA = Double.parseDouble(map.get("datesPercent").getValueA());
            Double dB = Double.parseDouble(map.get("datesPercent").getValueB());

            totalPropPercentageA = roundHalfDown((nA + pA + sA + snA + wA +lA + dA) / 7);
            totalPropPercentageB = roundHalfDown((nB + pB + sB + snB + wB +lB + dB) / 7);

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute total non empty percentages due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setTitle("Could not compute");

            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalPropPercentageA.toString(), totalPropPercentageB.toString(), null);

        pair.setTitle("Percentage of total properties in each dataset");

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

        //StatisticResultPair linkedPOIs = countLinkedPOIs(linksModel);//already labeled
        //StatisticResultPair linkedVsUnlinkedPOIs = countLinkedVsTotalPOIs(linksModel, totalPOIsA, totalPOIsB); //already labeled
        //StatisticResultPair linkedTotalTriples = countTotalLinkedTriples(linkedA, linkedB); //already labeled

        //map.put("linkedPois", linkedPOIs);
        //map.put("linkedVsTotal", linkedVsUnlinkedPOIs);
        //map.put("linkedTriples", linkedTotalTriples);

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
        
        Integer namesA = Integer.parseInt(pair1.getValueA());
        Integer namesB = Integer.parseInt(pair1.getValueB());
        Integer phonesA = Integer.parseInt(pair2.getValueA());
        Integer phonesB = Integer.parseInt(pair2.getValueB());
        Integer streetsA = Integer.parseInt(pair3.getValueA());
        Integer streetsB = Integer.parseInt(pair3.getValueB());
        Integer streetNumbersA = Integer.parseInt(pair4.getValueA());
        Integer streetNumbersB = Integer.parseInt(pair4.getValueB());
        Integer websitesA = Integer.parseInt(pair5.getValueA());
        Integer websitesB = Integer.parseInt(pair5.getValueB());
        Integer emailsA = Integer.parseInt(pair6.getValueA());
        Integer emailsB = Integer.parseInt(pair6.getValueB());
        Integer datesA = Integer.parseInt(pair7.getValueA());
        Integer datesB = Integer.parseInt(pair7.getValueB());
        
        StatisticResultPair linkedEmptyNames = computeEmptyLinkedProperty(namesA, namesB);
        StatisticResultPair linkedEmptyPhones = computeEmptyLinkedProperty(phonesA, phonesB);
        StatisticResultPair linkedEmptyStreets = computeEmptyLinkedProperty(streetsA, streetsB);
        StatisticResultPair linkedEmptyStreetNumbers = computeEmptyLinkedProperty(streetNumbersA, streetNumbersB);
        StatisticResultPair linkedEmptyWebsites = computeEmptyLinkedProperty(websitesA, websitesB);
        StatisticResultPair linkedEmptyEmails = computeEmptyLinkedProperty(emailsA, emailsB);
        StatisticResultPair linkedEmptyDates = computeEmptyLinkedProperty(datesA, datesB);

        linkedEmptyNames.setTitle("Linked Empty Names");
        linkedEmptyPhones.setTitle("Linked Empty Phones");
        linkedEmptyStreets.setTitle("Linked Empty Streets");
        linkedEmptyStreetNumbers.setTitle("Linked Empty Street Numbers");
        linkedEmptyWebsites.setTitle("Linked Empty Websites");
        linkedEmptyEmails.setTitle("Linked Empty Emails");
        linkedEmptyDates.setTitle("Linked Empty Dates");
        
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
                pair.setTitle("Linked Non Empty Names");
                break;
            case Namespace.PHONE:
                pair.setTitle("Linked Non Empty Phones");
                break;    
            case Namespace.ADDRESS:
                if(property2.equals(Namespace.STREET)){
                    pair.setTitle("Linked Non Empty Streets");
                } else if(property2.equals(Namespace.STREET_NUMBER)){
                    pair.setTitle("Linked Non Empty Street Numbers");
                } else {
                    throw new ApplicationException("Wrong property parameters. " + Namespace.ADDRESS 
                        + " parent does not have " + Namespace.STREET + " or " + Namespace.STREET_NUMBER + " child.");
                }
                break; 
            case Namespace.HOMEPAGE:
                pair.setTitle("Linked Non Empty Websites");
                break; 
            case Namespace.EMAIL:
                pair.setTitle("Linked Non Empty Emails");
                break; 
            case Namespace.DATE:
                pair.setTitle("Linked Non Empty Dates");
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
                pair.setTitle("Linked Non Empty Websites");
                break;
            case Namespace.DATE:
                pair.setTitle("Linked Non Empty Dates");
                break;                
            default:
                throw new ApplicationException("Property not supported for stats. " + property);
        }
        
        return pair;
    }

    public StatisticResultPair computeEmptyLinkedProperty(Integer nonEmptyA, Integer nonEmptyB){

        StatisticResultPair linkedPOIs = map.get("linkedPois");
        Integer emptyA = Integer.parseInt(linkedPOIs.getValueA()) - nonEmptyA;
        Integer emptyB = Integer.parseInt(linkedPOIs.getValueB()) - nonEmptyB;
        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);

        return pair;
    }
    
    public StatisticResultPair computeNonEmptyLinkedTotalProperties(){
        Integer totalA;
        Integer totalB;

        try{

            Integer a1 = Integer.parseInt(map.get("linkedNonEmptyNames").getValueA());
            Integer a2 = Integer.parseInt(map.get("linkedNonEmptyPhones").getValueA());
            Integer a3 = Integer.parseInt(map.get("linkedNonEmptyStreets").getValueA());
            Integer a4 = Integer.parseInt(map.get("linkedNonEmptyStreetNumbers").getValueA());
            Integer a5 = Integer.parseInt(map.get("linkedNonEmptyWebsites").getValueA());
            Integer a6 = Integer.parseInt(map.get("linkedNonEmptyEmails").getValueA());
            Integer a7 = Integer.parseInt(map.get("linkedNonEmptyDates").getValueA());

            Integer b1 = Integer.parseInt(map.get("linkedNonEmptyNames").getValueB());
            Integer b2 = Integer.parseInt(map.get("linkedNonEmptyPhones").getValueB());
            Integer b3 = Integer.parseInt(map.get("linkedNonEmptyStreets").getValueB());
            Integer b4 = Integer.parseInt(map.get("linkedNonEmptyStreetNumbers").getValueB());
            Integer b5 = Integer.parseInt(map.get("linkedNonEmptyWebsites").getValueB());
            Integer b6 = Integer.parseInt(map.get("linkedNonEmptyEmails").getValueB());
            Integer b7 = Integer.parseInt(map.get("linkedNonEmptyDates").getValueB());

            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4+ b5 + b6 + b7;

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute linked non empty properties due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setTitle("Could not compute");

            return pair;
        } 

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setTitle("Linked Non Empty properties");

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
            
            totalNonEmptyA = Integer.parseInt(map.get("linkedNonEmptyProperties").getValueA());
            totalNonEmptyB = Integer.parseInt(map.get("linkedNonEmptyProperties").getValueB());
            
            totalA = Integer.parseInt(map.get("linkedTriples").getValueA());
            totalB = Integer.parseInt(map.get("linkedTriples").getValueB());
            
            totalEmptyA = totalA - totalNonEmptyA;
            totalEmptyB = totalB - totalNonEmptyB;

        } catch(NumberFormatException ex){
            LOG.warn("Could not compute linked empty properties due to missing values. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0","0", null);
            pair.setTitle("Could not compute");
            
            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalEmptyA.toString(), totalEmptyB.toString(), null);
        pair.setTitle("Linked Empty properties");

        return pair;
    }

    public StatisticResultPair[] calculateAveragePropertiesPerPOI(Model leftModel, Model rightModel) {

        StatisticResultPair[] results = new StatisticResultPair[2];
        
        StatisticResultPair distinctProperties = map.get("distinctProperties");

        int distinctPropertiesA = Integer.parseInt(distinctProperties.getValueA());
        int distinctPropertiesB = Integer.parseInt(distinctProperties.getValueB());

        Double averagePropertiesA = SparqlRepository.averagePropertiesPerPOI(leftModel, distinctPropertiesA)[0];
        Double averagePropertiesB = SparqlRepository.averagePropertiesPerPOI(rightModel, distinctPropertiesB)[0];

        Double averageEmptyPropertiesA = SparqlRepository.averagePropertiesPerPOI(leftModel, distinctPropertiesA)[1];
        Double averageEmptyPropertiesB = SparqlRepository.averagePropertiesPerPOI(rightModel, distinctPropertiesB)[1];
        
        StatisticResultPair averageProperties 
                = new StatisticResultPair(averagePropertiesA.toString(), averagePropertiesB.toString(), null);
        StatisticResultPair averageEmptyProperties 
                = new StatisticResultPair(averageEmptyPropertiesA.toString(), averageEmptyPropertiesB.toString(), null);
        
        averageProperties.setTitle("Average number of properties per POI");
        averageEmptyProperties.setTitle("Average number of empty properties per POI");
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
        
        averageLinkedProperties.setTitle("Average number of properties of linked POI");
        longerNames.setTitle("Number of POI name properties that are longer than the names of the corresponding (linked) POIs of the two datasets.");
        longerPhones.setTitle("Number of POI phone properties that are longer than the phones of the corresponding (linked) POIs of the two datasets.");
        fullyMatchingStreets.setTitle("Number of fully matching address streets between linked POIs in the two datasets.");
        fullyMatchingStreetNumbers.setTitle("Number of fully matching street numbers between linked POIs in the two datasets.");
        
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
        
        int a = Integer.parseInt(nonEmptyProperties.getValueA());
        int b = Integer.parseInt(nonEmptyProperties.getValueB());
        
        Double avgA = a /(double)totalPOIsA;
        Double avgB = b /(double)totalPOIsB;
        
        StatisticResultPair averageLinkedProperties = new StatisticResultPair(avgA.toString(), avgB.toString(), null);
        
        averageLinkedProperties.setTitle("Average number of empty properties of linked POIs");
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
