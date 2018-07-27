package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsPointGeometryTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsPointGeometryTest.class);

    /**
     * Test of evaluate method, of class IsPointGeometry.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        String wkt1 = "POINT(38.1 23.8)";
        IsPointGeometry instance = new IsPointGeometry();
        
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(wkt1);
        assertEquals(expResult1, result1);
        
        String wkt2 = "LINESTRING(38 23, 39 23, 40 23)";
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wkt2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getName method, of class IsPointGeometry.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsPointGeometry instance = new IsPointGeometry();
        String expResult = SpecificationConstants.Functions.IS_POINT_GEOMETRY;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
}
