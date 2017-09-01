package gr.athena.innovation.fagi.fusers;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * Literal fusion class. Contains methods for checking and transforming literal values.
 * 
 * @author nkarag
 */
public class LiteralFuser {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LiteralFuser.class);
    
    public boolean isLiteralAbbreviation(String literal){

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
        
        if (!StringUtils.isBlank(literal)) {
            
            return true;
        } else {
            return false;
        }
        
    }    
    
    public String getName(){
        return "isLiteralAbbreviation";
    }
}
