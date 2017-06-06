package gr.athena.innovation.fagi.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author nkarag
 */
public class Entity {
    
    private String resourceURI;
    private RDFNode geometryNode;
    private Geometry geometry;
    private Metadata metadata;

    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }
    
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Metadata getMetadata() {
        if(metadata == null){
            return new Metadata(ModelFactory.createDefaultModel());
        }
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
    public String getWKTLiteral(){
        WKTWriter w = new WKTWriter();
        return w.write(geometry);
    }

    public RDFNode getGeometryNode() {
        return geometryNode;
    }

    public void setGeometryNode(RDFNode geometryNode) {
        this.geometryNode = geometryNode;
    }

}
