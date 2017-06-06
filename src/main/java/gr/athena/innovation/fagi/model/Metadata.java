package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author nkarag
 */
public class Metadata {
    
    private Model model = ModelFactory.createDefaultModel();
    
    public Metadata() {
        
    }
    
    public Metadata(Model model) {
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
}
