package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Property;

/**
 * Class representing an RDF property based on the slipo ontology. 
 * Wraps a single triple or a two-chain property.
 * 
 * @author nkarag
 */
public class CustomRDFProperty {
    private boolean singleLevel;
    private Property parent;
    private Property valueProperty;

    /**
     *
     * @return true if the property is single level, false otherwise.
     */
    public boolean isSingleLevel() {
        return singleLevel;
    }

    /**
     * Set the singleLevel value of this property object.
     * 
     * @param singleLevel the level as boolean.
     */
    public void setSingleLevel(boolean singleLevel) {
        this.singleLevel = singleLevel;
    }

    /**
     * Return the parent of this property.
     * 
     * @return the parent.
     */
    public Property getParent() {
        return parent;
    }

    /**
     * Set the parent of the property.
     * 
     * @param parent the parent.
     */
    public void setParent(Property parent) {
        this.parent = parent;
    }

    /**
     * Return the property that indicates the value. This is also the child property when the property is single-level.
     * 
     * @return the value property.
     */
    public Property getValueProperty() {
        return valueProperty;
    }

    /**
     * Set the property that indicates the value. This is also the child property when the property is single-level.
     * 
     * @param valueProperty the value property.
     */
    public void setValueProperty(Property valueProperty) {
        this.valueProperty = valueProperty;
    }
}
