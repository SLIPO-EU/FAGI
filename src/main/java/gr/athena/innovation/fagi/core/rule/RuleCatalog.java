package gr.athena.innovation.fagi.core.rule;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author nkarag
 * @param <Rule>
 */
public class RuleCatalog<Rule>{

	private final List<Rule> rules = new ArrayList<>(); 

	public RuleCatalog(){
	}
 
	public Object getItem(){
		return rules.get(0);
	}
 
	public void addItem(Rule rule){ 
		rules.add(rule);
	}
    
}
