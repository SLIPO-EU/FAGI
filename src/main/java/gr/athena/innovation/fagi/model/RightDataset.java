package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;

/**
 * Class holding information about the right dataset of the specification.
 * 
 * @author nkarag
 */
public final class RightDataset {
    
    private static RightDataset rightDataset = null;
    private Model model;
    private String namespace;
    private String filepath;

    private RightDataset() {
         //defeat instantiation
    }
    
    /**
     * Returns a new RightDataset object if it is not already instantiated.
     * 
     * @return the right dataset object.
     */
    public static RightDataset getRightDataset() {
        if(rightDataset == null) {
           rightDataset = new RightDataset();
        }
       
       return rightDataset;
    }
    
    /**
     * Sets the RDF model of the right dataset.
     * 
     * @param model the model.
     */
    public void setModel(Model model){
        this.model = model;
    }
    
    /**
     * Return the RDF model of the right dataset.
     * 
     * @return the model.
     */
    public Model getModel(){
        return model;
    }

    /**
     * Return the namespace of the right dataset.
     * 
     * @return the namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set the namespace of the right dataset.
     * 
     * @param namespace the namespace.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Return the file-path of the right dataset.
     * 
     * @return the filepath.
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Set the filepath of the right dataset.
     * 
     * @param filepath the filepath.
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
