package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.Cosine;
import gr.athena.innovation.fagi.core.similarity.Jaccard;
import gr.athena.innovation.fagi.core.similarity.Jaro;
import gr.athena.innovation.fagi.core.similarity.JaroWinkler;
import gr.athena.innovation.fagi.core.similarity.Levenshtein;
import gr.athena.innovation.fagi.core.similarity.LongestCommonSubsequenceMetric;
import gr.athena.innovation.fagi.core.similarity.NGram;
import gr.athena.innovation.fagi.core.similarity.SortedJaroWinkler;
import gr.athena.innovation.fagi.core.similarity.WeightedSimilarity;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for producing similarity scores of rdf properties between linked entities.
 *
 * @author nkarag
 */
public class QualityProcessor {

    private static final Logger logger = LogManager.getLogger(QualityProcessor.class);

    private final FusionSpecification fusionSpecification;
    
    private static final String ACCEPT = "ACCEPT";
    
    private static final String REJECT = "REJECT";
    
    private int totalRows = 0;
    
    double optimalThresholdLeven;
    
    private Map<String, List<Double>> aLevenPrecisionMap = new HashMap<>();
    //private List<Double> blevenList = new ArrayList<>();
    //private List<Double> clevenList = new ArrayList<>();

    public QualityProcessor(FusionSpecification fusionSpecification) {

        this.fusionSpecification = fusionSpecification;
        
        List<Double> init1 = new ArrayList<>();
        List<Double> init2 = new ArrayList<>();
        aLevenPrecisionMap.put(ACCEPT, init1);
        aLevenPrecisionMap.put(REJECT, init2);
    }

