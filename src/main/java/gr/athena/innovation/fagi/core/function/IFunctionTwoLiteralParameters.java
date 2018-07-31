package gr.athena.innovation.fagi.core.function;

import org.apache.jena.rdf.model.Literal;

/**
 * Interface for a condition function that takes two string parameters as input.
 * 
 * @author nkarag
 */
public interface IFunctionTwoLiteralParameters extends IFunction{
    
    /**
     * Evaluates the two string parameter function.
     * @param valueA the literal A.
     * @param valueB the literal B.
     * @return the evaluation result.
     */    
    public boolean evaluate(Literal valueA, Literal valueB);
    
}
