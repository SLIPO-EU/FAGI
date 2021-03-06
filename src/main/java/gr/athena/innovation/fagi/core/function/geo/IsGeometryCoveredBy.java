package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;
import gr.athena.innovation.fagi.utils.RDFUtils;

/**
 * Function class that evaluates the <code>coveredBy</code> geometry relationship.
 * 
 * @author nkarag
 */
public class IsGeometryCoveredBy  implements IFunction, IFunctionTwoLiteralParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesIntersect.class);

    /**
     * Tests if the first geometry is covered by the second geometry. 
     * <p>
     * This means that:
     * <ul>
     * <li>Every point of this geometry is a point of the other geometry.
     * <li>The DE-9IM Intersection Matrix for the two geometries matches
     * at least one of the following patterns:
     *  <ul>
     *   <li><code>[T*F**F***]</code>
     *   <li><code>[*TF**F***]</code>
     *   <li><code>[**FT*F***]</code>
     *   <li><code>[**F*TF***]</code>
     *  </ul>
     * <li><code>g.covers(this) = true</code>
     * <br>(<code>coveredBy</code> is the converse of {@link #covers})
     * </ul>
     * If either geometry is empty, the value of this predicate is <code>false</code>.
     * <p>
     *
     * @param wktA
     * @param wktB
     * @return True if the first argument geometry is covered by the second argument geometry.
     */
    @Override
    public boolean evaluate(Literal wktA, Literal wktB) {
        
        if(wktA == null || wktB == null){
            return false;
        }

        WKTReader reader = new WKTReader();
        Geometry geometryA;
        Geometry geometryB;

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

        return geometryA.coveredBy(geometryB);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
