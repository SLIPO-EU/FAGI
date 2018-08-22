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

    public boolean isSingleLevel() {
        return singleLevel;
    }

    public void setSingleLevel(boolean singleLevel) {
        this.singleLevel = singleLevel;
    }

    public Property getParent() {
        return parent;
    }

    public void setParent(Property parent) {
        this.parent = parent;
    }

    public Property getValueProperty() {
        return valueProperty;
    }

    public void setValueProperty(Property valueProperty) {
        this.valueProperty = valueProperty;
    }
}
