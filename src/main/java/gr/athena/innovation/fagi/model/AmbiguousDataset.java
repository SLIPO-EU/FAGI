package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Class containing the RDF model of the ambiguous dataset of the specification.
 * 
 * @author nkarag
 */
public final class AmbiguousDataset {
    
    private static AmbiguousDataset ambiguousDataset = null;
    private Model model;

    private AmbiguousDataset() {
         //defeat instantiation
    }
    
    /**
     * Returns a new AmbiguousDataset object if it is not already instantiated.
     * 
     * @return the ambiguous dataset object.
     */
    public static AmbiguousDataset getAmbiguousDataset() {
       if(ambiguousDataset == null) {
          ambiguousDataset = new AmbiguousDataset();
       }
       return ambiguousDataset;
    }
    
    /**
     * Set the RDF jena model.
     * 
     * @param model the model.
     */
    public void setModel(Model model){
        this.model = model;
    }
    
    /**
     * Return the model of the ambiguous dataset. If the model is null creates and returns a default model.
     * @return
     */
    public Model getModel(){
        if(model == null){
            model = ModelFactory.createDefaultModel();
        }
        return model;
    }
}
