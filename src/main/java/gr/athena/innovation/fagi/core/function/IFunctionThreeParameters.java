package gr.athena.innovation.fagi.core.function;

/**
 * Interface for a condition function that takes three parameters as input.
 * 
 * @author nkarag
 */
public interface IFunctionThreeParameters extends IFunction{
    
    /**
     * Evaluates the three parameter function.
     * @param valueA the input value A.
     * @param valueB the input value B.
     * @param valueC the input value C.
     * @return the evaluation result.
     */      
    public boolean evaluate(String valueA, String valueB, String valueC);
    
}
