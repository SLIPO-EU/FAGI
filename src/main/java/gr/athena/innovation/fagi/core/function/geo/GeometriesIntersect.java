package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoParameters;
import org.apache.logging.log4j.LogManager;

/**
 * Function class that evaluates geometry intersection.
 * 
 * @author nkarag
 */
public class GeometriesIntersect implements IFunction, IFunctionTwoParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesIntersect.class);

    /**
     * Checks if the geometries intersect.
     *
     * @param wktA
     * @param wktB
     * @return True if the geometries intersect, false otherwise.
     */
    @Override
    public boolean evaluate(String wktA, String wktB) {

        WKTReader reader = new WKTReader();
        Geometry geometryA;
        Geometry geometryB;

        try {
            geometryA = reader.read(wktA);
        } catch (ParseException ex) {
            LOG.warn("Could not parse WKT: " + wktA + "\nReturning false.");
            return false;
        }

        try {
            geometryB = reader.read(wktB);
        } catch (ParseException ex) {
            LOG.warn("Could not parse WKT: " + wktB + "\nReturning false.");
            return false;
        }
        
        return geometryA.intersects(geometryB);

    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
