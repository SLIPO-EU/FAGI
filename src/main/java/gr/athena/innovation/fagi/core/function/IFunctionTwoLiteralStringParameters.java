package gr.athena.innovation.fagi.core.function;

import org.apache.jena.rdf.model.Literal;

/**
 * Interface for a condition function that takes a Literal and a string as parameters.
 * 
 * @author nkarag
 */
public interface IFunctionTwoLiteralStringParameters extends IFunction{
    
    /**
     * Evaluates the the condition function with the Literal and string as parameters.
     * @param literal the input literal.
     * @param value the input string value.
     * @return the evaluation result.
     */    
    public boolean evaluate(Literal literal, String value);
}
