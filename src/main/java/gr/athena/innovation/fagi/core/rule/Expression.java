package gr.athena.innovation.fagi.core.rule;

import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.Node;

/**
 *
 * @author nkarag
 */
public class Expression extends NodeImpl implements Node{

    private String logicalType;
    private boolean isFunction;
    private int depth;
    private Expression parentExpression;
    private Expression childExpressionA;
    private Expression childExpressionB;
    private String function;

    public boolean isIsFunction() {
        return isFunction;
    }

    public void setIsFunction(boolean isFunction) {
        this.isFunction = isFunction;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Expression getParentExpression() {
        return parentExpression;
    }

    public void setParentExpression(Expression parentExpression) {
        this.parentExpression = parentExpression;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
    
    public String getLogicalType() {
        return logicalType;
    }

    public void setLogicalType(String logicalType) {
        this.logicalType = logicalType;
    }

    public Expression getChildExpressionA() {
        return childExpressionA;
    }

    public void setChildExpressionA(Expression childExpressionA) {
        this.childExpressionA = childExpressionA;
    }

    public Expression getChildExpressionB() {
        return childExpressionB;
    }

    public void setChildExpressionB(Expression childExpressionB) {
        this.childExpressionB = childExpressionB;
    }

    @Override
    public short getNodeType() {
        if(isFunction){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String getNodeName() {
        return childExpressionA.toString() + " " + logicalType + " " +  childExpressionB.toString();
    }
    
}
