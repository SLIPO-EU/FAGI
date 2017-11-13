package gr.athena.innovation.fagi.rule;

import gr.athena.innovation.fagi.rule.model.Function;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.rule.model.ActionRule;
import gr.athena.innovation.fagi.rule.model.ActionRuleSet;
import gr.athena.innovation.fagi.rule.model.Condition;
import gr.athena.innovation.fagi.rule.model.Expression;
import gr.athena.innovation.fagi.rule.model.Rule;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import static gr.athena.innovation.fagi.specification.SpecificationConstants.AND;
import static gr.athena.innovation.fagi.specification.SpecificationConstants.NOT;
import static gr.athena.innovation.fagi.specification.SpecificationConstants.OR;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 *  Reads the XML file that contains the fusion rules 
 *  and turns it into in-memory structures that the rest of the application can access.
 *  This processor is does not validate the XML input. 
 *  The validation is performed one step before using the {@link gr.athena.innovation.fagi.utils.XmlValidator}
 * 
 * @author nkarag
 */
public class RuleProcessor {

    private static final Logger logger = LogManager.getLogger(RuleProcessor.class);

    /**
     *
     *  The rules are parsed and modeled in memory as follows:
          Rule
            |
        ActionRule 
            |
        Condition
            |
        Expression

     * 
     * @param path of the rules XML file.
     * @return a {@link gr.athena.innovation.fagi.rule.RuleCatalog} object that holds the rules configuration. 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws gr.athena.innovation.fagi.exception.WrongInputException
     */
    public RuleCatalog parseRules(String path) throws ParserConfigurationException, SAXException, IOException, WrongInputException{
        RuleCatalog ruleCatalog = new RuleCatalog();
        logger.info("Parsing rules: " + path);

        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        NodeList defaultDatasetAction = doc.getElementsByTagName(SpecificationConstants.DEFAULT_DATASET_ACTION);
        
        if(defaultDatasetAction.getLength() == 1){
            Node datasetActionNode = defaultDatasetAction.item(0);
            EnumDatasetAction datasetAction = EnumDatasetAction.fromString(datasetActionNode.getTextContent());
            ruleCatalog.setDefaultDatasetAction(datasetAction);
            if(datasetAction.equals(EnumDatasetAction.UNDEFINED)){
                throw new WrongInputException
                    ("<" + SpecificationConstants.DEFAULT_DATASET_ACTION+"> tag not found in rules.xml file.");
            }
        } else {
            throw new WrongInputException
                ("<" + SpecificationConstants.DEFAULT_DATASET_ACTION+"> tag not found in rules.xml file.");
        }        
        
        //get all <RULE> elements of the XML. The rule elements are all in the same level
        NodeList rules = doc.getElementsByTagName(SpecificationConstants.RULE);
        for (int temp = 0; temp < rules.getLength(); temp++) {
            logger.info("----- Rule " + temp);

            Node ruleNode = rules.item(temp);
            NodeList ruleNodeList = ruleNode.getChildNodes();
            Rule rule = createRule(ruleNodeList);
            ruleCatalog.addItem(rule);

        }
        return ruleCatalog;
    }

    /*
        Parse propertyA, propertyB and ACTION_RULE_SET of the current rule
    */
    private Rule createRule(NodeList ruleNodeList) throws WrongInputException{
        Rule rule = new Rule();
        int length = ruleNodeList.getLength();
        ActionRuleSet actionRuleSet = null;
        for (int i = 0; i < length; i++) {
            
            if (ruleNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element ruleElement = (Element) ruleNodeList.item(i);
                if (ruleElement.getNodeName().contains(SpecificationConstants.PROPERTY_A)) {
                    logger.debug("property A: " + ruleElement.getTextContent());
                    rule.setPropertyA(ruleElement.getTextContent());
                } else if (ruleElement.getNodeName().contains(SpecificationConstants.PROPERTY_B)) {
                    logger.debug("property B: " + ruleElement.getTextContent());
                    rule.setPropertyB(ruleElement.getTextContent());
                } else if(ruleElement.getNodeName().contains(SpecificationConstants.DEFAULT_ACTION)){
                    EnumFusionAction defaultGeoAction = EnumFusionAction.fromString(ruleElement.getTextContent());
                    rule.setDefaultAction(defaultGeoAction);
                } else if(ruleElement.getNodeName().contains(SpecificationConstants.ACTION_RULE_SET)){
                    NodeList actionRuleNodeList = ruleElement.getElementsByTagName(SpecificationConstants.ACTION_RULE);                   
                    actionRuleSet = createActionRuleSet(actionRuleNodeList);
                    rule.setActionRuleSet(actionRuleSet);
                }
            }
        }
        
        if(actionRuleSet == null){
            logger.trace("# RULE without action rule set");
            logger.trace(rule.getDefaultAction());
        }
        
        return rule;
    }

