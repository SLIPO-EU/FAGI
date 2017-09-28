package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.core.action.EnumDatasetActions;
import gr.athena.innovation.fagi.fusers.MethodRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nkarag
 * 
 */

public class RuleCatalog{

	private final List<Rule> rules = new ArrayList<>(); 
    private EnumDatasetActions defaultDatasetAction;
    private MethodRegistry methodRegistry;

	public void addItem(Rule rule){ 
		rules.add(rule);
	}

    public List<Rule> getRules() {
        return rules;
    }

    public MethodRegistry getMethodRegistry() {
        return methodRegistry;
    }

    public void setMethodRegistry(MethodRegistry methodRegistry) {
        this.methodRegistry = methodRegistry;
    }

    public EnumDatasetActions getDefaultDatasetAction() {
        return defaultDatasetAction;
    }

    public void setDefaultDatasetAction(EnumDatasetActions defaultDatasetAction) {
        this.defaultDatasetAction = defaultDatasetAction;
    }
}
