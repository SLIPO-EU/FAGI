package gr.athena.innovation.fagi.core.rule;

/**
 * Condition represents the result of an expression or a function that decides if a fusion action is going to be applied.
 * 
 * @author nkarag
 */
public class Condition {

    private boolean singleFunction;
    private String function;
    private Expression expression;
    
    public boolean evaluate(){
        //TODO - evaluate condition from expressions/functions
        return false;
    }

    public boolean isSingleFunction() {
        return singleFunction;
    }

    public void setSingleFunction(boolean singleFunction) {
        this.singleFunction = singleFunction;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        if(isSingleFunction()){
            return "Condition{" + "singleFunction=" + singleFunction + ", function=" + function + "}";
        } else {
            return "Condition{ expression=" + expression + "}"; 
        }
    }    
}
