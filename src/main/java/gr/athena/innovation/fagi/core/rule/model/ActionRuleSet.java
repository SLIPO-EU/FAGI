package gr.athena.innovation.fagi.core.rule.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The ActionRuleSet contains all the ActionRules of each Rule. 
 * The priority of an ActionRule is the order of appearance.
 * 
 * @author nkarag
 */
public class ActionRuleSet {
    
    private List<ActionRule>  actionRuleList = new ArrayList<>();

    @Override
    public String toString() {
        return "ActionRuleSet{" + "actionRuleSet=" + actionRuleList.toString() + '}';
    }

    public void addActionRule(ActionRule actionRule){
        this.actionRuleList.add(actionRule);
    }
    
    public List<ActionRule> getActionRuleList() {
        return actionRuleList;
    }

    public void setActionRuleList(List<ActionRule> actionRuleList) {
        this.actionRuleList = actionRuleList;
    }
}