    /*
        Parse each action rule from the action rule set. All action rules are on the same level
    */
    private ActionRuleSet createActionRuleSet(NodeList actionRuleNodeList) throws WrongInputException{
        ActionRuleSet actionRuleSet = new ActionRuleSet();
        
        int length = actionRuleNodeList.getLength();
        for (int i = 0; i < length; i++) {

            Node actionRuleNode = actionRuleNodeList.item(i);

            if (actionRuleNode.getNodeType() == Node.ELEMENT_NODE) {
                Element actionRuleElement = (Element) actionRuleNode;

                ActionRule actionRule = createActionRule(actionRuleElement);
                actionRuleSet.addActionRule(actionRule);
            }
        }
        return actionRuleSet;
    }

    private ActionRule createActionRule(Element actionRuleElement) throws WrongInputException{
        ActionRule actionRule = new ActionRule();

        //Extract ACTION element and its text inside ACTION_RULE
        Node actionNode = actionRuleElement.getLastChild();

        int i = 0;
        while(!actionNode.getNodeName().equalsIgnoreCase(SpecificationConstants.ACTION)){
            actionNode = actionNode.getPreviousSibling();
            i++;
            if(i>5000){
                //TODO - remove this check when xsd validation is complete
                throw new WrongInputException
                    ("Could not find " + SpecificationConstants.ACTION + " tag inside " 
                            + SpecificationConstants.ACTION_RULE + ". Check the XML input.");
            }
        }

        EnumFusionAction action = EnumFusionAction.fromString(actionNode.getTextContent());
        
        if(action.equals(EnumFusionAction.UNDEFINED)){
            throw new WrongInputException("Wrong fusion action input: " + actionNode.getTextContent());
        } else {
            actionRule.setAction(action);
        }

        //Extract condition
        NodeList conditionsList = actionRuleElement.getElementsByTagName(SpecificationConstants.CONDITION);
        
        logger.trace(" condition size: " + conditionsList.getLength());
        if(conditionsList.getLength() != 1){
            //TODO - remove this check after xsd validation is complete
            throw new WrongInputException
                ("Condition should be exactly one inside " + SpecificationConstants.ACTION_RULE 
                        + ". Please check the XML input file.");
        }

        Node conditionNode = conditionsList.item(0);

        Condition con = constructCondition(conditionNode);

        actionRule.setCondition(con);

        return actionRule;
    }

    private Condition constructCondition(Node conditionNode) throws WrongInputException{
        
        Condition condition = new Condition();
        
        //There are two possibilities for a condition
        //1) Contains a single function
        //2) Contains an expression
        
        //1) Contains a single function:
        if(parentNodeContainsSingleFunction(conditionNode)){
            logger.trace("Condition contains only function");
            
            String func = getSingleFunction(conditionNode);
            logger.trace("found single function: " + func);
            Function function = new Function(func);
            condition.setSingleFunction(true);
            condition.setFunction(func);
            condition.setFunc(function);
            
            return condition;
        }
        
        //2) Contains an expression:
        if(!parentNodeContainsSingleFunction(conditionNode)){
            Expression expression = constructParentExpression(getParentExpressionNode(conditionNode));
            condition.setExpression(expression);
        }

        return condition;
    }

