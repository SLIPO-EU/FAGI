package gr.athena.innovation.fagi.core.function.geo;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoParameters;

/**
 *
 * @author nkarag
 */
public class IsGeometryMoreComplex implements IFunction, IFunctionTwoParameters{

    @Override
    public boolean evaluate(String valueA, String valueB) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    
}
