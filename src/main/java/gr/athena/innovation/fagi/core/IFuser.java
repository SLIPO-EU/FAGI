package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import java.util.Map;

/**
 * Interface for a Fuser core component.
 *
 * @author nkarag
 */
public interface IFuser {

    /**
     * Starts the fusion process for all the provided links from the input file using the provided rules.
     * 
     * @param config
     * @param ruleCatalog
     * @param functionMap
     * @throws ParseException
     * @throws gr.athena.innovation.fagi.exception.WrongInputException
     */    
    public void fuseAllWithRules(FusionSpecification config, RuleCatalog ruleCatalog, 
            Map<String, IFunction> functionMap) throws ParseException, WrongInputException;
    
}
