package gr.athena.innovation.fagi.core.function;

import org.apache.jena.rdf.model.Literal;

/**
 * Interface for a condition function that takes a single Literal parameter as input.
 * 
 * @author nkarag
 */
public interface IFunctionOneParameter extends IFunction{
    
    /**
     * Evaluates the single Literal parameter function.
     * @param value the input value.
     * @return the evaluation result.
     */
    public boolean evaluate(Literal value);
}
