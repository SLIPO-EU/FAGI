package gr.athena.innovation.fagi.core.function.property;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import gr.athena.innovation.fagi.core.function.IFunctionTwoModelStringParameters;

/**
 * Checks if the provided property is absent from the RDF model of a resource.
 * 
 * @author nkarag
 */
public class NotExists implements IFunction, IFunctionTwoModelStringParameters{

    /**
     * Evaluates the absence of the property in the model.
     * 
     * @param model the RDF model.
     * @param propertyString the property string.
     * @return true if the property exists in the model, false otherwise.
     */
    @Override
    public boolean evaluate(Model model, String propertyString) {
        Property property = ResourceFactory.createProperty(propertyString);
        return !propertyExistsInModel(model, property);
    }
    
    private static boolean propertyExistsInModel(Model model, Property property){

        for (StmtIterator i = model.listStatements( null, null, (RDFNode) null ); i.hasNext(); ) {

            Statement originalStatement = i.nextStatement();
            Property p = originalStatement.getPredicate();    
            if(p.equals(property)){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    
}

