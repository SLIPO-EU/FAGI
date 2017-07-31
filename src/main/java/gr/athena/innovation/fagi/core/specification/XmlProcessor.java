package gr.athena.innovation.fagi.core.specification;

import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import gr.athena.innovation.fagi.core.action.EnumMetadataActions;
import gr.athena.innovation.fagi.core.rule.ActionRule;
import gr.athena.innovation.fagi.core.rule.ConditionTag;
import gr.athena.innovation.fagi.core.rule.Expression;
import gr.athena.innovation.fagi.core.rule.ExpressionTag;
import gr.athena.innovation.fagi.core.rule.LogicalExpressionTag;
import gr.athena.innovation.fagi.core.rule.Rule;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.COMMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.ENTITY_NODE;
import static org.w3c.dom.Node.ENTITY_REFERENCE_NODE;
import static org.w3c.dom.Node.NOTATION_NODE;
import static org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

/**
 *
 *  Reads the XML file that contains the fusion rules 
 *  and turns it into in-memory structures that the rest of the application can access.
 *  This processor is does not validate the XML input. 
 *  The validation is performed one step before using the {@link gr.athena.innovation.fagi.core.rule.XmlValidator}
 * 
 * @author nkarag
 */
public class XmlProcessor {

    private static final Logger logger = LogManager.getLogger(XmlProcessor.class);
    private final RuleCatalog ruleCatalog;
    private int actionRuleCount = 0;
    
    /**
     *
     * @param ruleCatalog
     */
    public XmlProcessor(RuleCatalog ruleCatalog){
        this.ruleCatalog = ruleCatalog;
    }


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

