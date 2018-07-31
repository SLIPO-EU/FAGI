package gr.athena.innovation.fagi.core.function.phone;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoStringParameters;

/**
 * Class for evaluating if the first phone number has more digits than the other.
 * 
 * @author nkarag
 */
public class PhoneHasMoreDigits implements IFunction, IFunctionTwoStringParameters  {

    @Override
    public boolean evaluate(String literalA, String literalB) {

        int a = countDigits(literalA);
        int b = countDigits(literalB);

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
