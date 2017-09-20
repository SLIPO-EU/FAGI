package gr.athena.innovation.fagi.core.rule;

import java.util.LinkedList;

/**
 * ConditionTag represents the result of an expression that decides if a fusion action is going to be used or not.
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
    
   
    
    public void addNode(LogicalExpressionTag node){
        this.tagList.add(node);
    }

    public LinkedList<LogicalExpressionTag> getTagList() {
        return tagList;
    }

    private String tagListPrettyPrint(LinkedList<LogicalExpressionTag> tagList){
        StringBuilder sb = new StringBuilder();
        sb.append("\nTaglist[\n");
        for(LogicalExpressionTag tag : tagList){
            sb.append(tag.toString());
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "ConditionTag{" + "expressionTag=" + expressionTag + ", \n" +  tagListPrettyPrint(tagList) + '}';
    }     
}
