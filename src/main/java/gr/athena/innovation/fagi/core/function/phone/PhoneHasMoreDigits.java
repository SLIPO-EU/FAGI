package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;

/**
 * Class for evaluating if the first phone number has more digits than the other.
 * 
 * @author nkarag
 */
public class PhoneHasMoreDigits implements IFunction, IFunctionTwoLiteralParameters  {

    /**
     * Checks if the first phone number has more digits than the second.
     *
     * @param literalA the literal of A.
     * @param literalB the literal of B.
     * @return true if the literals have the same language tag, false otherwise.
     */
    @Override
    public boolean evaluate(Literal literalA, Literal literalB) {

        if(literalA == null || literalB == null){
            return false;
        }
        
        int a = countDigits(literalA.getString());
        int b = countDigits(literalB.getString());

        return a > b;
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }

    private static int countDigits(String value){
        int count = 0;
        for (int i = 0, len = value.length(); i < len; i++) {
            if (Character.isDigit(value.charAt(i))) {
                count++;
            }
        }
        return count;
    }
}
