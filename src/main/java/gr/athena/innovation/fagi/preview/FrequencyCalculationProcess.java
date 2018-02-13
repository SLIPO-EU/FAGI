package gr.athena.innovation.fagi.preview;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class FrequencyCalculationProcess {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FrequencyCalculationProcess.class);

    public void run(FusionSpecification fusionSpec, List<String> rdfProperties) {

        //word frequencies using the RDF properties from file
        int topK = 0; //topK zero and negative values return the complete list

        //Frequent terms
        FileFrequencyCounter termFrequency = new FileFrequencyCounter(fusionSpec, topK);
        termFrequency.setLocale(fusionSpec.getLocale());

        termFrequency.setProperties(rdfProperties);

        termFrequency.export(fusionSpec.getPathA());

        if (!StringUtils.isBlank(fusionSpec.getCategoriesA())) {
            FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
            frequencyExtractor.extract(topK, fusionSpec.getCategoriesA(), LeftModel.getLeftModel().getModel(),
                    fusionSpec, fusionSpec.getLocale());
        }

        if (!StringUtils.isBlank(fusionSpec.getCategoriesB())) {

            FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
            frequencyExtractor.extract(topK, fusionSpec.getCategoriesB(), RightModel.getRightModel().getModel(),
                    fusionSpec, fusionSpec.getLocale());
        }

        //similarity viewer for each pair and a,b,c normalization
        RDFInputSimilarityViewer qualityViewer = new RDFInputSimilarityViewer(fusionSpec);

        try {

            qualityViewer.printRDFSimilarityResults(rdfProperties);

        } catch (ParseException | IOException ex) {
            logger.error(ex);
            throw new ApplicationException(ex.getMessage());
        }
    }
}
