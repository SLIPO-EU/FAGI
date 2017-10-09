package gr.athena.innovation.fagi.core.transform.date;

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
        String date = "19-09-2015";
        String format = "yyyy/mm/dd";
        TransformToFormat instance = new TransformToFormat();
        String expResult = "2015/09/19";
        String result = instance.transformDateToFormat(date, format);
        assertEquals(expResult, result);
    }
    
}
