package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;

/**
 * Class holding information about the left dataset of the specification.
 * 
 * @author nkarag
 */
public final class LeftDataset {
    
    private static LeftDataset leftDataset = null;
    private Model model;
    private String namespace;
    private String filepath;

    private LeftDataset() {
         //defeat instantiation
    }
    
    /**
     * Returns a new LeftDataset object if it is not already instantiated.
     * 
     * @return the left dataset object.
     */
    public static LeftDataset getLeftDataset() {
       if(leftDataset == null) {
          leftDataset = new LeftDataset();
       }
       return leftDataset;
    }
    
    /**
     * Sets the RDF model of the left dataset.
     * 
     * @param model the model.
     */
    public void setModel(Model model){
        this.model = model;
    }
    
    /**
     * Return the RDF model of the left dataset.
     * 
     * @return the model.
     */
    public Model getModel(){
        return model;
    }

    /**
     * Return the namespace of the left dataset.
     * 
     * @return the namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set the namespace of the left dataset.
     * 
     * @param namespace the namespace.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Return the file-path of the left dataset.
     * 
     * @return the filepath.
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Set the filepath of the left dataset.
     * 
     * @param filepath the filepath.
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
