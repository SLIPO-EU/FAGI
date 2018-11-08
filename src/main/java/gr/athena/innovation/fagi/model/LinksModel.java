package gr.athena.innovation.fagi.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;

/**
 * Class holding information about the links between the datasets.
 * 
 * @author nkarag
 */
public final class LinksModel {
    
    private static LinksModel linksModel = null;
    private Model model;
    private List<Link> links;
    private final List<Link> rejected = new ArrayList<>();
    private String filepath;

    private LinksModel() {
         //defeat instantiation
    }
    
    /**
     * Get the current links object or create new if it does not exist.
     * 
     *  @return the current factory object
     */
    public static LinksModel getLinksModel() {
       if(linksModel == null) {
          linksModel = new LinksModel();
       }
       return linksModel;
    }
    
    /** 
     * set the RDF model of the links.
     * 
     * @param model the links RDF model
     */
    public void setModel(Model model){
        this.model = model;
    }
    
    /**
     * Get the RDF model of the links.
     * 
     * @return the model
     */
    public Model getModel(){
        return model;
    }

    /**
     * Get the list with the link objects.
     * 
     * @return the list of links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * Set the list with the link objects.
     * 
     * @param links the links
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * Set the links file path.
     * 
     * @param filepath the file path
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public List<Link> getRejected() {
        return rejected;
    }
}
