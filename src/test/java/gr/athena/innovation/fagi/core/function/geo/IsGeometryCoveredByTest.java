package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class IsGeometryCoveredByTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsGeometryCoveredByTest.class);

    /**
     * Test of evaluate method, of class IsGeometryCoveredBy.
     */
    @Test
    
    public void testEvaluate() {
        LOG.info("evaluate");

        String wktA1 = "LINESTRING(23.8035375 38.0361476, 23.8033229 38.0359194, 23.803677 38.0358941, 23.8035375 38.0361476)";
        String wktB1 = "POLYGON((23.80431 38.0374912, 23.8013166 38.0355096, 23.8048464 38.0351166, 23.80431 38.0374912))";

        IsGeometryCoveredBy instance = new IsGeometryCoveredBy();
        boolean expResult1 = true;
        boolean result = instance.evaluate(wktA1, wktB1);

        assertEquals(expResult1, result);

        String wktA2 = "POLYGON((23.80431 38.0374912, 23.8013166 38.0355096, 23.8048464 38.0351166, 23.80431 38.0374912))";
        String wktB2 = "LINESTRING(23.8035375 38.0361476, 23.8033229 38.0359194, 23.803677 38.0358941, 23.8035375 38.0361476)";

        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktA2, wktB2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getName method, of class IsGeometryCoveredBy.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        IsGeometryCoveredBy instance = new IsGeometryCoveredBy();
        String expResult = SpecificationConstants.Functions.IS_GEOMETRY_COVERED_BY;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
}
