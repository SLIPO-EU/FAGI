package gr.athena.innovation.fagi.core.functions;

/**
 * Interface for a condition function that takes a single parameter as input.
 * 
 * @author nkarag
 */
public interface IFunctionSingleParameter extends IFunction{
    
    /**
     * Evaluates the single parameter function.
     * @param value the input value.
     * @return the evaluation result.
     */
    public boolean evaluate(String value);
    
}
