package gr.athena.innovation.fagi.core.functions.property;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Class for utility method of a rule. Checks if the provided property exists in the RDF model of a resource.
 * 
 * @author nkarag
 */
public class Exists {
    
    public static boolean propertyExistsInModel(Model model, Property property){

        for (StmtIterator i = model.listStatements( null, null, (RDFNode) null ); i.hasNext(); ) {

            Statement originalStatement = i.nextStatement();
            Property p = originalStatement.getPredicate();    
            if(p.equals(property)){
                return true;
            }
        }
        return false;
    }
    
    public String getName(){
        String className = this.getClass().getSimpleName();
        return className;
    }
}