    private Expression constructParentExpression(Node rootExpressionNode) throws WrongInputException{

        Expression expression = new Expression();
        
        String operator = getLogicalOperationType(rootExpressionNode);
        expression.setLogicalOperatorParent(operator);
        
        //An expression can contain:
        //1)only single functions
        //2)only expressions
        //3)both functions and expressions

        //1)
        if(containsOnlyFunctionChilds(rootExpressionNode)){
            List<Function> functions = getFunctionsOfLogicalOperation2(rootExpressionNode);
            expression.setFunctions(functions);
            return expression;
        }

        //2
        if(containsOnlyExpressionChilds(rootExpressionNode)){
            List<Node> firstNodes = getLogicalExpressionChildNodes(rootExpressionNode);
            LinkedHashMap<String, List<Function>> expressionChildFunctions = new LinkedHashMap<>();
            for(Node n : firstNodes){
                
                String childOperator = getLogicalOperationType(n);
                
                //each of these childs should contain only function nodes.
                if(!containsOnlyFunctionChilds(n)){
                    throw new WrongInputException("Expression depth exceeded! Re-construct the conditions in rules.xml");
                } else {
                    List<Function> childFunctions = getFunctionsOfLogicalOperation2(n);
                    
                    if(expressionChildFunctions.containsKey(childOperator)){
                        List<Function> mergedFunctions = expressionChildFunctions.get(childOperator);
                        mergedFunctions.addAll(mergedFunctions);
                        expressionChildFunctions.put(childOperator, mergedFunctions);
                    } else {
                        expressionChildFunctions.put(childOperator, childFunctions);
                    }
                }
            }
            expression.setGroupsOfChildFunctions(expressionChildFunctions);
            return expression;
        }

        //3
        if(containsExpressionAndFunctionChilds(rootExpressionNode)){

            List<Node> firstNodes = getLogicalExpressionChildNodes(rootExpressionNode);
            LinkedHashMap<String, List<Function>> expressionChildFunctions = new LinkedHashMap<>();
            
            for(Node n : firstNodes){
                String childOperator = getLogicalOperationType(n);
             
                //each of these childs should contain only function nodes.
                if(!containsOnlyFunctionChilds(n)){
                    throw new WrongInputException("Expression depth exceeded! Re-construct the conditions in rules.xml");
                } else {
                    List<Function> childFunctions = getFunctionsOfLogicalOperation2(n);
                    if(expressionChildFunctions.containsKey(childOperator)){
                        List<Function> mergedFunctions = expressionChildFunctions.get(childOperator);
                        mergedFunctions.addAll(childFunctions);
                        expressionChildFunctions.put(childOperator, mergedFunctions);
                    } else {
                        expressionChildFunctions.put(childOperator, childFunctions);
                    }
                }
            }

            expression.setGroupsOfChildFunctions(expressionChildFunctions);
            return expression;
        }
        
        return expression;
    }

