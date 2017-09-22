package gr.athena.innovation.fagi.core.rule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author nkarag
 */
public class Expression {

    private String logicalOperatorParent;
    //private String logicalOperatorFirstLevelChilds;
    private List<String> funcs = new ArrayList<>();
    
    //private boolean expressionIsSingleFunction = false;
    private List<String> firstChildsFunctions = new ArrayList<>();;
    private LinkedHashMap<String, List<String>> groupsOfChildFunctions = new LinkedHashMap<>();

    @Override
    public String toString() {
        
        if(funcs.isEmpty()){
            
        }
        
        return "\nExpression{" 
                + "\n\tlogicalOperatorParent=" + logicalOperatorParent 
                + "\n\tfuncs=" + funcs 
                + "\n\tfirstChildsFunctions=" + firstChildsFunctions 
                + "\n\tgroupsOfChildFunctions=" + groupsOfChildFunctions + "\n}";
    }

    public String getLogicalOperatorParent() {
        return logicalOperatorParent;
    }

    public void setLogicalOperatorParent(String logicalOperatorParent) {
        this.logicalOperatorParent = logicalOperatorParent;
    }

    public List<String> getFuncs() {
        return funcs;
    }

    public void setFuncs(List<String> funcs) {
        this.funcs = funcs;
    }

    public List<String> getFirstChildsFunctions() {
        return firstChildsFunctions;
    }

    public void setFirstChildsFunctions(List<String> firstChildsFunctions) {
        this.firstChildsFunctions = firstChildsFunctions;
    }

    public LinkedHashMap<String, List<String>> getGroupsOfChildFunctions() {
        return groupsOfChildFunctions;
    }

    public void setGroupsOfChildFunctions(LinkedHashMap<String, List<String>> groupsOfChildFunctions) {
        this.groupsOfChildFunctions = groupsOfChildFunctions;
    }

}
