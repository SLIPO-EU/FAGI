package gr.athena.innovation.fagi.core.function.property;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import gr.athena.innovation.fagi.core.function.IFunctionTwoModelStringParameters;
import gr.athena.innovation.fagi.model.CustomRDFProperty;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.utils.RDFUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks if the provided property exists in the RDF model of a resource.
 * 
 * @author nkarag
 */
public class Exists implements IFunction, IFunctionTwoModelStringParameters{

    private static final Logger LOG = LogManager.getLogger(Exists.class);
    
    /**
     * Evaluates the existence of the property in the model.
     * 
     * @param model the RDF model.
     * @param propertyString the property string.
     * @return true if the property exists in the model, false otherwise.
     */
    @Override
    public boolean evaluate(Model model, String propertyString) {
        Property property = ResourceFactory.createProperty(propertyString);
        return propertyExistsInModel(model, property);
    }

    public boolean evaluate(Model model, CustomRDFProperty property) {
        return propertyExistsInModel(model, property);
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

    private static boolean propertyExistsInModel(Model model, CustomRDFProperty property){

        int c;
        if(property.isSingleLevel()){
            c = SparqlRepository.countProperty(model, RDFUtils.addBrackets(property.getValueProperty().toString()));
        } else {
            c = SparqlRepository.countPropertyChain(model, RDFUtils.addBrackets(property.getParent().toString()), 
                    RDFUtils.addBrackets(property.getValueProperty().toString()));
        }
        
        return c > 0;
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    
}
