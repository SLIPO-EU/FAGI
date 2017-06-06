package gr.athena.innovation.fagi.core;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.configuration.FusionConfig;

/**
 *
 * @author nkarag
 */
public interface IFuser {
    
    public void fuseAll(FusionConfig fusionConfig) throws ParseException;
    
}
