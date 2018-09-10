package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
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
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionThreeLiteralStringParameters;
import gr.athena.innovation.fagi.utils.RDFUtils;

/**
 * Function class with evaluation on the centroid of the geometries.
 *
 * @author nkarag
 */
public class IsSameCentroid implements IFunction, IFunctionThreeLiteralStringParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsSameCentroid.class);

    /**
     * Checks if the geometries have the same centroid given a tolerance value in meters.
     * Computes the orthodromic distance between two points by transforming the centroids using the 3857 CRS 
     * in order to calculate the distance in meters.
     *
     * @param wktA the WKT literal of A.
     * @param wktB the WKT literal of B.
     * @param tolerance the tolerance in meters.
     * @return True if the two centroids match given the tolerance, false otherwise.
     */
    @Override
    public boolean evaluate(Literal wktA, Literal wktB, String tolerance) {
        
        if(wktA == null || wktB == null){
            return false;
        }
        
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
            String aLexical = RDFUtils.extractGeometry(wktA).getLexicalForm();
            geometryA = reader.read(aLexical);
        } catch (ParseException ex) {
            LOG.warn("Could not parse WKT: " + wktA + "\nReturning false.");
            return false;
        }

        try {
            String bLexical = RDFUtils.extractGeometry(wktB).getLexicalForm();
            geometryB = reader.read(bLexical);
        } catch (ParseException ex) {
            LOG.warn("Could not parse WKT: " + wktB + "\nReturning false.");
            return false;
        }

        Point centroidA = geometryA.getCentroid();
        Point centroidB = geometryB.getCentroid();

        try {

            CoordinateReferenceSystem dataCRS = CRS.decode(SpecificationConstants.CRS_EPSG_4326);
            CoordinateReferenceSystem worldCRS = CRS.decode(SpecificationConstants.CRS_EPSG_3857);

            boolean lenient = true; // allow for some error due to different datums
            MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);
            Geometry targetGeometryA = JTS.transform(centroidA, transform);
            Geometry targetGeometryB = JTS.transform(centroidB, transform);

            Coordinate centroidCoordinateA = targetGeometryA.getCentroid().getCoordinate();
            Coordinate centroidCoordinateB = targetGeometryB.getCentroid().getCoordinate();
            
            double distance = JTS.orthodromicDistance(centroidCoordinateA, centroidCoordinateB, worldCRS);
            LOG.trace("Centroid distance: " + distance);
            return distance <= tlr;
            
            
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
