package gr.athena.innovation.fagi.core.funtions;

import gr.athena.innovation.fagi.core.functions.date.DateFuser;
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
public class DateFuserTest {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DateFuserTest.class);
    
    public DateFuserTest() {
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
     * Test of isDateKnownFormat method, of class DateFuser.
     */
    @Test
    public void testIsDateKnownFormat() {
        logger.info("isDateKnownFormat");
        
        String dateString1 = "12/05/2017";
        DateFuser dateFuser = new DateFuser();
        boolean expResult1 = true;
        boolean result1 = dateFuser.isDateKnownFormat(dateString1);
        assertEquals(expResult1, result1);

        String dateString2 = "31-08-1982 10:20:56";
        boolean expResult2 = true;
        boolean result2 = dateFuser.isDateKnownFormat(dateString2);
        assertEquals(expResult2, result2);        

        String dateString3 = "31/08-1982 10:20:56";
        boolean expResult3 = false;
        boolean result3 = dateFuser.isDateKnownFormat(dateString3);
        assertEquals(expResult3, result3); 

        String dateString4 = "19-19-2017"; //invalid date, but known format should return true
        boolean expResult4 = true;
        boolean result4 = dateFuser.isDateKnownFormat(dateString4);
        assertEquals(expResult4, result4);
        
    }

    /**
     * Test of isValidDate method, of class DateFuser.
     */
    @Test
    public void testIsValidDate() {
        logger.info("isValidDate");
        String format = "yyyy-MM-dd'T'HH:mm:ss";
        String dateString = "2010-12-37T07:65:00";
        DateFuser dateFuser = new DateFuser();
        boolean expResult = false;
        boolean result = dateFuser.isValidDate(dateString, format);
        assertEquals(expResult, result);

    }

    /**
     * Test of transformDateToFormat method, of class DateFuser.
     */
//    @Test
//    public void testTransformDateToFormat() {
//        System.out.println("transformDateToFormat");
//        String date = "";
//        String format = "";
//        DateFuser instance = new DateFuser();
//        String expResult = "";
//        String result = instance.transformDateToFormat(date, format);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }


    
}
