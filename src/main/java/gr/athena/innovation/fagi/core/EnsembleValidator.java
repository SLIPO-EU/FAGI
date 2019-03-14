package gr.athena.innovation.fagi.core;

import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.action.EnumValidationAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.CustomRDFProperty;
import gr.athena.innovation.fagi.model.EntityData;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.rule.model.ActionRule;
import gr.athena.innovation.fagi.rule.model.Condition;
import gr.athena.innovation.fagi.rule.model.ExternalProperty;
import gr.athena.innovation.fagi.rule.model.Rule;
import gr.athena.innovation.fagi.utils.RDFUtils;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Literal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class EnsembleValidator {
    
    private static final Logger LOG = LogManager.getLogger(EnsembleValidator.class);
    
    private EnumValidationAction validation = EnumValidationAction.UNDEFINED;
    
    public EnumValidationAction validate(List<Rule> validationRules, Map<String, IFunction> functionMap, 
            EntityData leftEntityData, EntityData rightEntityData) throws WrongInputException{

        //EntityData leftEntityData = leftNode.getEntityData();
        //EntityData rightEntityData = rightNode.getEntityData();
        LinkedPair pair = new LinkedPair(EnumDatasetAction.UNDEFINED);

        for (Rule validationRule : validationRules) {
            LOG.trace("Validating with Rule: " + validationRule);

            //assign nulls. Validation rule does not use basic properties, only external properties. 
            //These values will be ignored at condition evaluation. Todo: Consider a refactoring
            CustomRDFProperty validationProperty = null;
            Literal literalA = null;
            Literal literalB = null;

            //Checking if it is a simple rule with default actions and no conditions and functions are set.
            //Fuse with the rule defaults and break.
            if (validationRule.getActionRuleSet() == null || validationRule.getActionRuleSet().getActionRuleList().isEmpty()) {
                LOG.trace("Rule without ACTION RULE SET, using default validation action.");

                validation = validationRule.getDefaultValidationAction();

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

                    validation = validationAction;

                    actionRuleToApply = true;
                    break;
                }
            }

            //No action rule applied. Use default Action (accept)
            if (actionRuleToApply == false) {

                EnumValidationAction defaultAction = validationRule.getDefaultValidationAction();

                LOG.debug("All conditions evaluated to false in validation. Using default validation action: "
                        + defaultAction);

                validation = defaultAction;
            }
        }

        return validation;
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

        LOG.debug("valueA: " + valueA);
        LOG.debug("valueB: " + valueB);
        
        externalPropertyEntry.getValue().setValueA(valueA);
        externalPropertyEntry.getValue().setValueB(valueB);
    }
}
