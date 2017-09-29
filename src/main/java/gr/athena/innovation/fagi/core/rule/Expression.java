package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.xml.Function;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author nkarag
 */
public class Expression {

    private String logicalOperatorParent;
    //private List<String> funcs = new ArrayList<>();
    private List<Function> functions = new ArrayList<>();
    
    //private boolean expressionIsSingleFunction = false;
    private List<String> firstChildsFunctions = new ArrayList<>();;
    private LinkedHashMap<String, List<String>> groupsOfChildFunctions = new LinkedHashMap<>();
    private LinkedHashMap<String, List<Function>> groupsOfChildFuncts = new LinkedHashMap<>();

    @Override
    public String toString() {

        return "\nExpression{" 
                + "\n\tlogicalOperatorParent=" + logicalOperatorParent 
                + "\n\tfirstChildsFunctions=" + firstChildsFunctions 
                + "\n\tgroupsOfChildFunctions=" + groupsOfChildFunctions + "\n}";
    }

    public String getLogicalOperatorParent() {
        return logicalOperatorParent;
    }

    public void setLogicalOperatorParent(String logicalOperatorParent) {
        this.logicalOperatorParent = logicalOperatorParent;
    }

//    public List<String> getFuncs() {
//        return funcs;
//    }
//
//    public void setFuncs(List<String> funcs) {
//        this.funcs = funcs;
//    }

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

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public LinkedHashMap<String, List<Function>> getGroupsOfChildFuncts() {
        return groupsOfChildFuncts;
    }

    public void setGroupsOfChildFuncts(LinkedHashMap<String, List<Function>> groupsOfChildFuncts) {
        this.groupsOfChildFuncts = groupsOfChildFuncts;
    }

}
