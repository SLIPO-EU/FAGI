package gr.athena.innovation.fagi.core.normalizer.date;

import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
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
public class TransformToFormatTest {
    private static final org.apache.logging.log4j.Logger logger 
            = LogManager.getLogger(TransformToFormatTest.class);    
    public TransformToFormatTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of transformDateToFormat method, of class TransformToFormat.
     */
    @Test
    public void testTransformDateToFormat() {
        System.out.println("transformDateToFormat");
        NormalizeDateToFormat transformToFormat = new NormalizeDateToFormat();
        
        String date1 = "19-09-2015";
        String targetFormat1 = "yyyy/mm/dd";
        
        String expResult1 = "2015/09/19";
        String result1 = transformToFormat.normalize(date1, targetFormat1);
        assertEquals(expResult1, result1);
        
        String date = "11/11/2015";
        String targetFormat2 = "yyyy mm dd";
        String expResult = "2015 11 11";
        String result = transformToFormat.normalize(date, targetFormat2);
        assertEquals(expResult, result);        
    }
    /**
     * Test of getName method, of class TransformToFormat.
     */
    @Test
    public void testGetName() {
        logger.info("getName");
        NormalizeDateToFormat transformToFormat = new NormalizeDateToFormat();
        String expResult = SpecificationConstants.Transformations.TRANSFORM_DATE_TO_FORMAT;
        String result = transformToFormat.getName();
        assertEquals(expResult, result);
    }    
}
