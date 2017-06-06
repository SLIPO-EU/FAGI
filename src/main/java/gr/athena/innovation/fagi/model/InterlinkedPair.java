package gr.athena.innovation.fagi.model;

import com.vividsolutions.jts.geom.Geometry;
import gr.athena.innovation.fagi.fusers.CentroidShiftTranslator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing a pair of interlinked RDF entities.
 * 
 * @author nkarag
 */
public class InterlinkedPair {

    private static final Logger logger = LogManager.getLogger(InterlinkedPair.class);
    private Entity leftNode;
    private Entity rightNode;
    private Entity fusedEntity;

    public Entity getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Entity leftNode) {
        this.leftNode = leftNode;
    }

    public Entity getRightNode() {
        return rightNode;
    }

    public void setRightNode(Entity rightNode) {
        this.rightNode = rightNode;
    }

    public Entity getFusedEntity() {
        if(fusedEntity == null){
            logger.fatal("Current pair is not fused: " + this);
            throw new RuntimeException();
        }
        return fusedEntity;
    }

    public void fuse(EnumGeometricActions geoAction, EnumMetadataActions metaAction){
        fusedEntity = new Entity();
        //Metadata meta = new Metadata();
        fuseGeometry(geoAction);
        fuseMetadata(metaAction);
    }

    public void fuseGeometry(EnumGeometricActions geoAction){

        
        Geometry leftGeometry = leftNode.getGeometry();
        Geometry rightGeometry = rightNode.getGeometry();
        switch(geoAction){
            case KEEP_LEFT_GEOMETRY:
                fusedEntity.setGeometry(leftGeometry);
                break;
            case KEEP_RIGHT_GEOMETRY:
                fusedEntity.setGeometry(rightGeometry);
                break;
            case KEEP_MORE_POINTS:
                if(leftGeometry.getNumPoints() >= rightGeometry.getNumPoints()) {
                    fusedEntity.setGeometry(leftGeometry);
                } else {
                    fusedEntity.setGeometry(rightGeometry);
                }
                break;
            case KEEP_MORE_POINTS_AND_SHIFT:
                if(leftGeometry.getNumPoints() > rightGeometry.getNumPoints()) {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(leftGeometry);
                    fusedEntity.setGeometry(fusedGeometry);
                } else if(leftGeometry.getNumPoints() < rightGeometry.getNumPoints()){
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                    Geometry fusedGeometry = centroidTranslator.shift(rightGeometry);                    
                    fusedEntity.setGeometry(fusedGeometry);
                } else {
                    fusedEntity.setGeometry(leftGeometry);
                }    
                break;
            case SHIFT_LEFT_GEOMETRY:
                {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(rightGeometry);
                    Geometry shiftedToRightGeometry = centroidTranslator.shift(leftGeometry);
                    fusedEntity.setGeometry(shiftedToRightGeometry);
                    break;
                }
            case SHIFT_RIGHT_GEOMETRY:
                {
                    CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator(leftGeometry);
                    Geometry shiftedToLeftGeometry = centroidTranslator.shift(rightGeometry);
                    fusedEntity.setGeometry(shiftedToLeftGeometry);
                    break;
                }
            case KEEP_BOTH_GEOMETRIES:
                logger.fatal("Keep both geometries not supported yet.");
                throw new UnsupportedOperationException("Keep both geometries not supported yet.");                
            default:
                logger.fatal("Geometric fusion action is not defined.");
                throw new RuntimeException();
        }
    }

    public void fuseMetadata(EnumMetadataActions metaAction){
        
        Metadata leftMetadata = leftNode.getMetadata();
        Metadata rightMetadata = rightNode.getMetadata();

        Model fusedModel = ModelFactory.createDefaultModel();
        switch(metaAction){
            case KEEP_LEFT_METADATA:
                fusedEntity.setMetadata(leftMetadata);
                break;
            case KEEP_RIGHT_METADATA:
                fusedEntity.setMetadata(rightMetadata);
                break;
            case KEEP_BOTH_METADATA:
                Metadata fusedMetadata = fusedEntity.getMetadata();
                fusedModel.add(leftMetadata.getModel()).add(rightMetadata.getModel());
                fusedMetadata.setModel(fusedModel);
                fusedEntity.setMetadata(fusedMetadata);
                break;
        }        
    }
}
