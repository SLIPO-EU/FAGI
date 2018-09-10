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
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumberCustomNormalize;
import gr.athena.innovation.fagi.core.function.phone.IsSamePhoneNumberUsingExitCode;
import gr.athena.innovation.fagi.core.function.property.Exists;
import gr.athena.innovation.fagi.core.function.property.NotExists;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.LinkedPair;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionOneParameter;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralParameters;
import gr.athena.innovation.fagi.core.function.IFunctionTwoLiteralStringParameters;
import gr.athena.innovation.fagi.core.function.geo.GeometriesCloserThan;
import gr.athena.innovation.fagi.core.function.geo.GeometriesIntersect;
import gr.athena.innovation.fagi.core.function.literal.IsLiteralNumeric;
import gr.athena.innovation.fagi.core.function.literal.IsSameNormalized;
import gr.athena.innovation.fagi.core.function.literal.LiteralContains;
import gr.athena.innovation.fagi.model.CustomRDFProperty;

/**
 * Condition represents the result of an expression or a function that decides if a fusion action is going to be
 * applied.
 *
 * @author nkarag
 */
public class Condition {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(Condition.class);

    private boolean singleFunction;
    private String function;
    private Function func;
    private Expression expression;

    public boolean evaluate(Map<String, IFunction> functionMap, LinkedPair pair, CustomRDFProperty fusionProperty, 
            Literal valueA, Literal valueB, Map<String, ExternalProperty> externalProperties) throws WrongInputException {

        if (isSingleFunction()) {
            Function function2 = new Function(this.function);
            if (functionMap.containsKey(function2.getName())) {
                return evaluateOperator(functionMap, func, pair, fusionProperty, valueA, valueB, externalProperties);
            }
        } else if (expression.getGroupsOfChildFunctions().isEmpty()) {
            LOG.trace("Condition is not a single function");
            String parentOperation = expression.getLogicalOperatorParent();

            switch (parentOperation) {
                case SpecificationConstants.Rule.NOT: {
                    //NOT should contain a single function or a single expression child.
                    List<Function> functions = expression.getFunctions();
                    if (functions.size() == 1) {
                        Function notFunction = functions.get(0);
                        if (functionMap.containsKey(notFunction.getName())) {
                            return !evaluateOperator(functionMap, notFunction, pair, fusionProperty, valueA, valueB, externalProperties);
                        }
                    } else {
                        LOG.error(functions);
                        throw new WrongInputException("NOT expression in rules.xml does not contain a single function!");
                    }

                    break;
                }
                case SpecificationConstants.Rule.OR: {
                    List<Function> functions = expression.getFunctions();
                    //Check with priority of appearance the evaluation of each function:
                    for (Function orFunction : functions) {

                        if (functionMap.containsKey(orFunction.getName())) {
                            boolean evaluated = evaluateOperator(functionMap, orFunction, pair, fusionProperty, valueA, valueB, externalProperties);
                            if (evaluated) {
                                return true;
                            }
                        } else {
                            throw new WrongInputException("Function " + orFunction.getName() + " inside OR is not defined in the spec!");
                        }
                    }
                    break;
                }
                case SpecificationConstants.Rule.AND: {
                    List<Function> functions = expression.getFunctions();
                    //Check with priority of appearance the evaluation of each function:
                    boolean evaluated = true;
                    for (Function orFunction : functions) {
                        if (functionMap.containsKey(orFunction.getName())) {
                            evaluated = evaluateOperator(functionMap, orFunction, pair, fusionProperty, valueA, valueB, externalProperties)
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

        } else {

            LinkedHashMap<String, List<Function>> groups = expression.getGroupsOfChildFunctions();

            Boolean andEvaluation = null;
            Boolean orEvaluation = null;
            Boolean notEvaluation = null;

            for (Map.Entry<String, List<Function>> entry : groups.entrySet()) {
                switch (entry.getKey()) {
                    case SpecificationConstants.Rule.NOT: {
                        List<Function> functions = entry.getValue();//expression.getFunctions();
                        String parentOperation = expression.getLogicalOperatorParent();
                        switch(parentOperation){
                            case SpecificationConstants.Rule.AND:
                                notEvaluation = true;
                                for (Function notFunction : functions) {
                                    if (functionMap.containsKey(notFunction.getName())) {
                                        notEvaluation = evaluateOperator(functionMap, notFunction, pair, fusionProperty, valueA, valueB, externalProperties)
                                                && notEvaluation;
                                    } else {
                                        throw new WrongInputException("NOT expression in rules.xml does not contain a single function!");
                                    }
                                }
                                break;
                            case SpecificationConstants.Rule.OR:
                                notEvaluation = false;
                                for (Function notFunction : functions) {
                                    if (functionMap.containsKey(notFunction.getName())) {
                                        notEvaluation = evaluateOperator(functionMap, notFunction, pair, fusionProperty, valueA, valueB, externalProperties)
                                                || notEvaluation;
                                    } else {
                                        throw new WrongInputException("NOT expression in rules.xml does not contain a single function!");
                                    }
                                }                                
                                break;
                            case SpecificationConstants.Rule.NOT:    
                                throw new WrongInputException("NOT as parent cannot contain NOT childs in rules.xml");
                        }
                        break;
                    }
                    case SpecificationConstants.Rule.OR: {
                        List<Function> functions = entry.getValue();
                        //Check with priority of appearance the evaluation of each function:
                        orEvaluation = false;
                        for (Function orFunction : functions) {
                            if (functionMap.containsKey(orFunction.getName())) {
                                orEvaluation = evaluateOperator(functionMap, orFunction, pair, fusionProperty, valueA, valueB, externalProperties) 
                                        || orEvaluation;

                            } else {
                                throw new WrongInputException("Function " + orFunction.getName() 
                                        + " inside OR is not defined in the spec!");
                            }
                        }
                        break;
                    }
                    case SpecificationConstants.Rule.AND: {
                        List<Function> functions = entry.getValue();
                        //Check with priority of appearance the evaluation of each function:
                        andEvaluation = true;
                        for (Function orFunction : functions) {
                            if (functionMap.containsKey(orFunction.getName())) {
                                andEvaluation = evaluateOperator(functionMap, orFunction, pair, fusionProperty, valueA, valueB, externalProperties)
                                        && andEvaluation;

                            } else {
                                throw new WrongInputException("Function " + orFunction.getName() 
                                        + " inside AND is not defined in the spec!");
                            }
                        }
                        break;
                    }
                    default:
                        throw new WrongInputException("Unknown logical operator " + entry.getKey());

                }
            }

            String parentOperation = expression.getLogicalOperatorParent();

            //collect results and evaluate with the parent operation
            switch (parentOperation) {
                case SpecificationConstants.Rule.AND:
                    if (andEvaluation != null && orEvaluation != null && notEvaluation != null) {
                        return andEvaluation && orEvaluation && notEvaluation;
                    } else if (andEvaluation != null && orEvaluation != null && notEvaluation == null) {
                        return andEvaluation && orEvaluation;
                    } else if (andEvaluation != null && orEvaluation == null && notEvaluation != null) {
                        return andEvaluation && notEvaluation;
                    } else if (andEvaluation == null && orEvaluation != null && notEvaluation != null) {
                        return orEvaluation && notEvaluation;
                    } else if (andEvaluation == null && orEvaluation == null && notEvaluation != null) {
                        return notEvaluation; //not has been already calculated using parent expression.
                    } else {
                        throw new WrongInputException("Wrong operands for 'AND' operation.");
                    }
                case SpecificationConstants.Rule.OR:
                    if (andEvaluation != null && orEvaluation != null && notEvaluation != null) {
                        return andEvaluation || orEvaluation || notEvaluation;
                    } else if (andEvaluation != null && orEvaluation != null && notEvaluation == null) {
                        return andEvaluation || orEvaluation;
                    } else if (andEvaluation != null && orEvaluation == null && notEvaluation != null) {
                        return andEvaluation || notEvaluation;
                    } else if (andEvaluation == null && orEvaluation != null && notEvaluation != null) {
                        return orEvaluation || notEvaluation;
                    } else if (andEvaluation == null && orEvaluation == null && notEvaluation != null) {
                        return notEvaluation;
                    } else {
                        throw new WrongInputException("Wrong operands for 'OR' operation.");
                    }
                case SpecificationConstants.Rule.NOT:
                    if (andEvaluation == null && orEvaluation == null && notEvaluation != null) {
                        return !notEvaluation;
                    } else if (andEvaluation != null && orEvaluation == null && notEvaluation == null) {
                        return !andEvaluation;
                    } else if (andEvaluation == null && orEvaluation != null && notEvaluation == null) {
                        return !orEvaluation;
                    } else {
                        throw new WrongInputException("'NOT' operator has more than one logical expression childs.");
                    }
            }
        }
        return false;
    }

    private boolean evaluateOperator(Map<String, IFunction> functionMap, Function function, LinkedPair pair, 
            CustomRDFProperty fusionProperty, Literal valueA, Literal valueB, Map<String, ExternalProperty> externalProperties) 
                throws WrongInputException {

        //todo: add implemented functions
        switch (function.getName()) {
            case SpecificationConstants.Functions.IS_LITERAL_ABBREVIATION: {
                
                IsLiteralAbbreviation isLiteralAbbreviation = (IsLiteralAbbreviation) functionMap.get(function.getName());
                return evaluateOneParameterFunction(function, valueA, valueB, externalProperties, isLiteralAbbreviation);
                 
            }
            case SpecificationConstants.Functions.IS_LITERAL_NUMERIC: {
                
                IsLiteralNumeric isLiteralNumeric = (IsLiteralNumeric) functionMap.get(function.getName());
                return evaluateOneParameterFunction(function, valueA, valueB, externalProperties, isLiteralNumeric);
                 
            }
            case SpecificationConstants.Functions.LITERAL_CONTAINS: {

                String parameter = function.getParameters()[0];
                String stringValue = function.getParameters()[2];
                LiteralContains literalContains
                        = (LiteralContains) functionMap.get(function.getName());

                if (parameter.equals(SpecificationConstants.Rule.A) || parameter.equals(SpecificationConstants.Rule.B)) {

                    return literalContains.evaluate(valueA, stringValue);
                } else {
                    ExternalProperty property = externalProperties.get(parameter);

                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. "
                                + SpecificationConstants.Functions.IS_SAME_CUSTOM_NORMALIZE
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");
                    }

                    if (parameter.startsWith(SpecificationConstants.Rule.A)) {
                        return literalContains.evaluate(property.getValueA(), stringValue);
                    } else {
                        return literalContains.evaluate(property.getValueB(), stringValue);
                    }
                }
            }
            case SpecificationConstants.Functions.IS_PHONE_NUMBER_PARSABLE: {

                IsPhoneNumberParsable isPhoneNumberParsable = (IsPhoneNumberParsable) functionMap.get(function.getName());
                return evaluateOneParameterFunction(function, valueA, valueB, externalProperties, isPhoneNumberParsable);

            }
            case SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER: {
                
                IsSamePhoneNumber isSamePhoneNumber = (IsSamePhoneNumber) functionMap.get(function.getName());
                return evaluateTwoLiteralFunction(function, valueA, valueB, externalProperties, isSamePhoneNumber);

            }
            case SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_CUSTOM_NORMALIZE: {
                IsSamePhoneNumberCustomNormalize isSamePhoneNumberCustomNormalize
                        = (IsSamePhoneNumberCustomNormalize) functionMap.get(function.getName());
                
                return evaluateTwoLiteralFunction(function, valueA, valueB, externalProperties, isSamePhoneNumberCustomNormalize);

            }
            case SpecificationConstants.Functions.IS_SAME_NORMALIZED: {
                
                IsSameNormalized isSameNormalized = (IsSameNormalized) functionMap.get(function.getName());
                return evaluateTwoLiteralFunction(function, valueA, valueB, externalProperties, isSameNormalized);

            }
            case SpecificationConstants.Functions.GEOMETRIES_INTERSECT: {
                
                GeometriesIntersect geometriesIntersect = (GeometriesIntersect) functionMap.get(function.getName());
                return evaluateTwoLiteralFunction(function, valueA, valueB, externalProperties, geometriesIntersect);

            }
            case SpecificationConstants.Functions.GEOMETRIES_CLOSER_THAN: {

                String parameter = function.getParameters()[0];
                String threshold = function.getParameters()[2];
                GeometriesCloserThan geometriesCloserThan
                        = (GeometriesCloserThan) functionMap.get(function.getName());

                if (parameter.equals(SpecificationConstants.Rule.A) || parameter.equals(SpecificationConstants.Rule.B)) {

                    return geometriesCloserThan.evaluate(valueA, valueB, threshold);
                } else {
                    ExternalProperty property = externalProperties.get(parameter);

                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. "
                                + SpecificationConstants.Functions.IS_SAME_CUSTOM_NORMALIZE
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");
                    }

                    if (parameter.startsWith(SpecificationConstants.Rule.A)) {
                        return geometriesCloserThan.evaluate(property.getValueA(),property.getValueB(), threshold);
                    } else {
                        return geometriesCloserThan.evaluate(property.getValueB(),property.getValueB(), threshold);
                    }
                }
            }
            case SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_EXIT_CODE: {

                IsSamePhoneNumberUsingExitCode isSamePhoneNumberUsingExitCode
                        = (IsSamePhoneNumberUsingExitCode) functionMap.get(function.getName());

                String parameter = function.getParameters()[0];

                //Use the third parameter as the exit code digits
                String exitCodeDigits = function.getParameters()[2];

                if (parameter.equals(SpecificationConstants.Rule.A) || parameter.equals(SpecificationConstants.Rule.B)) {
                    return isSamePhoneNumberUsingExitCode.evaluate(valueA, valueB, exitCodeDigits);

                } else {
                    ExternalProperty property = externalProperties.get(parameter);

                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. "
                                + SpecificationConstants.Functions.IS_SAME_PHONE_NUMBER_EXIT_CODE
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");
                    }

                    if (parameter.startsWith(SpecificationConstants.Rule.A)) {
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

                if (parameter.equals(SpecificationConstants.Rule.A) || parameter.equals(SpecificationConstants.Rule.B)) {

                    return isSameSimpleNormalize.evaluate(valueA, valueB, threshold);

                } else {
                    ExternalProperty property = externalProperties.get(parameter);

                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. "
                                + SpecificationConstants.Functions.IS_SAME_SIMPLE_NORMALIZE
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");
                    }

                    if (parameter.startsWith(SpecificationConstants.Rule.A)) {
                        return isSameSimpleNormalize.evaluate(property.getValueA(), property.getValueB(), threshold);
                    } else {
                        return isSameSimpleNormalize.evaluate(property.getValueB(), property.getValueB(), threshold);
                    }
                }
            }
            case SpecificationConstants.Functions.IS_SAME_CUSTOM_NORMALIZE: {

                String parameter = function.getParameters()[0];
                String threshold = function.getParameters()[2];
                IsSameCustomNormalize isSameCustomNormalize
                        = (IsSameCustomNormalize) functionMap.get(function.getName());

                if (parameter.equals(SpecificationConstants.Rule.A) || parameter.equals(SpecificationConstants.Rule.B)) {

                    return isSameCustomNormalize.evaluate(valueA, valueB, threshold);
                } else {
                    ExternalProperty property = externalProperties.get(parameter);

                    if (property == null) {
                        throw new WrongInputException(parameter + " is wrong. "
                                + SpecificationConstants.Functions.IS_SAME_CUSTOM_NORMALIZE
                                + " requires one parameter a or b followed by the external property id number. Eg. a1");
                    }

                    if (parameter.startsWith(SpecificationConstants.Rule.A)) {
                        return isSameCustomNormalize.evaluate(property.getValueA(),property.getValueB(), threshold);
                    } else {
                        return isSameCustomNormalize.evaluate(property.getValueB(),property.getValueB(), threshold);
                    }
                }
            }
            case SpecificationConstants.Functions.EXISTS: {
                if (function.getParameters().length == 1) {
                    Exists exists = (Exists) functionMap.get(function.getName());
                    String parameter = function.getParameters()[0];

                    switch (parameter) {
                        case SpecificationConstants.Rule.A:
                            return exists.evaluate(pair.getLeftNode().getEntityData().getModel(), fusionProperty);
                            
                        case SpecificationConstants.Rule.B:
                            return exists.evaluate(pair.getRightNode().getEntityData().getModel(), fusionProperty);
                        default:
                            ExternalProperty property = externalProperties.get(parameter);

                            if (property == null) {
                                throw new WrongInputException(parameter + " is wrong. "
                                        + SpecificationConstants.Functions.EXISTS
                                        + " requires one parameter a or b followed by the external property id number. Eg. a1");
                            }

                            if (parameter.startsWith(SpecificationConstants.Rule.A)) {
                                return exists.evaluate(pair.getLeftNode().getEntityData().getModel(), 
                                        property.getValueA().getLexicalForm());
                            } else {
                                return exists.evaluate(pair.getRightNode().getEntityData().getModel(), 
                                        property.getValueB().getLexicalForm());
                            }
                    }
                } else {
                    throw new WrongInputException(SpecificationConstants.Functions.EXISTS + " requires one parameter!");
                }
            }
            case SpecificationConstants.Functions.NOT_EXISTS: {
                if (function.getParameters().length == 1) {
                    NotExists notExists = (NotExists) functionMap.get(function.getName());
                    String parameter = function.getParameters()[0];

                    switch (parameter) {
                        case SpecificationConstants.Rule.A:

                            return notExists.evaluate(pair.getLeftNode().getEntityData().getModel(), fusionProperty);

                        case SpecificationConstants.Rule.B:
                            return notExists.evaluate(pair.getRightNode().getEntityData().getModel(), fusionProperty);
                        default:
                            ExternalProperty property = externalProperties.get(parameter);

                            if (property == null) {
                                throw new WrongInputException(parameter + " is wrong. "
                                        + SpecificationConstants.Functions.NOT_EXISTS
                                        + " requires one parameter a or b followed by the external property id number. Eg. a1");
                            }

                            if (parameter.startsWith(SpecificationConstants.Rule.A)) {
                                return notExists.evaluate(pair.getLeftNode().getEntityData().getModel(), 
                                        property.getValueA().getLexicalForm());
                            } else {
                                return notExists.evaluate(pair.getRightNode().getEntityData().getModel(), 
                                        property.getValueB().getLexicalForm());
                            }
                    }
                } else {
                    throw new WrongInputException(SpecificationConstants.Functions.NOT_EXISTS + " requires one parameter!");
                }
            }
            case SpecificationConstants.Functions.IS_DATE_KNOWN_FORMAT: {
                
                IsDateKnownFormat isDateKnownFormat = (IsDateKnownFormat) functionMap.get(function.getName());

                return evaluateOneParameterFunction(function, valueA, valueB, externalProperties, isDateKnownFormat);
            }
            case SpecificationConstants.Functions.IS_DATE_PRIMARY_FORMAT: {
                IsDatePrimaryFormat isDatePrimaryFormat = (IsDatePrimaryFormat) functionMap.get(function.getName());
                
                return evaluateOneParameterFunction(function, valueA, valueB, externalProperties, isDatePrimaryFormat);
            }
            case SpecificationConstants.Functions.IS_VALID_DATE: {
                IsValidDate isValidDate = (IsValidDate) functionMap.get(function.getName());
                String parameterA = function.getParameters()[0];
                String parameterB = function.getParameters()[1];
                switch (parameterA) {
                    case SpecificationConstants.Rule.A:
                        return isValidDate.evaluate(valueA, parameterB);
                    case SpecificationConstants.Rule.B:
                        return isValidDate.evaluate(valueB, parameterB);
                    default:

                        ExternalProperty property = externalProperties.get(parameterA);

                        if (property == null) {
                            throw new WrongInputException(parameterA + " is wrong. "
                                    + SpecificationConstants.Functions.IS_VALID_DATE
                                    + " requires one parameter a or b followed by the external property id number. Eg. a1");
                        }

                        if (parameterA.startsWith(SpecificationConstants.Rule.A)) {
                            return isValidDate.evaluate(property.getValueA(), parameterB);
                        } else {
                            return isValidDate.evaluate(property.getValueB(), parameterB);
                        }
                }
            }
            default:
                throw new WrongInputException("Function used in rules.xml is malformed does not exist or currently not supported!" + function.getName());
        }
    }

    private boolean evaluateOneParameterFunction(Function function, Literal valueA, Literal valueB, 
            Map<String, ExternalProperty> externalProperties, IFunctionOneParameter singleParamFunction) 
            throws WrongInputException {

        if (function.getParameters().length != 1) {
            throw new WrongInputException("Number of parameters in function is wrong.");
        }

        String parameter = function.getParameters()[0];
        switch (parameter) {
            case SpecificationConstants.Rule.A:
                return singleParamFunction.evaluate(valueA);
            case SpecificationConstants.Rule.B:
                return singleParamFunction.evaluate(valueB);
            default:
                ExternalProperty property = externalProperties.get(parameter);

                if (property == null) {
                    throw new WrongInputException("Wrong parameter for external property: " + parameter);
                }

                if (parameter.startsWith(SpecificationConstants.Rule.A)) {
                    return singleParamFunction.evaluate(property.getValueA());
                } else {
                    return singleParamFunction.evaluate(property.getValueB());
                }
        }
    }

    private boolean evaluateTwoLiteralFunction(Function function, Literal valueA, Literal valueB, 
            Map<String, ExternalProperty> externalProperties, IFunctionTwoLiteralParameters twoParameterFunction) 
            throws WrongInputException {

        if (function.getParameters().length != 2) {
            throw new WrongInputException("Number of parameters in function is wrong.");
        }

        String parameter = function.getParameters()[0];
        //skip actual parameters because twoParameterFunction refers always to the two literals a,b
        if (parameter.equals(SpecificationConstants.Rule.A) || parameter.equals(SpecificationConstants.Rule.B)) {
            return twoParameterFunction.evaluate(valueA, valueB);
        } else {
            ExternalProperty property = externalProperties.get(parameter);
            
            if (property == null) {
                throw new WrongInputException("Wrong parameter for external property: " + parameter);
            }

            return twoParameterFunction.evaluate(property.getValueA(), property.getValueB());
        }
    }

    private boolean evaluateTwoLiteralStringFunction(Function function, Literal valueA, String valueB, 
            Map<String, ExternalProperty> externalProperties, IFunctionTwoLiteralStringParameters twoParameterFunction) 
            throws WrongInputException {

        if (function.getParameters().length != 2) {
            throw new WrongInputException("Number of parameters in function is wrong.");
        }

        String parameter = function.getParameters()[0];
        //skip actual parameters because twoParameterFunction refers always to the two literals a,b
        if (parameter.equals(SpecificationConstants.Rule.A) || parameter.equals(SpecificationConstants.Rule.B)) {
            return twoParameterFunction.evaluate(valueA, valueB);
        } else {
            ExternalProperty property = externalProperties.get(parameter);
            
            if (property == null) {
                throw new WrongInputException("Wrong parameter for external property: " + parameter);
            }

            return twoParameterFunction.evaluate(property.getValueA(), valueB);
        }
    }

    public boolean isSingleFunction() {
        return singleFunction;
    }

    public void setSingleFunction(boolean singleFunction) {
        this.singleFunction = singleFunction;
    }

    public void setFunction(String function) {
        this.function = function;
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

    public void setFunc(Function func) {
        this.func = func;
    }
}
