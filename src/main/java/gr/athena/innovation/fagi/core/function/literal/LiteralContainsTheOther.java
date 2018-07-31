package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;

/**
 * Class for evaluating if the first literal contains the second.
 * 
 * @author nkarag
 */
public class LiteralContainsTheOther  implements IFunction, IFunctionTwoLiteralParameters {

    /**
     * Checks if the first literal contains the second.
     * 
     * @param literalA the first literal.
     * @param literalB the second literal.
     * @return True if the first contains the second, false otherwise.
     */
    @Override
    public boolean evaluate(Literal literalA, Literal literalB) {
        if(literalA == null || literalB == null){
            return false;
        }

        if(StringUtils.isBlank(literalA.getString()) || StringUtils.isBlank(literalB.getString())){
            return false;
        }
        
        return literalA.getString().contains(literalB.getString());
    }
    
    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
