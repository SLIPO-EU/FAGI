package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author nkarag
 */
public class Entity {
    
    private String resourceURI;
    private EntityData entityData;

    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public EntityData getEntityData() {
        if(entityData == null){
            return new EntityData(ModelFactory.createDefaultModel());
        }
        return entityData;
    }

    public void setEntityData(EntityData entityData) {
        this.entityData = entityData;
    }
}
