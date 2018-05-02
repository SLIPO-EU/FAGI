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
    
    public static AmbiguousDataset getAmbiguousDataset() {
       if(ambiguousDataset == null) {
          ambiguousDataset = new AmbiguousDataset();
       }
       return ambiguousDataset;
    }
    
    public void setModel(Model model){
        this.model = model;
    }
    
    public Model getModel(){
        if(model == null){
            model = ModelFactory.createDefaultModel();
        }
        return model;
    }
}
