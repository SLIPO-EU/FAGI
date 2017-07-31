package gr.athena.innovation.fagi.core.rule;

import java.util.ArrayList;
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

}
