package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionSingleParameter;
import org.apache.logging.log4j.LogManager;

/**
 * Function class with evaluation method geometry type.
 * 
 * @author nkarag
 */
public class IsPointGeometry implements IFunction, IFunctionSingleParameter {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsPointGeometry.class);

    /**
     * Checks if the given WKT geometry is a POINT geometry.
     * 
     * @param wkt The wellknowntext geometry.
     * @return True if the geometry is a POINT geometry, false otherwise.
     */
    @Override
    public boolean evaluate(String wkt) {
        WKTReader reader = new WKTReader();
        Geometry geometry;

        try {
            geometry = reader.read(wkt);
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
