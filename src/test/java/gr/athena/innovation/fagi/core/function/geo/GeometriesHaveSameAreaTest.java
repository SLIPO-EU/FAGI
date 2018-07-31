package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class GeometriesHaveSameAreaTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesHaveSameAreaTest.class);

    /**
     * Test of evaluate method, of class GeometriesHaveSameArea.
     */
    @Test

    public void testEvaluate() {
        LOG.info("evaluate");

        GeometriesHaveSameArea instance = new GeometriesHaveSameArea();
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        
        String wktA1 = "POLYGON((23.8006327 38.0366525, 23.8006809 38.0365131, 23.8010135 38.0366292, 23.8009572 38.0367602, 23.8006327 38.0366525))";
        Literal wktA1Literal = ResourceFactory.createTypedLiteral(wktA1, geometryDatatype);
        String wktB1 = "POLYGON((23.8007386 38.0366546, 23.8007614 38.0366023, 23.8009129 38.036644, 23.8008935 38.0366942, 23.8007386 38.0366546))";
        Literal wktB1Literal = ResourceFactory.createTypedLiteral(wktB1, geometryDatatype);
        
        
        boolean expResult1 = false;
        boolean result = instance.evaluate(wktA1Literal, wktB1Literal, "50");

        assertEquals(expResult1, result);

        String wktA2 = "POLYGON((23.8006327 38.0366525, 23.8006809 38.0365131, 23.8010135 38.0366292, 23.8009572 38.0367602, 23.8006327 38.0366525))";
        Literal wktA2Literal = ResourceFactory.createTypedLiteral(wktA2, geometryDatatype);
        String wktB2 = "POLYGON((23.8007386 38.0366546, 23.8007614 38.0366023, 23.8009129 38.036644, 23.8008935 38.0366942, 23.8007386 38.0366546))";
        Literal wktB2Literal = ResourceFactory.createTypedLiteral(wktB2, geometryDatatype);
        
        boolean expResult2 = true;
        boolean result2 = instance.evaluate(wktA2Literal, wktB2Literal, "600");
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getName method, of class GeometriesHaveSameArea.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        GeometriesHaveSameArea instance = new GeometriesHaveSameArea();
        String expResult = SpecificationConstants.Functions.GEOMETRIES_HAVE_SAME_AREA;
        String result = instance.getName();
        assertEquals(expResult, result);
    }
}