        Its node may have multiple children
     * 
     * @param path
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void parseRules(String path) throws ParserConfigurationException, SAXException, IOException{

        logger.info("Reading specification from path: " + path);

        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        
        //get all <RULE> elements of the XML. The rule elements are all in the same level
        NodeList nList = doc.getElementsByTagName("RULE");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            logger.info("rule " + temp);
            Rule rule = new Rule();
            Node ruleNode = nList.item(temp);
            NodeList ruleNodeList = ruleNode.getChildNodes();
            createRule(ruleNodeList, rule);
            //ruleCatalog.addItem(constructRule(ruleChilds.item(k)));

        }
        //System.exit(0);
    }

    /*
        Parse propertyA, propertyB and ACTION_RULE_SET of the current rule
    */
    private void createRule(NodeList ruleNodeList, Rule rule){
        int length = ruleNodeList.getLength();
        for (int i = 0; i < length; i++) {
            logger.info("rule iter " + i);
            short type = ruleNodeList.item(i).getNodeType();
            logger.debug("CREATE RULE, TYPE: " + nodeType(type));
            
            if (ruleNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element ruleElement = (Element) ruleNodeList.item(i);
                if (ruleElement.getNodeName().contains("PROPERTYA")) {
                    logger.debug("property A: " + ruleElement.getTextContent());
                    rule.setPropertyA(ruleElement.getTextContent());

                } else if (ruleElement.getNodeName().contains("PROPERTYB")) {
                    logger.debug("property B: " + ruleElement.getTextContent());
                    rule.setPropertyB(ruleElement.getTextContent());
                } else if(ruleElement.getNodeName().contains("DEFAULT_GEO_ACTION")){
                    rule.setDefaultMetaAction(EnumMetadataActions.fromString(ruleElement.getTextContent()));
                } else if(ruleElement.getNodeName().contains("DEFAULT_META_ACTION")){
                    rule.setDefaultMetaAction(EnumMetadataActions.fromString(ruleElement.getTextContent()));
                } else if(ruleElement.getNodeName().contains("ACTION_RULE_SET")){
                    logger.debug("found rules set, count: " + ruleElement.getFirstChild().getNodeType());
                    NodeList actionRuleNodeList = ruleElement.getElementsByTagName("ACTION_RULE");
                    createActionRules(actionRuleNodeList, rule);
                }
            }
        }        
    }
    
    /*
        Parse each action rule from the action rule set. All action rules are on the same level
    */
    private void createActionRules(NodeList actionRuleNodeList, Rule rule){
        logger.info("~~~~~~~ Action Rules ~~~~~~~");
        
        int length = actionRuleNodeList.getLength();
        for (int i = 0; i < length; i++) {

            Node actionRuleNode = actionRuleNodeList.item(i);

            if (actionRuleNode.getNodeType() == Node.ELEMENT_NODE) {
                ActionRule actionRule = new ActionRule();
                Element actionRuleElement = (Element) actionRuleNode;
                
                createActionRule(actionRuleElement, actionRule);
            }
        }          
    }
    
    private void createActionRule(Element actionRuleElement, ActionRule actionRule){
        logger.fatal("new ACTION RULE " + actionRuleCount);
        actionRuleCount++;
        //NodeList actionRuleChilds = actionRuleElement.getChildNodes();
        
        //Extract ACTION element and its text inside ACTION_RULE
        Node action = actionRuleElement.getLastChild();

        int i = 0;
        while(!action.getNodeName().equalsIgnoreCase("ACTION")){
            action = action.getPreviousSibling();
            i++;
            if(i>5000){
                //TODO - remove this check when xsd validation is complete
                logger.fatal("Could not find \"ACTION\" tag inside \"ACTION_RULE\". Check the XML input.");
                throw new RuntimeException();
            }
        }

        EnumGeometricActions geoAction = EnumGeometricActions.fromString(action.getTextContent());
        if(geoAction.equals(EnumGeometricActions.UNDEFINED)){
            EnumMetadataActions metaAction = EnumMetadataActions.fromString(action.getTextContent());
            if(metaAction.equals(EnumMetadataActions.UNDEFINED)){
                logger.fatal("Wrong action input: " + action.getTextContent());
                //TODO - remove exception when the xsd validation is complete.
                throw new RuntimeException();
            } else {
                actionRule.setMetaAction(metaAction);
            }
        } else {
            actionRule.setGeoAction(geoAction);
        }

        NodeList conditionsList = actionRuleElement.getElementsByTagName("CONDITION");
        if(conditionsList.getLength() != 1){
            //TODO - remove this check after xsd validation is complete
            logger.fatal("Found more than one condition inside ACTION_RULE. Please check the XML input file.");
            throw new RuntimeException();
        }

        //logger.fatal("CLASS " + conditionsList.item(0).getClass());

        Node conditionNode = conditionsList.item(0);
        
        ConditionTag conditionTag = new ConditionTag();
        
        ConditionTag con = createCondition(conditionNode, conditionTag, 0);
        
        logger.trace("FINAL CONDITION: " + con.toString());
        

    }
    
    private ConditionTag createCondition(Node conditionNode, ConditionTag conditionTag, int depth){
        
        Element co = (Element) conditionNode;
        //If condition contains expressions:
        String logicalOp = getLogicalExpressionOpIfExists(conditionNode);
        if(logicalOp.equalsIgnoreCase("AND")){
            
            LogicalExpressionTag let = new LogicalExpressionTag("AND", depth);
            System.out.println("GETTING LOGICAL EXPRESSION CHILD, USING NODE: " + conditionNode.getNodeName());
            Node andNode = getLogicalExpressionChild(conditionNode, "AND");
            logger.fatal("name of andNode!! " + andNode.getNodeName());
            List<ExpressionTag> letChilds = getLogicalExpressionChilds(andNode, "AND", depth);
            let.setExpressionTags(letChilds);
            
            conditionTag.setExpressionTag(let);
            return conditionTag;
        } else if(logicalOp.equalsIgnoreCase("OR")){

            LogicalExpressionTag let = new LogicalExpressionTag("OR", depth);
            
            Node orNode = getLogicalExpressionChild(conditionNode, "OR");
            logger.fatal("name of orNode!! " + orNode.getNodeName());
            List<ExpressionTag> letChilds = getLogicalExpressionChilds(orNode, "OR", depth);
            let.setExpressionTags(letChilds);
            conditionTag.setExpressionTag(let);
            return conditionTag;
        } else {
            //condition is a simple function:
            NodeList simpleFunctions = co.getElementsByTagName("FUNCTION");
            String function = null;

                function = simpleFunctions.item(0).getTextContent();
                ExpressionTag expressionTag = new ExpressionTag();
                expressionTag.setExpression(function);
                //ConditionTag conditionTag = new ConditionTag();
                conditionTag.setExpressionTag(expressionTag);
                //condition.s
                logger.debug("simple function: " + function);
                return conditionTag;            
            
//            if(simpleFunctions.getLength() == 1){
//                function = simpleFunctions.item(0).getTextContent();
//                ExpressionTag expressionTag = new ExpressionTag();
//                expressionTag.setExpression(function);
//                //ConditionTag conditionTag = new ConditionTag();
//                conditionTag.setExpressionTag(expressionTag);
//                //condition.s
//                logger.debug("simple function: " + function);
//                return conditionTag;
//            } else {
//                //TODO - check at xsd level
//                logger.fatal("Condition contains more than one function expression: " + function);
//            }
            
        }
    }

    private Node getLogicalExpressionChild(Node node, String type){
        
        Node childNode = node.getFirstChild();

        while( childNode != null ){
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                
                if(childElement.getNodeName().equalsIgnoreCase(type)){
                    return childElement;
                } else if(childElement.getNodeName().equalsIgnoreCase(type)){
                    return childElement;
                }
            }
            childNode = childNode.getNextSibling();
        }
        return null;
    }
    
    private static String getLogicalExpressionOpIfExists(Node node){
        
        Node childNode = node.getFirstChild();

        while( childNode != null ){
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                
                if(childElement.getNodeName().equalsIgnoreCase("AND")){
                    return "AND";
                } else if(childElement.getNodeName().equalsIgnoreCase("OR")){
                    return "OR";
                }
            }
            childNode = childNode.getNextSibling();
        }
        return "";
        
