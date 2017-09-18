package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.rule.RuleCatalog;
import gr.athena.innovation.fagi.core.specification.FusionConfig;
import gr.athena.innovation.fagi.core.specification.FusionSpecification;

/**
 * Interface for a Fuser core component.
 *
 * @author nkarag
 */
public interface IFuser {
    
    /**
     * Starts the fusion process for all the provided links from the input file.
     * 
     * @param fusionConfig the fusion configuration
     * @throws ParseException
     */
    public void fuseAll(FusionConfig fusionConfig) throws ParseException;
    
    /**
     * Starts the fusion process for all the provided links from the input file using the provided rules.
     * 
     * @param config
     * @param ruleCatalog
     * @throws ParseException
     */    
    public void fuseAllWithRules(FusionSpecification config, RuleCatalog ruleCatalog) throws ParseException;
    
}
