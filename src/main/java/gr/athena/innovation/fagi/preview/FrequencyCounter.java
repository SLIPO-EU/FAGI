package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.specification.EnumDataset;

/**
 * Interface for frequency calculation.
 * 
 * @author nkarag
 */
public interface FrequencyCounter {
    
    /**
     * Exports the files with the frequency results.
     * 
     * @param inputFilePath the input file containing the RDF properties.
     * @param dataset the dataset ID.
     */
    public void export(String inputFilePath, EnumDataset dataset);
    
}
