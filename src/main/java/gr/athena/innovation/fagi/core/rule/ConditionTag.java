package gr.athena.innovation.fagi.core.rule;

import java.util.LinkedList;

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
    
    @Override
    public String toString() {
        return "ConditionTag{" + "expressionTag=" + expressionTag + ", \n" +  tagListPrettyPrint(tagList) + '}';
    }    
    
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

}
