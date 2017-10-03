package gr.athena.innovation.fagi.core.functions;

import gr.athena.innovation.fagi.core.functions.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.core.functions.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.core.functions.property.Exists;
import gr.athena.innovation.fagi.core.functions.date.IsValidDate;
import gr.athena.innovation.fagi.core.functions.geo.IsGeometryMoreComplicated;
import gr.athena.innovation.fagi.core.functions.phone.IsPhoneNumberParsable;
import gr.athena.innovation.fagi.core.functions.phone.IsSamePhoneNumber;
import gr.athena.innovation.fagi.core.functions.phone.IsSamePhoneNumberUsingExitCode;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 * Registers all fusion functions.
 * 
 * @author nkarag
 */
public class FunctionRegistry {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FunctionRegistry.class);
    private boolean isInitialized = false;
    private HashMap<String, IFunction> functionMap;
    
    public void init(){

        functionMap = new HashMap<>();
        
        //date
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();
        IsValidDate isValidDate = new IsValidDate();

        //geo
        IsGeometryMoreComplicated isGeometryMoreComplicated = new IsGeometryMoreComplicated();
        
        //literal
        IsLiteralAbbreviation isAbbreviation = new IsLiteralAbbreviation();

        //phone
        IsPhoneNumberParsable isPhoneNumberParsable = new IsPhoneNumberParsable();
        IsSamePhoneNumber isSamePhoneNumber = new IsSamePhoneNumber();
        IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode = new IsSamePhoneNumberUsingExitCode();
        
        //property
        Exists exists = new Exists();
        
        //register all functions
        functionMap.put(isDateKnownFormat.getName(), isDateKnownFormat);
        functionMap.put(isValidDate.getName(), isValidDate);

        //geo
        functionMap.put(isGeometryMoreComplicated.getName(), isGeometryMoreComplicated);
        
        //literal
        functionMap.put(isAbbreviation.getName(), isAbbreviation);
        
        //phone
        functionMap.put(isPhoneNumberParsable.getName(), isPhoneNumberParsable);
        functionMap.put(isSamePhoneNumber.getName(), isSamePhoneNumber);
        functionMap.put(exists.getName(), exists);
        
        //property
        functionMap.put(isSamePhoneNumberUsingExitCode.getName(), isSamePhoneNumberUsingExitCode);

        isInitialized = true;
    }
    
    public Map<String, IFunction> getFunctionMap(){
        if(!isInitialized){
            logger.fatal("Method registry is not initialized.");
            throw new RuntimeException();
        } else {
            return functionMap;
        }
    }    
}
