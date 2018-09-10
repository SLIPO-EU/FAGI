package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionOneParameter;
import gr.athena.innovation.fagi.utils.RDFUtils;

/**
 * Function class with evaluation method geometry type.
 * 
 * @author nkarag
 */
public class IsPointGeometry implements IFunction, IFunctionOneParameter {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsPointGeometry.class);

    /**
     * Checks if the given WKT geometry is a POINT geometry.
     * 
     * @param wkt The WKT literal object.
     * @return True if the geometry is a POINT geometry, false otherwise.
     */
    @Override
    public boolean evaluate(Literal wkt) {

        WKTReader reader = new WKTReader();
        Geometry geometry;

        try {
            String lexical = RDFUtils.extractGeometry(wkt).getLexicalForm();
            
            geometry = reader.read(lexical);
            String geometryType = geometry.getGeometryType().toUpperCase();
            return geometryType.equals("POINT");
            
        } catch (ParseException ex) {
            LOG.warn(ex);
            LOG.warn("Could not parse WKT: " + wkt + "\nReturning false.");
            return false;
        }
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
