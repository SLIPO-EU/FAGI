package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionOneParameter;

/**
 * Class for evaluating if the given literal is numeric.
 * 
 * @author nkarag
 */
public class IsLiteralNumeric implements IFunction, IFunctionOneParameter {

    /**
     * Checks if the given literal is numeric (at least one digit or more).
     * 
     * @param literal the literal object.
     * @return True if the literal is numerical, false otherwise.
     * 
     */
    @Override
    public boolean evaluate(Literal literal) {
        return literal.getLexicalForm().matches(SpecificationConstants.Regex.NUMERIC);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
