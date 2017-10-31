package gr.athena.innovation.fagi.repository;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class SparqlRepository {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(SparqlRepository.class);
    
    public static String getObjectOfProperty(Property p, Model model){
        String rdfObjectValue = null;

        List<RDFNode> objectList = model.listObjectsOfProperty(p).toList();

        if(objectList.size() == 1){
            RDFNode object = objectList.get(0);
            if(object.isLiteral()){
                return object.toString();
            } else {
                logger.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else if(objectList.size()>1){
            //Possible duplicate triple. Happens with synthetic data. Returns the first literal
            RDFNode object = objectList.get(0);
            if(object.isLiteral()){
                return object.toString();
            } else {
                logger.fatal("Object is not a Literal! " + object.toString());
                return null;
            }            
        }
        else {
            logger.debug("Problem finding unique result with property: " + p + "\nObjects returned: " + objectList.size());
        }
        return rdfObjectValue;
    }    
}
