package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author nkarag
 */
public class Entity {
    
    private String resourceURI;
    private Metadata metadata;

    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
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
}
