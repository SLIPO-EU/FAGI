package gr.athena.innovation.fagi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class FagiInstanceTest {

    public FagiInstanceTest() {

    }

    @BeforeClass
    public static void setUpClass() {

        try {

            Path leftDatasetPath = Files.createTempFile("left", ".nt");
            Path rightDatasetPath = Files.createTempFile("right", ".nt");
            Path linksPath = Files.createTempFile("links", ".nt");
            Path config = Files.createTempFile("config", ".xml");
            Path rules = Files.createTempFile("rules", ".xml");

        } catch (IOException ex) {
            Logger.getLogger(FagiInstanceTest.class.getName()).log(Level.SEVERE, null, ex);
        }

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
     * Test of run method, of class FagiInstance.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        //FagiInstance instance = new FagiInstance();
        //instance.run();
        // TODO review the generated test code and remove the default call to fail.
        ///fail("The test case is a prototype.");
    }

    /**
     * Test of computeStatistics method, of class FagiInstance.
     */
    @Test
    public void testComputeStatistics() throws Exception {
//        System.out.println("computeStatistics");
//        List<String> selected = null;
//        FagiInstance instance = null;
//        String expResult = "";
//        String result = instance.computeStatistics(selected);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
