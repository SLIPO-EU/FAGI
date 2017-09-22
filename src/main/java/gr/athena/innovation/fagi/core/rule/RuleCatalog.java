package gr.athena.innovation.fagi.core.rule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author nkarag
 * 
 */

public class RuleCatalog{

	private final List<Rule> rules = new ArrayList<>(); 

	public void addItem(Rule rule){ 
		rules.add(rule);
	}

    public List<Rule> getRules() {
        return rules;
    }

//    public int getMaxLevel(){
//        int max = 0;
//        for(Rule rule : rules){
//            int level = getMaxLevelOfRule(rule);
//            if(level > max){
//                max = level;
//            }
//        }
//        return max;
//    }
    
//    public int getMaxLevelOfRule(Rule rule){
//        int max = 0;
//        List<ActionRule> actionRules = rule.getActionRuleSet().getActionRuleList();
//        for(ActionRule actionRule : actionRules){
//            int level = getMaxLevelOfActionRule(actionRule);
//            if(level > max){
//                max = level;
//            }
//        }
//        return max;
//    }
    
//    public int getMaxLevelOfActionRule(ActionRule actionRule){
//        int max = 0;
//        int level;// = 0;
//        ExpressionTag expressionTag = actionRule.getConditionTag().getExpressionTag();
//
//        if(expressionTag instanceof LogicalExpressionTag){
//            LogicalExpressionTag logEx = (LogicalExpressionTag) expressionTag;
//            level = logEx.getLevel();
//            if(level > max){
//                max = level;
//            }
//        }
//
//        LinkedList<LogicalExpressionTag> tagList = actionRule.getConditionTag().getTagList();
//        
//        for(LogicalExpressionTag tag : tagList){
//            level = tag.getLevel();
//            if(level > max){
//                max = level;
//            }
//        }
//        return max;
//    }
}
