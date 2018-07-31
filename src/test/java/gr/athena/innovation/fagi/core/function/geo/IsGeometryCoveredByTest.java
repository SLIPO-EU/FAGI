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
public class IsGeometryCoveredByTest {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsGeometryCoveredByTest.class);

    /**
     * Test of evaluate method, of class IsGeometryCoveredBy.
     */
    @Test
    
    public void testEvaluate() {
        LOG.info("evaluate");

        IsGeometryCoveredBy instance = new IsGeometryCoveredBy();
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        
        String wktA1 = "LINESTRING(23.8035375 38.0361476, 23.8033229 38.0359194, 23.803677 38.0358941, 23.8035375 38.0361476)";
        Literal wktA1Literal = ResourceFactory.createTypedLiteral(wktA1, geometryDatatype);
        String wktB1 = "POLYGON((23.80431 38.0374912, 23.8013166 38.0355096, 23.8048464 38.0351166, 23.80431 38.0374912))";
        Literal wktB1Literal = ResourceFactory.createTypedLiteral(wktB1, geometryDatatype);

        
        boolean expResult1 = true;
        boolean result = instance.evaluate(wktA1Literal, wktB1Literal);

        assertEquals(expResult1, result);

        String wktA2 = "POLYGON((23.80431 38.0374912, 23.8013166 38.0355096, 23.8048464 38.0351166, 23.80431 38.0374912))";
        Literal wktA2Literal = ResourceFactory.createTypedLiteral(wktA2, geometryDatatype);
        String wktB2 = "LINESTRING(23.8035375 38.0361476, 23.8033229 38.0359194, 23.803677 38.0358941, 23.8035375 38.0361476)";
        Literal wktB2Literal = ResourceFactory.createTypedLiteral(wktB2, geometryDatatype);

        boolean expResult2 = false;
        boolean result2 = instance.evaluate(wktA2Literal, wktB2Literal);
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
