package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.ByteArrayInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import gr.athena.innovation.fagi.core.function.IFunctionTwoStringParameters;

/**
 * Checks if the value of the name property is tagged as official.
 * 
 * @author nkarag
 */
public class IsNameValueOfficial implements IFunction, IFunctionTwoStringParameters{

    /**
     * Checks if the value of the name property is tagged as official.
     * 
     * @param modelText
     * @param literal The string literal.
     * @return True if the literal matches the pattern of regular expression that represents an abbreviation.
     */
    @Override
    public boolean evaluate(String modelText, String literal){

        if (StringUtils.isBlank(literal)) {
            return false;
        }

        final Model model = ModelFactory.createDefaultModel();
        
        model.read(new ByteArrayInputStream(modelText.getBytes()), null);
        
        return false;
    }

    /**
     * This is the actual method that it is used to evaluate if the name value is tagged as official.
     * 
     * @param model
     * @param propertyString
     * @return
     */
    public boolean evaluate(Model model, String propertyString) {
        //todo: add test
        String name = Namespace.NAME_NO_BRACKETS;
        String nameValue = Namespace.NAME_VALUE_NO_BRACKETS;
        
        String literal = SparqlRepository.getObjectOfPropertyChain(name, nameValue, model, true);

        return !StringUtils.isBlank(literal);
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
