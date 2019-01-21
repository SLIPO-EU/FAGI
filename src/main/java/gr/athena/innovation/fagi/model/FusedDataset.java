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
    
    /**
     * Returns a new FusedDataset object if it is not already instantiated.
     * 
     * @return the fused dataset object.
     */
    public static FusedDataset getFusedDataset() {
       if(fusedDataset == null) {
          fusedDataset = new FusedDataset();
       }
       return fusedDataset;
    }
    
    /**
     * Sets the RDF model of the fused dataset.
     * 
     * @param model the model.
     */
    public void setModel(Model model){
        this.model = model;
    }
    
    /**
     * Return the RDF model of the fused dataset.
     * 
     * @return the model.
     */
    public Model getModel(){
        return model;
    }

    /**
     * Return the namespace of the fused dataset.
     * 
     * @return the namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set the namespace of the fused dataset.
     * 
     * @param namespace the namespace.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Return the file-path of the fused dataset.
     * 
     * @return the filepath.
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Set the filepath of the fused dataset.
     * 
     * @param filepath the filepath.
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
