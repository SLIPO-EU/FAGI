package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Class wrapping the RDF model of an entity.
 * 
 * @author nkarag
 */
public class EntityData {
    
    private Model model = ModelFactory.createDefaultModel();
    private String uri;
    
    /**
     * Empty constructor.
     */
    public EntityData() {
        //create entity data without initial RDF model.
    }
    
    /**
     * Constructor with a model.
     * 
     * @param model the RDF jena model.
     */
    public EntityData(Model model) {
        this.model = model;
    }

    /**
     * Return the model of this EntityData object, or a new default model if the model does not exist. 
     * 
     * @return the model. 
     */
    public Model getModel() {
        if(model == null){
            return ModelFactory.createDefaultModel();
        }
        return model;
    }

    /**
     * Set the model of this EntityData object.
     * 
     * @param model the model. 
     */
    public void setModel(Model model) {
        this.model = model;
    }
    
    /**
     * Checks if the model of the current EntityData object contains the given RDF property.
     * 
     * @param property the RDF property.
     * @return true if the model contains the property, false otherwise.
     */
    public boolean containsProperty(Property property){
        for (StmtIterator i = model.listStatements( null, null, (RDFNode) null ); i.hasNext(); ) {

            Statement originalStatement = i.nextStatement();
            Property p = originalStatement.getPredicate();    
            if(p.equals(property)){
                return true;
            }
        }
        return false;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
