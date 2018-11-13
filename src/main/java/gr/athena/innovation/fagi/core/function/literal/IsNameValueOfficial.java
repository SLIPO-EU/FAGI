package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Namespace;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoModelStringParameters;

/**
 * Checks if the value of the name property is tagged as official.
 * 
 * @author nkarag
 */
public class IsNameValueOfficial implements IFunction, IFunctionTwoModelStringParameters{

    /**
     * Checks if the value of the name property is tagged as official.
     * 
     * @param model the RDF model.
     * @param propertyString the property string.
     * @return true if the name is tagged as official, false otherwise.
     */
    @Override
    public boolean evaluate(Model model, String propertyString) {
        //todo: add test
        String name = Namespace.NAME_NO_BRACKETS;
        String nameValue = Namespace.NAME_VALUE_NO_BRACKETS;
        
        Literal literal = SparqlRepository.getLiteralFromPropertyChain(name, nameValue, model, true);

        return !StringUtils.isBlank(literal.toString());
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
