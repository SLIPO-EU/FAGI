package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.Link;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract repository for reading RDF input. 
 * 
 * @author nkarag
 */
public abstract class AbstractRepository {
    
    private static final Logger logger = LogManager.getLogger(AbstractRepository.class);
    
    public abstract void readFile(String path);
    
    /**
     * Loads the given RDF file into a RDF model as the left dataset.
     * 
     * @param filepath
     * @throws gr.athena.innovation.fagi.exception.WrongInputException
     */
    public abstract void parseLeft(String filepath) throws WrongInputException;
    
    /**
     * Loads the given RDF file into a RDF model as the right dataset.
     * @param filepath
     * @throws gr.athena.innovation.fagi.exception.WrongInputException
     */  
    public abstract void parseRight(String filepath) throws WrongInputException;
    
    /**
     * Loads the given links file into a RDF model.
     * 
     * @param filepath
     * @throws java.text.ParseException if link file contains invalid links
     * @throws gr.athena.innovation.fagi.exception.WrongInputException
     */    
    public abstract void parseLinks(String filepath) throws ParseException, WrongInputException;
    
    /**
     * Parses given RDF link file into a list of @Link.
     * 
     * @param linksFile link file
     * @return list of links
     * @throws java.text.ParseException if link file contains invalid links
     */    
    public static List<Link> parseLinksFile(final String linksFile) throws ParseException {

        List<Link> links = new ArrayList<>();
        
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, linksFile);
        
        final StmtIterator iter = model.listStatements();
        
        while(iter.hasNext()) {
            
            final Statement statement = iter.nextStatement();
            final String nodeA = statement.getSubject().getURI();
            final String uriA = statement.getSubject().getLocalName();
            final String nodeB;
            final String uriB;
            final RDFNode object = statement.getObject();

            if(object.isResource()) {
                nodeB = object.asResource().getURI();
                uriB = object.asResource().getLocalName();
            }
            else {
                throw new ParseException("Failed to parse link (object not a resource): " + statement.toString(), 0);
            }
            Link link = new Link(nodeA, uriA, nodeB, uriB);
            links.add(link);
        }
        
        return links;       
    }       
}
