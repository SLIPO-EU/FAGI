package gr.athena.innovation.fagi.core.functions;

/**
 * Interface for a condition function that takes two parameters as input.
 * 
 * @author nkarag
 */
public interface IFunctionTwoParameters extends IFunction{
    
    /**
     * Evaluates the two parameter function.
     * @param valueA the input value A.
     * @param valueB the input value B.
     * @return the evaluation result.
     */    
    public boolean evaluate(String valueA, String valueB);
    
}
