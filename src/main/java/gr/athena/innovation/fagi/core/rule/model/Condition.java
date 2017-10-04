package gr.athena.innovation.fagi.core.rule.model;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import gr.athena.innovation.fagi.core.functions.date.IsDateKnownFormat;
import gr.athena.innovation.fagi.core.functions.date.IsValidDate;
import gr.athena.innovation.fagi.core.functions.literal.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.core.functions.phone.IsPhoneNumberParsable;
import gr.athena.innovation.fagi.core.functions.phone.IsSamePhoneNumber;
import gr.athena.innovation.fagi.core.functions.phone.IsSamePhoneNumberUsingExitCode;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

/**
 * Condition represents the result of an expression or a function that decides if a fusion action is going to be applied.
 * 
 * @author nkarag
 */
public class Condition {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Condition.class);
    
    private boolean singleFunction;
    private String function;
    private Function func;
    private Expression expression;
    
    public boolean evaluate(Map<String, IFunction> functionMap, String valueA, String valueB){
        
        if(isSingleFunction()){
            logger.trace("\nEvaluating: " + func.getName() + " with values: " + valueA + ", " + valueB);
            Function function2 = new Function(function);
            if(functionMap.containsKey(function2.getName())){
                return evaluateNOT(functionMap, func, valueA, valueB);
            }
        } else {
            logger.trace("Condition is not a single function");
            String parentOperation = expression.getLogicalOperatorParent();
            
            switch (parentOperation) {
                case SpecificationConstants.NOT:
                {    
                    //NOT should contain a single function or a single expression child.
                    List<Function> functions = expression.getFunctions();
                    if(functions.size() == 1){
                        Function notFunction = functions.get(0);
                        if(functionMap.containsKey(notFunction.getName())){
                            return evaluateNOT(functionMap, notFunction, valueA, valueB);
                        }                        
                    } else {
                        logger.fatal("NOT expression in rules.xml does not contain a single function!");
                        throw new RuntimeException();
                    }
                    
                    break;
                }
                case SpecificationConstants.OR:
                {
                    List<Function> functions = expression.getFunctions();
                    //Check with priority of appearance the evaluation of each function:
                    for(Function orFunction : functions){
                        
                        if(functionMap.containsKey(orFunction.getName())){
                            
                            
                        } else {
                            logger.fatal("Function " + orFunction.getName() + " inside OR is not defined in the spec!");
                            throw new RuntimeException();                            
                        }
                    }
                    break;
                }
                case SpecificationConstants.AND:
                    break;
                default:
                    break;
            }
            
        }
        return false;
    }

    private boolean evaluateNOT(Map<String, IFunction> functionMap, Function function, String valueA, String valueB){
        
        switch(function.getName()){
            case "isdateknownformat":
            {
                IsDateKnownFormat isDateKnownFormat = (IsDateKnownFormat) functionMap.get(function.getName());
                String parameter = function.getParameters()[0];
                logger.trace("Parameter to use isDateKnownFormat evaluation: " + parameter);
                switch (parameter) {
                    case SpecificationConstants.A:
                        return !isDateKnownFormat.evaluate(valueA);
                    case SpecificationConstants.B:
                        return !isDateKnownFormat.evaluate(valueB);
                    default:
                        logger.fatal("IsDateKnownFormat requires one parameter A or B");
                        throw new RuntimeException();
                }  
            }
            case "isvaliddate":
            {
                IsValidDate isValidDate = (IsValidDate) functionMap.get(function.getName());
                String parameterA = function.getParameters()[0];
                String parameterB = function.getParameters()[1];
                logger.trace("Parameter to use isValidDate evaluation: " + parameterA);
                switch (parameterA) {
                    case SpecificationConstants.A:
                        return !isValidDate.evaluate(valueA, parameterB);
                    case SpecificationConstants.B:
                        return !isValidDate.evaluate(valueB, parameterB);
                    default:
                        logger.fatal("IsDateKnownFormat requires one parameter A or B and a date format string");
                        throw new RuntimeException();
                }  
            }
            case "isliteralabbreviation":
            {
                if(function.getParameters().length == 1){
                    IsLiteralAbbreviation isLiteralAbbreviation = (IsLiteralAbbreviation) functionMap.get(function.getName());
                    String parameter = function.getParameters()[0];
                    logger.trace("Parameter to use in abbreviation evaluation: " + parameter);
                    switch (parameter) {
                        case SpecificationConstants.A:
                            return !isLiteralAbbreviation.evaluate(valueA);
                        case SpecificationConstants.B:
                            return !isLiteralAbbreviation.evaluate(valueB);
                        default:
                            logger.fatal("Is abbreviation requires one parameter A or B");
                            throw new RuntimeException();
                    }
                } else {
                    logger.fatal("IsAbbreviation requires one parameter!");
                    throw new RuntimeException();
                }
            }
            case "isphonenumberparsable":
            {
                if(function.getParameters().length == 1){
                    IsPhoneNumberParsable isPhoneNumberParsable = (IsPhoneNumberParsable) functionMap.get(function.getName());
                    String parameter = function.getParameters()[0];
                    logger.trace("Parameter to use in IsPhoneNumberParsable evaluation: " + parameter);
                    switch (parameter) {
                        case SpecificationConstants.A:
                            return !isPhoneNumberParsable.evaluate(valueA);
                        case SpecificationConstants.B:
                            return !isPhoneNumberParsable.evaluate(valueB);
                        default:
                            logger.fatal("IsPhoneNumberParsable requires one parameter A or B");
                            throw new RuntimeException();
                    }
                } else {
                    logger.fatal("IsPhoneNumberParsable requires one parameter!");
                    throw new RuntimeException();
                }
            } 
            case "issamephonenumber":
            {
                
                IsSamePhoneNumber isSamePhoneNumber = (IsSamePhoneNumber) functionMap.get(function.getName());
                //skip actual parameters because isSamePhoneNumber refers always to the two literals a,b
                return !isSamePhoneNumber.evaluate(valueA, valueB); 
            }  
            case "issamephonenumberusingexitcode":
            {
                
                IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode 
                        = (IsSamePhoneNumberUsingExitCode) functionMap.get(function.getName());
                //skip actual parameters because isSamePhoneNumber refers always to the two literals a,b
                //Use the third parameter as the exit code digits
                String exitCodeDigits = function.getParameters()[2];
                return !isSamePhoneNumberUsingExitCode.evaluate(valueA, valueB, exitCodeDigits); 
            }            
            default:
                logger.fatal("Function used in rules.xml is malformed does not exist or currently not supported!" 
                        + function.getName());
                throw new RuntimeException();
        }
    }
  
//    private boolean evaluateWithOneParameter(Function function, String valueA, String valueB){
//        if(function.getParameters().length == 1){
//            String parameter = function.getParameters()[0];
//            logger.trace("Parameter to use in abbreviation evaluation: " + parameter);
//            switch (parameter) {
//                case SpecificationConstants.A:
//                    return !IsDateKnownFormat.evaluate(valueA);
//                case SpecificationConstants.B:
//                    return !IsDateKnownFormat.evaluate(valueB);
//                default:
//                    logger.fatal(function.getName() + "requires one parameter A or B");
//                    throw new RuntimeException();
//            }             
//        } else {
//            logger.fatal("Is abbreviation requires one parameter!");
//            throw new RuntimeException();
//        }
//    }
    
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
        if(isSingleFunction()){
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
