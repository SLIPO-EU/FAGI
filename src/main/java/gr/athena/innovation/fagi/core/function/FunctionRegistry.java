package gr.athena.innovation.fagi.core.function;

import gr.athena.innovation.fagi.core.function.date.DatesAreSame;
import gr.athena.innovation.fagi.core.function.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.core.function.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.core.function.property.Exists;
import gr.athena.innovation.fagi.core.function.date.IsValidDate;
import gr.athena.innovation.fagi.core.function.geo.GeometriesCloserThan;
import gr.athena.innovation.fagi.core.function.geo.GeometriesHaveSameArea;
import gr.athena.innovation.fagi.core.function.geo.GeometriesIntersect;
import gr.athena.innovation.fagi.core.function.geo.IsGeometryCoveredBy;
import gr.athena.innovation.fagi.core.function.geo.IsGeometryMoreComplex;
import gr.athena.innovation.fagi.core.function.geo.IsPointGeometry;
import gr.athena.innovation.fagi.core.function.geo.IsSameCentroid;
import gr.athena.innovation.fagi.core.function.literal.IsLiteralNumeric;
import gr.athena.innovation.fagi.core.function.literal.IsSameCustomNormalize;
import gr.athena.innovation.fagi.core.function.literal.IsSameNormalized;
import gr.athena.innovation.fagi.core.function.literal.IsSameSimpleNormalize;
import gr.athena.innovation.fagi.core.function.literal.LiteralContains;
import gr.athena.innovation.fagi.core.function.literal.LiteralContainsTheOther;
import gr.athena.innovation.fagi.core.function.literal.LiteralHasLanguageAnnotation;
import gr.athena.innovation.fagi.core.function.phone.IsPhoneNumberParsable;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumber;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumberCustomNormalize;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumberUsingExitCode;
import gr.athena.innovation.fagi.core.function.phone.PhoneHasMoreDigits;
import gr.athena.innovation.fagi.core.function.property.NotExists;
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

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(FunctionRegistry.class);
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
        IsDatePrimaryFormat isDatePrimaryFormat = new IsDatePrimaryFormat();
        IsValidDate isValidDate = new IsValidDate();
        DatesAreSame datesAreSame = new DatesAreSame();

        //geo
        IsGeometryMoreComplex isGeometryMoreComplex = new IsGeometryMoreComplex();
        IsSameCentroid isSameCentroid = new IsSameCentroid();
        IsPointGeometry isPointGeometry = new IsPointGeometry();
        GeometriesIntersect geometriesIntersect = new GeometriesIntersect();
        GeometriesCloserThan geometriesCloserThan = new GeometriesCloserThan();
        IsGeometryCoveredBy isGeometryCoveredBy = new IsGeometryCoveredBy();
        GeometriesHaveSameArea geometriesHaveSameArea = new GeometriesHaveSameArea();
        
        //literal
        IsLiteralAbbreviation isLiteralAbbreviation = new IsLiteralAbbreviation();
        IsSameNormalized isSameNormalized = new IsSameNormalized();
        IsSameSimpleNormalize isSameSimpleNormalize = new IsSameSimpleNormalize();
        IsSameCustomNormalize isSameCustomNormalize = new IsSameCustomNormalize();
        IsLiteralNumeric isLiteralNumeric = new IsLiteralNumeric();
        LiteralContains literalContains = new LiteralContains();
        LiteralContainsTheOther literalContainsTheOther = new LiteralContainsTheOther();
        LiteralHasLanguageAnnotation literalHasLanguageAnnotation = new LiteralHasLanguageAnnotation();

        //phone
        IsSamePhoneNumberCustomNormalize isSamePhoneNumberCustomNormalize = new IsSamePhoneNumberCustomNormalize();
        IsPhoneNumberParsable isPhoneNumberParsable = new IsPhoneNumberParsable();
        IsSamePhoneNumber isSamePhoneNumber = new IsSamePhoneNumber();
        IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode = new IsSamePhoneNumberUsingExitCode();
        PhoneHasMoreDigits phoneHasMoreDigits = new PhoneHasMoreDigits();
        
        //property
        Exists exists = new Exists();
        NotExists notExists = new NotExists();
        
        //register all functions
        functionMap.put(isDateKnownFormat.getName(), isDateKnownFormat);
        functionMap.put(isDatePrimaryFormat.getName(), isDatePrimaryFormat);
        functionMap.put(isValidDate.getName(), isValidDate);
        functionMap.put(datesAreSame.getName(), datesAreSame);

        //geo
        functionMap.put(isGeometryMoreComplex.getName(), isGeometryMoreComplex);
        functionMap.put(isSameCentroid.getName(), isSameCentroid);
        functionMap.put(isPointGeometry.getName(), isPointGeometry);
        functionMap.put(geometriesIntersect.getName(), geometriesIntersect);
        functionMap.put(geometriesCloserThan.getName(), geometriesCloserThan);
        functionMap.put(isGeometryCoveredBy.getName(), isGeometryCoveredBy);
        functionMap.put(geometriesHaveSameArea.getName(), geometriesHaveSameArea);
        
        //literal
        functionMap.put(isLiteralAbbreviation.getName(), isLiteralAbbreviation);
        functionMap.put(isSameNormalized.getName(), isSameNormalized);
        functionMap.put(isSameSimpleNormalize.getName(), isSameSimpleNormalize);
        functionMap.put(isSameCustomNormalize.getName(), isSameCustomNormalize);
        functionMap.put(isLiteralNumeric.getName(), isLiteralNumeric);
        functionMap.put(literalContains.getName(), literalContains);
        functionMap.put(literalContainsTheOther.getName(), literalContainsTheOther);
        functionMap.put(literalHasLanguageAnnotation.getName(), literalHasLanguageAnnotation);
        
        //phone
        functionMap.put(isSamePhoneNumberCustomNormalize.getName(), isSamePhoneNumberCustomNormalize);
        functionMap.put(isPhoneNumberParsable.getName(), isPhoneNumberParsable);
        functionMap.put(isSamePhoneNumber.getName(), isSamePhoneNumber);
        functionMap.put(isSamePhoneNumberUsingExitCode.getName(), isSamePhoneNumberUsingExitCode);
        functionMap.put(phoneHasMoreDigits.getName(), phoneHasMoreDigits);
        
        //property
        functionMap.put(exists.getName(), exists);
        functionMap.put(notExists.getName(), notExists);

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
