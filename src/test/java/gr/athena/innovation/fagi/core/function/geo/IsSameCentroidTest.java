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
public class IsSameCentroidTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameCentroidTest.class);

    /**
     * Test of evaluate method, of class IsSameCentroid.
     */
    @Test
    public void testEvaluate() {
        LOG.info("evaluate");
        
        IsSameCentroid instance = new IsSameCentroid();
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        
        String wktA = "POINT(38.03667880446578 23.80251648397791)";
        Literal wktALiteral = ResourceFactory.createTypedLiteral(wktA, geometryDatatype);
        
        String wktB = "POINT (38.043919 23.804989)";
        Literal wktBLiteral = ResourceFactory.createTypedLiteral(wktB, geometryDatatype);

        String tolerance1 = "1000";
        
        boolean expResult1 = true;
        boolean result = instance.evaluate(wktALiteral, wktBLiteral, tolerance1);

        assertEquals(expResult1, result);
        
        String tolerance2 = "100";
        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktALiteral, wktBLiteral, tolerance2);
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
