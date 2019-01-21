package gr.athena.innovation.fagi.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Model to hold typedNameAttributes and nameAttributes in the same class.
 * Name attributes with types are name resources that are indicated as official, international etc.
 * 
 * @author nkarag
 */
public class NameModel {

    private LinkedHashSet<TypedNameAttribute> typed;
    private LinkedHashSet<NameAttribute> withoutType;

    /**
     * Return the set of the typed name attributes.
     * 
     * @return
     */
    public Set<TypedNameAttribute> getTyped() {
        return typed;
    }

    /**
     * Set the set of the typed name attributes. The current implementation requires a LinkedHashSet.
     * 
     * @param typed the typed name attributes.
     */
    public void setTyped(LinkedHashSet<TypedNameAttribute> typed) {
        this.typed = typed;
    }

    /**
     * Return the set of the name attributes that do not have a type.
     * 
     * @return the name attributes without a type.
     */
    public Set<NameAttribute> getWithoutType() {
        return withoutType;
    }

    /**
     * Set the name attributes that do not have a type. The current implementation requires a LinkedHashSet.
     * 
     * @param withoutType the name attributes without a type.
     */
    public void setWithoutType(LinkedHashSet<NameAttribute> withoutType) {
        this.withoutType = withoutType;
    }
}
