package gr.athena.innovation.fagi.model;

import java.util.List;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author nkarag
 */
public final class LinksModel {
    
    private static LinksModel linksModel = null;
    private Model model;
    private List<Link> links;
    private String filepath;

    private LinksModel() {
         //defeat instantiation
    }
    
    public static LinksModel getLinksModel() {
       if(linksModel == null) {
          linksModel = new LinksModel();
       }
       return linksModel;
    }
    
    public void setModel(Model model){
        this.model = model;
    }
    
    public Model getModel(){
        return model;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
}
