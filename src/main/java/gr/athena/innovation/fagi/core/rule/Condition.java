package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import gr.athena.innovation.fagi.fusers.IsDateKnownFormat;
import gr.athena.innovation.fagi.fusers.IsLiteralAbbreviation;
import gr.athena.innovation.fagi.xml.Function;
import java.util.HashMap;
import java.util.List;
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
    
    public boolean evaluate(HashMap<String, Object> functionMap, String valueA, String valueB){
        
        if(isSingleFunction()){
            logger.fatal("\nEvaluating: " + func.getName() + " with values: " + valueA + ", " + valueB);
            Function function2 = new Function(function);

            if(functionMap.containsKey(function2.getName())){
                Object functionToCast = functionMap.get(func.getName());
                logger.fatal("\n\nchecking cast " + func.getName());
                //resolve which function to use:
                switch(func.getName()){
                    case "isliteralabbreviation":
                        if(func.getParameters().length == 1){
                            String parameter = func.getParameters()[0];
                            logger.trace("Parameter to use in abbreviation evaluation: " + parameter);
                            switch (parameter) {
                                case SpecificationConstants.A:
                                    return IsLiteralAbbreviation.evaluate(valueA);
                                case SpecificationConstants.B:
                                    return IsLiteralAbbreviation.evaluate(valueB);
                                default:
                                    logger.fatal("Is abbreviation requires one parameter A or B");
                                    throw new RuntimeException();
                            }
                        } else {
                            logger.fatal("Is abbreviation requires one parameter!");
                            throw new RuntimeException();
                        }

                    case "isdateknownformat":
                        //IsDateKnownFormat isDateKnownFormat = (IsDateKnownFormat) functionToCast;    
                        if(func.getParameters().length == 1){
                            String parameter = func.getParameters()[0];
                            logger.trace("Parameter to use in abbreviation evaluation: " + parameter);
                            switch (parameter) {
                                case SpecificationConstants.A:
                                    return IsDateKnownFormat.isDateKnownFormat(valueA);
                                case SpecificationConstants.B:
                                    return IsDateKnownFormat.isDateKnownFormat(valueB);
                                default:
                                    logger.fatal("Is abbreviation requires one parameter A or B");
                                    throw new RuntimeException();
                            }
                        } else {
                            logger.fatal("Is abbreviation requires one parameter!");
                            throw new RuntimeException();
                        }
                        //break;
                    default:
                        logger.fatal("Function used in rules.xml is malformed or doesn' t exist!! " + func.getName());
                        throw new RuntimeException();
                }
            }
        } else {
            logger.fatal("Not a single function!");
            String parentOperation = expression.getLogicalOperatorParent();
            
            switch (parentOperation) {
                case SpecificationConstants.NOT:
                    //NOT should contain a single function or a single expression child.
                    List<Function> functions = expression.getFunctions();
                    if(functions.size() == 1){
                        Function notFuntion = functions.get(0);
                        if(functionMap.containsKey(notFuntion.getName())){
                            //Object functionToCast = functionMap.get(notFuntion.getName());
                            //resolve which function to use:
                            switch(notFuntion.getName()){
                                case "isliteralabbreviation":
                                    if(notFuntion.getParameters().length == 1){
                                        String parameter = notFuntion.getParameters()[0];
                                        logger.trace("Parameter to use in abbreviation evaluation: " + parameter);
                                        switch (parameter) {
                                            case SpecificationConstants.A:
                                                return !IsLiteralAbbreviation.evaluate(valueA);
                                            case SpecificationConstants.B:
                                                return !IsLiteralAbbreviation.evaluate(valueB);
                                            default:
                                                logger.fatal("Is abbreviation requires one parameter A or B");
                                                throw new RuntimeException();
                                        }
                                    } else {
                                        logger.fatal("Is abbreviation requires one parameter!");
                                        throw new RuntimeException();
                                    }

                                case "isdateknownformat":
                                    //IsDateKnownFormat isDateKnownFormat = (IsDateKnownFormat) functionToCast;    
                                    if(notFuntion.getParameters().length == 1){
                                        String parameter = notFuntion.getParameters()[0];
                                        logger.trace("Parameter to use in abbreviation evaluation: " + parameter);
                                        switch (parameter) {
                                            case SpecificationConstants.A:
                                                return !IsLiteralAbbreviation.evaluate(valueA);
                                            case SpecificationConstants.B:
                                                return !IsLiteralAbbreviation.evaluate(valueB);
                                            default:
                                                logger.fatal("Is abbreviation requires one parameter A or B");
                                                throw new RuntimeException();
                                        }
                                    } else {
                                        logger.fatal("Is abbreviation requires one parameter!");
                                        throw new RuntimeException();
                                    }
                                default:
                                    logger.fatal("Function used in rules.xml is malformed or doesn' t exist!! " + func.getName());
                                    throw new RuntimeException();
                            }
                        }                        
                    } else {
                        logger.fatal("NOT expression in rules.xml does not contain a single function!");
                        throw new RuntimeException();
                    }
                    
                    break;
                case SpecificationConstants.OR:
                    break;
                case SpecificationConstants.AND:
                    break;
                default:
                    break;
            }
            
        }
        return false;
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
