package gr.athena.innovation.fagi.core.function;

import org.apache.jena.rdf.model.Literal;

/**
 * Interface for a condition function that takes five string parameters as input.
 * 
 * @author nkarag
 */
public interface IFunctionFiveStringParameters extends IFunction{
    
    /**
     * Evaluates the five parameter function.
     * 
     * @param literalA
     * @param paramA
     * @param literalB
     * @param paramB
     * @param value
     * @return the evaluation result.
     */
    public boolean evaluate(Literal literalA, String paramA, Literal literalB, String paramB, String value);
    
}
