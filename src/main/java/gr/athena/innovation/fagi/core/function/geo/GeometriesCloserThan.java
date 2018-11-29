package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
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
import java.util.Arrays;
import java.util.Set;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CRSAuthorityFactory;

/**
 * Function class that checks if the given geometries are closer than the given distance.
 * 
 * @author nkarag
 */
public class GeometriesCloserThan implements IFunction, IFunctionThreeLiteralStringParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesCloserThan.class);

    /**
     * Checks if the minimum distance (in meters) of the geometries are closer than the provided distance value.
     * The method transforms the geometries to 3857 CRS, computes the nearest points between them 
     * and finally it calculates the orthodromic distance between the nearest points.
     *
     * @param wktA the WKT literal of A.
     * @param wktB the WKT literal of B.
     * @param distance the distance in meters.
     * @return True if the geometries are closer than the distance, false otherwise.
     */
    @Override
    public boolean evaluate(Literal wktA, Literal wktB, String distance) {

        if(wktA == null || wktB == null){
            return false;
        }

        WKTReader reader = new WKTReader();
        Geometry geometryA;
        Geometry geometryB;
        double dis = 0;

        if (!StringUtils.isBlank(distance)) {
            try {
                dis = Double.parseDouble(distance);
            } catch (NumberFormatException ex) {
                throw new ApplicationException("Tolerance provided is not a double number: " + distance);
            }
        }

        try {
            String aLexical = RDFUtils.extractGeometry(wktA).getLexicalForm();
            geometryA = reader.read(aLexical);
        } catch (ParseException ex) {
            LOG.warn(ex);
            LOG.warn("Could not parse WKT: " + wktA + "\nReturning false.");
            return false;
        }

        try {
            String bLexical = RDFUtils.extractGeometry(wktB).getLexicalForm();
            geometryB = reader.read(bLexical);
        } catch (ParseException ex) {
            LOG.warn(ex);
            LOG.warn("Could not parse WKT: " + wktB + "\nReturning false.");
            return false;
        }

        try {

            CoordinateReferenceSystem dataCRS = CRS.decode(SpecificationConstants.CRS_EPSG_4326);
            CoordinateReferenceSystem worldCRS = CRS.decode(SpecificationConstants.CRS_EPSG_3857);

            boolean lenient = true; // allow for some error due to different datums

            //TODO: find a workaround or use only centroids as points and reverse coordinates beforehand
            //WARNING when using EPSG:3857
            //Axis elements found in a wkt definition, the force longitude first axis order hint might not be respected:
            //
            //PROJCS["Google Projection",GEOGCS["WGS 84",DATUM["World Geodetic System 1984",SPHEROID["WGS 84", 6378137.0, 298.257223563, 
            //AUTHORITY["EPSG","7030"]],AUTHORITY["EPSG","6326"]],PRIMEM["Greenwich", 0.0, 
            //AUTHORITY["EPSG","8901"]],UNIT["degree", 0.017453292519943295],AXIS["Geodetic longitude", EAST],AXIS["Geodetic latitude", NORTH],
            //AUTHORITY["EPSG","4326"]],PROJECTION["Popular Visualisation Pseudo Mercator", 
            //AUTHORITY["EPSG","1024"]],PARAMETER["semi_minor", 6378137.0],PARAMETER["latitude_of_origin", 0.0],PARAMETER["central_meridian", 0.0],PARAMETER["scale_factor", 1.0],PARAMETER["false_easting", 0.0],PARAMETER["false_northing", 0.0],UNIT["m", 1.0]]

            //tranforming with jts found faster compared to geotools geodetic calcutaror.
            MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);
            Geometry targetGeometryA = JTS.transform(geometryA, transform);
            Geometry targetGeometryB = JTS.transform(geometryB, transform);

            Coordinate[] nearest = DistanceOp.nearestPoints(targetGeometryA, targetGeometryB);

            double minimumDistance = JTS.orthodromicDistance(nearest[0], nearest[1], worldCRS);

            LOG.trace("Minimum distance: " + minimumDistance);

            return minimumDistance <= dis;
        } catch (MismatchedDimensionException | FactoryException | TransformException  ex) {
            LOG.warn("Fail to transform geometries. Evaluating to false.", ex);
            LOG.warn(wktA + "\n" + wktB);
            return false;
        }
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}