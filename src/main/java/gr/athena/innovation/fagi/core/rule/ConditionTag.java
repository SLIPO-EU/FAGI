package gr.athena.innovation.fagi.core.rule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Node;

/**
 *
 * @author nkarag
 */
public class ConditionTag {
    private ExpressionTag expressionTag;
    private LinkedList<LogicalExpressionTag> tagList = new LinkedList<>();

    public ExpressionTag getExpressionTag() {
        return expressionTag;
    }

    public void setExpressionTag(ExpressionTag expressionTag) {
        this.expressionTag = expressionTag;
    }

//    @Override
//    public String toString() {
//        return "ConditionTag{ tagList=" + tagList + '}';
//    }
    
    @Override
    public String toString() {
        return "ConditionTag{" + "expressionTag=" + expressionTag + ", \n" +  tagListPrettyPrint(tagList) + '}';
    }

//    @Override
//    public String toString() {
//        return "ConditionTag{" + "expressionTag=" + expressionTag + '}';
//    }    
    
    public void addNode(LogicalExpressionTag node){
        this.tagList.add(node);
    }

    private String tagListPrettyPrint(LinkedList<LogicalExpressionTag> tagList){
        StringBuilder sb = new StringBuilder();
        sb.append("Taglist[\n");
        for(LogicalExpressionTag tag : tagList){
            sb.append(tag.toString());
            sb.append("\n");
        }
        
        return sb.toString();
    }
//    public LinkedList<Node> getList() {
//        return list;
//    }

}
