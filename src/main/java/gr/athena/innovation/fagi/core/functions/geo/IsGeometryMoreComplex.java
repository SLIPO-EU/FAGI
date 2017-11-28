package gr.athena.innovation.fagi.core.functions.geo;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.functions.IFunctionTwoParameters;

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
