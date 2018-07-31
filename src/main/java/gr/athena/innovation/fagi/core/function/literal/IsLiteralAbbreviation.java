package gr.athena.innovation.fagi.core.function.literal;

import gr.athena.innovation.fagi.core.function.IFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionOneParameter;

/**
 * Literal fusion class. Contains methods for checking and transforming literal values.
 * 
 * @author nkarag
 */
public class IsLiteralAbbreviation implements IFunction, IFunctionOneParameter{
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(IsLiteralAbbreviation.class);
    
    /**
     * Checks if the given literal is an abbreviation by using a regular expression from the SpecificationConstants.
     * 
     * @param literal The literal.
     * @return True if the literal matches the pattern of regular expression that represents an abbreviation.
     */
    @Override
    public boolean evaluate(Literal literal){
        LOG.trace("Evaluating literal: " + literal);
        //1) check dictionary/wordsList
        
        //2) https://stackoverflow.com/questions/7331462/check-if-a-string-is-a-possible-abbrevation-for-a-name
        //>The first letter of the abbreviation must match the first letter of the text
        
        //The rest of the abbreviation (the abbrev minus the first letter) must be an abbreviation for:
        //
        //    the remaining words, or
        //    the remaining text starting from any position in the first word.
        
        //3)
        //Abbreviations (usually):
        //> Are not morphologically well-formed words
        //> Infringe upon the phonotactics of the language in which they occur 
        //> Employ punctuation marks, predominantly the period "." , within them
        //> Have the same collocations as their unabbreviated counterparts 
        //They also might:
        //> Use atypical alphanumeric characters such as /, & or ~
        //> Resemble a phonetic transcription of their unabbreviated counterparts
        //> Exploit the rebus principle (eg. inb4 "in before", NRG "energy")       

        if(literal == null){
            return false;
        }
        
        String literalString = literal.getString();
        if (!StringUtils.isBlank(literalString)) {
            AbbreviationAndAcronymResolver resolver = AbbreviationAndAcronymResolver.getInstance();
            boolean result = resolver.containsAbbreviationOrAcronym(literalString);
            return result;
        } else {
            return false;
        }
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
