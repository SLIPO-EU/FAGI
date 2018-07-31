package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nkarag
 */
public class GeometriesIntersectTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesIntersectTest.class);
    
    /**
     * Test of evaluate method, of class GeometriesIntersect.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        GeometriesIntersect instance = new GeometriesIntersect();
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;

        String wktA1 = "LINESTRING(23.6940765 37.9381054, 23.7117577 37.951372, 23.7313271 37.9653128)";
        Literal wktA1Literal = ResourceFactory.createTypedLiteral(wktA1, geometryDatatype);

        String wktB1 = "LINESTRING(23.6896133 37.9630121, 23.6980247 37.9552973, 23.7225723 37.9374285)";
        Literal wktB1Literal = ResourceFactory.createTypedLiteral(wktB1, geometryDatatype);
        boolean expResult1 = true;
        boolean result = instance.evaluate(wktA1Literal, wktB1Literal);

        assertEquals(expResult1, result);

      
        String wktA2 = "LINESTRING(23.6868668 37.9432499, 23.6968231 37.9470403, 23.7181091 37.9616587)";
        Literal wktA2Literal = ResourceFactory.createTypedLiteral(wktA2, geometryDatatype);
        String wktB2 = "LINESTRING(23.6966515 37.9360746, 23.7072945 37.9427084, 23.7199974 37.9527256)";
        Literal wktB2Literal = ResourceFactory.createTypedLiteral(wktB2, geometryDatatype);
        
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktA2Literal, wktB2Literal);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getName method, of class GeometriesIntersect.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        GeometriesIntersect instance = new GeometriesIntersect();
        String expResult = SpecificationConstants.Functions.GEOMETRIES_INTERSECT;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
}
