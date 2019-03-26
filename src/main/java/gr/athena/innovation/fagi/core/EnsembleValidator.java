package gr.athena.innovation.fagi.core;

import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.CustomRDFProperty;
import gr.athena.innovation.fagi.model.EntityData;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.rule.RuleSpecification;
import gr.athena.innovation.fagi.rule.model.ActionRule;
import gr.athena.innovation.fagi.rule.model.Condition;
import gr.athena.innovation.fagi.rule.model.ExternalProperty;
import gr.athena.innovation.fagi.rule.model.Rule;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.utils.RDFUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for validating ensemble POIS.
 * 
 * @author nkarag
 */
public class EnsembleValidator {

    private static final Logger LOG = LogManager.getLogger(EnsembleValidator.class);

    private EnumValidationAction validationAction = EnumValidationAction.UNDEFINED;
    private int rejected = 0;

    /**
     * Validates an ensemble link.Validating essentially accepts (does nothing) or removes models from the ensemble link.  
     * 
     * @param link the link.
     * @param functionMap the map of the functions.
     * @param ruleSpec the rule specification.
     * @param modelsA the models from A.
     * @param modelsB the models from B.
     * @return accepts the link by returning true, or false if all links of the ensemble were rejected.
     * @throws WrongInputException wrong input.
     */
    public boolean validateEnsemble(Link link, Map<String, IFunction> functionMap, RuleSpecification ruleSpec,
            Map<String, Model> modelsA, Map<String, Model> modelsB) throws WrongInputException {

        Set<String> a = link.getEnsemblesA();
        Set<String> b = link.getEnsemblesB();
        final EnumOutputMode mode = Configuration.getInstance().getOutputMode();

        switch (mode) {
            case AA_MODE:
            case A_MODE:
            case AB_MODE:
            case L_MODE: {
                //a based mode defines that ensembles in A, is actually a single node.
                if (a.size() > 1) {
                    LOG.error("size: " + a.size());
                    throw new IllegalStateException("Ensembles in A should be a single node, considering this fusion mode.");
                }

                EntityData leftData = new EntityData();
                Map.Entry<String, Model> entryA = modelsA.entrySet().iterator().next();

                leftData.setUri(entryA.getKey());
                leftData.setModel(entryA.getValue());

                for (Map.Entry<String, Model> entry : modelsB.entrySet()) {
                    EntityData rightData = new EntityData();
                    rightData.setUri(entry.getKey());
                    rightData.setModel(entry.getValue());

                    /* VALIDATION */
                    EnumValidationAction validation = validate(ruleSpec.getValidationRules(),
                            functionMap, leftData, rightData);

                    switch (validation) {
                        case ACCEPT:
                        case ACCEPT_MARK_AMBIGUOUS:
                        case ML_VALIDATION:
                            //do nothing
                            break;
                        case REJECT:
                        case REJECT_MARK_AMBIGUOUS:
                            //remove node from ensemble set
                            b.remove(entry.getKey());
                            rejected++;

                            break;
                    }
                }

                break;
            }
            case BB_MODE:
            case B_MODE:
            case BA_MODE: {
                if (b.size() > 1) {
                    LOG.error("size: " + b.size());
                    throw new IllegalStateException("Ensembles in B should be a single node, considering this fusion mode.");
                }

                EntityData rightData = new EntityData();
                Map.Entry<String, Model> entryB = modelsB.entrySet().iterator().next();

                rightData.setUri(entryB.getKey());
                rightData.setModel(entryB.getValue());

                for (Map.Entry<String, Model> entry : modelsA.entrySet()) {
                    EntityData leftData = new EntityData();
                    leftData.setUri(entry.getKey());
                    leftData.setModel(entry.getValue());

                    /* VALIDATION */
                    EnumValidationAction validation = validate(ruleSpec.getValidationRules(),
                            functionMap, leftData, rightData);

                    switch (validation) {
                        case ACCEPT:
                        case ACCEPT_MARK_AMBIGUOUS:
                        case ML_VALIDATION:
                            //do nothing
                            break;
                        case REJECT:
                        case REJECT_MARK_AMBIGUOUS:
                            //remove node from ensemble set
                            a.remove(entry.getKey());
                            rejected++;
                            break;
                    }
                }

                break;
            }
        }
        
        switch (mode) {
            case AA_MODE:
            case A_MODE:
            case AB_MODE:
            case L_MODE: {
                if(b.isEmpty()){
                    //all nodes from B were rejected.
                    return false;
                }
                break;
            }
            case BB_MODE:
            case B_MODE:
            case BA_MODE: {
                if(a.isEmpty()){
                    //all nodes from A were rejected.
                    return false;
                }
                break;
            }
        }

        return true;
    }

