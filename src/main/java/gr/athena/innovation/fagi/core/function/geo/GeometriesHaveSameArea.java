package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import gr.athena.innovation.fagi.core.function.IFunctionThreeStringParameters;

/**
 * Function class that checks if the given geometries have the same area with a tolerance value provided.
 * 
 * @author nkarag
 */
public class GeometriesHaveSameArea  implements IFunction, IFunctionThreeStringParameters {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesHaveSameArea.class);

    /**
     * Checks if the areas of the two geometries are the same given a tolerance value in square meters.
     * The method transforms the geometries to 3857 CRS before calculating the areas.
     *
     * @param wktA
     * @param wktB
     * @param tolerance the tolerance in square meters.
     * @return True if the geometries have the same area, false otherwise.
     */
    @Override
    public boolean evaluate(String wktA, String wktB, String tolerance) {

        WKTReader reader = new WKTReader();
        Geometry geometryA;
        Geometry geometryB;
        double tlr = 0;

        if (!StringUtils.isBlank(tolerance)) {
            try {
                tlr = Double.parseDouble(tolerance);
            } catch (NumberFormatException ex) {
                throw new ApplicationException("Tolerance provided is not a double number: " + tolerance);
            }
        }

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

        try {

            CoordinateReferenceSystem dataCRS = CRS.decode(SpecificationConstants.CRS_EPSG_4326);
            CoordinateReferenceSystem worldCRS = CRS.decode(SpecificationConstants.CRS_EPSG_3857);

            boolean lenient = true; // allow for some error due to different datums

            MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);
            Geometry targetGeometryA = JTS.transform(geometryA, transform);
            Geometry targetGeometryB = JTS.transform(geometryB, transform);

            double areaA = targetGeometryA.getArea();
            double areaB = targetGeometryB.getArea();

            double difference = Math.abs(areaA - areaB);
            LOG.trace("Difference: " + difference);
            
            return difference < tlr;
        } catch (FactoryException | TransformException ex) {
            LOG.warn("Fail to transform geometries. Evaluating to false.", ex);
            return false;
        }
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
