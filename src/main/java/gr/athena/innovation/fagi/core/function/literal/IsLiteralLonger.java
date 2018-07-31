package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import java.text.Normalizer;
import gr.athena.innovation.fagi.core.function.IFunctionTwoStringParameters;

/**
 * Literal fusion class. Tests if the first literal has longer value than the second.
 * 
 * @author nkarag
 */
public class IsLiteralLonger implements IFunction, IFunctionTwoStringParameters {

    /**
     * Checks if the first literal is longer than the second. 
     * The method normalizes the two literals using the NFC normalization before comparing the lengths. 
     * 
     * @param literalA the first literal.
     * @param literalB the second literal.
     * @return True if the telephone number representation can be parsed as an integer and false otherwise.
     * 
     */
    @Override
    public boolean evaluate(String literalA, String literalB) {

        String a = Normalizer.normalize(literalA, Normalizer.Form.NFC);
        String b = Normalizer.normalize(literalB, Normalizer.Form.NFC);

        return a.length() > b.length();
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
