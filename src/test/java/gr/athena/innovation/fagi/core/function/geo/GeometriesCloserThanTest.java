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
public class GeometriesCloserThanTest {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesCloserThanTest.class);

    /**
     * Test of evaluate method, of class GeometriesCloserThan.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        GeometriesCloserThan instance = new GeometriesCloserThan();
        
        String wktA = "POINT(38.03667880446578 23.80251648397791)";
        Literal wktALiteral = ResourceFactory.createTypedLiteral(wktA, geometryDatatype);
        String wktB = "POINT (38.043919 23.804989)";
        Literal wktBLiteral = ResourceFactory.createTypedLiteral(wktB, geometryDatatype);
        String tolerance1 = "850";
        
        boolean expResult1 = true;
        boolean result = instance.evaluate(wktALiteral, wktBLiteral, tolerance1);

        assertEquals(expResult1, result);
        
        String tolerance2 = "800";
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktALiteral, wktBLiteral, tolerance2);
        assertEquals(expResult2, result2);
        
        String wktA3 = "<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(38.03667880446578 23.80251648397791)^^http://www.opengis.net/ont/geosparql#wktLiteral";
        Literal wktALiteral3 = ResourceFactory.createTypedLiteral(wktA3);
        String wktB3 = "<http://www.opengis.net/def/crs/EPSG/0/4326> POINT (38.043919 23.804989)^^http://www.opengis.net/ont/geosparql#wktLiteral";
        Literal wktBLiteral3 = ResourceFactory.createTypedLiteral(wktB3);
        String tolerance3 = "850";
        
        boolean expResult3 = true;
        boolean result3 = instance.evaluate(wktALiteral3, wktBLiteral3, tolerance3);

        assertEquals(expResult3, result3);
        
        
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
