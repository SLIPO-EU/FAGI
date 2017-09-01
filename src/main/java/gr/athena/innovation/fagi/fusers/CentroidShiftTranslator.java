package gr.athena.innovation.fagi.fusers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author nkarag
 */
public class CentroidShiftTranslator {
    
    private final Geometry targetGeometry; 

    public CentroidShiftTranslator(Geometry target) { 
      this.targetGeometry = target; 
    } 

    /**
     * Shifts a geometry to the target geometry. 
     *  
     * @param originalGeometry 
     * @return  the shifted geometry as Geometry object
     */ 
    public Geometry shift(Geometry originalGeometry) { 
        Geometry shiftedGeometry = (Geometry) originalGeometry.clone(); 
        Coordinate targetCentroid = targetGeometry.getCentroid().getCoordinate(); 
        Coordinate originalCentroid = originalGeometry.getCentroid().getCoordinate(); 
        Coordinate deltaShift = subtract(targetCentroid, originalCentroid); 
        translate(shiftedGeometry, deltaShift);

        return shiftedGeometry; 
    } 

    /**
     * Moves geometry so that delta is at (0,0). 
     * @param geometry the Geometry to modify 
     * @param deltaShift the Coordinate of the translation
     */ 
    public void translate(Geometry geometry, final Coordinate deltaShift) { 
        geometry.apply(new CoordinateFilter() {
            @Override
            public void filter(Coordinate coordinate) {
                coordinate.x += deltaShift.x;
                coordinate.y += deltaShift.y;
            }
        }); 
    } 

    private Coordinate subtract(Coordinate coordinateA, Coordinate coordinateB) { 
        return new Coordinate(coordinateA.x - coordinateB.x, coordinateA.y - coordinateB.y); 
    }     
  }
