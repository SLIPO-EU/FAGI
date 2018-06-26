package gr.athena.innovation.fagi.evaluation;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.IOException;

/**
 *
 * @author nkarag
 */
public class Evaluation {
    
    public void run(Configuration fusionSpec, String csvPath){

            //on version change, all weights update (along with notes)
            String version = "v2.2a";
            String evaluationPath = "";
            String resultsPath = evaluationPath + version + "/";
            String nameMetrics = "name_metrics_" + version + ".csv";
            String nameSimilarities = "name_similarities_" + version + ".txt";
            String thresholds = "optimalThresholds_" + version + ".txt";

            setWeights(version);

            String baseW = SpecificationConstants.Evaluation.BASE_WEIGHT.toString();
            String misW = SpecificationConstants.Evaluation.MISMATCH_WEIGHT.toString();
            String spW = SpecificationConstants.Evaluation.SPECIAL_TERMS_WEIGHT.toString();
            String comW = SpecificationConstants.Evaluation.COMMON_SPECIAL_TERM_WEIGHT.toString();

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
            SpecificationConstants.Evaluation.BASE_WEIGHT = 0.5;
            SpecificationConstants.Evaluation.MISMATCH_WEIGHT = 0.5;
            SpecificationConstants.Evaluation.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.Evaluation.BASE_WEIGHT + SpecificationConstants.Evaluation.MISMATCH_WEIGHT;
            SpecificationConstants.Evaluation.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.Evaluation.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else if (version.endsWith("b")) {
            SpecificationConstants.Evaluation.BASE_WEIGHT = 0.6;
            SpecificationConstants.Evaluation.MISMATCH_WEIGHT = 0.4;
            SpecificationConstants.Evaluation.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.Evaluation.BASE_WEIGHT + SpecificationConstants.Evaluation.MISMATCH_WEIGHT;
            SpecificationConstants.Evaluation.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.Evaluation.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else if (version.endsWith("c")) {
            SpecificationConstants.Evaluation.BASE_WEIGHT = 0.7;
            SpecificationConstants.Evaluation.MISMATCH_WEIGHT = 0.3;
            SpecificationConstants.Evaluation.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.Evaluation.BASE_WEIGHT + SpecificationConstants.Evaluation.MISMATCH_WEIGHT;
            SpecificationConstants.Evaluation.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.Evaluation.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else if (version.endsWith("d")) {
            SpecificationConstants.Evaluation.BASE_WEIGHT = 0.8;
            SpecificationConstants.Evaluation.MISMATCH_WEIGHT = 0.2;
            SpecificationConstants.Evaluation.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.Evaluation.BASE_WEIGHT + SpecificationConstants.Evaluation.MISMATCH_WEIGHT;
            SpecificationConstants.Evaluation.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.Evaluation.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        } else {
            SpecificationConstants.Evaluation.BASE_WEIGHT = 0.7;
            SpecificationConstants.Evaluation.MISMATCH_WEIGHT = 0.3;
            SpecificationConstants.Evaluation.MERGED_BASE_MISMATCH_WEIGHT
                    = SpecificationConstants.Evaluation.BASE_WEIGHT + SpecificationConstants.Evaluation.MISMATCH_WEIGHT;
            SpecificationConstants.Evaluation.SPECIAL_TERMS_WEIGHT = 0.0;
            SpecificationConstants.Evaluation.COMMON_SPECIAL_TERM_WEIGHT = 0.0;
        }
    }    
}
