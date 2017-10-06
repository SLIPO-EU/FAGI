package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.core.rule.model.Rule;
import gr.athena.innovation.fagi.core.action.EnumDatasetActions;
import gr.athena.innovation.fagi.core.functions.FunctionRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * Container class for managing rules. 
 * 
 * @author nkarag
 * 
 */

public class RuleCatalog{

	private final List<Rule> rules = new ArrayList<>(); 
    private EnumDatasetActions defaultDatasetAction;
    private FunctionRegistry methodRegistry;

    /**
     * Adds a rule to the catalog.
     * @param rule the rule to add.
     */
    public void addItem(Rule rule){ 
		rules.add(rule);
	}

    /**
     * @return the List that contains the rules.
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * @return the Function Registry object.
     */
    public FunctionRegistry getMethodRegistry() {
        return methodRegistry;
    }

    /**
     * Sets the FunctionRegistry object.
     * 
     * @param methodRegistry
     */
    public void setMethodRegistry(FunctionRegistry methodRegistry) {
        this.methodRegistry = methodRegistry;
    }

    /**
     * @return the default dataset action as a {@link EnumDatasetActions} object.
     */
    public EnumDatasetActions getDefaultDatasetAction() {
        return defaultDatasetAction;
    }

    /**
     * Sets the default Dataset action.
     * 
     * @param defaultDatasetAction
     */
    public void setDefaultDatasetAction(EnumDatasetActions defaultDatasetAction) {
        this.defaultDatasetAction = defaultDatasetAction;
    }
}
