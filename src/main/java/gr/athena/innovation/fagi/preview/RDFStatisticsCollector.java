package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.preview.statistics.StatisticsContainer;
import gr.athena.innovation.fagi.preview.statistics.StatisticsCollector;
import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.EnumEntity;
//import gr.athena.innovation.fagi.model.FusedDataset;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.preview.statistics.EnumStatGroup;
import gr.athena.innovation.fagi.preview.statistics.EnumStatViewType;
import gr.athena.innovation.fagi.preview.statistics.StatGroup;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.specification.Configuration;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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
public class RDFStatisticsCollector implements StatisticsCollector {

    private static final Logger LOG = LogManager.getLogger(RDFStatisticsCollector.class);
    private Integer totalPOIsA = null;
    private Integer totalPOIsB = null;
    private Integer fusedPOIs = null;
    private Integer rejected = null;
    private final StatisticsContainer container = new StatisticsContainer();
    private final Map<String, StatisticResultPair> map = new HashMap<>();

    @Override
    public StatisticsContainer collect() {

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
        map.put(EnumStat.NON_EMPTY_NAMES.getKey(), countNonEmptyProperty(leftModel, rightModel,
                EnumStat.NON_EMPTY_NAMES, Namespace.NAME, Namespace.NAME_VALUE));
        map.put(EnumStat.NON_EMPTY_PHONES.getKey(), countNonEmptyProperty(leftModel, rightModel,
                EnumStat.NON_EMPTY_PHONES, Namespace.PHONE));
        map.put(EnumStat.NON_EMPTY_STREETS.getKey(), countNonEmptyProperty(leftModel, rightModel,
                EnumStat.NON_EMPTY_STREETS, Namespace.STREET));
        map.put(EnumStat.NON_EMPTY_STREET_NUMBERS.getKey(), countNonEmptyProperty(leftModel, rightModel,
                EnumStat.NON_EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER));
        map.put(EnumStat.NON_EMPTY_WEBSITES.getKey(), countNonEmptyProperty(leftModel, rightModel,
                EnumStat.NON_EMPTY_WEBSITES, Namespace.HOMEPAGE));
        map.put(EnumStat.NON_EMPTY_EMAILS.getKey(), countNonEmptyProperty(leftModel, rightModel,
                EnumStat.NON_EMPTY_EMAILS, Namespace.EMAIL));
        map.put(EnumStat.NON_EMPTY_DATES.getKey(), countNonEmptyProperty(leftModel, rightModel,
                EnumStat.NON_EMPTY_DATES, Namespace.DATE));

        /* Empty properties */
        map.put(EnumStat.EMPTY_NAMES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_NAMES,
                Namespace.NAME_VALUE));
        map.put(EnumStat.EMPTY_PHONES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_PHONES,
                Namespace.PHONE));
        map.put(EnumStat.EMPTY_STREETS.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_STREETS,
                Namespace.STREET));
        map.put(EnumStat.EMPTY_STREET_NUMBERS.getKey(), countEmptyProperty(leftModel, rightModel,
                EnumStat.EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER));
        map.put(EnumStat.EMPTY_WEBSITES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_WEBSITES,
                Namespace.HOMEPAGE));
        map.put(EnumStat.EMPTY_EMAILS.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_EMAILS,
                Namespace.EMAIL));
        map.put(EnumStat.EMPTY_DATES.getKey(), countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_DATES,
                Namespace.DATE));

        /* Distinct properties */
        map.put(EnumStat.EMPTY_DATES.getKey(), countDistinctProperties(leftModel, rightModel));

        /* Percentages */
        map.put(EnumStat.PRIMARY_DATE_FORMATS_PERCENT.getKey(),
                calculatePercentageOfPrimaryDateFormats(leftModel, rightModel, EnumStat.PRIMARY_DATE_FORMATS_PERCENT));

        map.put(EnumStat.NAMES_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.NAMES_PERCENT, Namespace.NAME));
        map.put(EnumStat.PHONES_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.PHONES_PERCENT, Namespace.PHONE));
        map.put(EnumStat.STREETS_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREETS_PERCENT, Namespace.STREET));
        map.put(EnumStat.STREET_NUMBERS_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREET_NUMBERS_PERCENT,
                        Namespace.STREET_NUMBER));
        map.put(EnumStat.WEBSITE_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.WEBSITE_PERCENT, Namespace.HOMEPAGE));
        map.put(EnumStat.EMAIL_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.EMAIL_PERCENT, Namespace.EMAIL));
        map.put(EnumStat.LOCALITY_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.LOCALITY_PERCENT, Namespace.LOCALITY));
        map.put(EnumStat.DATES_PERCENT.getKey(),
                calculatePropertyPercentage(leftModel, rightModel, EnumStat.DATES_PERCENT, Namespace.DATE));

        /* Statistics for linked POIs*/
        map.put(EnumStat.LINKED_POIS.getKey(), countLinkedPOIs(linksModel));
        map.put(EnumStat.LINKED_VS_TOTAL.getKey(), countLinkedVsTotalPOIs(leftModel, rightModel, linksModel));
        map.put(EnumStat.LINKED_TRIPLES.getKey(), countLinkedTriples(leftModel, rightModel, linksModel));

        map.put(EnumStat.LINKED_NON_EMPTY_NAMES.getKey(), computeNonEmptyLinkedPropertyChain(
                leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_NAMES, Namespace.NAME,
                Namespace.NAME_VALUE));

        map.put(EnumStat.LINKED_NON_EMPTY_PHONES.getKey(), computeNonEmptyLinkedPropertyChain(
                leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_PHONES, Namespace.PHONE,
                Namespace.CONTACT_VALUE));

        map.put(EnumStat.LINKED_NON_EMPTY_STREETS.getKey(), computeNonEmptyLinkedPropertyChain(
                leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_STREETS, Namespace.ADDRESS,
                Namespace.STREET));

        map.put(EnumStat.LINKED_NON_EMPTY_STREET_NUMBERS.getKey(), computeNonEmptyLinkedPropertyChain(
                leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_STREET_NUMBERS, Namespace.ADDRESS,
                Namespace.STREET_NUMBER));

        map.put(EnumStat.LINKED_NON_EMPTY_WEBSITES.getKey(),
                computeNonEmptyLinkedProperty(leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_WEBSITES,
                        Namespace.HOMEPAGE));

        map.put(EnumStat.LINKED_NON_EMPTY_EMAILS.getKey(), computeNonEmptyLinkedPropertyChain(
                leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_EMAILS, Namespace.EMAIL,
                Namespace.CONTACT_VALUE));

        map.put(EnumStat.LINKED_NON_EMPTY_DATES.getKey(),
                computeNonEmptyLinkedProperty(leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_DATES,
                        Namespace.DATE));

        map.put(EnumStat.LINKED_EMPTY_NAMES.getKey(),
                computeEmptyLinkedPropertyChain(leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_NAMES,
                        Namespace.NAME, Namespace.NAME_VALUE));

        map.put(EnumStat.LINKED_EMPTY_PHONES.getKey(),
                computeEmptyLinkedPropertyChain(leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_PHONES,
                        Namespace.PHONE, Namespace.CONTACT_VALUE));

        map.put(EnumStat.LINKED_EMPTY_STREETS.getKey(),
                computeEmptyLinkedPropertyChain(leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_STREETS,
                        Namespace.ADDRESS, Namespace.STREET));

        map.put(EnumStat.LINKED_EMPTY_STREET_NUMBERS.getKey(),
                computeEmptyLinkedPropertyChain(leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_STREET_NUMBERS,
                        Namespace.ADDRESS, Namespace.STREET_NUMBER));

        map.put(EnumStat.LINKED_EMPTY_WEBSITES.getKey(),
                computeEmptyLinkedProperty(leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_WEBSITES,
                        Namespace.HOMEPAGE));

        map.put(EnumStat.LINKED_EMPTY_EMAILS.getKey(),
                computeEmptyLinkedPropertyChain(leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_EMAILS,
                        Namespace.EMAIL, Namespace.CONTACT_VALUE));

        map.put(EnumStat.LINKED_EMPTY_DATES.getKey(),
                computeEmptyLinkedProperty(leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_DATES,
                        Namespace.DATE));

        /* Aggregate statistics */
        map.put(EnumStat.TOTAL_NON_EMPTY_PROPERTIES.getKey(), countTotalNonEmptyProperties(leftModel, rightModel));
        map.put(EnumStat.TOTAL_EMPTY_PROPERTIES.getKey(), countTotalEmptyProperties(leftModel, rightModel));
        map.put(EnumStat.TOTAL_PROPERTIES_PERCENTAGE.getKey(),
                calculateTotalNonEmptyPropertiesPercentage(leftModel, rightModel));

        /* Average statistics */
        map.put(EnumStat.AVERAGE_PROPERTIES_PER_POI.getKey(),
                calculateAveragePropertiesPerPOI(leftModel, rightModel));
        map.put(EnumStat.AVERAGE_EMPTY_PROPERTIES_PER_POI.getKey(),
                calculateAverageEmptyPropertiesPerPOI(leftModel, rightModel));
        map.put(EnumStat.LINKED_AVERAGE_PROPERTIES.getKey(),
                calculateAverageLinkedProperties(leftModel, rightModel, links));
        map.put(EnumStat.LINKED_AVERAGE_EMPTY_PROPERTIES.getKey(),
                calculateAverageEmptyLinkedProperties(leftModel, rightModel, links));

        map.put(EnumStat.LONGER_NAMES.getKey(),
                countLongerValue(leftModel, rightModel, links, EnumStat.LONGER_NAMES, Namespace.NAME,
                        Namespace.NAME_VALUE));

        map.put(EnumStat.LONGER_PHONES.getKey(),
                countLongerValue(leftModel, rightModel, links, EnumStat.LONGER_NAMES, Namespace.PHONE,
                        Namespace.CONTACT_VALUE));

        map.put(EnumStat.FULL_MATCH_STREETS.getKey(),
                countFullMatchingValue(leftModel, rightModel, links, EnumStat.FULL_MATCH_STREETS, Namespace.ADDRESS,
                        Namespace.STREET));

        map.put(EnumStat.FULL_MATCH_STREET_NUMBERS.getKey(),
                countFullMatchingValue(leftModel, rightModel, links, EnumStat.FULL_MATCH_STREET_NUMBERS,
                        Namespace.ADDRESS, Namespace.STREET_NUMBER));

        if (totalPOIsA == null || totalPOIsA == 0 || totalPOIsB == null || totalPOIsB == 0) {
            container.setValid(false);
        } else {
            container.setValid(true);
        }

        container.setMap(map);
        container.setComplete(true && container.isValid());

        return container;
    }

    @Override
    public StatisticsContainer collect(List<String> selected) {

        container.setComplete(false);

        Model leftModel = LeftDataset.getLeftDataset().getModel();
        Model rightModel = RightDataset.getRightDataset().getModel();
        Model linksModel = LinksModel.getLinksModel().getModel();
        List<Link> links = LinksModel.getLinksModel().getLinks();

        Map<String, EnumStat> statMap = EnumStat.getMap();
        for (String statKey : selected) {
            computeStat(statMap, statKey, leftModel, rightModel, linksModel, links);
        }

        container.setMap(map);
        container.setValid(true);
        container.setComplete(true);

        return container;
    }

    private void computeStat(Map<String, EnumStat> statMap, String statKey, Model leftModel, Model rightModel, 
            Model linksModel, List<Link> links) {

        EnumStat stat = statMap.get(statKey);
        switch (stat) {
            case TOTAL_POIS:
                map.put(EnumStat.TOTAL_POIS.getKey(), countTotalEntities(leftModel, rightModel));
                break;
            case TOTAL_TRIPLES:
                map.put(EnumStat.TOTAL_TRIPLES.getKey(), countTriples(leftModel, rightModel));
                break;
            case NON_EMPTY_NAMES:
                map.put(EnumStat.NON_EMPTY_NAMES.getKey(),
                        countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_NAMES, Namespace.NAME));
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
                        countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREET_NUMBERS,
                                Namespace.STREET_NUMBER));
                break;
            case NON_EMPTY_WEBSITES:
                map.put(EnumStat.NON_EMPTY_WEBSITES.getKey(),
                        countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_WEBSITES,
                                Namespace.HOMEPAGE));
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
                        countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_NAMES, Namespace.NAME));
                break;
            case EMPTY_PHONES:
                map.put(EnumStat.EMPTY_PHONES.getKey(),
                        countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_PHONES, Namespace.PHONE));
                break;
            case EMPTY_STREETS:
                map.put(EnumStat.EMPTY_STREETS.getKey(),
                        countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_STREETS, Namespace.STREET));
                break;
            case EMPTY_STREET_NUMBERS:
                map.put(EnumStat.EMPTY_STREET_NUMBERS.getKey(),
                        countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_STREET_NUMBERS,
                                Namespace.STREET_NUMBER));
                break;
            case EMPTY_WEBSITES:
                map.put(EnumStat.EMPTY_WEBSITES.getKey(),
                        countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_WEBSITES, Namespace.HOMEPAGE));
                break;
            case EMPTY_EMAILS:
                map.put(EnumStat.EMPTY_EMAILS.getKey(),
                        countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_EMAILS, Namespace.EMAIL));
                break;
            case EMPTY_DATES:
                map.put(EnumStat.EMPTY_DATES.getKey(),
                        countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_DATES, Namespace.DATE));
                break;
            case DISTINCT_PROPERTIES:
                map.put(EnumStat.DISTINCT_PROPERTIES.getKey(), countDistinctProperties(leftModel, rightModel));
                break;
            case PRIMARY_DATE_FORMATS_PERCENT:
                map.put(EnumStat.PRIMARY_DATE_FORMATS_PERCENT.getKey(),
                        calculatePercentageOfPrimaryDateFormats(leftModel, rightModel,
                                EnumStat.PRIMARY_DATE_FORMATS_PERCENT));
                break;
            case NAMES_PERCENT:
                map.put(EnumStat.NAMES_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.NAMES_PERCENT,
                                Namespace.NAME));
                break;
            case WEBSITE_PERCENT:
                map.put(EnumStat.WEBSITE_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.WEBSITE_PERCENT,
                                Namespace.HOMEPAGE));
                break;
            case EMAIL_PERCENT:
                map.put(EnumStat.EMAIL_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.EMAIL_PERCENT,
                                Namespace.EMAIL));
                break;
            case PHONES_PERCENT:
                map.put(EnumStat.PHONES_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.PHONES_PERCENT,
                                Namespace.PHONE));
                break;
            case STREETS_PERCENT:
                map.put(EnumStat.STREETS_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREETS_PERCENT,
                                Namespace.STREET));
                break;
            case STREET_NUMBERS_PERCENT:
                map.put(EnumStat.STREET_NUMBERS_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREET_NUMBERS_PERCENT,
                                Namespace.STREET_NUMBER));
                break;
            case LOCALITY_PERCENT:
                map.put(EnumStat.LOCALITY_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.LOCALITY_PERCENT,
                                Namespace.LOCALITY));
                break;
            case DATES_PERCENT:
                map.put(EnumStat.DATES_PERCENT.getKey(),
                        calculatePropertyPercentage(leftModel, rightModel, EnumStat.DATES_PERCENT, Namespace.DATE));
                break;
            case LINKED_POIS:
                map.put(EnumStat.LINKED_POIS.getKey(), countLinkedPOIs(linksModel));
                break;
            case LINKED_VS_TOTAL:
                map.put(EnumStat.LINKED_VS_TOTAL.getKey(),
                        countLinkedVsTotalPOIs(leftModel, rightModel, linksModel));
                break;
            case LINKED_TRIPLES:
                map.put(EnumStat.LINKED_TRIPLES.getKey(), countLinkedTriples(leftModel, rightModel, linksModel));
                break;
            case LINKED_NON_EMPTY_NAMES:
                map.put(EnumStat.LINKED_NON_EMPTY_NAMES.getKey(), computeNonEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_NAMES, Namespace.NAME,
                        Namespace.NAME_VALUE));
                break;
            case LINKED_NON_EMPTY_PHONES:
                map.put(EnumStat.LINKED_NON_EMPTY_PHONES.getKey(), computeNonEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_PHONES, Namespace.PHONE,
                        Namespace.CONTACT_VALUE));
                break;
            case LINKED_NON_EMPTY_STREETS:
                map.put(EnumStat.LINKED_NON_EMPTY_STREETS.getKey(), computeNonEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_STREETS, Namespace.ADDRESS,
                        Namespace.STREET));
                break;
            case LINKED_NON_EMPTY_STREET_NUMBERS:
                map.put(EnumStat.LINKED_NON_EMPTY_STREET_NUMBERS.getKey(), computeNonEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_STREET_NUMBERS,
                        Namespace.ADDRESS, Namespace.STREET_NUMBER));
                break;
            case LINKED_NON_EMPTY_WEBSITES:
                map.put(EnumStat.LINKED_NON_EMPTY_WEBSITES.getKey(), computeNonEmptyLinkedProperty(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_WEBSITES, Namespace.HOMEPAGE));
                break;
            case LINKED_NON_EMPTY_EMAILS:
                map.put(EnumStat.LINKED_NON_EMPTY_EMAILS.getKey(), computeNonEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_EMAILS, Namespace.EMAIL,
                        Namespace.CONTACT_VALUE));
                break;
            case LINKED_NON_EMPTY_DATES:
                map.put(EnumStat.LINKED_NON_EMPTY_DATES.getKey(), computeNonEmptyLinkedProperty(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_NON_EMPTY_DATES, Namespace.DATE));
                break;
            case LINKED_EMPTY_NAMES:
                map.put(EnumStat.LINKED_EMPTY_NAMES.getKey(), computeEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_NAMES, Namespace.NAME,
                        Namespace.NAME_VALUE));
                break;
            case LINKED_EMPTY_PHONES:
                map.put(EnumStat.LINKED_EMPTY_PHONES.getKey(), computeEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_PHONES, Namespace.PHONE,
                        Namespace.CONTACT_VALUE));
                break;
            case LINKED_EMPTY_STREETS:
                map.put(EnumStat.LINKED_EMPTY_STREETS.getKey(), computeEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_STREETS, Namespace.ADDRESS,
                        Namespace.STREET));
                break;
            case LINKED_EMPTY_STREET_NUMBERS:
                map.put(EnumStat.LINKED_EMPTY_STREET_NUMBERS.getKey(), computeEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_STREET_NUMBERS, Namespace.ADDRESS,
                        Namespace.STREET_NUMBER));
                break;
            case LINKED_EMPTY_WEBSITES:
                map.put(EnumStat.LINKED_EMPTY_WEBSITES.getKey(), computeEmptyLinkedProperty(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_WEBSITES, Namespace.HOMEPAGE));
                break;
            case LINKED_EMPTY_EMAILS:
                map.put(EnumStat.LINKED_EMPTY_EMAILS.getKey(), computeEmptyLinkedPropertyChain(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_EMAILS, Namespace.EMAIL,
                        Namespace.CONTACT_VALUE));
                break;
            case LINKED_EMPTY_DATES:
                map.put(EnumStat.LINKED_EMPTY_DATES.getKey(), computeEmptyLinkedProperty(
                        leftModel, rightModel, linksModel, EnumStat.LINKED_EMPTY_DATES, Namespace.DATE));
                break;
            case TOTAL_NON_EMPTY_PROPERTIES:
                map.put(EnumStat.TOTAL_NON_EMPTY_PROPERTIES.getKey(),
                        countTotalNonEmptyProperties(leftModel, rightModel));
                break;
            case TOTAL_EMPTY_PROPERTIES:
                map.put(EnumStat.TOTAL_EMPTY_PROPERTIES.getKey(), countTotalEmptyProperties(leftModel, rightModel));
                break;
            case TOTAL_PROPERTIES_PERCENTAGE:
                map.put(EnumStat.TOTAL_PROPERTIES_PERCENTAGE.getKey(),
                        calculateTotalNonEmptyPropertiesPercentage(leftModel, rightModel));
                break;
            case AVERAGE_PROPERTIES_PER_POI:
                map.put(EnumStat.AVERAGE_PROPERTIES_PER_POI.getKey(),
                        calculateAveragePropertiesPerPOI(leftModel, rightModel));
                break;
            case AVERAGE_EMPTY_PROPERTIES_PER_POI:
                map.put(EnumStat.AVERAGE_EMPTY_PROPERTIES_PER_POI.getKey(),
                        calculateAverageEmptyPropertiesPerPOI(leftModel, rightModel));
                break;
            case LINKED_AVERAGE_PROPERTIES:
                map.put(EnumStat.LINKED_AVERAGE_PROPERTIES.getKey(),
                        calculateAverageLinkedProperties(leftModel, rightModel, links));
                break;
            case LINKED_AVERAGE_EMPTY_PROPERTIES:
                map.put(EnumStat.LINKED_AVERAGE_EMPTY_PROPERTIES.getKey(),
                        calculateAverageEmptyLinkedProperties(leftModel, rightModel, links));
                break;
            case LONGER_NAMES:
                map.put(EnumStat.LONGER_NAMES.getKey(),
                        countLongerValue(leftModel, rightModel, links, EnumStat.LONGER_NAMES, Namespace.NAME,
                                Namespace.NAME_VALUE));
                break;
            case LONGER_PHONES:
                map.put(EnumStat.LONGER_PHONES.getKey(),
                        countLongerValue(leftModel, rightModel, links, EnumStat.LONGER_NAMES, Namespace.PHONE,
                                Namespace.CONTACT_VALUE));
                break;
            case FULL_MATCH_STREETS:
                map.put(EnumStat.FULL_MATCH_STREETS.getKey(),
                        countFullMatchingValue(leftModel, rightModel, links, EnumStat.FULL_MATCH_STREETS,
                                Namespace.ADDRESS, Namespace.STREET));
                break;
            case FULL_MATCH_STREET_NUMBERS:
                map.put(EnumStat.FULL_MATCH_STREET_NUMBERS.getKey(),
                        countFullMatchingValue(leftModel, rightModel, links, EnumStat.FULL_MATCH_STREET_NUMBERS,
                                Namespace.ADDRESS, Namespace.STREET_NUMBER));
                break;
            case FUSED_VS_LINKED:
                map.put(EnumStat.FUSED_VS_LINKED.getKey(), countFusedVsLinked(links, EnumStat.FUSED_VS_LINKED));
                break;
            case FUSED_REJECTED_VS_LINKED:
                map.put(EnumStat.FUSED_REJECTED_VS_LINKED.getKey(), countRejectedVsLinked(links, EnumStat.FUSED_REJECTED_VS_LINKED));
                break;
            case FUSED_INITIAL:
                LOG.info(":counting initial");
                map.put(EnumStat.FUSED_INITIAL.getKey(), countInitialVsFused(leftModel, rightModel, EnumStat.FUSED_INITIAL));
                break;
        }
    }

    public StatisticResultPair countTotalEntities(Model a, Model b) {

        Integer totalA = SparqlRepository.countPOIs(a);
        Integer totalB = SparqlRepository.countPOIs(b);
        Integer total = totalA + totalB;

        totalPOIsA = totalA;
        totalPOIsB = totalB;

        if (cannotCompute(totalPOIsA, totalPOIsB)) {
            return getFailedStatistic(EnumStat.TOTAL_POIS, Namespace.SOURCE);
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        pair.setValueTotal(total.toString());
        pair.setTitle(EnumStat.TOTAL_POIS.toString());
        pair.setLegendTotal(EnumStat.TOTAL_POIS.getLegendTotal());
        pair.setLegendA(EnumStat.TOTAL_POIS.getLegendA());
        pair.setLegendB(EnumStat.TOTAL_POIS.getLegendB());

        return pair;
    }

    public StatisticResultPair countTriples(Model a, Model b) {

        Long totalA = a.size();
        Long totalB = b.size();
        Long total = totalA + totalB;

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.TRIPLE_BASED));
        pair.setValueTotal(total.toString());
        pair.setTitle(EnumStat.TOTAL_TRIPLES.toString());
        pair.setLegendTotal(EnumStat.TOTAL_TRIPLES.getLegendTotal());
        pair.setLegendA(EnumStat.TOTAL_TRIPLES.getLegendA());
        pair.setLegendB(EnumStat.TOTAL_TRIPLES.getLegendB());

        return pair;
    }

    public StatisticResultPair countLinkedVsTotalPOIs(Model a, Model b, Model links) {

        Integer linkedA = SparqlRepository.countLinkedPOIsA(links);
        Integer linkedB = SparqlRepository.countLinkedPOIsB(links);

        Integer linked = (linkedA + linkedB);

        if (totalPOIsA == null || totalPOIsB == null) {
            StatisticResultPair totalEntities = countTotalEntities(a, b);
            totalPOIsA = Integer.parseInt(totalEntities.getValueA());
            totalPOIsB = Integer.parseInt(totalEntities.getValueB());
        }

        if (cannotCompute(totalPOIsA, totalPOIsB)) {
            return getFailedStatistic(EnumStat.LINKED_VS_TOTAL, Namespace.SOURCE);
        }

        Integer total = totalPOIsA + totalPOIsB;

        StatisticResultPair pair = new StatisticResultPair(linked.toString(), total.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        pair.setTitle(EnumStat.LINKED_VS_TOTAL.toString());
        pair.setLegendTotal(EnumStat.LINKED_VS_TOTAL.getLegendTotal());
        pair.setLegendA(EnumStat.LINKED_VS_TOTAL.getLegendA());
        pair.setLegendB(EnumStat.LINKED_VS_TOTAL.getLegendB());
        return pair;
    }

    public StatisticResultPair countLinkedPOIs(Model links) {

        Integer linkedPOIsA = SparqlRepository.countDistinctSubjects(links);
        Integer linkedPOIsB = SparqlRepository.countDistinctObjects(links);

        StatisticResultPair pair = new StatisticResultPair(linkedPOIsA.toString(), linkedPOIsB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        pair.setTitle(EnumStat.LINKED_POIS.toString());
        pair.setLegendTotal(EnumStat.LINKED_POIS.getLegendTotal());
        pair.setLegendA(EnumStat.LINKED_POIS.getLegendA());
        pair.setLegendB(EnumStat.LINKED_POIS.getLegendB());

        return pair;
    }

    public StatisticResultPair countLinkedTriples(Model modelA, Model modelB, Model linksModel) {

        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);

        Integer linkedTriplesA = SparqlRepository.countLinkedTriplesA(linkedA);
        Integer linkedTriplesB = SparqlRepository.countLinkedTriplesB(linkedB);

        StatisticResultPair pair = new StatisticResultPair(linkedTriplesA.toString(), linkedTriplesB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.TRIPLE_BASED));
        pair.setTitle(EnumStat.LINKED_TRIPLES.toString());
        pair.setLegendTotal(EnumStat.LINKED_TRIPLES.getLegendTotal());
        pair.setLegendA(EnumStat.LINKED_TRIPLES.getLegendA());
        pair.setLegendB(EnumStat.LINKED_TRIPLES.getLegendB());

        return pair;
    }

    public StatisticResultPair countDistinctProperties(Model a, Model b) {

        Integer distinctPropertiesA = SparqlRepository.countDistinctProperties(a);
        Integer distinctPropertiesB = SparqlRepository.countDistinctProperties(b);
        Integer total = distinctPropertiesA + distinctPropertiesB;

        StatisticResultPair pair
                = new StatisticResultPair(distinctPropertiesA.toString(), distinctPropertiesB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(EnumStat.DISTINCT_PROPERTIES.toString());
        pair.setLegendTotal(EnumStat.DISTINCT_PROPERTIES.getLegendTotal());
        pair.setLegendA(EnumStat.DISTINCT_PROPERTIES.getLegendA());
        pair.setLegendB(EnumStat.DISTINCT_PROPERTIES.getLegendB());

        return pair;
    }

    public StatisticResultPair countNonEmptyProperty(Model a, Model b, EnumStat stat, String property) {

        Integer propertyA = countNonEmptyProperty(property, a);
        Integer propertyB = countNonEmptyProperty(property, b);
        Integer total = propertyA + propertyB;

        StatisticResultPair pair = new StatisticResultPair(propertyA.toString(), propertyB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair countNonEmptyProperty(Model a, Model b, EnumStat stat, String property1, String property2) {

        Integer propertyA = countNonEmptyProperty(property1, property2, a);
        Integer propertyB = countNonEmptyProperty(property1, property2, b);
        Integer total = propertyA + propertyB;

        StatisticResultPair pair = new StatisticResultPair(propertyA.toString(), propertyB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair countEmptyProperty(Model a, Model b, EnumStat stat, String property) {

        Integer nA;
        Integer nB;
        Integer total;

        if (map.get(stat.getKey()) != null) {
            try {

                nA = Integer.parseInt(map.get(stat.getKey()).getValueA());
                nB = Integer.parseInt(map.get(stat.getKey()).getValueB());
                total = nA + nB;

            } catch (NumberFormatException ex) {
                LOG.info("Could not compute empty names. ", ex);
                StatisticResultPair pair = new StatisticResultPair("0", "0", null);
                pair.setTitle(EnumStat.UNDEFINED.toString());

                return pair;
            }
        } else {
            //todo: consider counting all non-empty using chain query. Only names have multiple distinct nodes for the same property for now.
            if(stat.equals(EnumStat.EMPTY_NAMES)){
                StatisticResultPair nonEmptyProperty = countNonEmptyProperty(a, b, stat, Namespace.NAME, Namespace.NAME_VALUE);
                nA = Integer.parseInt(nonEmptyProperty.getValueA());
                nB = Integer.parseInt(nonEmptyProperty.getValueB());
            } else {
                StatisticResultPair nonEmptyProperty = countNonEmptyProperty(a, b, stat, property);
                nA = Integer.parseInt(nonEmptyProperty.getValueA());
                nB = Integer.parseInt(nonEmptyProperty.getValueB());
            }

            total = nA + nB;
        }

        if (totalPOIsA == null || totalPOIsB == null) {
            StatisticResultPair totalEntities = countTotalEntities(a, b);
            totalPOIsA = Integer.parseInt(totalEntities.getValueA());
            totalPOIsB = Integer.parseInt(totalEntities.getValueB());
        }

        if (cannotCompute(totalPOIsA, totalPOIsB)) {
            return getFailedStatistic(stat, property);
        }

        Integer emptyA = totalPOIsA - nA;
        Integer emptyB = totalPOIsB - nB;

        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair countTotalNonEmptyProperties(Model leftModel, Model rightModel) {

        Integer totalA;
        Integer totalB;

        try {

            StatisticResultPair names = map.get(EnumStat.NON_EMPTY_NAMES.getKey());
            StatisticResultPair phones = map.get(EnumStat.NON_EMPTY_PHONES.getKey());
            StatisticResultPair streets = map.get(EnumStat.NON_EMPTY_STREETS.getKey());
            StatisticResultPair streetNumbers = map.get(EnumStat.NON_EMPTY_STREET_NUMBERS.getKey());
            StatisticResultPair websites = map.get(EnumStat.NON_EMPTY_WEBSITES.getKey());
            StatisticResultPair emails = map.get(EnumStat.NON_EMPTY_EMAILS.getKey());
            StatisticResultPair dates = map.get(EnumStat.NON_EMPTY_DATES.getKey());

            if (names == null) {
                names = countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_NAMES, Namespace.NAME);
            }

            if (phones == null) {
                phones = countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_PHONES, Namespace.PHONE);
            }

            if (streets == null) {
                streets = countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREETS, Namespace.STREET);
            }

            if (streetNumbers == null) {
                streetNumbers = countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_STREET_NUMBERS,
                        Namespace.STREET_NUMBER);
            }

            if (websites == null) {
                websites = countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_WEBSITES,
                        Namespace.HOMEPAGE);
            }

            if (emails == null) {
                emails = countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_EMAILS, Namespace.EMAIL);
            }

            if (dates == null) {
                dates = countNonEmptyProperty(leftModel, rightModel, EnumStat.NON_EMPTY_DATES, Namespace.DATE);
            }

            Integer a1 = Integer.parseInt(names.getValueA());
            Integer a2 = Integer.parseInt(phones.getValueA());
            Integer a3 = Integer.parseInt(streets.getValueA());
            Integer a4 = Integer.parseInt(streetNumbers.getValueA());
            Integer a5 = Integer.parseInt(websites.getValueA());
            Integer a6 = Integer.parseInt(emails.getValueA());
            Integer a7 = Integer.parseInt(dates.getValueA());

            Integer b1 = Integer.parseInt(names.getValueB());
            Integer b2 = Integer.parseInt(phones.getValueB());
            Integer b3 = Integer.parseInt(streets.getValueB());
            Integer b4 = Integer.parseInt(streetNumbers.getValueB());
            Integer b5 = Integer.parseInt(websites.getValueB());
            Integer b6 = Integer.parseInt(emails.getValueB());
            Integer b7 = Integer.parseInt(dates.getValueB());

            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4 + b5 + b6 + b7;

        } catch (NumberFormatException ex) {
            LOG.warn("Could not compute total non empty properties due to missing properties. ", ex);
            StatisticResultPair pair = new StatisticResultPair("0", "0", null);
            pair.setTitle("Could not compute");

            return pair;
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        EnumStat stat = EnumStat.TOTAL_NON_EMPTY_PROPERTIES;
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair countTotalEmptyProperties(Model leftModel, Model rightModel) {

        StatisticResultPair names = map.get(EnumStat.EMPTY_NAMES.getKey());
        StatisticResultPair phones = map.get(EnumStat.EMPTY_PHONES.getKey());
        StatisticResultPair streets = map.get(EnumStat.EMPTY_STREETS.getKey());
        StatisticResultPair streetNumbers = map.get(EnumStat.EMPTY_STREET_NUMBERS.getKey());
        StatisticResultPair websites = map.get(EnumStat.EMPTY_WEBSITES.getKey());
        StatisticResultPair emails = map.get(EnumStat.EMPTY_EMAILS.getKey());
        StatisticResultPair dates = map.get(EnumStat.EMPTY_DATES.getKey());

        if (names == null) {
            names = countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_NAMES, Namespace.NAME);
        }

        if (phones == null) {
            phones = countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_PHONES, Namespace.PHONE);
        }

        if (streets == null) {
            streets = countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_STREETS, Namespace.STREET);
        }

        if (streetNumbers == null) {
            streetNumbers = countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_STREET_NUMBERS,
                    Namespace.STREET_NUMBER);
        }

        if (websites == null) {
            websites = countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_WEBSITES, Namespace.HOMEPAGE);
        }

        if (emails == null) {
            emails = countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_EMAILS, Namespace.EMAIL);
        }

        if (dates == null) {
            dates = countEmptyProperty(leftModel, rightModel, EnumStat.EMPTY_DATES, Namespace.DATE);
        }

        Integer totalA;
        Integer totalB;

        try {

            Integer a1 = Integer.parseInt(names.getValueA());
            Integer a2 = Integer.parseInt(phones.getValueA());
            Integer a3 = Integer.parseInt(streets.getValueA());
            Integer a4 = Integer.parseInt(streetNumbers.getValueA());
            Integer a5 = Integer.parseInt(websites.getValueA());
            Integer a6 = Integer.parseInt(emails.getValueA());
            Integer a7 = Integer.parseInt(dates.getValueA());

            Integer b1 = Integer.parseInt(names.getValueB());
            Integer b2 = Integer.parseInt(phones.getValueB());
            Integer b3 = Integer.parseInt(streets.getValueB());
            Integer b4 = Integer.parseInt(streetNumbers.getValueB());
            Integer b5 = Integer.parseInt(websites.getValueB());
            Integer b6 = Integer.parseInt(emails.getValueB());
            Integer b7 = Integer.parseInt(dates.getValueB());

            totalA = a1 + a2 + a3 + a4 + a5 + a6 + a7;
            totalB = b1 + b2 + b3 + b4 + b5 + b6 + b7;

        } catch (NumberFormatException ex) {
            return getFailedStatistic(EnumStat.TOTAL_EMPTY_PROPERTIES, "");
        }

        StatisticResultPair pair = new StatisticResultPair(totalA.toString(), totalB.toString(), null);
        EnumStat stat = EnumStat.TOTAL_EMPTY_PROPERTIES;
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair calculatePercentageOfPrimaryDateFormats(Model leftModel, Model rightModel, EnumStat stat) {

        String date = Namespace.DATE;

        int totalDatesA = SparqlRepository.countProperty(leftModel, date);
        int totalDatesB = SparqlRepository.countProperty(rightModel, date);

        if (cannotCompute(totalDatesA, totalDatesB)) {
            return getFailedStatistic(stat, Namespace.DATE);
        }

        IsDatePrimaryFormat isDatePrimaryFormat = new IsDatePrimaryFormat();

        int primaryFormatCounter = 0;
        NodeIterator objectsA = SparqlRepository.getObjectsOfProperty(date, leftModel);
        while (objectsA.hasNext()) {
            RDFNode node = objectsA.next();
            if (node.isLiteral()) {
                Literal dateLiteral = node.asLiteral();
                if (isDatePrimaryFormat.evaluate(dateLiteral)) {
                    primaryFormatCounter++;
                }
            }
        }

        Double percentA = roundHalfDown(primaryFormatCounter / (double) totalDatesA);

        primaryFormatCounter = 0;

        NodeIterator objectsB = SparqlRepository.getObjectsOfProperty(date, rightModel);
        while (objectsB.hasNext()) {
            RDFNode node = objectsB.next();
            if (node.isLiteral()) {
                Literal literalDate = node.asLiteral();
                if (isDatePrimaryFormat.evaluate(literalDate)) {
                    primaryFormatCounter++;
                }
            }
        }

        Double percentB = roundHalfDown(primaryFormatCounter / (double) totalDatesB);

        StatisticResultPair pair = new StatisticResultPair(percentA.toString(), percentB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PERCENT));
        pair.setValueTotal("100");
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());
        return pair;
    }

    public StatisticResultPair calculatePropertyPercentage(Model a, Model b, EnumStat stat, String property) {

        if (totalPOIsA == null || totalPOIsB == null) {
            StatisticResultPair totalPois = countTotalEntities(a, b);
            totalPOIsA = Integer.parseInt(totalPois.getValueA());
            totalPOIsB = Integer.parseInt(totalPois.getValueB());
        }

        if (cannotCompute(totalPOIsA, totalPOIsB)) {
            return getFailedStatistic(stat, Namespace.SOURCE);
        }

        int propertyA = SparqlRepository.countProperty(a, property);
        int propertyB = SparqlRepository.countProperty(b, property);

        Double percentageA = roundHalfDown((100 * propertyA) / (double) totalPOIsA);
        Double percentageB = roundHalfDown((100 * propertyB) / (double) totalPOIsB);

        StatisticResultPair pair = new StatisticResultPair(percentageA.toString(), percentageB.toString(), null);
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PERCENT));
        pair.setValueTotal("100");
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair calculateTotalNonEmptyPropertiesPercentage(Model leftModel, Model rightModel) {

        Double totalPropPercentageA;
        Double totalPropPercentageB;

        StatisticResultPair names = map.get(EnumStat.NAMES_PERCENT.getKey());
        StatisticResultPair phones = map.get(EnumStat.PHONES_PERCENT.getKey());
        StatisticResultPair streets = map.get(EnumStat.STREETS_PERCENT.getKey());
        StatisticResultPair streetNumbers = map.get(EnumStat.STREET_NUMBERS_PERCENT.getKey());
        StatisticResultPair websites = map.get(EnumStat.WEBSITE_PERCENT.getKey());
        StatisticResultPair emails = map.get(EnumStat.EMAIL_PERCENT.getKey());
        StatisticResultPair locality = map.get(EnumStat.LOCALITY_PERCENT.getKey());
        StatisticResultPair dates = map.get(EnumStat.DATES_PERCENT.getKey());

        if (names == null) {
            names = calculatePropertyPercentage(leftModel, rightModel, EnumStat.NAMES_PERCENT, Namespace.NAME);
        }

        if (phones == null) {
            phones = calculatePropertyPercentage(leftModel, rightModel, EnumStat.PHONES_PERCENT, Namespace.PHONE);
        }

        if (streets == null) {
            streets = calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREETS_PERCENT, Namespace.STREET);
        }

        if (streetNumbers == null) {
            streetNumbers = calculatePropertyPercentage(leftModel, rightModel, EnumStat.STREET_NUMBERS_PERCENT,
                    Namespace.STREET_NUMBER);
        }

        if (websites == null) {
            websites = calculatePropertyPercentage(leftModel, rightModel, EnumStat.WEBSITE_PERCENT, Namespace.HOMEPAGE);
        }

        if (emails == null) {
            emails = calculatePropertyPercentage(leftModel, rightModel, EnumStat.EMAIL_PERCENT, Namespace.EMAIL);
        }

        if (locality == null) {
            locality = calculatePropertyPercentage(leftModel, rightModel, EnumStat.LOCALITY_PERCENT, Namespace.LOCALITY);
        }

        if (dates == null) {
            dates = calculatePropertyPercentage(leftModel, rightModel, EnumStat.NON_EMPTY_DATES, Namespace.DATE);
        }

        try {

            Double nA = Double.parseDouble(names.getValueA());
            Double nB = Double.parseDouble(names.getValueB());
            Double pA = Double.parseDouble(phones.getValueA());
            Double pB = Double.parseDouble(phones.getValueB());
            Double sA = Double.parseDouble(streets.getValueA());
            Double sB = Double.parseDouble(streets.getValueB());
            Double snA = Double.parseDouble(streetNumbers.getValueA());
            Double snB = Double.parseDouble(streetNumbers.getValueB());
            Double wA = Double.parseDouble(websites.getValueA());
            Double wB = Double.parseDouble(websites.getValueB());
            Double eA = Double.parseDouble(emails.getValueA());
            Double eB = Double.parseDouble(emails.getValueB());
            Double lA = Double.parseDouble(locality.getValueA());
            Double lB = Double.parseDouble(locality.getValueB());
            Double dA = Double.parseDouble(dates.getValueA());
            Double dB = Double.parseDouble(dates.getValueB());

            totalPropPercentageA = roundHalfDown((nA + pA + sA + snA + wA + eA + lA + dA) / 8);
            totalPropPercentageB = roundHalfDown((nB + pB + sB + snB + wB + eB + lB + dB) / 8);

        } catch (NullPointerException | NumberFormatException e) {//parseDouble(null) throws NullPointerException, not NumberFormatException
            return getFailedStatistic(EnumStat.TOTAL_PROPERTIES_PERCENTAGE, "");
        }

        StatisticResultPair pair
                = new StatisticResultPair(totalPropPercentageA.toString(), totalPropPercentageB.toString(), null);

        EnumStat stat = EnumStat.TOTAL_PROPERTIES_PERCENTAGE;
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PERCENT));
        pair.setValueTotal("100");
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    private Integer countNonEmptyProperty(String property, Model model) {
        return SparqlRepository.countProperty(model, property);
    }

    private Integer countNonEmptyProperty(String property1, String property2, Model model) {
        return SparqlRepository.countPropertyChains(model, property1, property2);
    }

    public StatisticResultPair computeNonEmptyLinkedPropertyChain(Model modelA, Model modelB, Model linksModel,
            EnumStat stat, String property1, String property2) {

        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);

        Integer nonEmptyCountA = SparqlRepository.countLinkedWithPropertyA(linkedA, property1, property2);
        Integer nonEmptyCountB = SparqlRepository.countLinkedWithPropertyB(linkedB, property1, property2);
        Integer total = nonEmptyCountA + nonEmptyCountB;

        StatisticResultPair pair = new StatisticResultPair(nonEmptyCountA.toString(), nonEmptyCountB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair computeNonEmptyLinkedProperty(Model modelA, Model modelB, Model linksModel,
            EnumStat stat, String property) {

        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel);

        Integer nonEmptyCountA = SparqlRepository.countLinkedWithPropertyA(linkedA, property);
        Integer nonEmptyCountB = SparqlRepository.countLinkedWithPropertyB(linkedB, property);
        Integer total = nonEmptyCountA + nonEmptyCountB;

        StatisticResultPair pair = new StatisticResultPair(nonEmptyCountA.toString(), nonEmptyCountB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair computeEmptyLinkedPropertyChain(Model a, Model b, Model linksModel,
            EnumStat stat, String property1, String property2) {

        StatisticResultPair linkedPOIs = map.get(EnumStat.LINKED_POIS.getKey());

        if (linkedPOIs == null) {
            linkedPOIs = countLinkedPOIs(linksModel);
        }

        StatisticResultPair nonEmpty = map.get(stat.getKey());

        if (nonEmpty == null) {
            nonEmpty = computeNonEmptyLinkedPropertyChain(a, b, linksModel, stat, property1, property2);
        }

        Integer emptyA = Integer.parseInt(linkedPOIs.getValueA()) - Integer.parseInt(nonEmpty.getValueA());
        Integer emptyB = Integer.parseInt(linkedPOIs.getValueB()) - Integer.parseInt(nonEmpty.getValueB());
        Integer total = emptyA + emptyB;

        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair computeEmptyLinkedProperty(Model a, Model b, Model linksModel,
            EnumStat stat, String property) {

        StatisticResultPair linkedPOIs = map.get(EnumStat.LINKED_POIS.getKey());

        if (linkedPOIs == null) {
            linkedPOIs = countLinkedPOIs(linksModel);
        }

        StatisticResultPair nonEmpty = map.get(stat.getKey());

        if (nonEmpty == null) {
            nonEmpty = computeNonEmptyLinkedProperty(a, b, linksModel, stat, property);
        }

        Integer emptyA = Integer.parseInt(linkedPOIs.getValueA()) - Integer.parseInt(nonEmpty.getValueA());
        Integer emptyB = Integer.parseInt(linkedPOIs.getValueB()) - Integer.parseInt(nonEmpty.getValueB());
        Integer total = emptyA + emptyB;

        StatisticResultPair pair = new StatisticResultPair(emptyA.toString(), emptyB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair calculateAveragePropertiesPerPOI(Model leftModel, Model rightModel) {

        StatisticResultPair distinctProperties = map.get(EnumStat.DISTINCT_PROPERTIES.getKey());

        if (distinctProperties == null) {
            distinctProperties = countDistinctProperties(leftModel, rightModel);
        }

        try {
            
            int distinctPropertiesA = Integer.parseInt(distinctProperties.getValueA());
            int distinctPropertiesB = Integer.parseInt(distinctProperties.getValueB());

            Double averagePropertiesA = SparqlRepository.averagePropertiesPerPOI(leftModel, distinctPropertiesA);
            Double averagePropertiesB = SparqlRepository.averagePropertiesPerPOI(rightModel, distinctPropertiesB);
            Double total = averagePropertiesA + averagePropertiesB;

            StatisticResultPair pair
                    = new StatisticResultPair(averagePropertiesA.toString(), averagePropertiesB.toString(), null);

            EnumStat stat = EnumStat.AVERAGE_PROPERTIES_PER_POI;
            pair.setType(EnumStatViewType.BAR);
            pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
            pair.setValueTotal(total.toString());
            pair.setTitle(stat.toString());
            pair.setLegendTotal(stat.getLegendTotal());
            pair.setLegendA(stat.getLegendA());
            pair.setLegendB(stat.getLegendB());

            return pair;

        } catch (NumberFormatException ex) {
            return getFailedStatistic(EnumStat.AVERAGE_PROPERTIES_PER_POI, "");
        }
    }

    public StatisticResultPair calculateAverageEmptyPropertiesPerPOI(Model leftModel, Model rightModel) {

        StatisticResultPair distinctProperties = map.get(EnumStat.DISTINCT_PROPERTIES.getKey());

        if (distinctProperties == null) {
            distinctProperties = countDistinctProperties(leftModel, rightModel);
        }

        try {

            int distinctPropertiesA = Integer.parseInt(distinctProperties.getValueA());
            int distinctPropertiesB = Integer.parseInt(distinctProperties.getValueB());

            Double averageEmptyPropertiesA
                    = SparqlRepository.averageEmptyPropertiesPerPOI(leftModel, distinctPropertiesA);
            Double averageEmptyPropertiesB
                    = SparqlRepository.averageEmptyPropertiesPerPOI(rightModel, distinctPropertiesB);
            Double total = averageEmptyPropertiesA + averageEmptyPropertiesB;

            StatisticResultPair pair
                    = new StatisticResultPair(averageEmptyPropertiesA.toString(), averageEmptyPropertiesB.toString(), null);

            EnumStat stat = EnumStat.AVERAGE_EMPTY_PROPERTIES_PER_POI;
            pair.setType(EnumStatViewType.BAR);
            pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
            pair.setValueTotal(total.toString());
            pair.setTitle(stat.toString());
            pair.setLegendTotal(stat.getLegendTotal());
            pair.setLegendA(stat.getLegendA());
            pair.setLegendB(stat.getLegendB());

            return pair;

        } catch (NumberFormatException ex) {
            return getFailedStatistic(EnumStat.AVERAGE_EMPTY_PROPERTIES_PER_POI, "");
        }
    }

    public StatisticResultPair calculateAverageLinkedProperties(Model leftModel, Model rightModel, List<Link> links) {

        if (totalPOIsA == null || totalPOIsB == null) {
            StatisticResultPair totalPois = countTotalEntities(leftModel, rightModel);
            totalPOIsA = Integer.parseInt(totalPois.getValueA());
            totalPOIsB = Integer.parseInt(totalPois.getValueB());
        }

        if (cannotCompute(totalPOIsA, totalPOIsB)) {
            return getFailedStatistic(EnumStat.LINKED_AVERAGE_PROPERTIES, Namespace.SOURCE);
        }

        int sumA = 0;
        int sumB = 0;

        for (Link link : links) {
            sumA = getAverageProperties(leftModel, sumA, link.getNodeA());
            sumB = getAverageProperties(leftModel, sumA, link.getNodeB());
        }

        Double averageLinkedPropertiesA = sumA / (double) totalPOIsA;
        Double averageLinkedPropertiesB = sumB / (double) totalPOIsB;
        Double total = averageLinkedPropertiesA + averageLinkedPropertiesB;

        StatisticResultPair pair
                = new StatisticResultPair(averageLinkedPropertiesA.toString(), averageLinkedPropertiesB.toString(), null);

        EnumStat stat = EnumStat.LINKED_AVERAGE_PROPERTIES;
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
        pair.setValueTotal(total.toString());
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair calculateAverageEmptyLinkedProperties(Model leftModel, Model rightModel, List<Link> links) {

        if (totalPOIsA == null || totalPOIsB == null) {
            StatisticResultPair totalPois = countTotalEntities(leftModel, rightModel);
            totalPOIsA = Integer.parseInt(totalPois.getValueA());
            totalPOIsB = Integer.parseInt(totalPois.getValueB());
        }

        if (cannotCompute(totalPOIsA, totalPOIsB)) {
            return getFailedStatistic(EnumStat.LINKED_AVERAGE_EMPTY_PROPERTIES, Namespace.SOURCE);
        }

        StatisticResultPair distinctProperties = map.get(EnumStat.DISTINCT_PROPERTIES.getKey());

        if (distinctProperties == null) {
            distinctProperties = countDistinctProperties(leftModel, rightModel);
        }

        StatisticResultPair averageProperties = map.get(EnumStat.LINKED_AVERAGE_PROPERTIES.getKey());

        if (averageProperties == null) {
            averageProperties = calculateAverageLinkedProperties(leftModel, rightModel, links);
        }

        try {
            
            int distinctA = Integer.parseInt(distinctProperties.getValueA());
            int distinctB = Integer.parseInt(distinctProperties.getValueB());

            Double avgEmptyA = distinctA - Double.parseDouble(averageProperties.getValueA());
            Double avgEmptyB = distinctB - Double.parseDouble(averageProperties.getValueB());

            Double total = avgEmptyA + avgEmptyB;

            StatisticResultPair pair
                    = new StatisticResultPair(avgEmptyA.toString(), avgEmptyB.toString(), null);

            EnumStat stat = EnumStat.LINKED_AVERAGE_EMPTY_PROPERTIES;
            pair.setType(EnumStatViewType.BAR);
            pair.setGroup(new StatGroup(EnumStatGroup.PROPERTY));
            pair.setValueTotal(total.toString());
            pair.setTitle(stat.toString());
            pair.setLegendTotal(stat.getLegendTotal());
            pair.setLegendA(stat.getLegendA());
            pair.setLegendB(stat.getLegendB());

            return pair;
        } catch (NumberFormatException ex) {
            return getFailedStatistic(EnumStat.LINKED_AVERAGE_EMPTY_PROPERTIES, "");
        }
    }

    public StatisticResultPair countLongerValue(Model leftModel, Model rightModel, List<Link> links,
            EnumStat stat, String property1, String property2) {

        Integer countLongerValueA = 0;
        Integer countLongerValueB = 0;

        for (Link link : links) {

            EnumEntity longerValue = countLongerProperty(link, property1, property2, leftModel, rightModel);
            switch (longerValue) {
                case LEFT:
                    countLongerValueA++;
                    break;
                case RIGHT:
                    countLongerValueB++;
                    break;
                case UNDEFINED:
                    //donnot count
                    break;
            }
        }

        StatisticResultPair pair
                = new StatisticResultPair(countLongerValueA.toString(), countLongerValueB.toString(), null);

        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.TRIPLE_BASED));
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());
        pair.setLegendA(stat.getLegendA());
        pair.setLegendB(stat.getLegendB());

        return pair;
    }

    public StatisticResultPair countFullMatchingValue(Model leftModel, Model rightModel, List<Link> links,
            EnumStat stat, String property1, String property2) {

        Integer fullMatchingValueCount = 0;

        for (Link link : links) {

            boolean fullMatch = countFullMatch(link, property1, property2, leftModel, rightModel);
            if (fullMatch) {
                fullMatchingValueCount++;
            }
        }

        StatisticResultPair pair
                = new StatisticResultPair(null, null, fullMatchingValueCount.toString());
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.TRIPLE_BASED));
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());

        return pair;
    }

    public StatisticResultPair countFusedVsLinked(List<Link> links, EnumStat stat) {

        if(fusedPOIs == null){
            getFailedStatistic(EnumStat.FUSED_VS_LINKED, Namespace.SOURCE);
        }

        Integer linkedPOIs = links.size();
        Integer total = fusedPOIs + linkedPOIs;
        StatisticResultPair pair = new StatisticResultPair(fusedPOIs.toString(), linkedPOIs.toString(), total.toString());
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());

        return pair;
    }

    public StatisticResultPair countRejectedVsLinked(List<Link> links, EnumStat stat) {

        if(rejected == null){
            getFailedStatistic(EnumStat.FUSED_REJECTED_VS_LINKED, Namespace.SOURCE);
        }

        Integer linkedPOIs = links.size();
        StatisticResultPair pair = new StatisticResultPair(rejected.toString(), linkedPOIs.toString(), linkedPOIs.toString());
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());

        return pair;
    }

    public StatisticResultPair countInitialVsFused(Model a, Model b, EnumStat stat) {

        if(fusedPOIs == null){
            getFailedStatistic(EnumStat.FUSED_INITIAL, Namespace.SOURCE);
        }

        if (totalPOIsA == null || totalPOIsB == null) {
            StatisticResultPair totalEntities = countTotalEntities(a, b);
            totalPOIsA = Integer.parseInt(totalEntities.getValueA());
            totalPOIsB = Integer.parseInt(totalEntities.getValueB());
        }

        if (cannotCompute(totalPOIsA, totalPOIsB)) {
            return getFailedStatistic(EnumStat.FUSED_INITIAL, Namespace.SOURCE);
        }

        Integer totalPoisInFused = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getInstance().getFused()),"utf-8"));
            for(String line; (line = br.readLine()) !=null;) {
                if(line.contains(Namespace.SOURCE)){
                    totalPoisInFused++;
                }
            }
        } catch (ApplicationException | IOException ex) {
            LOG.fatal(ex);
            throw new ApplicationException(ex.getMessage());
        }
        
        StatisticResultPair pair = new StatisticResultPair(totalPOIsA.toString(), totalPOIsB.toString(), null);
        pair.setValueTotal(totalPoisInFused.toString());
        pair.setType(EnumStatViewType.BAR);
        pair.setGroup(new StatGroup(EnumStatGroup.POI_BASED));
        pair.setTitle(stat.toString());
        pair.setLegendTotal(stat.getLegendTotal());

        return pair;
    }

    private int getAverageProperties(Model model, int sum, String node) {
        int properties = SparqlRepository.countDistinctPropertiesOfResource(model, node);
        sum = sum + properties;

        return sum;
    }

    private double roundHalfDown(Double d) {
        return new BigDecimal(d).
                setScale(SpecificationConstants.Similarity.ROUND_DECIMALS_2, RoundingMode.DOWN).doubleValue();
    }

    private EnumEntity countLongerProperty(Link link, String prop1, String prop2, Model modelA, Model modelB) {

        Literal literalA = SparqlRepository.getObjectOfProperty(link.getNodeA(), prop1, prop2, modelA);
        Literal literalB = SparqlRepository.getObjectOfProperty(link.getNodeB(), prop1, prop2, modelB);

        if (literalA == null && literalB == null) {
            return EnumEntity.UNDEFINED;
        } else if (literalA != null && literalB == null) {
            return EnumEntity.LEFT;
        } else if (literalB != null && literalA == null) {
            return EnumEntity.RIGHT;
        }

        //both are not null, checked above
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
        Literal literalB = SparqlRepository.getObjectOfProperty(link.getNodeB(), prop1, prop2, modelB);

        if (literalA == null || literalB == null) {
            return false;
        }

        String nameA = literalA.getString();
        String nameB = literalB.getString();

        return nameA.equals(nameB);

    }

    private boolean cannotCompute(Integer totalA, Integer totalB) {

        return totalA == null || totalB == null || totalA.equals(0) || totalB.equals(0);
    }

    private StatisticResultPair getFailedStatistic(EnumStat stat, String property) {

        if (StringUtils.isBlank(property)) {
            LOG.warn("Cannot compute \" " + stat.toString() + " \"");
        } else {
            LOG.warn("Cannot compute \" " + stat.toString() + " \"" + property + " property missing.");
        }

        StatisticResultPair pair = new StatisticResultPair(null, null, null);
        pair.setType(EnumStatViewType.UNDEFINED);
        pair.setTitle(EnumStat.UNDEFINED.toString() + " \"" + stat.toString() + "\"");
        pair.setGroup(new StatGroup(EnumStatGroup.UNDEFINED));
        return pair;
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

    public void setFusedPOIs(Integer fusedPOIs) {
        this.fusedPOIs = fusedPOIs;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }
}
