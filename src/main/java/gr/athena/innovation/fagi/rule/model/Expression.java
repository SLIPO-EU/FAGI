package gr.athena.innovation.fagi.rule.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Expression class is a model for a logical operation defined in a Condition.
 * 
 * @author nkarag
 */
public class Expression {

    private String logicalOperatorParent;
    private List<Function> functions = new ArrayList<>();
    private LinkedHashMap<String, List<Function>> groupsOfChildFunctions = new LinkedHashMap<>();

    @Override
    public String toString() {

        return "\nExpression{" 
                + "\n\tlogicalOperatorParent=" + logicalOperatorParent 
                + "\n\tfunctions=" + functions 
                + "\n\tgroupsOfChildFunctions=" + groupsOfChildFunctions + "\n}";
    }

    public String getLogicalOperatorParent() {
        return logicalOperatorParent;
    }

    public void setLogicalOperatorParent(String logicalOperatorParent) {
        this.logicalOperatorParent = logicalOperatorParent;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public LinkedHashMap<String, List<Function>> getGroupsOfChildFunctions() {
        return groupsOfChildFunctions;
    }

    public void setGroupsOfChildFunctions(LinkedHashMap<String, List<Function>> groupsOfChildFunctions) {
        this.groupsOfChildFunctions = groupsOfChildFunctions;
    }

}
