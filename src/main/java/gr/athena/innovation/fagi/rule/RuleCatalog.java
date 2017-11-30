package gr.athena.innovation.fagi.rule;

import gr.athena.innovation.fagi.rule.model.Rule;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.function.FunctionRegistry;
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
    private EnumDatasetAction defaultDatasetAction;
    private FunctionRegistry functionRegistry;

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
    public FunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    /**
     * Sets the FunctionRegistry object.
     * 
     * @param functionRegistry
     */
    public void setFunctionRegistry(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    /**
     * @return the default dataset action as a {@link EnumDatasetAction} object.
     */
    public EnumDatasetAction getDefaultDatasetAction() {
        return defaultDatasetAction;
    }

    /**
     * Sets the default Dataset action.
     * 
     * @param defaultDatasetAction
     */
    public void setDefaultDatasetAction(EnumDatasetAction defaultDatasetAction) {
        this.defaultDatasetAction = defaultDatasetAction;
    }
}
