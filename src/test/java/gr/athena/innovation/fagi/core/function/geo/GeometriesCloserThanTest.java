package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class GeometriesCloserThanTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesCloserThanTest.class);

    /**
     * Test of evaluate method, of class GeometriesCloserThan.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        String wktA = "POINT(38.03667880446578 23.80251648397791)";
        String wktB = "POINT (38.043919 23.804989)";

        String tolerance1 = "850";
        GeometriesCloserThan instance = new GeometriesCloserThan();
        boolean expResult1 = true;
        boolean result = instance.evaluate(wktA, wktB, tolerance1);

        assertEquals(expResult1, result);
        
        String tolerance2 = "800";
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktA, wktB, tolerance2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getName method, of class GeometriesCloserThan.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        GeometriesCloserThan instance = new GeometriesCloserThan();
        String expResult = SpecificationConstants.Functions.GEOMETRIES_CLOSER_THAN;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
}