//        
//        
//        int length = node.getChildNodes().getLength();
//        NodeList childs = node.getChildNodes();
//        for (int i = 0; i < length; i++) {
//            if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
//                if(childs.item(i).getNodeName().equalsIgnoreCase("AND")) {
//                    return "AND";
//                } else if(childs.item(i).getNodeName().equalsIgnoreCase("OR")){
//                    return "OR";
//                }
//            }
//            
//        }
//        return null;
    }
    
    private List<ExpressionTag> getLogicalExpressionChilds(Node node, String type, int depth){
        depth++;
        ArrayList<ExpressionTag> list = new ArrayList<>();
        Node childNode = node.getFirstChild();

        logger.trace("initial node: " + node.getNodeName());
        logger.trace("parent node: " + node.getParentNode().getNodeName());
        //logger.trace("node child: " + childNode.getNodeName());
        while( childNode != null ){            
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                logger.trace(" child of initial: " + childElement.getNodeName());
                String logicalOp_ = getLogicalExpressionOpIfExists(childElement);
                if(logicalOp_.equalsIgnoreCase("AND")){

                    LogicalExpressionTag let = new LogicalExpressionTag("AND", depth);
                    Node andNode = getLogicalExpressionChild(childElement, "AND");

                    List<ExpressionTag> letChilds = getLogicalExpressionChilds(andNode, type, depth);
                    let.setExpressionTags(letChilds);
                    list.add(let);
                    //return conditionTag;
                } else if(logicalOp_.equalsIgnoreCase("OR")){

                    LogicalExpressionTag let = new LogicalExpressionTag("OR", depth);

                    Node orNode = getLogicalExpressionChild(childElement, "OR");
                    List<ExpressionTag> letChilds = getLogicalExpressionChilds(orNode, type, depth);
                    let.setExpressionTags(letChilds);
                    list.add(let);

                } 
                
                if(childElement.getNodeName().equalsIgnoreCase("EXPRESSION")){
                    
                    String logicalOp = getLogicalExpressionOpIfExists(childNode);
                    if(logicalOp.equalsIgnoreCase("AND")){

                        LogicalExpressionTag let = new LogicalExpressionTag("AND", depth);
                        logger.fatal("##### name of child!! " + childNode.getNodeName());
                        
                        
                        List<ExpressionTag> letChilds = getLogicalExpressionChilds(childNode, type, depth);
                        let.setExpressionTags(letChilds);
                        list.add(let);
                        
                    } else if(logicalOp.equalsIgnoreCase("OR")){

                        LogicalExpressionTag let = new LogicalExpressionTag("OR", depth);
                        logger.fatal("name of child!! " + childNode.getNodeName());
                        List<ExpressionTag> letChilds = getLogicalExpressionChilds(childNode, type, depth);
                        let.setExpressionTags(letChilds);
                        list.add(let);
                    } else {
                        NodeList simpleFunctions = childElement.getElementsByTagName("FUNCTION");
                        String function = simpleFunctions.item(0).getTextContent();
                        ExpressionTag expressionTag = new ExpressionTag();
                        expressionTag.setExpression(function);
                        //ConditionTag conditionTag = new ConditionTag();
                        //conditionTag.setExpressionTag(expressionTag);
                        //condition.s
                        list.add(expressionTag);
                        logger.debug("child simple function: " + function);                        
                    }

                } else if(childElement.getNodeName().equalsIgnoreCase("FUNCTION")){
                    
                    String expr = childElement.getTextContent();
                    if(depth > 0){
                        logger.trace("%%%%%%%%%%%%%%%%% found function: " + expr);
                    } else {
                        logger.trace("found function: " + expr);
                    }
                    
                    
                    ExpressionTag ext = new ExpressionTag();
                    ext.setExpression(expr);
                    list.add(ext);
                }
            }
            childNode = childNode.getNextSibling();
        }
        
        System.out.println(list.toString());
        //depth++;
        return list;
    }

    private LogicalExpressionTag getLogicalExpressionTag(Node node, String type, int depth){
        LogicalExpressionTag let = new LogicalExpressionTag("OR", depth);
        
        return let;
    }
    private static boolean nodeContainsLogicalOps(Node node){
        int length = node.getChildNodes().getLength();
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < length; i++) {
            if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if(childs.item(i).getNodeName().equalsIgnoreCase("AND") 
                        || childs.item(i).getNodeName().equalsIgnoreCase("OR")){
                    return true;
                }
            }
        }        
        
        return false;
    }

    private static boolean containsLogicalExpression(Node node){
        int length = node.getChildNodes().getLength();
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < length; i++) {
            if (childs.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if(childs.item(i).getNodeName().equalsIgnoreCase("AND") 
                        || childs.item(i).getNodeName().equalsIgnoreCase("OR")){
                    return true;
                }
            }
            
        }
        return false;
    }
    
    private void recursiveCondition(Expression expression, int depth){
        
    }
    
    private static String nodeType(short type) {
        
        switch(type) {
            case ELEMENT_NODE:                return "Element";
            case DOCUMENT_TYPE_NODE:          return "Document type";
            case ENTITY_NODE:                 return "Entity";
            case ENTITY_REFERENCE_NODE:       return "Entity reference";
            case NOTATION_NODE:               return "Notation";
            case TEXT_NODE:                   return "Text";
            case COMMENT_NODE:                return "Comment";
            case CDATA_SECTION_NODE:          return "CDATA Section";
            case ATTRIBUTE_NODE:              return "Attribute";
            case PROCESSING_INSTRUCTION_NODE: return "Attribute";
        }
        return "Unidentified";
    }    
}
