package gr.athena.innovation.fagi.core.rule;

/**
 *
 * @author nkarag
 */
public class Condition {
    private Expression expression;
    private boolean isTrue;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }
    
}
