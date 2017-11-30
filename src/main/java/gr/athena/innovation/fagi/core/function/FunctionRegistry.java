package gr.athena.innovation.fagi.core.function;

import gr.athena.innovation.fagi.core.function.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.core.function.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.core.function.property.Exists;
import gr.athena.innovation.fagi.core.function.date.IsValidDate;
import gr.athena.innovation.fagi.core.function.geo.IsGeometryMoreComplex;
import gr.athena.innovation.fagi.core.function.literal.IsSameNormalized;
import gr.athena.innovation.fagi.core.function.phone.IsPhoneNumberParsable;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumber;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumberUsingExitCode;
import gr.athena.innovation.fagi.exception.ApplicationException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 * Registers all available functions that can be defined inside rule conditions.
 * 
 * @author nkarag
 */
public class FunctionRegistry {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FunctionRegistry.class);
    private boolean isInitialized = false;
    private HashMap<String, IFunction> functionMap;
    
    /**
     * Initializes a FunctionRegistry object. Creates all available function objects and puts them in the functionMap.
     * The function map contains key-value entries of function names along with their corresponding function object.
     * 
     */
    public void init(){

        functionMap = new HashMap<>();
        
        //date
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();
        IsValidDate isValidDate = new IsValidDate();

        //geo
        IsGeometryMoreComplex isGeometryMoreComplex = new IsGeometryMoreComplex();
        
        //literal
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        IsSameNormalized isSameNormalized = new IsSameNormalized();

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
        functionMap.put(isGeometryMoreComplex.getName(), isGeometryMoreComplex);
        
        //literal
        functionMap.put(isLiteralAbbreviation.getName(), isLiteralAbbreviation);
        functionMap.put(isSameNormalized.getName(), isSameNormalized);
        
        //phone
        functionMap.put(isPhoneNumberParsable.getName(), isPhoneNumberParsable);
        functionMap.put(isSamePhoneNumber.getName(), isSamePhoneNumber);
        functionMap.put(isSamePhoneNumberUsingExitCode.getName(), isSamePhoneNumberUsingExitCode);
        
        //property
        functionMap.put(exists.getName(), exists);

        isInitialized = true;
    }
    
    /**
     * Returns the map that contains the function names as keys and the corresponding function objects as values.
     * 
     * @return the map
     */
    public Map<String, IFunction> getFunctionMap() {
        if(!isInitialized){
            throw new ApplicationException("Method registry is not initialized.");
        } else {
            return functionMap;
        }
    }    
}
