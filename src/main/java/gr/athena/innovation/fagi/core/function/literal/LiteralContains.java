package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * Class for evaluating if the literal contains a text parameter.
 * 
 * @author nkarag
 */
public class LiteralContains implements IFunction, IFunctionTwoParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(LiteralContains.class);

    /**
     * Checks if the first parameter (literal) contains the second. The method returns false is the literal is blank
     * and true if the value is blank. A string is considered blank when it is empty or null.
     *
     * @param literal the literal.
     * @param value the value.
     * @return true if the literal contains the given value.
     */
    @Override
    public boolean evaluate(String literal, String value) {

        if(StringUtils.isBlank(literal)){
            return false;
        }
        
        if(StringUtils.isBlank(value)){
            return true;
        }
        
        return literal.contains(value);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
