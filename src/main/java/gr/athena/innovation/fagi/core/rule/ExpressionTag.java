package gr.athena.innovation.fagi.core.rule;

/**
 *
 * @author nkarag
 */
public class ExpressionTag {
    
    private String expression;

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

}