    public void executeEvaluation(String csvPath, String resultsPath, String propertyName) throws FileNotFoundException, IOException {

        String line;
        String cvsSplitBy = "\\^";
        Locale locale = fusionSpecification.getLocale();

        String propertyPath = resultsPath + propertyName + ".txt";
        File file = new File(propertyPath);

        if (file.exists()) {
            //clear contents
            PrintWriter pw = new PrintWriter(propertyPath);
            pw.close();
        } else {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }     

        int l = 0;
        BufferedReader br = new BufferedReader(new FileReader(csvPath));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            
            double[] thresholds = 
                {0.05, 0.1, 0.15, 0.2, 0.25, 0.30, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95};
            
            for (int i = 0; i < thresholds.length; i++) {

                aLevenPrecisionMap.get(ACCEPT).clear();
                aLevenPrecisionMap.get(REJECT).clear();
                
                double thres = thresholds[i];
                
                Accuracy initialAccuracy = new Accuracy();
                Accuracy basicNormAccuracy = new Accuracy();
                Accuracy advancedNormAccuracy = new Accuracy();
                
                executeThreshold(writer, csvPath, resultsPath, propertyName, thres, initialAccuracy, basicNormAccuracy, advancedNormAccuracy);
            }
        }
    }

    private void executeThreshold(BufferedWriter writer, String csvPath, String resultsPath, String propertyName, 
            double threshold, Accuracy initialAccuracy, Accuracy basicNormAccuracy, 
            Accuracy advancedNormAccuracy) throws FileNotFoundException, IOException{
        
        totalRows = 0;
                
        String line;
        String cvsSplitBy = "\\^";
        Locale locale = fusionSpecification.getLocale();

        int l = 0;
        BufferedReader br = new BufferedReader(new FileReader(csvPath));

        try {
        
            l = 0;
            while ((line = br.readLine()) != null) {

                //skip first two lines of csv
                if (l < 2) {
                    l++;
                    continue;
                }

                // use comma as separator
                String[] spl = line.split(cvsSplitBy);

                //StringBuffer sb = new StringBuffer("");
                if (spl.length < 22) {
                    continue;
                }
                String idA = spl[0];
                String idB = spl[1];

                //String distanceMeters = spl[2];

                String nameA = spl[3];
                String nameB = spl[4];
                //String nameFusionAction = spl[5];

                String streetA = spl[6];
                String streetB = spl[7];
                //String streetFusionAction = spl[8];

                String streetNumberA = spl[9];
                String streetNumberB = spl[10];

                String phoneA = spl[11];
                String phoneB = spl[12];
                //String phoneFusionAction = spl[13];

                String emailA = spl[14];
                String emailB = spl[15];
                //String emailFusionAction = spl[16];

                String websiteA = spl[17];
                String websiteB = spl[18];
                //String websiteFusionAction = spl[19];

                //String score = spl[20];
                //String names1 = spl[21];
                String acceptance = spl[22];

                computePairOutputResult(idA, idB, nameA, nameB, locale, acceptance, threshold, 
                        initialAccuracy, basicNormAccuracy, advancedNormAccuracy);

                l++;
            }
            
            int levenPrecisionAcceptCounter = 0;
            int levenPrecisionRejectCounter = 0;
            
            List<Double> acceptedLeven = aLevenPrecisionMap.get(ACCEPT);
            List<Double> rejectedLeven = aLevenPrecisionMap.get(REJECT);
            for(Double elem : acceptedLeven){
                if(elem > threshold){
                    levenPrecisionAcceptCounter++;
                }
            }

            for(Double elem : rejectedLeven){
                if(elem > threshold){
                    levenPrecisionRejectCounter++;
                }
            }
            
            double levenAccuracy = initialAccuracy.getLevenshteinCount() / (double)totalRows;
            double levenPrecision;
            double levenRecall;
            double harmonicMean;
            if(levenPrecisionAcceptCounter == 0 && levenPrecisionRejectCounter == 0){
                levenPrecision = 0;
                levenRecall = 0;
                harmonicMean = 0;
            } else {
                levenPrecision = levenPrecisionAcceptCounter / (double)(levenPrecisionAcceptCounter + levenPrecisionRejectCounter);
                levenRecall = levenPrecisionAcceptCounter / (double)aLevenPrecisionMap.get(ACCEPT).size();
                harmonicMean = 2 * levenPrecision * levenRecall / (double)(levenPrecision + levenRecall); 
            }

            double precision = 0;
            double recall = 0;

            double basicPrecision = 0;
            double basicRecall = 0;

            double advancedPrecision = 0;
            double advancedRecall = 0;
            
            String initialScores = "### Threshold: " + threshold + ", Total: " + totalRows + ", (Accuracy, Precision, Recall, harmonicMean)"
 
                + " \n\tLevenstein_a" + threshold + "           :" + levenAccuracy + " " + levenPrecision +  " " + levenRecall + " " + harmonicMean
                + " \n\t2Gram_a" + threshold + "                :" + initialAccuracy.getNgram2Count() + " " + precision +  " " + recall
                + " \n\tCosine_a" + threshold + "               :" + initialAccuracy.getCosineCount() + " " + precision +  " " + recall
                + " \n\tLongestCommonSubseq_a" + threshold + "  :" + initialAccuracy.getLqsCount() + " " + precision +  " " + recall
                + " \n\tJaccard_a" + threshold + "              :" + initialAccuracy.getJaccardCount() + " " + precision +  " " + recall
                + " \n\tJaro_a" + threshold + "                 :" + initialAccuracy.getJaroCount() + " " + precision +  " " + recall
                + " \n\tJaroWinkler_a" + threshold + "          :" + initialAccuracy.getJaroWinklerCount() + " " + precision +  " " + recall
                + " \n\tSortedJaroWinkler_a" + threshold + "    :" + initialAccuracy.getJaroWinklerSortedCount() + " " + precision +  " " + recall;
            
            writer.append(initialScores);
            writer.newLine();
            
            String basicNormScores = 
                  " \n\tLevenstein_b" + threshold + "           :" + basicNormAccuracy.getLevenshteinCount() + " " + basicPrecision +  " " + basicRecall 
                + " \n\t2Gram_b" + threshold + "                :" + basicNormAccuracy.getNgram2Count() + " " + basicPrecision +  " " + basicRecall
                + " \n\tCosine_b" + threshold + "               :" + basicNormAccuracy.getCosineCount() + " " + basicPrecision +  " " + basicRecall 
                + " \n\tLongestCommonSubseq_b" + threshold + "  :" + basicNormAccuracy.getLqsCount() + " " + basicPrecision +  " " + basicRecall 
                + " \n\tJaccard_b" + threshold + "              :" + basicNormAccuracy.getJaccardCount() + " " + basicPrecision +  " " + basicRecall 
                + " \n\tJaro_b" + threshold + "                 :" + basicNormAccuracy.getJaroCount() + " " + basicPrecision +  " " + basicRecall 
                + " \n\tJaroWinkler_b" + threshold + "          :" + basicNormAccuracy.getJaroWinklerCount() + " " + basicPrecision +  " " + basicRecall 
                + " \n\tSortedJaroWinkler_b" + threshold + "    :" + basicNormAccuracy.getJaroWinklerSortedCount() + " " + basicPrecision +  " " + basicRecall ;            

            writer.append(basicNormScores);
            writer.newLine();

            String advancedNormScores = 
                  " \n\tLevenstein_c" + threshold + "           :" + advancedNormAccuracy.getLevenshteinCount() + " " + advancedPrecision +  " " + advancedRecall  
                + " \n\t2Gram_c" + threshold + "                :" + advancedNormAccuracy.getNgram2Count() + " " + advancedPrecision +  " " + advancedRecall 
                + " \n\tCosine_c" + threshold + "               :" + advancedNormAccuracy.getCosineCount() + " " + advancedPrecision +  " " + advancedRecall 
                + " \n\tLongestCommonSubseq_c" + threshold + "  :" + advancedNormAccuracy.getLqsCount() + " " + advancedPrecision +  " " + advancedRecall 
                + " \n\tJaccard_c" + threshold + "              :" + advancedNormAccuracy.getJaccardCount() + " " + advancedPrecision +  " " + advancedRecall         
                + " \n\tJaro_c" + threshold + "                 :" + advancedNormAccuracy.getJaroCount() + " " + advancedPrecision +  " " + advancedRecall 
                + " \n\tJaroWinkler_c" + threshold + "          :" + advancedNormAccuracy.getJaroWinklerCount() + " " + advancedPrecision +  " " + advancedRecall 
                + " \n\tSortedJaroWinkle_c" + threshold + "     :" + advancedNormAccuracy.getJaroWinklerSortedCount() + " " + advancedPrecision +  " " + advancedRecall ;            

            writer.append(advancedNormScores);
            writer.newLine();
            writer.newLine();
            
        } catch(IOException | RuntimeException ex){
            writer.flush();
            writer.close();
            
            throw new RuntimeException();
        }
        logger.info("Total lines: " + l);        
    }

    private void computePairOutputResult(String idA, String idB, String valueA, String valueB, 
            Locale locale, String acceptance, double threshold, Accuracy accuracy, 
            Accuracy basicNormAccuracy, Accuracy advancedNormAccuracy) {
        
        totalRows++;
        
        NormalizedLiteral basicA = getBasicNormalization(valueA, valueB, locale);
        NormalizedLiteral basicB = getBasicNormalization(valueB, valueA, locale);
        
        WeightedPairLiteral normalizedPair = getAdvancedNormalization(basicA, basicB, locale);
        
        String basicNormA = basicA.getNormalized();
        String basicNormB = basicB.getNormalized();
        
        double leven = Levenshtein.computeSimilarity(valueA, valueB, null);
        double ngram2 = NGram.computeSimilarity(valueA, valueB, 2);
        double cosine = Cosine.computeSimilarity(valueA, valueB);
        double lqs = LongestCommonSubsequenceMetric.computeSimilarity(valueA, valueB);
        double jac = Jaccard.computeSimilarity(valueA, valueB);
        double jaro = Jaro.computeSimilarity(valueA, valueB);
        double jaroWinkler = JaroWinkler.computeSimilarity(valueA, valueB);
        double jaroWinklerSorted = SortedJaroWinkler.computeSimilarity(valueA, valueB);

        if(acceptance.equals(ACCEPT)){
            aLevenPrecisionMap.get(ACCEPT).add(leven);
        } else {
            aLevenPrecisionMap.get(REJECT).add(leven);
        }

        double basicLeven = Levenshtein.computeSimilarity(basicNormA, basicNormB, null);
        double basicNGram2 = NGram.computeSimilarity(basicNormA, basicNormB, 2);
        double basicCosine = Cosine.computeSimilarity(basicNormA, basicNormB);
        double basicLqs = LongestCommonSubsequenceMetric.computeSimilarity(basicNormA, basicNormB);
        double basicJac = Jaccard.computeSimilarity(basicNormA, basicNormB);
        double basicJaro = Jaro.computeSimilarity(basicNormA, basicNormB);
        double basicJaroWinkler = JaroWinkler.computeSimilarity(basicNormA, basicNormB);
        double basicJaroWinklerSorted = SortedJaroWinkler.computeSimilarity(basicNormA, basicNormB);
        
        //blevenList.add(leven);
                
        double advancedLeven = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "levenshtein");
        double advancedNGram2 = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "2Gram");
        double advancedCosine = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "cosine");
        double advancedLqs = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "longestcommonsubsequence");
        double advancedJac = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jaccard");
        double advancedJaro = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jaro");
        double advancedJaroWinkler = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jarowinkler");
        double advancedJaroWinklerSorted = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "sortedjarowinkler");
        
        //clevenList.add(leven);
                
        if((leven > threshold && acceptance.equals(ACCEPT)) || leven < threshold && acceptance.equals(REJECT)){
            accuracy.setLevenshteinCount(accuracy.getLevenshteinCount() + 1);
        }

        if((ngram2 > threshold && acceptance.equals(ACCEPT)) || ngram2 < threshold && acceptance.equals(REJECT)){
            accuracy.setNgram2Count(accuracy.getNgram2Count() + 1);
        } 

        if((cosine > threshold && acceptance.equals(ACCEPT)) || cosine < threshold && acceptance.equals(REJECT)){
            accuracy.setCosineCount(accuracy.getCosineCount() + 1);
        } 

        if((lqs > threshold && acceptance.equals(ACCEPT)) || lqs < threshold && acceptance.equals(REJECT)){
            accuracy.setLqsCount(accuracy.getLqsCount() + 1);
        } 

        if((jac > threshold && acceptance.equals(ACCEPT)) || jac < threshold && acceptance.equals(REJECT)){
            accuracy.setJaccardCount(accuracy.getJaccardCount() + 1);
        }
        
        if((jaro > threshold && acceptance.equals(ACCEPT)) || jaro < threshold && acceptance.equals(REJECT)){
            accuracy.setJaroCount(accuracy.getJaroCount() + 1);
        } 

        if((jaroWinkler > threshold && acceptance.equals(ACCEPT)) || jaroWinkler < threshold && acceptance.equals(REJECT)){
            accuracy.setJaroWinklerCount(accuracy.getJaroWinklerCount() + 1);
        } 

        if((jaroWinklerSorted > threshold && acceptance.equals(ACCEPT)) || jaroWinklerSorted < threshold && acceptance.equals(REJECT)){
            accuracy.setJaroWinklerSortedCount(accuracy.getJaroWinklerSortedCount() + 1);
        } 
        
        
        
        //basic norm
        
        if((basicLeven > threshold && acceptance.equals(ACCEPT)) || basicLeven < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setLevenshteinCount(basicNormAccuracy.getLevenshteinCount() + 1);
        }

        if((basicNGram2 > threshold && acceptance.equals(ACCEPT)) || basicNGram2 < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setNgram2Count(basicNormAccuracy.getNgram2Count() + 1);
        } 

        if((basicCosine > threshold && acceptance.equals(ACCEPT)) || basicCosine < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setCosineCount(basicNormAccuracy.getCosineCount() + 1);
        } 

        if((basicLqs > threshold && acceptance.equals(ACCEPT)) || basicLqs < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setLqsCount(basicNormAccuracy.getLqsCount() + 1);
        } 
        
        if((basicJac > threshold && acceptance.equals(ACCEPT)) || basicJac < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setJaccardCount(basicNormAccuracy.getJaccardCount() + 1);
        }
        
        if((basicJaro > threshold && acceptance.equals(ACCEPT)) || basicJaro < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setJaroCount(basicNormAccuracy.getJaroCount() + 1);
        } 

        if((basicJaroWinkler > threshold && acceptance.equals(ACCEPT)) || basicJaroWinkler < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setJaroWinklerCount(basicNormAccuracy.getJaroWinklerCount() + 1);
        } 

        if((basicJaroWinklerSorted > threshold && acceptance.equals(ACCEPT)) || basicJaroWinklerSorted < threshold && acceptance.equals(REJECT)){
            basicNormAccuracy.setJaroWinklerSortedCount(basicNormAccuracy.getJaroWinklerSortedCount() + 1);
        } 
        
        
        
        //advanced norm
        
        if((advancedLeven > threshold && acceptance.equals(ACCEPT)) || advancedLeven < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setLevenshteinCount(advancedNormAccuracy.getLevenshteinCount() + 1);
        }

        if((advancedNGram2 > threshold && acceptance.equals(ACCEPT)) || advancedNGram2 < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setNgram2Count(advancedNormAccuracy.getNgram2Count() + 1);
        } 

        if((advancedCosine > threshold && acceptance.equals(ACCEPT)) || advancedCosine < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setCosineCount(advancedNormAccuracy.getCosineCount() + 1);
        } 

        if((advancedLqs > threshold && acceptance.equals(ACCEPT)) || advancedLqs < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setLqsCount(advancedNormAccuracy.getLqsCount() + 1);
        } 
        
        if((advancedJac > threshold && acceptance.equals(ACCEPT)) || advancedJac < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setJaccardCount(advancedNormAccuracy.getJaccardCount() + 1);
        }
        
        if((advancedJaro > threshold && acceptance.equals(ACCEPT)) || advancedJaro < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setJaroCount(advancedNormAccuracy.getJaroCount() + 1);
        } 

        if((advancedJaroWinkler > threshold && acceptance.equals(ACCEPT)) || advancedJaroWinkler < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setJaroWinklerCount(advancedNormAccuracy.getJaroWinklerCount() + 1);
        } 

        if((advancedJaroWinklerSorted > threshold && acceptance.equals(ACCEPT)) || advancedJaroWinklerSorted < threshold && acceptance.equals(REJECT)){
            advancedNormAccuracy.setJaroWinklerSortedCount(advancedNormAccuracy.getJaroWinklerSortedCount() + 1);
        } 
    }
    
    private String getPropertyLine(String idA, String idB, String propertyA, String propertyB, 
            Locale locale, String acceptance) {
        
        NormalizedLiteral basicA = getBasicNormalization(propertyA, propertyB, locale);
        NormalizedLiteral basicB = getBasicNormalization(propertyB, propertyA, locale);

        WeightedPairLiteral normalizedPair = getAdvancedNormalization(basicA, basicB, locale);
        
        String namesLine = "id_a: " + idA + " id_b: " + idB + " property: Name"
            + " \nOriginal values: " + propertyA + " <--> " + propertyB
            + " \n\tLevenstein           :" + Levenshtein.computeSimilarity(propertyA, propertyB, null)
            + " \n\t2Gram                :" + NGram.computeSimilarity(propertyA, propertyB, 2)
            + " \n\tCosine               :" + Cosine.computeSimilarity(propertyA, propertyB)
            + " \n\tLongestCommonSubseq  :" + LongestCommonSubsequenceMetric.computeSimilarity(propertyA, propertyB)
            + " \n\tJaro                 :" + Jaro.computeSimilarity(propertyA, propertyB)
            + " \n\tJaroWinkler          :" + JaroWinkler.computeSimilarity(propertyA, propertyB)
            + " \n\tSortedJaroWinkler    :" + SortedJaroWinkler.computeSimilarity(propertyA, propertyB)
            //+ " \nPermJaroWinkler      :" + per.computeDistance(literalA, literalB) //too slow                        

            + " \nSimple normalization: " + basicA.getNormalized() + " <--> " + basicB.getNormalized()
            + " \n\tLevenstein           :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "levenshtein")
            + " \n\t2Gram                :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "2Gram")
            + " \n\tCosine               :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "cosine")
            + " \n\tLongestCommonSubseq  :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "longestcommonsubsequence")
            + " \n\tJaro                 :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "jaro")
            + " \n\tJaroWinkler          :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "jarowinkler")
            + " \n\tSortedJaroWinkler    :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "sortedjarowinkler")

            + " \nCustom normalization full: " + normalizedPair.getCompleteA() + " <--> " + normalizedPair.getCompleteB()
            + " \nBase: " + normalizedPair.getBaseValueA() + " <--> " + normalizedPair.getBaseValueB()
            + " \nMismatch: " + normalizedPair.mismatchToStringA() + " <--> " + normalizedPair.mismatchToStringB()
            + " \nSpecial terms: " + normalizedPair.specialTermsToStringA() + " <--> " + normalizedPair.specialTermsToStringB()
            + " \nCommon terms: " + normalizedPair.commonTermsToString()
            + " \n\tLevenstein           :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "levenshtein")
            + " \n\t2Gram                :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "2Gram")
            + " \n\tCosine               :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "cosine")
            + " \n\tLongestCommonSubseq  :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "longestcommonsubsequence")
            + " \n\tJaro                 :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jaro")
            + " \n\tJaroWinkler          :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jarowinkler")
            + " \n\tSortedJaroWinkler    :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "sortedjarowinkler")                

            + " \n" + acceptance + "\n";
        return namesLine;
    }
    
    private NormalizedLiteral getBasicNormalization(String literalA, String literalB, Locale locale) {
        BasicGenericNormalizer bgn = new BasicGenericNormalizer();
        NormalizedLiteral normalizedLiteral = bgn.getNormalizedLiteral(literalA, literalB, locale);
        return normalizedLiteral;
    }

    private WeightedPairLiteral getAdvancedNormalization(NormalizedLiteral normA, NormalizedLiteral normB, Locale locale) {
        AdvancedGenericNormalizer agn = new AdvancedGenericNormalizer();
        WeightedPairLiteral weightedPairLiteral = agn.getWeightedPair(normA, normB, locale);
        return weightedPairLiteral;
    }
    
    private Model constructEntityMetadataModel(String node, Model sourceModel, int depth) {

        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct();

        return model;
    }
}
