package gr.athena.innovation.fagi.evaluation;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nkarag
 */
public class Evaluation {
    
    public void run(FusionSpecification fusionSpec, String csvPath){

            //on version change, all weights update (along with notes)
            String version = "v2.3a";
            String evaluationPath = "";
            String resultsPath = evaluationPath + version + "/";
            String nameMetrics = "name_metrics_" + version + ".csv";
            String nameSimilarities = "name_similarities_" + version + ".txt";
            String thresholds = "optimalThresholds_" + version + ".txt";

            setWeights(version);

            String baseW = SpecificationConstants.BASE_WEIGHT.toString();
            String misW = SpecificationConstants.MISMATCH_WEIGHT.toString();
            String spW = SpecificationConstants.SPECIAL_TERMS_WEIGHT.toString();
            String comW = SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT.toString();

            String notes = "JaroWinkler mismatch threshold (collator): 0.75\n"
                    + "Base weight: " + baseW + "\n"
                    + "mismatch weight: " + misW + "\n"
                    + "special terms weight: " + spW + "\n"
                    + "common special terms weight: " + comW + "\n";

            if (!resultsPath.endsWith("/")) {
                resultsPath = resultsPath + "/";
            }

            SimilarityCalculator similarityCalculator = new SimilarityCalculator(fusionSpec);
            similarityCalculator.calculateCSVPairSimilarities(csvPath, resultsPath, nameSimilarities);

            MetricProcessor metricProcessor = new MetricProcessor(fusionSpec);
            
            try {
                
                metricProcessor.executeEvaluation(csvPath, resultsPath, nameMetrics, thresholds, notes);
                
            } catch (IOException ex) {
                throw new ApplicationException(ex.getMessage());
            }
    }
    
    private void setWeights(String version) {
        if (version.endsWith("a")) {
            SpecificationConstants.BASE_WEIGHT = 0.5;
            SpecificationConstants.MISMATCH_WEIGHT = 0.5;
            SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.BASE_WEIGHT + SpecificationConstants.MISMATCH_WEIGHT;
            SpecificationConstants.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else if (version.endsWith("b")) {
            SpecificationConstants.BASE_WEIGHT = 0.6;
            SpecificationConstants.MISMATCH_WEIGHT = 0.4;
            SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.BASE_WEIGHT + SpecificationConstants.MISMATCH_WEIGHT;
            SpecificationConstants.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else if (version.endsWith("c")) {
            SpecificationConstants.BASE_WEIGHT = 0.7;
            SpecificationConstants.MISMATCH_WEIGHT = 0.3;
            SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.BASE_WEIGHT + SpecificationConstants.MISMATCH_WEIGHT;
            SpecificationConstants.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else if (version.endsWith("d")) {
            SpecificationConstants.BASE_WEIGHT = 0.8;
            SpecificationConstants.MISMATCH_WEIGHT = 0.2;
            SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.BASE_WEIGHT + SpecificationConstants.MISMATCH_WEIGHT;
            SpecificationConstants.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else {
            SpecificationConstants.BASE_WEIGHT = 0.7;
            SpecificationConstants.MISMATCH_WEIGHT = 0.3;
            SpecificationConstants.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.BASE_WEIGHT + SpecificationConstants.MISMATCH_WEIGHT;
            SpecificationConstants.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        }
    }    
}
