package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.action.EnumDatasetAction;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.rule.RuleSpecification;
import gr.athena.innovation.fagi.specification.Configuration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface for a Fuser core component.
 *
 * @author nkarag
 */
public interface Fuser {

    /**
     * Starts the fusion process for all the provided links from the input file using the provided rules.
     * 
     * @param config The configuration object.
     * @param ruleSpec The rule specification object.
     * @param functionMap the map containing the name of the available functions mapped to the functions.
     * @return the list of the fused interlinked entities
     * @throws ParseException Thrown by a <code>WKTReader</code> when a parsing problem occurs.
     * @throws gr.athena.innovation.fagi.exception.WrongInputException Indicates input error.
     * @throws java.io.IOException I/O exception.
     */    
    public List<LinkedPair> fuseAll(Configuration config, RuleSpecification ruleSpec, 
            Map<String, IFunction> functionMap) throws ParseException, WrongInputException, IOException;
    
    /**
     * Produces the output result by creating a new graph to the specified output 
     * or combines the fused entities with the source datasets based on the fusion mode.
     * 
     * @param configuration The configuration object.
     * @param fusedEntities The list with fused <code>LinkedPair</code> objects. 
     * @param defaultDatasetAction the default dataset action enumeration.
     * @throws FileNotFoundException Thrown when file was not found.
     */ 
    public void combine(Configuration configuration, List<LinkedPair> fusedEntities, 
            EnumDatasetAction defaultDatasetAction) throws FileNotFoundException, IOException;    
}
