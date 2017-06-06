package gr.athena.innovation.fagi.model;

import org.apache.jena.rdf.model.Model;

/**
 *
 * @author nkarag
 */
public final class RightModel {
    
    private static RightModel rightModel = null;
    private Model model;
    private String namespace;

    private RightModel() {
         //defeat instantiation
    }
    
    public static RightModel getRightModel() {
        if(rightModel == null) {
           rightModel = new RightModel();
        }
       
       return rightModel;
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
