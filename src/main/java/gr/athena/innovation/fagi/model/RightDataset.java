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
    
    public static RightDataset getRightDataset() {
        if(rightDataset == null) {
           rightDataset = new RightDataset();
        }
       
       return rightDataset;
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
