package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionOneParameter;
import org.apache.commons.lang3.StringUtils;

/**
 * Class evaluating if a literal has language annotaion.
 * @author nkarag
 */
public class LiteralHasLanguageAnnotation implements IFunction, IFunctionOneParameter{

    /**
     * Checks if the Literal contains a language annotation (tag).
     *
     * @param literal the literal.
     * @return true if the literal contains a language tag, false otherwise.
     */
    @Override
    public boolean evaluate(Literal literal) {
        if(literal == null){
            return false;
        }
        String languageTag = literal.getLanguage();
        
        return !StringUtils.isBlank(languageTag);
    }
    
    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
