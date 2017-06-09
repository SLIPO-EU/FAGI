package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;

/**
 *
 * @author nkarag
 */
public final class LeftModel {
    
    private static LeftModel leftModel = null;
    private Model model;
    private String namespace;

    private LeftModel() {
         //defeat instantiation
    }
    
    public static LeftModel getLeftModel() {
       if(leftModel == null) {
          leftModel = new LeftModel();
       }
       return leftModel;
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
    
}
