package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.specification.EnumDataset;

/**
 * Interface for frequency calculation.
 * 
 * @author nkarag
 */
public interface FrequencyCounter {
    
    public void export(String inputFilePath, EnumDataset dataset);
    
}
