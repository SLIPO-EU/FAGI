package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import java.util.List;
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
     * @param config The fusion specification object.
     * @param ruleCatalog The rule catalog object.
     * @param functionMap the map containing the name of the available functions mapped to the functions.
     * @return the list of the fused interlinked entities
     * @throws ParseException Thrown by a <code>WKTReader</code> when a parsing problem occurs.
     * @throws gr.athena.innovation.fagi.exception.WrongInputException Indicates input error.
     */    
    public List<LinkedPair> fuseAll(FusionSpecification config, RuleCatalog ruleCatalog, 
            Map<String, IFunction> functionMap) throws ParseException, WrongInputException;
    
}
