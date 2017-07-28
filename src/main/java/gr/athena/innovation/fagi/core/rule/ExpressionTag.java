package gr.athena.innovation.fagi.core.rule;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nkarag
 */
public class ExpressionTag {
    
    private String expression;
    //private List<String> tags = new ArrayList<>();


    @Override
    public String toString() {
        return "ExpressionTag{" + "expression=" + expression + '}';
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }

}
