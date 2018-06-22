package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Property;

/**
 *
 * @author nkarag
 */
public class CustomRDFProperty {
    private boolean singleLevel;
    private Property parent;
    private Property child;

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

    public Property getChild() {
        return child;
    }

    public void setChild(Property child) {
        this.child = child;
    }
}
