package gr.athena.innovation.fagi.model;

import gr.athena.innovation.fagi.specification.Namespace;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

/**
 * Class holding information about the links between the datasets.
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
     * Get the links file path.
     * 
     * @return the file path
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Set the links file path.
     * 
     * @param filepath the file path
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
    /**
     * Removes a link from the links-list as well as from the RDF model. Helps the link rejection process.
     * 
     * @param link the link to be removed
     */
    public void removeLink(Link link){
        
        //remove link from the list
        links.remove(link);
        
        //remove link from the RDF model.
        Resource a = ResourceFactory.createResource(link.getNodeA());
        Property pred = ResourceFactory.createProperty(Namespace.SAME_AS);
        Resource b = ResourceFactory.createResource(link.getNodeB());
        
        Statement statement = ResourceFactory.createStatement(a, pred, b);
        model.remove(statement);
    }
}
