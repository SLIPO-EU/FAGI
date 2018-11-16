package gr.athena.innovation.fagi.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Model to hold typedNameAttributes and nameAttributes in the same class.
 * 
 * @author nkarag
 */
public class NameModel {

    private LinkedHashSet<TypedNameAttribute> typed;
    private LinkedHashSet<NameAttribute> withoutType;

    public Set<TypedNameAttribute> getTyped() {
        return typed;
    }

    public void setTyped(LinkedHashSet<TypedNameAttribute> typed) {
        this.typed = typed;
    }

    public Set<NameAttribute> getWithoutType() {
        return withoutType;
    }

    public void setWithoutType(LinkedHashSet<NameAttribute> withoutType) {
        this.withoutType = withoutType;
    }
}
