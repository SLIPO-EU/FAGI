package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.specification.EnumDataset;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class FrequencyCalculationProcess {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(FrequencyCalculationProcess.class);

    public void run(FusionSpecification fusionSpec, List<String> rdfProperties) {

        //word frequencies using the RDF properties from file
        int topK = 0; //topK zero and negative values return the complete list

        //Frequent terms
        FileFrequencyCounter termFrequency = new FileFrequencyCounter(topK);
        termFrequency.setLocale(fusionSpec.getLocale());

        termFrequency.setProperties(rdfProperties);

        termFrequency.export(fusionSpec.getPathDatasetA(), EnumDataset.LEFT);
        
        termFrequency.export(fusionSpec.getPathDatasetB(), EnumDataset.RIGHT);

        if (!StringUtils.isBlank(fusionSpec.getCategoriesA())) {
            FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
            frequencyExtractor.extract(topK, fusionSpec.getCategoriesA(), LeftDataset.getLeftDataset().getModel(),
                    fusionSpec.getOutputDir(), fusionSpec.getLocale(), EnumDataset.LEFT);
        }

        if (!StringUtils.isBlank(fusionSpec.getCategoriesB())) {

            FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
            frequencyExtractor.extract(topK, fusionSpec.getCategoriesB(), RightDataset.getRightDataset().getModel(),
                    fusionSpec.getOutputDir(), fusionSpec.getLocale(), EnumDataset.RIGHT);
        }
    }
}
