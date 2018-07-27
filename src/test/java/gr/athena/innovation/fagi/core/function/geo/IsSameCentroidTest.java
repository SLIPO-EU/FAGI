package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsSameCentroidTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameCentroidTest.class);

    /**
     * Test of evaluate method, of class IsSameCentroid.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        String wktA = "POINT(38.03667880446578 23.80251648397791)";
        String wktB = "POINT (38.043919 23.804989)";

        String tolerance1 = "1000";
        IsSameCentroid instance = new IsSameCentroid();
        boolean expResult1 = true;
        boolean result = instance.evaluate(wktA, wktB, tolerance1);

        assertEquals(expResult1, result);
        
        String tolerance2 = "100";
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktA, wktB, tolerance2);
        assertEquals(expResult2, result2);
        
    }

    /**
     * Test of getName method, of class IsSameCentroid.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsSameCentroid instance = new IsSameCentroid();
        String expResult = SpecificationConstants.Functions.IS_SAME_CENTROID;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
