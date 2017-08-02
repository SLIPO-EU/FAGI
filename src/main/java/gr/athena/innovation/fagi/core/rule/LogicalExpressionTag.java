package gr.athena.innovation.fagi.core.rule;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nkarag
 */
public class LogicalExpressionTag extends ExpressionTag{
    
    private String key;
    private final String logicalOp;
    private final int level;
    private List<ExpressionTag> expressionTags = new ArrayList<>();;
    private List<LogicalExpressionTag> logicalExpressionTags = new ArrayList<>();

    public LogicalExpressionTag(String logicalOp, int level){
        this.level = level;
        switch(logicalOp){
            case "AND":
            case "OR":
            case "NOT":
                this.logicalOp = logicalOp;
                break;
            //TODO - check if throwing exception from constructor integrates well with spring    
            default: throw new IllegalArgumentException();
        }
    }

    public String getLogicalOp() {
        return logicalOp;
    }

    public List<ExpressionTag> getExpressionTags() {
        return expressionTags;
    }

    public void setExpressionTags(List<ExpressionTag> expressionTags) {
        this.expressionTags = expressionTags;
    }

    public List<LogicalExpressionTag> getLogicalExpressionTags() {
        return logicalExpressionTags;
    }

    public void setLogicalExpressionTags(List<LogicalExpressionTag> logicalExpressionTags) {
        this.logicalExpressionTags = logicalExpressionTags;
    }

    public void addLogicalExpressionTag(LogicalExpressionTag logicalExpressionTag){
        this.logicalExpressionTags.add(logicalExpressionTag);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public String toString() {
        return "LogicalExpressionTag{" + "key=" + key + ", logicalOp=" + logicalOp + ", level=" + level + ", expressionTags=" + expressionTags + ", logicalExpressionTags=" + logicalExpressionTags + '}';
    }    
}
