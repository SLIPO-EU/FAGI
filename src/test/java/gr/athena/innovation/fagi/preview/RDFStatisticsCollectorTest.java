package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.preview.statistics.StatisticResultPair;
import java.io.ByteArrayInputStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
    private final String links;

    public RDFStatisticsCollectorTest() {
        
        datasetA = "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#source> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://slipo.eu/def#sourceInfo> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/sourceInfo> <http://slipo.eu/def#sourceRef> \"Foo-A\" .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.opengis.net/ont/sf#POINT> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/geom> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(9.60 47.32)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://slipo.eu/def#name> <http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/name> .\n"
                + "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600/name> <http://slipo.eu/def#nameValue> \"Michail Foufoutos\" .\n"
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

        links = "<http://slipo.eu/id/poi/db05380d-5286-372e-b2b8-387d7794c600> <http://www.w3.org/2002/07/owl#sameAs> <http://slipo.eu/id/poi/d361a055-8672-306f-b620-81d31d1606e0> .";

    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        
        modelA = ModelFactory.createDefaultModel();
        modelA.read(new ByteArrayInputStream(datasetA.getBytes()), null, "N-TRIPLES");

        modelB = ModelFactory.createDefaultModel();
        modelB.read(new ByteArrayInputStream(datasetB.getBytes()), null, "N-TRIPLES");

        linksModel = ModelFactory.createDefaultModel();
        linksModel.read(new ByteArrayInputStream(links.getBytes()), null, "N-TRIPLES");
        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of collect method, of class RDFStatisticsCollector.
     */
//    @Test
//    public void testCollect() {
//        System.out.println("collect");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticsContainer expResult = null;
//        StatisticsContainer result = instance.collect();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    /**
     * Test of countTriples method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTriples() {
        LOG.info("countTriples");
        RDFStatisticsCollector instance = new RDFStatisticsCollector();
        
        StatisticResultPair result = instance.countTriples(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("17", "22");
        expResult.setLabel("Total triples");
        assertEquals(expResult, result);
    } 
    
    /**
     * Test of countTotalEntities method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountTotalEntities() {
        LOG.info("countTotalEntities");
        RDFStatisticsCollector instance = new RDFStatisticsCollector();
        StatisticResultPair expResult = new StatisticResultPair("2", "2");
        expResult.setLabel("Total POIs");
        
        StatisticResultPair result = instance.countTotalEntities(modelA, modelB);
        
        assertEquals(expResult, result);

    }

    /**
     * Test of countLinkedVsTotalPOIs method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountLinkedVsTotalPOIs() {
        LOG.info("countLinkedVsTotalPOIs");

        RDFStatisticsCollector instance = new RDFStatisticsCollector();
        
        StatisticResultPair result = instance.countLinkedVsTotalPOIs(linksModel, 2, 2);
        
        StatisticResultPair expResult = new StatisticResultPair("2", "4");
        expResult.setLabel("Linked vs Total POIS");
        
        assertEquals(expResult, result);

    }
    
    /**
     * Test of countNonEmptyNames method, of class RDFStatisticsCollector.
     */
    @Test
    public void testCountNonEmptyNames() {
        LOG.info("countNonEmptyNames");
        RDFStatisticsCollector instance = new RDFStatisticsCollector();
        
        StatisticResultPair result = instance.countNonEmptyNames(modelA, modelB);
        StatisticResultPair expResult = new StatisticResultPair("2", "2");
        expResult.setLabel("Non empty Names");
        assertEquals(expResult, result);
    }    
    
//
//    /**
//     * Test of countLinkedPOIs method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountLinkedPOIs() {
//        System.out.println("countLinkedPOIs");
//        Model links = null;
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countLinkedPOIs(links);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countTotalLinkedTriples method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountTotalLinkedTriples() {
//        System.out.println("countTotalLinkedTriples");
//        Model linkedA = null;
//        Model linkedB = null;
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countTotalLinkedTriples(linkedA, linkedB);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countDistinctProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountDistinctProperties() {
//        System.out.println("countDistinctProperties");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countDistinctProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyPhones method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyPhones() {
//        System.out.println("countNonEmptyPhones");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countNonEmptyPhones();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyStreets method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyStreets() {
//        System.out.println("countNonEmptyStreets");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countNonEmptyStreets();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyStreetNumbers method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyStreetNumbers() {
//        System.out.println("countNonEmptyStreetNumbers");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countNonEmptyStreetNumbers();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyWebsites method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyWebsites() {
//        System.out.println("countNonEmptyWebsites");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countNonEmptyWebsites();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyEmails method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyEmails() {
//        System.out.println("countNonEmptyEmails");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countNonEmptyEmails();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyDates method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyDates() {
//        System.out.println("countNonEmptyDates");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countNonEmptyDates();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countTotalNonEmptyProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountTotalNonEmptyProperties() {
//        System.out.println("countTotalNonEmptyProperties");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countTotalNonEmptyProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countEmptyNames method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountEmptyNames() {
//        System.out.println("countEmptyNames");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countEmptyNames();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countEmptyPhones method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountEmptyPhones() {
//        System.out.println("countEmptyPhones");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countEmptyPhones();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countEmptyStreets method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountEmptyStreets() {
//        System.out.println("countEmptyStreets");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countEmptyStreets();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countEmptyStreetNumbers method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountEmptyStreetNumbers() {
//        System.out.println("countEmptyStreetNumbers");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countEmptyStreetNumbers();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countEmptyWebsites method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountEmptyWebsites() {
//        System.out.println("countEmptyWebsites");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countEmptyWebsites();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countEmptyEmails method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountEmptyEmails() {
//        System.out.println("countEmptyEmails");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countEmptyEmails();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countEmptyDates method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountEmptyDates() {
//        System.out.println("countEmptyDates");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countEmptyDates();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countTotalEmptyProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountTotalEmptyProperties() {
//        System.out.println("countTotalEmptyProperties");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.countTotalEmptyProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculatePercentageOfPrimaryDateFormats method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculatePercentageOfPrimaryDateFormats() {
//        System.out.println("calculatePercentageOfPrimaryDateFormats");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculatePercentageOfPrimaryDateFormats();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateNamePercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateNamePercentage() {
//        System.out.println("calculateNamePercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculateNamePercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateWebsitePercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateWebsitePercentage() {
//        System.out.println("calculateWebsitePercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculateWebsitePercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculatePhonePercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculatePhonePercentage() {
//        System.out.println("calculatePhonePercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculatePhonePercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateStreetPercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateStreetPercentage() {
//        System.out.println("calculateStreetPercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculateStreetPercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateStreetNumberPercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateStreetNumberPercentage() {
//        System.out.println("calculateStreetNumberPercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculateStreetNumberPercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateLocalityPercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateLocalityPercentage() {
//        System.out.println("calculateLocalityPercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculateLocalityPercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateDatePercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateDatePercentage() {
//        System.out.println("calculateDatePercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculateDatePercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//

//
//    /**
//     * Test of calculateTotalNonEmptyPropertiesPercentage method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateTotalNonEmptyPropertiesPercentage() {
//        System.out.println("calculateTotalNonEmptyPropertiesPercentage");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.calculateTotalNonEmptyPropertiesPercentage();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyProperty method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyProperty() {
//        System.out.println("countNonEmptyProperty");
//        String property = "";
//        EnumDataset dataset = null;
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        Integer expResult = null;
//        Integer result = instance.countNonEmptyProperty(property, dataset);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countNonEmptyPropertyChain method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCountNonEmptyPropertyChain() {
//        System.out.println("countNonEmptyPropertyChain");
//        String property1 = "";
//        String property2 = "";
//        EnumDataset dataset = null;
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        Integer expResult = null;
//        Integer result = instance.countNonEmptyPropertyChain(property1, property2, dataset);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeLinkStats method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeLinkStats() {
//        System.out.println("computeLinkStats");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        instance.computeLinkStats();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeNonEmptyLinkedProperty method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeNonEmptyLinkedProperty() {
//        System.out.println("computeNonEmptyLinkedProperty");
//        Model linkedA = null;
//        Model linkedB = null;
//        String property = "";
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = RDFStatisticsCollector.computeNonEmptyLinkedProperty(linkedA, linkedB, property);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeEmptyLinkedProperty method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeEmptyLinkedProperty() {
//        System.out.println("computeEmptyLinkedProperty");
//        Integer nonEmptyA = null;
//        Integer nonEmptyB = null;
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.computeEmptyLinkedProperty(nonEmptyA, nonEmptyB);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeNonEmptyLinkedTotalProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeNonEmptyLinkedTotalProperties() {
//        System.out.println("computeNonEmptyLinkedTotalProperties");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.computeNonEmptyLinkedTotalProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of computeEmptyLinkedTotalProperties method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testComputeEmptyLinkedTotalProperties() {
//        System.out.println("computeEmptyLinkedTotalProperties");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        StatisticResultPair expResult = null;
//        StatisticResultPair result = instance.computeEmptyLinkedTotalProperties();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateAveragePropertiesPerPOI method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateAveragePropertiesPerPOI() {
//        System.out.println("calculateAveragePropertiesPerPOI");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        instance.calculateAveragePropertiesPerPOI();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateAveragePropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateAveragePropertiesOfLinkedPOIs() {
//        System.out.println("calculateAveragePropertiesOfLinkedPOIs");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        instance.calculateAveragePropertiesOfLinkedPOIs();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateAverageEmptyPropertiesOfLinkedPOIs method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testCalculateAverageEmptyPropertiesOfLinkedPOIs() {
//        System.out.println("calculateAverageEmptyPropertiesOfLinkedPOIs");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        instance.calculateAverageEmptyPropertiesOfLinkedPOIs();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMap method, of class RDFStatisticsCollector.
//     */
//    @Test
//    public void testGetMap() {
//        System.out.println("getMap");
//        RDFStatisticsCollector instance = new RDFStatisticsCollector();
//        Map<String, StatisticResultPair> expResult = null;
//        Map<String, StatisticResultPair> result = instance.getMap();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