    /**
     * Validates based on the given rule specification.
     * 
     * @param validationRules the list of the validation rules.
     * @param functionMap the map of functions.
     * @param leftEntityData the entity data of the left node.
     * @param rightEntityData the entity data of the right node.
     * @return the enumeration of the validation action.
     * @throws WrongInputException wrong input.
     */
    public EnumValidationAction validate(List<Rule> validationRules, Map<String, IFunction> functionMap,
            EntityData leftEntityData, EntityData rightEntityData) throws WrongInputException {

        LOG.debug("validating: " + leftEntityData.getUri() + " " + rightEntityData.getUri());
        LinkedPair pair = new LinkedPair(EnumDatasetAction.UNDEFINED);

        for (Rule validationRule : validationRules) {
            LOG.trace("Validating with Rule: " + validationRule);

            //assign nulls. Validation rule does not use basic properties, only external properties. 
            //These values will be ignored at condition evaluation. Todo: Consider a refactoring
            CustomRDFProperty validationProperty = null;
            Literal literalA = null;
            Literal literalB = null;

            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Validate with the rule defaults and break.
            if (validationRule.getActionRuleSet() == null || validationRule.getActionRuleSet().getActionRuleList().isEmpty()) {
                LOG.trace("Rule without ACTION RULE SET, using default validation action.");

                validationAction = validationRule.getDefaultValidationAction();

                break;
            }

            List<ActionRule> actionRules = validationRule.getActionRuleSet().getActionRuleList();
            int actionRuleCount = 0;
            boolean actionRuleToApply = false;
            for (ActionRule actionRule : actionRules) {

                LOG.debug("-- Action rule: " + actionRuleCount);

                EnumValidationAction validationAction = null;

                if (actionRule.getValidationAction() != null) {
                    validationAction = actionRule.getValidationAction();
                }

                Condition condition = actionRule.getCondition();

                //switch case for evaluation using external properties.
                for (Map.Entry<String, ExternalProperty> externalPropertyEntry : validationRule.getExternalProperties().entrySet()) {
                    evaluateExternalProperty(externalPropertyEntry, leftEntityData, rightEntityData);
                }

                boolean isActionRuleToBeApplied = condition.evaluate(functionMap, pair, validationProperty,
                        literalA, literalB, validationRule.getExternalProperties());

                actionRuleCount++;

                if (isActionRuleToBeApplied) {
                    LOG.debug("Condition : " + condition + " evaluated true. Validating link with: " + validationAction);

                    this.validationAction = validationAction;

                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action (accept)
            if (actionRuleToApply == false) {

                EnumValidationAction defaultAction = validationRule.getDefaultValidationAction();

                LOG.debug("All conditions evaluated to false in validation. Using default validation action: "
                        + defaultAction);

                validationAction = defaultAction;
            }
        }

        return validationAction;
    }

    private void evaluateExternalProperty(Map.Entry<String, ExternalProperty> externalPropertyEntry,
            EntityData leftEntityData, EntityData rightEntityData) {

        //There are two cases here: (a) Single property refers to node. (b) the external property contains a chain
        //separated by a whitespace.
        String extPropertyText = externalPropertyEntry.getValue().getProperty();
        Literal valueA;
        Literal valueB;

        if (extPropertyText.contains(" ")) {
            String[] chains = extPropertyText.split(" ");
            valueA = RDFUtils.getLiteralValueFromChain(chains[0], chains[1], leftEntityData.getModel());
            valueB = RDFUtils.getLiteralValueFromChain(chains[0], chains[1], rightEntityData.getModel());
        } else {
            valueA = RDFUtils.getLiteralValue(externalPropertyEntry.getValue().getProperty(), leftEntityData.getModel());
            valueB = RDFUtils.getLiteralValue(externalPropertyEntry.getValue().getProperty(), rightEntityData.getModel());
        }

        LOG.trace("valueA: " + valueA);
        LOG.trace("valueB: " + valueB);

        externalPropertyEntry.getValue().setValueA(valueA);
        externalPropertyEntry.getValue().setValueB(valueB);
    }

    /**
     * 
     * @return the number of rejected links. 
     */
    public int getRejected() {
        return rejected;
    }
}
