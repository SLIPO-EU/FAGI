package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class RDFStatisticsCollectorTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RDFStatisticsCollectorTest.class);
    
    private Model modelA;
    private Model modelB;
    private Model linksModel;
    private final String datasetA;
    private final String datasetB;
    private final String linksText;
    private List<Link> links;

    public RDFStatisticsCollectorTest() {

        datasetA = "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-A\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(9.60 47.32)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/name> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/name> <http://slipo.eu/def#nameValue> \"Michail Foufoutos\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#phone> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/phone> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/phone> <http://slipo.eu/def#contactValue> \"0123456789\" ."
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#email> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> <http://slipo.eu/def#contactValue> \"email_example@mail.com\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> <http://slipo.eu/def#contactType> \"email\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/email> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#contact> ."
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-A\" .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/geom> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(9.61 47.34)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/name> .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/name> <http://slipo.eu/def#nameValue> \"Med. Foufoutos\" .\n"
                + "<http://slipo.eu/id/poi/5b8bb367-f3a5-30c1-b33c-381ef1e2f045/name> <http://slipo.eu/def#nameType> \"official\" .";

        datasetB = "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-B\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/geom> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT (9.63 47.45)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> <http://slipo.eu/def#nameValue> \"Michail Foufoutos\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> <http://slipo.eu/def#language> \"en\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/name> <http://slipo.eu/def#nameType> \"official\" .\n"
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#homepage> <http://www.website-example.com> ."
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> <http://slipo.eu/def#address> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/address> ."
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/address> <http://slipo.eu/def#street> \"Street Name\"@en ."
                + "<http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0/address> <http://slipo.eu/def#number> \"11\" ."
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-B\" .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/geom> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT (9.65 47.42)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/geosparql#Feature> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://slipo.eu/def#nameValue> \"Dr.Med. Fouf\"@en .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://slipo.eu/def#language> \"en\" .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://slipo.eu/def#nameType> \"official\" .\n"
                + "<http://slipo.eu/id/poi/fb2c3089-2102-3fe0-b882-7e2416536bea/name> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#name> .";

        linksText = "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://www.w3.org/2002/07/owl#sameAs> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> .";

    }

    @Before
    public void setUp() throws ParseException {

        modelA = ModelFactory.createDefaultModel();
        modelA.read(new ByteArrayInputStream(datasetA.getBytes()), null, "N-TRIPLES");

        modelB = ModelFactory.createDefaultModel();
        modelB.read(new ByteArrayInputStream(datasetB.getBytes()), null, "N-TRIPLES");

        linksModel = ModelFactory.createDefaultModel();
        linksModel.read(new ByteArrayInputStream(linksText.getBytes()), null, "N-TRIPLES");

        links = new ArrayList<>();

        final StmtIterator iter = linksModel.listStatements();
        
        while(iter.hasNext()) {
            
            final Statement statement = iter.nextStatement();
            final String nodeA = statement.getSubject().getURI();
            final String uriA = statement.getSubject().getLocalName();
            final String nodeB;
            final String uriB;
            final RDFNode object = statement.getObject();

            if(object.isResource()) {
                nodeB = object.asResource().getURI();
                uriB = object.asResource().getLocalName();
            }
            else {
                throw new ParseException("Failed to parse link (object not a resource): " + statement.toString(), 0);
            }
            Link link = new Link(nodeA, uriA, nodeB, uriB);
            links.add(link);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of collect method, of class RDFStatisticsCollector.
     */
//    @Test
//    public void testCollect() {
//    }
    
    /**
     * Test of countTriples method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTriples() {
        LOG.info("countTriples");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countTriples(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("23", "26", null);
        expResult.setTitle(EnumStat.TOTAL_TRIPLES.toString());
        assertEquals(expResult, result);
    } 
    
    /**
     * Test of countTotalEntities method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalEntities() {
        LOG.info("countTotalEntities");
        
        StatisticResultPair expResult = new StatisticResultPair("2", "2", null);
        expResult.setTitle(EnumStat.TOTAL_POIS.toString());
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countTotalEntities(modelA, modelB);

        assertEquals(expResult, result);

    }
    
    /**
     * Test of countNonEmptyNames method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyNames() {
        LOG.info("countNonEmptyNames");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_NAMES, Namespace.NAME_VALUE);
        StatisticResultPair expResult = new StatisticResultPair("2", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_NAMES.toString());
        
        assertEquals(expResult, result);
    }    

    /**
     * Test of countNonEmptyPhones method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyPhones() {
        LOG.info("countNonEmptyPhones");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_PHONES, Namespace.PHONE);
        StatisticResultPair expResult = new StatisticResultPair("1", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_PHONES.toString());
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyStreets method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyStreets() {
        LOG.info("countNonEmptyStreets");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_STREETS, Namespace.STREET);
        StatisticResultPair expResult = new StatisticResultPair("0", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREETS.toString());
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyStreetNumbers method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyStreetNumbers() {
        LOG.info("countNonEmptyStreetNumbers");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER);
        StatisticResultPair expResult = new StatisticResultPair("0", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREET_NUMBERS.toString());
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyWebsites method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyWebsites() {
        LOG.info("countNonEmptyWebsites");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_WEBSITES, Namespace.HOMEPAGE);
        StatisticResultPair expResult = new StatisticResultPair("0", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_WEBSITES.toString());
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countNonEmptyEmails method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyEmails() {
        LOG.info("countNonEmptyEmails");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_EMAILS, Namespace.EMAIL);
        StatisticResultPair expResult = new StatisticResultPair("1", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_EMAILS.toString());
        
        assertEquals(expResult, result);
    }    
    /**
     * Test of countNonEmptyDates method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyDates() {
        LOG.info("countNonEmptyDates");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countNonEmptyProperty(modelA, modelB, EnumStat.NON_EMPTY_DATES, Namespace.DATE);
        StatisticResultPair expResult = new StatisticResultPair("0", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_DATES.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyNames method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyNames() {
        LOG.info("countEmptyNames");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("2", "2", null);
        stat.setTitle("Non empty Names");
        
        collector.getMap().put("nonEmptyNames", stat);
        
        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_NAMES, Namespace.NAME);
        StatisticResultPair expResult = new StatisticResultPair("0", "0", null);
        expResult.setTitle(EnumStat.NON_EMPTY_NAMES.toString());
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countEmptyPhones method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyPhones() {
        LOG.info("countEmptyPhones");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("1", "0", null);
        stat.setTitle("Non empty Phones");
        
        collector.getMap().put("nonEmptyPhones", stat);
        
        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_PHONES, Namespace.PHONE);
        StatisticResultPair expResult = new StatisticResultPair("1", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_PHONES.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyStreets method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyStreets() {
        LOG.info("countEmptyStreets");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("0", "1", null);
        stat.setTitle("Non empty Streets");
        
        collector.getMap().put("nonEmptyStreets", stat);
        
        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_STREETS, Namespace.STREET);
        StatisticResultPair expResult = new StatisticResultPair("2", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREETS.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyStreetNumbers method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyStreetNumbers() {
        LOG.info("countEmptyStreetNumbers");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("0", "1", null);
        stat.setTitle("Non empty Street Numbers");
        
        collector.getMap().put("nonEmptyStreetNumbers", stat);
        
        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_STREET_NUMBERS, Namespace.STREET_NUMBER);
        StatisticResultPair expResult = new StatisticResultPair("2", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_STREET_NUMBERS.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyWebsites method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyWebsites() {
        LOG.info("countEmptyWebsites");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("0", "1", null);
        stat.setTitle("Non empty Websites");
        
        collector.getMap().put("nonEmptyWebsites", stat);
        
        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_WEBSITES, Namespace.HOMEPAGE);
        StatisticResultPair expResult = new StatisticResultPair("2", "1", null);
        expResult.setTitle(EnumStat.NON_EMPTY_WEBSITES.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyEmails method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyEmails() {
        LOG.info("countEmptyEmails");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("1", "0", null);
        stat.setTitle("Non empty Emails");
        
        collector.getMap().put("nonEmptyEmails", stat);
        
        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_EMAILS, Namespace.EMAIL);
        StatisticResultPair expResult = new StatisticResultPair("1", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_EMAILS.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countEmptyDates method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountEmptyDates() {
        LOG.info("countEmptyDates");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair stat = new StatisticResultPair("0", "0", null);
        stat.setTitle("Non empty Dates");
        
        collector.getMap().put("nonEmptyDates", stat);
        
        StatisticResultPair result = collector.countEmptyProperty(modelA, modelB, EnumStat.EMPTY_DATES, Namespace.DATE);
        StatisticResultPair expResult = new StatisticResultPair("2", "2", null);
        expResult.setTitle(EnumStat.NON_EMPTY_DATES.toString());
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countDistinctProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountDistinctProperties() {
        LOG.info("countDistinctProperties");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countDistinctProperties(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("12", "13", null);
        expResult.setTitle(EnumStat.DISTINCT_PROPERTIES.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of calculatePercentageOfPrimaryDateFormats method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculatePercentageOfPrimaryDateFormats() {
        LOG.info("calculatePercentageOfPrimaryDateFormats");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.calculatePercentageOfPrimaryDateFormats(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("0", "0", null);
        expResult.setTitle("Percentage of primary date formats");
        
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateNamePercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateNamePercentage() {
        LOG.info("calculateNamePercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair result = collector.calculatePropertyPercentage(modelA, modelB, EnumStat.NAMES_PERCENT, Namespace.NAME_VALUE);
        StatisticResultPair expResult = new StatisticResultPair("100.0", "100.0", null);
        expResult.setTitle(EnumStat.NAMES_PERCENT.toString());
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of countLinkedPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountLinkedPOIs() {
        LOG.info("countLinkedPOIs");

        StatisticResultPair expResult =  new StatisticResultPair("1", "1", null);
        expResult.setTitle(EnumStat.LINKED_POIS.toString());

        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedPOIs(linksModel);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countLinkedVsTotalPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountLinkedVsTotalPOIs() {
        LOG.info("countLinkedVsTotalPOIs");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedVsTotalPOIs(modelA, modelB, linksModel);
        
        StatisticResultPair expResult = new StatisticResultPair("2", "4", null);
        expResult.setTitle(EnumStat.LINKED_VS_TOTAL.toString());
        
        assertEquals(expResult, result);

    }    
    
    /**
     * Test of countTotalLinkedTriples method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalLinkedTriples() {
        LOG.info("countTotalLinkedTriples");  
        
        StatisticResultPair expResult = new StatisticResultPair("6", "5", null);
        expResult.setTitle(EnumStat.LINKED_TRIPLES.toString());
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair result = collector.countLinkedTriples(modelA, modelB, linksModel);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of computeNonEmptyLinkedProperty method, of class RDFStatisticsCollector.
     */
    @Test
    public void testComputeNonEmptyLinkedProperty() {
        LOG.info("computeNonEmptyLinkedProperty");
        
        Model linkedA = modelA.union(linksModel);
        Model linkedB = modelB.union(linksModel); 

        StatisticResultPair expResult1 = new StatisticResultPair("1", "1", null);
        StatisticResultPair expResult2 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult3 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult4 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult5 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult6 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult7 = new StatisticResultPair("0", "0", null);
        
        expResult1.setTitle("Linked Non Empty Names");
        expResult2.setTitle("Linked Non Empty Phones");
        expResult3.setTitle("Linked Non Empty Streets");
        expResult4.setTitle("Linked Non Empty Street Numbers");
        expResult5.setTitle("Linked Non Empty Websites");
        expResult6.setTitle("Linked Non Empty Emails");
        expResult7.setTitle("Linked Non Empty Dates");

        StatisticResultPair result1 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(
                linkedA, linkedB, Namespace.NAME, Namespace.NAME_VALUE);
        StatisticResultPair result2 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(
                linkedA, linkedB, Namespace.PHONE, Namespace.CONTACT_VALUE);
        StatisticResultPair result3 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(
                linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET);
        StatisticResultPair result4 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(
                linkedA, linkedB, Namespace.ADDRESS, Namespace.STREET_NUMBER);
        StatisticResultPair result5 = RDFStatisticsCollector.computeNonEmptyLinkedProperty(
                linkedA, linkedB, Namespace.HOMEPAGE);
        StatisticResultPair result6 = RDFStatisticsCollector.computeNonEmptyLinkedPropertyChain(
                linkedA, linkedB, Namespace.EMAIL, Namespace.CONTACT_VALUE);
        StatisticResultPair result7 = RDFStatisticsCollector.computeNonEmptyLinkedProperty(
                linkedA, linkedB, Namespace.DATE);        

        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
        assertEquals(expResult3, result3);
        assertEquals(expResult4, result4);
        assertEquals(expResult5, result5);
        assertEquals(expResult6, result6);
        assertEquals(expResult7, result7);

    }

    /**
     * Test of computeEmptyLinkedProperty method, of class RDFStatisticsCollector.
     */
    @Test
    public void testComputeEmptyLinkedProperty() {
        LOG.info("computeEmptyLinkedProperty");

        StatisticResultPair linkedPOIs = new StatisticResultPair("1", "1", null);
        StatisticResultPair stat1 = new StatisticResultPair("1", "1", null);
        StatisticResultPair stat2 = new StatisticResultPair("1", "0", null);
        StatisticResultPair stat3 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat4 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat5 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat6 = new StatisticResultPair("1", "0", null);
        StatisticResultPair stat7 = new StatisticResultPair("0", "0", null);

        linkedPOIs.setTitle("Linked POIs");
        stat1.setTitle("Linked Empty Names");
        stat2.setTitle("Linked Empty Phones");
        stat3.setTitle("Linked Empty Streets");
        stat4.setTitle("Linked Empty Street Numbers");
        stat5.setTitle("Linked Empty Websites");
        stat6.setTitle("Linked Empty Emails");
        stat7.setTitle("Linked Empty Dates");        

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        collector.getMap().put("linkedPois", linkedPOIs);
        collector.getMap().put("linkedEmptyNames", stat1);
        collector.getMap().put("linkedEmptyPhones", stat2);
        collector.getMap().put("linkedEmptyStreets", stat3);
        collector.getMap().put("linkedEmptyStreetNumbers", stat4);
        collector.getMap().put("linkedEmptyWebsites", stat5);
        collector.getMap().put("linkedEmptyEmails", stat6);
        collector.getMap().put("linkedEmptyDates", stat7);

        StatisticResultPair result1 = collector.computeEmptyLinkedProperty(Integer.parseInt(stat1.getValueA()), Integer.parseInt(stat1.getValueB()));
        StatisticResultPair result2 = collector.computeEmptyLinkedProperty(Integer.parseInt(stat2.getValueA()), Integer.parseInt(stat2.getValueB()));
        StatisticResultPair result3 = collector.computeEmptyLinkedProperty(Integer.parseInt(stat3.getValueA()), Integer.parseInt(stat3.getValueB()));
        StatisticResultPair result4 = collector.computeEmptyLinkedProperty(Integer.parseInt(stat4.getValueA()), Integer.parseInt(stat4.getValueB()));
        StatisticResultPair result5 = collector.computeEmptyLinkedProperty(Integer.parseInt(stat5.getValueA()), Integer.parseInt(stat5.getValueB()));
        StatisticResultPair result6 = collector.computeEmptyLinkedProperty(Integer.parseInt(stat6.getValueA()), Integer.parseInt(stat6.getValueB()));
        StatisticResultPair result7 = collector.computeEmptyLinkedProperty(Integer.parseInt(stat7.getValueA()), Integer.parseInt(stat7.getValueB()));

        result1.setTitle("Linked Empty Names");
        result2.setTitle("Linked Empty Phones");
        result3.setTitle("Linked Empty Streets");
        result4.setTitle("Linked Empty Street Numbers");
        result5.setTitle("Linked Empty Websites");
        result6.setTitle("Linked Empty Emails");
        result7.setTitle("Linked Empty Dates");        

        StatisticResultPair expResult1 = new StatisticResultPair("0", "0", null);
        StatisticResultPair expResult2 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult3 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult4 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult5 = new StatisticResultPair("1", "0", null);
        StatisticResultPair expResult6 = new StatisticResultPair("0", "1", null);
        StatisticResultPair expResult7 = new StatisticResultPair("1", "1", null);

        expResult1.setTitle("Linked Empty Names");
        expResult2.setTitle("Linked Empty Phones");
        expResult3.setTitle("Linked Empty Streets");
        expResult4.setTitle("Linked Empty Street Numbers");
        expResult5.setTitle("Linked Empty Websites");
        expResult6.setTitle("Linked Empty Emails");
        expResult7.setTitle("Linked Empty Dates");

        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
        assertEquals(expResult3, result3);
        assertEquals(expResult4, result4);
        assertEquals(expResult5, result5);
        assertEquals(expResult6, result6);
        assertEquals(expResult7, result7);
    }

    /**
     * Test of computeNonEmptyLinkedTotalProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testComputeNonEmptyLinkedTotalProperties() {
        LOG.info("computeNonEmptyLinkedTotalProperties");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair stat1 = new StatisticResultPair("1", "1", null);
        StatisticResultPair stat2 = new StatisticResultPair("1", "0", null);
        StatisticResultPair stat3 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat4 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat5 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat6 = new StatisticResultPair("1", "0", null);
        StatisticResultPair stat7 = new StatisticResultPair("0", "0", null);

        stat1.setTitle("Linked Empty Names");
        stat2.setTitle("Linked Empty Phones");
        stat3.setTitle("Linked Empty Streets");
        stat4.setTitle("Linked Empty Street Numbers");
        stat5.setTitle("Linked Empty Websites");
        stat6.setTitle("Linked Empty Emails");
        stat7.setTitle("Linked Empty Dates");

        collector.getMap().put("linkedNonEmptyNames", stat1);
        collector.getMap().put("linkedNonEmptyPhones", stat2);
        collector.getMap().put("linkedNonEmptyStreets", stat3);
        collector.getMap().put("linkedNonEmptyStreetNumbers", stat4);
        collector.getMap().put("linkedNonEmptyWebsites", stat5);
        collector.getMap().put("linkedNonEmptyEmails", stat6);
        collector.getMap().put("linkedNonEmptyDates", stat7);

        StatisticResultPair resultTotal = collector.computeNonEmptyLinkedTotalProperties();
        StatisticResultPair expResultTotal = new StatisticResultPair("3","4", null);
        expResultTotal.setTitle("Linked Non Empty properties");

        assertEquals(expResultTotal, resultTotal);

    }

    /**
     * Test of computeEmptyLinkedTotalProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testComputeEmptyLinkedTotalProperties() {
        LOG.info("computeEmptyLinkedTotalProperties");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        StatisticResultPair stat1 = new StatisticResultPair("3","4", null);
        stat1.setTitle("Linked Non Empty properties");
        
        StatisticResultPair stat2 = new StatisticResultPair("6", "5", null);
        stat2.setTitle("Linked Triples");
        
        collector.getMap().put("linkedNonEmptyProperties", stat1);
        collector.getMap().put("linkedTriples", stat2);
        
        StatisticResultPair expResult = new StatisticResultPair("3", "1", null);
        expResult.setTitle("Linked Empty properties");
        
        StatisticResultPair result = collector.computeEmptyLinkedTotalProperties();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of countTotalNonEmptyProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalNonEmptyProperties() {
        LOG.info("countTotalNonEmptyProperties");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        
        StatisticResultPair stat1 = new StatisticResultPair("2", "2", null);
        StatisticResultPair stat2 = new StatisticResultPair("1", "0", null);
        StatisticResultPair stat3 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat4 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat5 = new StatisticResultPair("0", "1", null);
        StatisticResultPair stat6 = new StatisticResultPair("1", "0", null);
        StatisticResultPair stat7 = new StatisticResultPair("0", "0", null);

        collector.getMap().put("nonEmptyNames", stat1);
        collector.getMap().put("nonEmptyPhones", stat2);
        collector.getMap().put("nonEmptyStreets", stat3);
        collector.getMap().put("nonEmptyStreetNumbers", stat4);
        collector.getMap().put("nonEmptyWebsites", stat5);
        collector.getMap().put("nonEmptyEmails", stat6);
        collector.getMap().put("nonEmptyDates", stat7);

        StatisticResultPair expResult = new StatisticResultPair("4", "5", null);
        expResult.setTitle("Non empty properties");
        
        StatisticResultPair result = collector.countTotalNonEmptyProperties();

        assertEquals(expResult, result);

    }

    /**
     * Test of countTotalEmptyProperties method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalEmptyProperties() {
        LOG.info("countTotalEmptyProperties");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        
        StatisticResultPair statB1 = new StatisticResultPair("2", "2", null);
        StatisticResultPair statB2 = new StatisticResultPair("1", "0", null);
        StatisticResultPair statB3 = new StatisticResultPair("0", "1", null);
        StatisticResultPair statB4 = new StatisticResultPair("0", "1", null);
        StatisticResultPair statB5 = new StatisticResultPair("0", "1", null);
        StatisticResultPair statB6 = new StatisticResultPair("1", "0", null);
        StatisticResultPair statB7 = new StatisticResultPair("0", "0", null);

        collector.getMap().put("emptyNames", statB1);
        collector.getMap().put("emptyPhones", statB2);
        collector.getMap().put("emptyStreets", statB3);
        collector.getMap().put("emptyStreetNumbers", statB4);
        collector.getMap().put("emptyWebsites", statB5);
        collector.getMap().put("emptyEmails", statB6);
        collector.getMap().put("emptyDates", statB7);

        StatisticResultPair expResult = new StatisticResultPair("4", "5", null);
        expResult.setTitle("Empty properties");
        
        StatisticResultPair result = collector.countTotalEmptyProperties();

        assertEquals(expResult, result);
    }

    /**
     * Test of calculateTotalNonEmptyPropertiesPercentage method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateTotalNonEmptyPropertiesPercentage() {
        LOG.info("calculateTotalNonEmptyPropertiesPercentage");

        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticResultPair stat1 = new StatisticResultPair("100.0", "100.0", null);
        StatisticResultPair stat2 = new StatisticResultPair("0.0", "50.0", null);
        StatisticResultPair stat3 = new StatisticResultPair("50.0", "0", null);
        StatisticResultPair stat4 = new StatisticResultPair("0.0", "50.0", null);
        StatisticResultPair stat5 = new StatisticResultPair("0.0", "50.0", null);
        StatisticResultPair stat6 = new StatisticResultPair("0.0", "0.0", null);
        StatisticResultPair stat7 = new StatisticResultPair("0.0", "0.0", null);

        collector.getMap().put("namesPercent", stat1);
        collector.getMap().put("phonesPercent", stat2);
        collector.getMap().put("streetsPercent", stat3);
        collector.getMap().put("streetNumbersPercent", stat4);
        collector.getMap().put("websitesPercent", stat5);
        collector.getMap().put("localityPercent", stat6);
        collector.getMap().put("datesPercent", stat7);

        StatisticResultPair expResult = new StatisticResultPair("21.42", "35.71", null);
        expResult.setTitle("Percentage of total properties in each dataset");
        StatisticResultPair result = collector.calculateTotalNonEmptyPropertiesPercentage();
        
        assertEquals(expResult, result);

    }

    /**
     * Test of calculateAveragePropertiesPerPOI method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateAveragePropertiesPerPOI() {
        LOG.info("calculateAveragePropertiesPerPOI");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        
        StatisticResultPair stat1 = new StatisticResultPair("12", "13", null);
        stat1.setTitle(EnumStat.DISTINCT_PROPERTIES.toString());
        
        collector.getMap().put("distinctProperties", stat1);

        StatisticResultPair expResult1 = new StatisticResultPair("4.0", "4.5", null);
        expResult1.setTitle("Average number of properties per POI");
        
        StatisticResultPair expResult2 = new StatisticResultPair("8.0", "8.5", null);
        expResult2.setTitle("Average number of empty properties per POI");

        StatisticResultPair[] result = collector.calculateAveragePropertiesPerPOI(modelA, modelB);
        
        assertEquals(expResult1, result[0]);
        assertEquals(expResult2, result[1]);

    }
    
    /**
     * Test of calculateAveragePropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateAveragePropertiesOfLinkedPOIs() {
        LOG.info("calculateAveragePropertiesOfLinkedPOIs");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);
        
        StatisticResultPair expResult = new StatisticResultPair("2.5", "2.5", null);
        expResult.setTitle("Average number of properties of linked POI");
        StatisticResultPair[] results = collector.calculateStatsPerLink(modelA, modelB, links);
        
        assertEquals(expResult, results[0]);
    }
    
    /**
     * Test of calculateAverageEmptyPropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCalculateAverageEmptyPropertiesOfLinkedPOIs() {
        LOG.info("calculateAverageEmptyPropertiesOfLinkedPOIs");
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();
        collector.setTotalPOIsA(2);
        collector.setTotalPOIsB(2);

        StatisticResultPair stat = new StatisticResultPair("4", "5", null);
        stat.setTitle("Non empty properties");
        collector.getMap().put("nonEmptyProperties", stat);

        StatisticResultPair expResult = new StatisticResultPair("2.0", "2.5", null);
        expResult.setTitle("Average number of empty properties of linked POIs");
        StatisticResultPair result = collector.calculateAverageEmptyPropertiesOfLinkedPOIs();

        assertEquals(expResult, result);
    }
}
