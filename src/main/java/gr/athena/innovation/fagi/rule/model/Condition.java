package gr.athena.innovation.fagi.rule.model;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.core.function.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.core.function.date.IsDatePrimaryFormat;
import gr.athena.innovation.fagi.core.function.date.IsValidDate;
import gr.athena.innovation.fagi.core.function.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.core.function.literal.IsSameCustomNormalize;
import gr.athena.innovation.fagi.core.function.literal.IsSameSimpleNormalize;
import gr.athena.innovation.fagi.core.function.phone.IsPhoneNumberParsable;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumber;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumberUsingExitCode;
import gr.athena.innovation.fagi.exception.WrongInputException;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 * Condition represents the result of an expression or a function that decides if a fusion action is going to be
 * applied.
 *
 * @author nkarag
 */
public class Condition {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Condition.class);
    
    private boolean singleFunction;
    private String function;
    private Function func;
    private Expression expression;
    
    public boolean evaluate(Map<String, IFunction> functionMap, String valueA, String valueB,
            Map<String, ExternalProperty> externalProperties) throws WrongInputException {
        
        if (isSingleFunction()) {
            logger.trace("\nEvaluating: " + func.getName() + " with values: " + valueA + ", " + valueB);
            Function function2 = new Function(this.function);
            if (functionMap.containsKey(function2.getName())) {
                return evaluateOperator(functionMap, func, valueA, valueB, externalProperties);
            }
        } else {
            logger.trace("Condition is not a single function");
            String parentOperation = expression.getLogicalOperatorParent();
            
            switch (parentOperation) {
                case SpecificationConstants.NOT: {
                    //NOT should contain a single function or a single expression child.
                    List<Function> functions = expression.getFunctions();
                    if (functions.size() == 1) {
                        Function notFunction = functions.get(0);
                        if (functionMap.containsKey(notFunction.getName())) {
                            return !evaluateOperator(functionMap, notFunction, valueA, valueB, externalProperties);
                        }                        
                    } else {
                        throw new WrongInputException("NOT expression in rules.xml does not contain a single function!");
                    }
                    
                    break;
                }
                case SpecificationConstants.OR: {
                    List<Function> functions = expression.getFunctions();
                    //Check with priority of appearance the evaluation of each function:
                    for (Function orFunction : functions) {
                        
                        if (functionMap.containsKey(orFunction.getName())) {
                            boolean evaluated = evaluateOperator(functionMap, orFunction, valueA, valueB, externalProperties);
                            if (evaluated) {
                                return true;
                            }
                        } else {
                            throw new WrongInputException("Function " + orFunction.getName() + " inside OR is not defined in the spec!");                            
                        }
                    }
                    break;
                }
                case SpecificationConstants.AND: {
                    List<Function> functions = expression.getFunctions();
                    //Check with priority of appearance the evaluation of each function:
                    boolean evaluated = true;
                    for (Function orFunction : functions) {
                        if (functionMap.containsKey(orFunction.getName())) {
                            evaluated = evaluateOperator(functionMap, orFunction, valueA, valueB, externalProperties)
                                    && evaluated;
                            
                        } else {
                            throw new WrongInputException("Function " + orFunction.getName() + " inside AND is not defined in the spec!");                            
                        }
                    }
                    return evaluated;
                }
                default:
                    break;
            }
            
        }
        return false;
    }
    
    private boolean evaluateOperator(Map<String, IFunction> functionMap, Function function, String valueA, String valueB,
            Map<String, ExternalProperty> externalProperties) throws WrongInputException {
        
        switch (function.getName()) {
            case SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT: {
                IsDateKnownFormat isDateKnownFormat = (IsDateKnownFormat) functionMap.get(function.getName());
                String parameter = function.getParameters()[0];

                //todo add case for parameters a1,a2 etc. add a param to this method with List<ExternalProps>
                //and iterate it to find id (change externalProps to Map maybe.
                switch (parameter) {
                    case SpecificationConstants.A:
                        return isDateKnownFormat.evaluate(valueA);
                    case SpecificationConstants.B:
                        return isDateKnownFormat.evaluate(valueB);                    
                    default:
                        
                        ExternalProperty property = externalProperties.get(parameter);
                        
                        if (property == null) {
                            throw new WrongInputException(parameter + " is wrong. " 
                                    + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                    + " requires one parameter a or b followed by the external property id number. Eg. a1");                            
                        }
                        
                        if (parameter.startsWith(SpecificationConstants.A)) {
                            return isDateKnownFormat.evaluate(property.getValueA());
                        } else {
                            return isDateKnownFormat.evaluate(property.getValueB());
                        }
                    
                }
            }
            case SpecificationConstants.Functions.IS_DATE_PRIMARY_FORMAT: {
                IsDatePrimaryFormat isDatePrimaryFormat = (IsDatePrimaryFormat) functionMap.get(function.getName());
                String parameter = function.getParameters()[0];
                switch (parameter) {
                    case SpecificationConstants.A:
                        return isDatePrimaryFormat.evaluate(valueA);
                    case SpecificationConstants.B:
                        return isDatePrimaryFormat.evaluate(valueB);
                    default:
                        ExternalProperty property = externalProperties.get(parameter);
                        
                        if (property == null) {
                            throw new WrongInputException(parameter + " is wrong. " 
                                    + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                    + " requires one parameter a or b followed by the external property id number. Eg. a1");                            
                        }
                        
                        if (parameter.startsWith(SpecificationConstants.A)) {
                            return isDatePrimaryFormat.evaluate(property.getValueA());
                        } else {
                            return isDatePrimaryFormat.evaluate(property.getValueB());
                        }
                }
            }            
            case SpecificationConstants.Functions.IS_VALID_DATE: {
                IsValidDate isValidDate = (IsValidDate) functionMap.get(function.getName());
                String parameterA = function.getParameters()[0];
                String parameterB = function.getParameters()[1];
                switch (parameterA) {
                    case SpecificationConstants.A:
                        return isValidDate.evaluate(valueA, parameterB);
                    case SpecificationConstants.B:
                        return isValidDate.evaluate(valueB, parameterB);
                    default:                        
                        
                        ExternalProperty property = externalProperties.get(parameterA);
                        
                        if (property == null) {
                            throw new WrongInputException(parameterA + " is wrong. " 
                                    + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                    + " requires one parameter a or b followed by the external property id number. Eg. a1");                            
                        }
                        
                        if (parameterA.startsWith(SpecificationConstants.A)) {
                            return isValidDate.evaluate(property.getValueA(), parameterB);
                        } else {
                            return isValidDate.evaluate(property.getValueB(), parameterB);
                        }                    
                }
            }
            case SpecificationConstants.Functions.IS_LITERAL_ABBREVIATION: {
                if (function.getParameters().length == 1) {
                    IsLiteralAbbreviation isLiteralAbbreviation = (IsLiteralAbbreviation) functionMap.get(function.getName());
                    String parameter = function.getParameters()[0];
                    switch (parameter) {
                        case SpecificationConstants.A:
                            return isLiteralAbbreviation.evaluate(valueA);
                        case SpecificationConstants.B:
                            return isLiteralAbbreviation.evaluate(valueB);
                        default:
                            ExternalProperty property = externalProperties.get(parameter);
                            
                            if (property == null) {
                                
                                throw new WrongInputException(parameter + " is wrong. " 
                                        + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                        + " requires one parameter a or b followed by the external property id number. Eg. a1");                                
                            }
                            
                            if (parameter.startsWith(SpecificationConstants.A)) {
                                return isLiteralAbbreviation.evaluate(property.getValueA());
                            } else {
                                return isLiteralAbbreviation.evaluate(property.getValueB());
                            }
                    }
                } else {
                    throw new WrongInputException(SpecificationConstants.Functions.IS_LITERAL_ABBREVIATION + " requires one parameter!");
                }
            }
            case SpecificationConstants.Functions.IS_PHONE_NUMBER_PARSABLE: {
                if (function.getParameters().length == 1) {
                    IsPhoneNumberParsable isPhoneNumberParsable = (IsPhoneNumberParsable) functionMap.get(function.getName());
                    String parameter = function.getParameters()[0];
                    switch (parameter) {
                        case SpecificationConstants.A:
                            return isPhoneNumberParsable.evaluate(valueA);
                        case SpecificationConstants.B:
                            return isPhoneNumberParsable.evaluate(valueB);
                        default:
                            ExternalProperty property = externalProperties.get(parameter);
                            
                            if (property == null) {
                                throw new WrongInputException(parameter + " is wrong. " 
                                        + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                        + " requires one parameter a or b followed by the external property id number. Eg. a1");                                
                            }
                            
                            if (parameter.startsWith(SpecificationConstants.A)) {
                                return isPhoneNumberParsable.evaluate(property.getValueA());
                            } else {
                                return isPhoneNumberParsable.evaluate(property.getValueB());
                            }
                    }
                } else {
                    throw new WrongInputException(SpecificationConstants.Functions.IS_PHONE_NUMBER_PARSABLE + " requires one parameter!");
                }
            }            
            case SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER: {
                IsSamePhoneNumber isSamePhoneNumber = (IsSamePhoneNumber) functionMap.get(function.getName());
                
                String parameter = function.getParameters()[0];

                //skip actual parameters because isSamePhoneNumber refers always to the two literals a,b
                if (parameter.equals(SpecificationConstants.A) || parameter.equals(SpecificationConstants.B)) {
                    return isSamePhoneNumber.evaluate(valueA, valueB);
                } else {
                    ExternalProperty property = externalProperties.get(parameter);
                    
                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. " 
                                + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");                        
                    }
                    
                    return isSamePhoneNumber.evaluate(property.getValueA(), property.getValueB());
                }
            }            
            case SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_EXIT_CODE: {
                
                IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode
                        = (IsSamePhoneNumberUsingExitCode) functionMap.get(function.getName());
                
                String parameter = function.getParameters()[0];

                //Use the third parameter as the exit code digits
                String exitCodeDigits = function.getParameters()[2];
                
                if (parameter.equals(SpecificationConstants.A) || parameter.equals(SpecificationConstants.B)) {
                    return isSamePhoneNumberUsingExitCode.evaluate(valueA, valueB, exitCodeDigits);
                    
                } else {
                    ExternalProperty property = externalProperties.get(parameter);
                    
                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. " 
                                + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");                        
                    }
                    
                    if (parameter.startsWith(SpecificationConstants.A)) {
                        return isSamePhoneNumberUsingExitCode.evaluate(property.getValueA(),
                                property.getValueB(), exitCodeDigits);
                    } else {
                        return isSamePhoneNumberUsingExitCode.evaluate(property.getValueB(),
                                property.getValueB(), exitCodeDigits);
                    }                    
                }
                
            }
            case SpecificationConstants.Functions.IS_SAME_SIMPLE_NORMALIZE: {
                
                IsSameSimpleNormalize isSameSimpleNormalize
                        = (IsSameSimpleNormalize) functionMap.get(function.getName());
                
                String parameter = function.getParameters()[0];
                String threshold = function.getParameters()[2];
                
                if (parameter.equals(SpecificationConstants.A) || parameter.equals(SpecificationConstants.B)) {
                    
                    return isSameSimpleNormalize.evaluate(valueA, valueB, threshold);
                    
                } else {
                    ExternalProperty property = externalProperties.get(parameter);
                    
                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. " 
                                + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");                        
                    }
                    
                    if (parameter.startsWith(SpecificationConstants.A)) {
                        return isSameSimpleNormalize.evaluate(property.getValueA(),
                                property.getValueB(), threshold);
                    } else {
                        return isSameSimpleNormalize.evaluate(property.getValueB(),
                                property.getValueB(), threshold);
                    }                    
                }
            }            
            case SpecificationConstants.Functions.IS_SAME_CUSTOM_NORMALIZE: {
                
                String parameter = function.getParameters()[0];
                String threshold = function.getParameters()[2];
                IsSameCustomNormalize isSameCustomNormalize
                        = (IsSameCustomNormalize) functionMap.get(function.getName());                
                
                if (parameter.equals(SpecificationConstants.A) || parameter.equals(SpecificationConstants.B)) {
                    
                    return isSameCustomNormalize.evaluate(valueA, valueB, threshold);                    
                } else {
                    ExternalProperty property = externalProperties.get(parameter);
                    
                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. " 
                                + SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");                        
                    }
                    
                    if (parameter.startsWith(SpecificationConstants.A)) {
                        return isSameCustomNormalize.evaluate(property.getValueA(),
                                property.getValueB(), threshold);
                    } else {
                        return isSameCustomNormalize.evaluate(property.getValueB(),
                                property.getValueB(), threshold);
                    }                    
                }
            }            
            default:
                throw new WrongInputException("Function used in rules.xml is malformed does not exist or currently not supported!" + function.getName());
        }
    }
    
    public boolean isSingleFunction() {
        return singleFunction;
    }
    
    public void setSingleFunction(boolean singleFunction) {
        this.singleFunction = singleFunction;
    }
    
    public String getFunction() {
        return function;
    }
    
    public void setFunction(String function) {
        this.function = function;
    }
    
    public Expression getExpression() {
        return expression;
    }
    
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        if (isSingleFunction()) {
            return "Condition{" + "singleFunction=" + singleFunction + ", function=" + function + "}";
        } else {
            return "Condition{ expression=" + expression + "}";            
        }
    }    
    
    public Function getFunc() {
        return func;
    }
    
    public void setFunc(Function func) {
        this.func = func;
    }
}
