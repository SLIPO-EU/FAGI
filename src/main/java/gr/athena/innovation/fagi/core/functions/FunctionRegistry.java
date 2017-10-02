package gr.athena.innovation.fagi.core.functions;

import gr.athena.innovation.fagi.core.functions.geo.CentroidShiftTranslator;
import gr.athena.innovation.fagi.core.functions.phone.PhoneNumberValidator;
import gr.athena.innovation.fagi.core.functions.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.core.functions.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.core.functions.property.Exists;
import gr.athena.innovation.fagi.core.functions.date.DateFuser;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class FunctionRegistry {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FunctionRegistry.class);
    private HashSet<String> methodRegistry;
    private boolean isInitialized = false;
    private HashMap<String, Object> functionMap;
    
    public void init(){
        
        methodRegistry = new HashSet<>();
        functionMap = new HashMap<>();
        
        IsLiteralAbbreviation isAbbreviation = new IsLiteralAbbreviation();
        DateFuser dateFuser = new DateFuser();
        IsDateKnownFormat isDateKnownFormat = new IsDateKnownFormat();
        Exists exists = new Exists();
        PhoneNumberValidator phoneValidator = new PhoneNumberValidator();
        CentroidShiftTranslator centroidTranslator = new CentroidShiftTranslator();
        
        functionMap.put(isAbbreviation.getName(), isAbbreviation);
        functionMap.put(dateFuser.getName(), dateFuser);
        functionMap.put(isDateKnownFormat.getName(), isDateKnownFormat);
        functionMap.put(exists.getName(), exists);
        functionMap.put(phoneValidator.getName(), phoneValidator);
        functionMap.put(centroidTranslator.getName(), centroidTranslator);
        
        methodRegistry.add(dateFuser.getName());
        methodRegistry.add(isDateKnownFormat.getName());
        methodRegistry.add(exists.getName());
        methodRegistry.add(isAbbreviation.getName());
        methodRegistry.add(phoneValidator.getName());
        methodRegistry.add(centroidTranslator.getName());
        
        isInitialized = true;
    }
    
    public HashSet<String> getMethodRegistryList(){
        if(!isInitialized){
            logger.fatal("Method registry is not initialized.");
            throw new RuntimeException();
        } else {
            return methodRegistry;
        }
    }
    
    public HashMap<String, Object> getFunctionMap(){
        if(!isInitialized){
            logger.fatal("Method registry is not initialized.");
            throw new RuntimeException();
        } else {
            return functionMap;
        }
    }    
}
