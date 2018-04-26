package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.ModelFactory;

/**
 * Class representing entities. 
 * 
 * @author nkarag
 */
public class Entity {
    
    private String resourceURI;
    private String localName;
    private EntityData entityData;

    /**
     * Returns the resource URI.
     * 
     * @return the string representation of the resource in the dataset.
     */
    public String getResourceURI() {
        return resourceURI;
    }

    /**
     * Sets the resource URI.
     * 
     * @param resourceURI. The string representation of the resource URI.
     */
    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    /**
     * Returns the local name of the entity's URI. This is usually the alphanumeric id of the resource.
     * 
     * @return the localName
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Set the local name of the entity's URI.
     * 
     * @param localName. The local name
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }
    
    /**
     * Return the data object of the entity. If data object is null, return a new object containing a default RDF model.
     * 
     * @return the entity data object.
     */
    public EntityData getEntityData() {
        if(entityData == null){
            return new EntityData(ModelFactory.createDefaultModel());
        }
        return entityData;
    }

    /**
     * Set the entity data object.
     * 
     * @param entityData
     */
    public void setEntityData(EntityData entityData) {
        this.entityData = entityData;
    }

}
