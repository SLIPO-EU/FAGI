package gr.athena.innovation.fagi.core.function;

import org.apache.jena.rdf.model.Model;

/**
 * Interface for a condition function that takes a model and a string parameter.
 * 
 * @author nkarag
 */
public interface IFunctionModelStringTwoParameters extends IFunction{
    
    /**
     * Evaluates the model and string condition function.
     * @param model the input model.
     * @param value the input value.
     * @return the evaluation result.
     */    
    public boolean evaluate(Model model, String value);
}