    private Node getParentExpressionNode(Node conditionNode) throws WrongInputException{

        Node child = conditionNode.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                return child;
            }
            child = child.getNextSibling();
        }
        
        throw new WrongInputException("Condition tag has no expression child!");
    }

    private boolean containsOnlyFunctionChilds(Node expression) throws WrongInputException {

        boolean hasOnlyFunctions = false;
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();

        while(child != null){
            
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.FUNCTION)){
                    hasOnlyFunctions = true;
                } else {
                    return false;
                }
            }
            child = child.getNextSibling();
        }
        return hasOnlyFunctions;
    }
    
    private boolean containsExpressionAndFunctionChilds(Node expression) throws WrongInputException {
        boolean containsFunction = false;
        boolean containsExpression = false;
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();

        while(child != null){
            
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.FUNCTION)){
                    containsFunction = true;
                } else if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                    containsExpression = true;
                }
            }
            child = child.getNextSibling();
        }
        return containsFunction && containsExpression;
    }
    
    private boolean containsOnlyExpressionChilds(Node expression) throws WrongInputException {

        boolean hasOnlyExpressions = false;
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node child = logicalOperationNode.getFirstChild();

        while(child != null){
            
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                    hasOnlyExpressions = true;
                } else {
                    return false;
                }
            }
            child = child.getNextSibling();
        }
        return hasOnlyExpressions;
    }    

    private boolean parentNodeContainsSingleFunction(Node parentExpression) {
        if(parentExpression.getNodeType() == Node.ELEMENT_NODE){
            Element parentExpressionElement = (Element) parentExpression;
            NodeList functions = parentExpressionElement.getElementsByTagName(SpecificationConstants.FUNCTION);
            
            //a NOT expression can contain a single function. 
            //If such element exists, the function is not considered single and the method should return false
            NodeList possibleNotExpression = parentExpressionElement.getElementsByTagName(SpecificationConstants.NOT);

            return functions.getLength() == 1 && possibleNotExpression.getLength() == 0;
        }
        return false;
    }

    private String getSingleFunction(Node parentExpression) {
        Node child = parentExpression.getFirstChild();
        while(child !=null){
            if(child.getNodeType() == Node.ELEMENT_NODE){
                if (child.getNodeName().equals(SpecificationConstants.FUNCTION)){
                    return child.getTextContent();
                }
            }
            child = child.getNextSibling();
        }
        return null;
    }

    private String getLogicalOperationType(Node parentExpression) throws WrongInputException {
        logger.trace("Extracting logical operation: " + parentExpression.getNodeName());
        Node child = parentExpression.getFirstChild();
        while(child != null){
            if(child.getNodeType() == Node.ELEMENT_NODE){
                String name = child.getNodeName();
                switch(name){
                    case AND:
                        return AND;
                    case OR:
                        return OR;
                    case NOT:
                        return NOT;                        
                    default:
                        throw new WrongInputException
                            ("Expression in XML does not contain a logical operation! " + child.getNodeName());
                }
            }
            child = child.getNextSibling();
        }
        
        throw new WrongInputException("Expression in XML does not contain a logical operation! " + parentExpression);
    }
    
    //this method returns a list with all functions under a logical operation. 
    //The input is the parent node of the logical operation (Expression node)
    //IMPORTANT: Assumes that the parent node contains only <FUNCTION> tags.
    private List<Function> getFunctionsOfLogicalOperation2(Node node) throws WrongInputException {
        List<Function> list = new ArrayList<>();

        Node logicalOperationNode = getLogicalOperationNode(node);
        Node child = logicalOperationNode.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase(SpecificationConstants.FUNCTION)){
                list.add(new Function(child.getTextContent()));
            }
            child = child.getNextSibling();
        }
        return list;
    }

    private Node getLogicalOperationNode(Node expression) throws WrongInputException{
        
        Node logicalOperationNode = expression.getFirstChild(); //first level child is the logical operation of the expression
        logger.trace("logical operation: " + logicalOperationNode.getNodeName());
        //get the logical operation node. Should always exist:
        int i=0;
        while(true){
            
            if(logicalOperationNode.getNodeType() == Node.ELEMENT_NODE){
                if(logicalOperationNode.getNodeName().equalsIgnoreCase(AND) 
                        || logicalOperationNode.getNodeName().equalsIgnoreCase(OR) 
                                || logicalOperationNode.getNodeName().equalsIgnoreCase(NOT)){
                    break;
                } 
            }
            
            if(i>5000){ //erroneous xml input check
                throw new WrongInputException
                    ("Expression in XML does not contain a logical operation! " + logicalOperationNode);
            }

            i++;
            logicalOperationNode = logicalOperationNode.getNextSibling();
        }
        
        return logicalOperationNode;
    }

    private List<Node> getLogicalExpressionChildNodes(Node expression) throws WrongInputException {
        List<Node> childExpressions = new ArrayList<>();
        Node logicalOperationNode = getLogicalOperationNode(expression);
        Node childExpression = logicalOperationNode.getFirstChild();
        while(childExpression != null){
            if(childExpression.getNodeType() == Node.ELEMENT_NODE){
                if(childExpression.getNodeName().equalsIgnoreCase(SpecificationConstants.EXPRESSION)){
                    childExpressions.add(childExpression);
                }
            }
            childExpression = childExpression.getNextSibling();
        }
        return childExpressions;
    }
}
