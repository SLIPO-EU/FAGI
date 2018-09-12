package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;

/**
 * Class holding information about the fused dataset of the specification.
 * 
 * @author nkarag
 */
public final class FusedDataset {
    
    private static FusedDataset fusedDataset = null;
    private Model model;
    private String namespace;
    private String filepath;

    private FusedDataset() {
         //defeat instantiation
    }
    
    public static FusedDataset getFusedDataset() {
       if(fusedDataset == null) {
          fusedDataset = new FusedDataset();
       }
       return fusedDataset;
    }
    
    public void setModel(Model model){
        this.model = model;
    }
    
    public Model getModel(){
        return model;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
}
