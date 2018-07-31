package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;

/**
 *
 * @author nkarag
 */
public class IsGeometryMoreComplex implements IFunction, IFunctionTwoLiteralParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsGeometryMoreComplex.class);

    @Override
    public boolean evaluate(Literal wktA, Literal wktB) {
        
        if(wktA == null || wktB == null){
            return false;
        }
        
        WKTReader reader = new WKTReader();
        Geometry geometryA;
        Geometry geometryB;

        try {
            geometryA = reader.read(wktA.getLexicalForm());
        } catch (ParseException ex) {
            LOG.warn(ex);
            LOG.warn("Could not parse WKT: " + wktA + "\nReturning false.");
            return false;
        }

        try {
            geometryB = reader.read(wktB.getLexicalForm());
        } catch (ParseException ex) {
            LOG.warn(ex);
            LOG.warn("Could not parse WKT: " + wktB + "\nReturning false.");
            return false;
        }

        return geometryA.getNumPoints() > geometryB.getNumPoints();
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
