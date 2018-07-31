package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralStringParameters;

/**
 * Class for evaluating if the literal contains a text parameter.
 * 
 * @author nkarag
 */
public class LiteralContains implements IFunction, IFunctionTwoLiteralStringParameters {

    /**
     * Checks if the Literal contains the a string value. The method returns false is the Literal is blank
     * and true if the value is blank.
     *
     * @param literal the literal.
     * @param value the string value.
     * @return true if the literal contains the given value.
     */
    @Override
    public boolean evaluate(Literal literal, String value) {

        if(literal == null){
            return false;
        }

        if(StringUtils.isBlank(literal.getString())){
            return false;
        }
        
        if(StringUtils.isBlank(value)){
            return true;
        }

        return literal.getString().contains(value);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
