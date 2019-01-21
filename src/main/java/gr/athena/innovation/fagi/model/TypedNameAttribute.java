package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.exception.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

/**
 * Typed name attribute refers to names that have the "nameType" property defined. 
 * This value could be "official", "international", "brand-name" etc.
 * The equals and hashCode are overridden in order to check only the type for equality.
 * Attributes with the same type are considered duplicates regardless of the name value.
 * 
 * @author nkarag
 */
public class TypedNameAttribute {

    private static final Property RDF_TYPE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static final Property NAME_CLASS = ResourceFactory.createProperty("http://slipo.eu/def#name");
    private static final Property NAME_VALUE = ResourceFactory.createProperty("http://slipo.eu/def#nameValue");
    private static final Property LANG = ResourceFactory.createProperty("http://slipo.eu/def#language");
    private static final Property TYPE = ResourceFactory.createProperty("http://slipo.eu/def#nameType");

    private Resource objectURI;
    private final Literal nameValue;
    private final Literal nameType;
    private final Literal language;
    private final Statement classOf;
    private final Statement parentTriple;

    /**
     * Constructor of a typed name attribute.
     * 
     * @param poiURI the URI of the POI.
     * @param nameValue the value of the name.
     * @param nameType the type of the name.
     * @param language the language of the name.
     * @param objectURI the object URI.
     */
    public TypedNameAttribute(Resource poiURI, RDFNode nameValue, RDFNode nameType, RDFNode language, RDFNode objectURI) {
        if(nameValue.isLiteral() && nameType.isLiteral() && language.isLiteral() && objectURI.isResource()){
            this.nameValue = nameValue.asLiteral();
            this.nameType = nameType.asLiteral();
            this.language = language.asLiteral();
            this.objectURI = objectURI.asResource();
        } else {
            throw new ApplicationException("Wrong construction of typed name attribute");
        }

        parentTriple = ResourceFactory.createStatement(poiURI, NAME_CLASS, objectURI.asResource());
        classOf = ResourceFactory.createStatement(objectURI.asResource(), RDF_TYPE, NAME_CLASS);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.nameType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TypedNameAttribute other = (TypedNameAttribute) obj;

        return Objects.equals(this.nameType, other.nameType);
    }

    /**
     * Return the RDF statements of the typed name attribute.
     * 
     * @return the statements.
     */
    public List<Statement> getStatements(){
        
        List<Statement> statements = new ArrayList<>();
        Statement stValue = ResourceFactory.createStatement(objectURI, NAME_VALUE, nameValue);
        Statement stLanguage = ResourceFactory.createStatement(objectURI, LANG, language);
        Statement stType = ResourceFactory.createStatement(objectURI, TYPE, nameType);
        
        statements.add(parentTriple);
        statements.add(classOf);
        statements.add(stValue);
        statements.add(stLanguage);
        statements.add(stType);

        return statements;
    }
    
    /**
     * Return the value of the typed name attribute as a RDF literal.
     * 
     * @return the name value.
     */
    public Literal getNameValue() {
        return nameValue;
    }
}
