package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
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
        IsPointGeometry instance = new IsPointGeometry();
        
        String wkt1 = "POINT(38.1 23.8)";
        Literal wktLiteral1 = ResourceFactory.createStringLiteral(wkt1);
        
        
        boolean expResult1 = true;
        boolean result1 = instance.evaluate(wktLiteral1);
        assertEquals(expResult1, result1);
        
        String wkt3 = "<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(38.1 23.8)^^http://www.opengis.net/ont/geosparql#wktLiteral";
        Literal wktLiteral3 = ResourceFactory.createStringLiteral(wkt3);
        
        
        boolean expResult3 = true;
        boolean result3 = instance.evaluate(wktLiteral3);
        assertEquals(expResult3, result3);

        String wkt2 = "LINESTRING(38 23, 39 23, 40 23)";
        Literal wktLiteral2 = ResourceFactory.createStringLiteral(wkt2);
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktLiteral2);
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
