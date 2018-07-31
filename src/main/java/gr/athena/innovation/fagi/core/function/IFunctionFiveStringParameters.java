package gr.athena.innovation.fagi.core.function;

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
    public boolean evaluate(String literalA, String paramA, String literalB, String paramB, String value);
    
}
