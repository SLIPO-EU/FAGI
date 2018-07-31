package gr.athena.innovation.fagi.core.function;

import org.apache.jena.rdf.model.Literal;

/**
 * Interface for a condition function that takes two literals and one string parameter as input.
 * 
 * @author nkarag
 */
public interface IFunctionThreeLiteralStringParameters extends IFunction{
    
    /**
     * Evaluates the three parameter function.
     * @param literalA the literal of A.
     * @param literalB the literal of B.
     * @param parameter the string parameter.
     * @return the evaluation result.
     */      
    public boolean evaluate(Literal literalA, Literal literalB, String parameter);
    
}
