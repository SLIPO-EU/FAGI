package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.specification.EnumDataset;

/**
 *
 * @author nkarag
 */
public interface FrequencyCounter {
    
    public void export(String inputFilePath, EnumDataset dataset);
    
}
