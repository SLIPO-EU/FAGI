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
    
    public EntityData() {
        //create entity data without initial RDF model.
    }
    
    public EntityData(Model model) {
        this.model = model;
    }

    public Model getModel() {
        if(model == null){
            return ModelFactory.createDefaultModel();
        }
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
    
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
}
