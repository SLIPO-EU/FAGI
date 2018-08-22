package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;

/**
 * Class evaluating if the given literals have the same language annotation.
 * @author nkarag
 */
public class LiteralsHaveSameLanguageAnnotation implements IFunction, IFunctionTwoLiteralParameters {

    /**
     * Checks if the two literals have the same language annotation (tag).
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

        String languageTagA = literalA.getLanguage();
        String languageTagB = literalB.getLanguage();
        
        if(StringUtils.isBlank(languageTagA) || StringUtils.isBlank(languageTagB)){
            return false;
        }
        
        return languageTagA.equals(languageTagB);
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
