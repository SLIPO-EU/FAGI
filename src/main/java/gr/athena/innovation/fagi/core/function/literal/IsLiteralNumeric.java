package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.core.function.IFunctionSingleStringParameter;

/**
 * Class for evaluating if the given literal is numeric.
 * 
 * @author nkarag
 */
public class IsLiteralNumeric implements IFunction, IFunctionSingleStringParameter {

    /**
     * Checks if the given number is numeric (at least one digit or more).
     * 
     * @param literal the literal to evaluate.
     * @return True if the literal is numerical, false otherwise.
     * 
     */
    @Override
    public boolean evaluate(String literal) {

        return literal.matches(SpecificationConstants.Regex.NUMERIC);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
