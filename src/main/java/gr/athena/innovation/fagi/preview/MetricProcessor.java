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
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for calculation of accuracy, precision, recall and harmonic mean for linked pairs and acceptance label.
 *
 * @author nkarag
 */
public class MetricProcessor {

    private static final Logger logger = LogManager.getLogger(MetricProcessor.class);
    private static final String SEP = " ";
    private final FusionSpecification fusionSpecification;
    private static final String ACCEPT = "ACCEPT";
    private static final String REJECT = "REJECT";
    private int totalRows = 0;
    double optimalThresholdLeven;
    
    private Map<String, List<Double>> aLevenPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bLevenPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cLevenPrecisionMap = new HashMap<>();
    
    private Map<String, List<Double>> aNGramPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bNGramPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cNGramPrecisionMap = new HashMap<>();    

    private Map<String, List<Double>> aCosinePrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bCosinePrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cCosinePrecisionMap = new HashMap<>(); 
    
    private Map<String, List<Double>> aLqsPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> bLqsPrecisionMap = new HashMap<>();
    private Map<String, List<Double>> cLqsPrecisionMap = new HashMap<>();      
    
    public MetricProcessor(FusionSpecification fusionSpecification) {

        this.fusionSpecification = fusionSpecification;
        
        aLevenPrecisionMap.put(ACCEPT, new ArrayList<>());
        aLevenPrecisionMap.put(REJECT, new ArrayList<>());
        bLevenPrecisionMap.put(ACCEPT, new ArrayList<>());
        bLevenPrecisionMap.put(REJECT, new ArrayList<>());
        cLevenPrecisionMap.put(ACCEPT, new ArrayList<>());
        cLevenPrecisionMap.put(REJECT, new ArrayList<>());
        
        aNGramPrecisionMap.put(ACCEPT, new ArrayList<>());
        aNGramPrecisionMap.put(REJECT, new ArrayList<>());
        bNGramPrecisionMap.put(ACCEPT, new ArrayList<>());
        bNGramPrecisionMap.put(REJECT, new ArrayList<>());
        cNGramPrecisionMap.put(ACCEPT, new ArrayList<>());
        cNGramPrecisionMap.put(REJECT, new ArrayList<>());
        
        aCosinePrecisionMap.put(ACCEPT, new ArrayList<>());
        aCosinePrecisionMap.put(REJECT, new ArrayList<>());
        bCosinePrecisionMap.put(ACCEPT, new ArrayList<>());
        bCosinePrecisionMap.put(REJECT, new ArrayList<>());
        cCosinePrecisionMap.put(ACCEPT, new ArrayList<>());
        cCosinePrecisionMap.put(REJECT, new ArrayList<>());
        
        aLqsPrecisionMap.put(ACCEPT, new ArrayList<>());
        aLqsPrecisionMap.put(REJECT, new ArrayList<>());
        bLqsPrecisionMap.put(ACCEPT, new ArrayList<>());
        bLqsPrecisionMap.put(REJECT, new ArrayList<>());
        cLqsPrecisionMap.put(ACCEPT, new ArrayList<>());
        cLqsPrecisionMap.put(REJECT, new ArrayList<>());        
        
    }

    public void executeEvaluation(String csvPath, String resultsPath, String propertyName) 
            throws FileNotFoundException, IOException {

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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            
            double[] thresholds = 
                {0.05, 0.1, 0.15, 0.2, 0.25, 0.30, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95};
            
            for (int i = 0; i < thresholds.length; i++) {

                clearPrecisionMapLists(aLevenPrecisionMap);
                clearPrecisionMapLists(bLevenPrecisionMap);
                clearPrecisionMapLists(cLevenPrecisionMap);
                
                clearPrecisionMapLists(aNGramPrecisionMap);
                clearPrecisionMapLists(bNGramPrecisionMap);
                clearPrecisionMapLists(cNGramPrecisionMap);

                clearPrecisionMapLists(aCosinePrecisionMap);
                clearPrecisionMapLists(bCosinePrecisionMap);
                clearPrecisionMapLists(cCosinePrecisionMap);

                clearPrecisionMapLists(aLqsPrecisionMap);
                clearPrecisionMapLists(bLqsPrecisionMap);
                clearPrecisionMapLists(cLqsPrecisionMap);
                
                double thres = thresholds[i];
                
                Accuracy aAccuracy = new Accuracy();
                Accuracy bAccuracy = new Accuracy();
                Accuracy cAccuracy = new Accuracy();
                
                executeThreshold(writer, csvPath, thres, aAccuracy, bAccuracy, cAccuracy);
            }
        }
    }

    private void executeThreshold(BufferedWriter writer, String csvPath, double threshold, 
            Accuracy aAccuracy, Accuracy bAccuracy, Accuracy cAccuracy) throws FileNotFoundException, IOException{
        
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

                computePairOutputResult(nameA, nameB, locale, acceptance, threshold, aAccuracy, bAccuracy, cAccuracy);

                l++;
            }         
            
            int aLevenPrecisionAcceptCounter = count(aLevenPrecisionMap.get(ACCEPT), threshold);
            int aLevenPrecisionRejectCounter = count(aLevenPrecisionMap.get(REJECT), threshold);
            int bLevenPrecisionAcceptCounter = count(bLevenPrecisionMap.get(ACCEPT), threshold);
            int bLevenPrecisionRejectCounter = count(bLevenPrecisionMap.get(REJECT), threshold);
            int cLevenPrecisionAcceptCounter = count(cLevenPrecisionMap.get(ACCEPT), threshold);
            int cLevenPrecisionRejectCounter = count(cLevenPrecisionMap.get(REJECT), threshold);

            int aNGramPrecisionAcceptCounter = count(aNGramPrecisionMap.get(ACCEPT), threshold);
            int aNGramPrecisionRejectCounter = count(aNGramPrecisionMap.get(REJECT), threshold);
            int bNGramPrecisionAcceptCounter = count(bNGramPrecisionMap.get(ACCEPT), threshold);
            int bNGramPrecisionRejectCounter = count(bNGramPrecisionMap.get(REJECT), threshold);
            int cNGramPrecisionAcceptCounter = count(cNGramPrecisionMap.get(ACCEPT), threshold);
            int cNGramPrecisionRejectCounter = count(cNGramPrecisionMap.get(REJECT), threshold);

            int aCosinePrecisionAcceptCounter = count(aCosinePrecisionMap.get(ACCEPT), threshold);
            int aCosinePrecisionRejectCounter = count(aCosinePrecisionMap.get(REJECT), threshold);
            int bCosinePrecisionAcceptCounter = count(bCosinePrecisionMap.get(ACCEPT), threshold);
            int bCosinePrecisionRejectCounter = count(bCosinePrecisionMap.get(REJECT), threshold);
            int cCosinePrecisionAcceptCounter = count(cCosinePrecisionMap.get(ACCEPT), threshold);
            int cCosinePrecisionRejectCounter = count(cCosinePrecisionMap.get(REJECT), threshold);

            int aLqsPrecisionAcceptCounter = count(aLqsPrecisionMap.get(ACCEPT), threshold);
            int aLqsPrecisionRejectCounter = count(aLqsPrecisionMap.get(REJECT), threshold);
            int bLqsPrecisionAcceptCounter = count(bLqsPrecisionMap.get(ACCEPT), threshold);
            int bLqsPrecisionRejectCounter = count(bLqsPrecisionMap.get(REJECT), threshold);
            int cLqsPrecisionAcceptCounter = count(cLqsPrecisionMap.get(ACCEPT), threshold);
            int cLqsPrecisionRejectCounter = count(cLqsPrecisionMap.get(REJECT), threshold);
            
            double aLevenAccuracy = calculateAccuracy(aAccuracy.getLevenshteinCount(), totalRows);
            double aLevenPrecision = calculatePrecision(aLevenPrecisionAcceptCounter, aLevenPrecisionRejectCounter);
            double aLevenRecall= calculateRecall(aLevenPrecisionAcceptCounter, aLevenPrecisionMap);
            double aLevenHarmonicMean = calculateHarmonicMean(aLevenPrecision, aLevenRecall);

            double bLevenAccuracy = calculateAccuracy(bAccuracy.getLevenshteinCount(), totalRows);
            double bLevenPrecision = calculatePrecision(bLevenPrecisionAcceptCounter, bLevenPrecisionRejectCounter);
            double bLevenRecall= calculateRecall(bLevenPrecisionAcceptCounter, bLevenPrecisionMap);
            double bLevenHarmonicMean = calculateHarmonicMean(bLevenPrecision, bLevenRecall);

            double cLevenAccuracy = calculateAccuracy(cAccuracy.getLevenshteinCount(), totalRows);
            double cLevenPrecision = calculatePrecision(cLevenPrecisionAcceptCounter, cLevenPrecisionRejectCounter);
            double cLevenRecall= calculateRecall(cLevenPrecisionAcceptCounter, cLevenPrecisionMap);
            double cLevenHarmonicMean = calculateHarmonicMean(cLevenPrecision, cLevenRecall);

            double aNGramAccuracy = calculateAccuracy(aAccuracy.getNgram2Count(), totalRows);
            double aNGramPrecision = calculatePrecision(aNGramPrecisionAcceptCounter, aNGramPrecisionRejectCounter);
            double aNGramRecall= calculateRecall(aNGramPrecisionAcceptCounter, aNGramPrecisionMap);
            double aNGramHarmonicMean = calculateHarmonicMean(aNGramPrecision, aNGramRecall);

            double bNGramAccuracy = calculateAccuracy(bAccuracy.getNgram2Count(), totalRows);
            double bNGramPrecision = calculatePrecision(bNGramPrecisionAcceptCounter, bNGramPrecisionRejectCounter);
            double bNGramRecall= calculateRecall(bNGramPrecisionAcceptCounter, bNGramPrecisionMap);
            double bNGramHarmonicMean = calculateHarmonicMean(bNGramPrecision, bNGramRecall);

            double cNGramAccuracy = calculateAccuracy(cAccuracy.getNgram2Count(), totalRows);
            double cNGramPrecision = calculatePrecision(cNGramPrecisionAcceptCounter, cNGramPrecisionRejectCounter);
            double cNGramRecall= calculateRecall(cNGramPrecisionAcceptCounter, cNGramPrecisionMap);
            double cNGramHarmonicMean = calculateHarmonicMean(cNGramPrecision, cNGramRecall);

            double aCosineAccuracy = calculateAccuracy(aAccuracy.getCosineCount(), totalRows);
            double aCosinePrecision = calculatePrecision(aCosinePrecisionAcceptCounter, aCosinePrecisionRejectCounter);
            double aCosineRecall= calculateRecall(aCosinePrecisionAcceptCounter, aCosinePrecisionMap);
            double aCosineHarmonicMean = calculateHarmonicMean(aCosinePrecision, aCosineRecall);

            double bCosineAccuracy = calculateAccuracy(bAccuracy.getCosineCount(), totalRows);
            double bCosinePrecision = calculatePrecision(bCosinePrecisionAcceptCounter, bCosinePrecisionRejectCounter);
            double bCosineRecall= calculateRecall(bCosinePrecisionAcceptCounter, bCosinePrecisionMap);
            double bCosineHarmonicMean = calculateHarmonicMean(bCosinePrecision, bCosineRecall);

            double cCosineAccuracy = calculateAccuracy(cAccuracy.getCosineCount(), totalRows);
            double cCosinePrecision = calculatePrecision(cCosinePrecisionAcceptCounter, cCosinePrecisionRejectCounter);
            double cCosineRecall= calculateRecall(cCosinePrecisionAcceptCounter, cCosinePrecisionMap);
            double cCosineHarmonicMean = calculateHarmonicMean(cCosinePrecision, cCosineRecall);

            double aLqsAccuracy = calculateAccuracy(aAccuracy.getLqsCount(), totalRows);
            double aLqsPrecision = calculatePrecision(aLqsPrecisionAcceptCounter, aLqsPrecisionRejectCounter);
            double aLqsRecall= calculateRecall(aLqsPrecisionAcceptCounter, aLqsPrecisionMap);
            double aLqsHarmonicMean = calculateHarmonicMean(aLqsPrecision, aLqsRecall);

            double bLqsAccuracy = calculateAccuracy(bAccuracy.getLqsCount(), totalRows);
            double bLqsPrecision = calculatePrecision(bLqsPrecisionAcceptCounter, bLqsPrecisionRejectCounter);
            double bLqsRecall= calculateRecall(bLqsPrecisionAcceptCounter, bLqsPrecisionMap);
            double bLqsHarmonicMean = calculateHarmonicMean(bLqsPrecision, bLqsRecall);

            double cLqsAccuracy = calculateAccuracy(cAccuracy.getLqsCount(), totalRows);
            double cLqsPrecision = calculatePrecision(cLqsPrecisionAcceptCounter, cLqsPrecisionRejectCounter);
            double cLqsRecall= calculateRecall(cLqsPrecisionAcceptCounter, cLqsPrecisionMap);
            double cLqsHarmonicMean = calculateHarmonicMean(cLqsPrecision, cLqsRecall);
            
            double precision = 0;
            double recall = 0;

            double basicPrecision = 0;
            double basicRecall = 0;

            double advancedPrecision = 0;
            double advancedRecall = 0;

            String initialScores = "### Threshold: " + threshold + ", Total: " + totalRows + ", (Accuracy, Precision, Recall, harmonicMean)"
 
                + " \n\tLevenstein_a" + threshold + "           :" + aLevenAccuracy + SEP + aLevenPrecision +  SEP + aLevenRecall + SEP + aLevenHarmonicMean
                + " \n\t2Gram_a" + threshold + "                :" + aNGramAccuracy + SEP + aNGramPrecision +  SEP + aNGramRecall + SEP + aNGramHarmonicMean
                + " \n\tCosine_a" + threshold + "               :" + aCosineAccuracy + SEP + aCosinePrecision +  SEP + aCosineRecall + SEP + aCosineHarmonicMean
                + " \n\tLongestCommonSubseq_a" + threshold + "  :" + aLqsAccuracy + SEP + aLqsPrecision +  SEP + aLqsRecall + SEP + aLqsHarmonicMean
                + " \n\tJaccard_a" + threshold + "              :" + aAccuracy.getJaccardCount() + SEP + precision +  SEP + recall
                + " \n\tJaro_a" + threshold + "                 :" + aAccuracy.getJaroCount() + SEP + precision +  SEP + recall
                + " \n\tJaroWinkler_a" + threshold + "          :" + aAccuracy.getJaroWinklerCount() + SEP + precision +  SEP + recall
                + " \n\tSortedJaroWinkler_a" + threshold + "    :" + aAccuracy.getJaroWinklerSortedCount() + SEP + precision +  SEP + recall;
            
            writer.append(initialScores);
            writer.newLine();
            
            String basicNormScores = 
                  " \n\tLevenstein_b" + threshold + "           :" + bLevenAccuracy + SEP + bLevenPrecision +  SEP + bLevenRecall + SEP + bLevenHarmonicMean
                + " \n\t2Gram_b" + threshold + "                :" + bNGramAccuracy + SEP + bNGramPrecision +  SEP + bNGramRecall + SEP + bNGramHarmonicMean
                + " \n\tCosine_b" + threshold + "               :" + bCosineAccuracy + SEP + bCosinePrecision +  SEP + bCosineRecall + SEP + bCosineHarmonicMean 
                + " \n\tLongestCommonSubseq_b" + threshold + "  :" + bLqsAccuracy + SEP + bLqsPrecision +  SEP + bLqsRecall + SEP + bLqsHarmonicMean
                + " \n\tJaccard_b" + threshold + "              :" + bAccuracy.getJaccardCount() + SEP + basicPrecision +  SEP + basicRecall 
                + " \n\tJaro_b" + threshold + "                 :" + bAccuracy.getJaroCount() + SEP + basicPrecision +  SEP + basicRecall 
                + " \n\tJaroWinkler_b" + threshold + "          :" + bAccuracy.getJaroWinklerCount() + SEP + basicPrecision +  SEP + basicRecall 
                + " \n\tSortedJaroWinkler_b" + threshold + "    :" + bAccuracy.getJaroWinklerSortedCount() + SEP + basicPrecision +  SEP + basicRecall ;            

            writer.append(basicNormScores);
            writer.newLine();

            String advancedNormScores = 
                  " \n\tLevenstein_c" + threshold + "           :" + cLevenAccuracy + SEP + cLevenPrecision +  SEP + cLevenRecall + SEP + cLevenHarmonicMean
                + " \n\t2Gram_c" + threshold + "                :" + cNGramAccuracy + SEP + cNGramPrecision +  SEP + cNGramRecall + SEP + cNGramHarmonicMean 
                + " \n\tCosine_c" + threshold + "               :" + cCosineAccuracy + SEP + cCosinePrecision +  SEP + cCosineRecall + SEP + cCosineHarmonicMean 
                + " \n\tLongestCommonSubseq_c" + threshold + "  :" + cLqsAccuracy + SEP + cLqsPrecision +  SEP + cLqsRecall + SEP + cLqsHarmonicMean
                + " \n\tJaccard_c" + threshold + "              :" + cAccuracy.getJaccardCount() + SEP + advancedPrecision +  SEP + advancedRecall         
                + " \n\tJaro_c" + threshold + "                 :" + cAccuracy.getJaroCount() + SEP + advancedPrecision +  SEP + advancedRecall 
                + " \n\tJaroWinkler_c" + threshold + "          :" + cAccuracy.getJaroWinklerCount() + SEP + advancedPrecision +  SEP + advancedRecall 
                + " \n\tSortedJaroWinkle_c" + threshold + "     :" + cAccuracy.getJaroWinklerSortedCount() + SEP + advancedPrecision +  SEP + advancedRecall ;            

            writer.append(advancedNormScores);
            writer.newLine();
            writer.newLine();
            
        } catch(IOException | RuntimeException ex){
            
            writer.close();
            throw new ApplicationException(ex.getMessage());
        }
        logger.info("Total lines: " + l);        
    }

    private void computePairOutputResult(String valueA, String valueB, 
            Locale locale, String acceptance, double threshold, Accuracy aAccuracy, 
            Accuracy bAccuracy, Accuracy cAccuracy) {
        
        totalRows++;
        
        NormalizedLiteral basicA = getBasicNormalization(valueA, valueB, locale);
        NormalizedLiteral basicB = getBasicNormalization(valueB, valueA, locale);
        
        WeightedPairLiteral normalizedPair = getAdvancedNormalization(basicA, basicB, locale);
        
        String basicNormA = basicA.getNormalized();
        String basicNormB = basicB.getNormalized();
        
        double aLeven = Levenshtein.computeSimilarity(valueA, valueB, null);
        double aNGram = NGram.computeSimilarity(valueA, valueB, 2);
        double aCosine = Cosine.computeSimilarity(valueA, valueB);
        double aLqs = LongestCommonSubsequenceMetric.computeSimilarity(valueA, valueB);
        double jac = Jaccard.computeSimilarity(valueA, valueB);
        double jaro = Jaro.computeSimilarity(valueA, valueB);
        double jaroWinkler = JaroWinkler.computeSimilarity(valueA, valueB);
        double jaroWinklerSorted = SortedJaroWinkler.computeSimilarity(valueA, valueB);

        constructPrecisionMap(aLevenPrecisionMap, acceptance, aLeven);
        constructPrecisionMap(aNGramPrecisionMap, acceptance, aNGram);
        constructPrecisionMap(aCosinePrecisionMap, acceptance, aCosine);
        constructPrecisionMap(aLqsPrecisionMap, acceptance, aLqs);

        double bLeven = Levenshtein.computeSimilarity(basicNormA, basicNormB, null);
        double bNGram = NGram.computeSimilarity(basicNormA, basicNormB, 2);
        double bCosine = Cosine.computeSimilarity(basicNormA, basicNormB);
        double bLqs = LongestCommonSubsequenceMetric.computeSimilarity(basicNormA, basicNormB);
        double basicJac = Jaccard.computeSimilarity(basicNormA, basicNormB);
        double basicJaro = Jaro.computeSimilarity(basicNormA, basicNormB);
        double basicJaroWinkler = JaroWinkler.computeSimilarity(basicNormA, basicNormB);
        double basicJaroWinklerSorted = SortedJaroWinkler.computeSimilarity(basicNormA, basicNormB);
        
        constructPrecisionMap(bLevenPrecisionMap, acceptance, bLeven);
        constructPrecisionMap(bNGramPrecisionMap, acceptance, bNGram);
        constructPrecisionMap(bCosinePrecisionMap, acceptance, bCosine);
        constructPrecisionMap(bLqsPrecisionMap, acceptance, bLqs);
                
        double cLeven = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "levenshtein");
        double cNGram = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "2Gram");
        double cCosine = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "cosine");
        double cLqs = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "longestcommonsubsequence");
        double advancedJac = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jaccard");
        double advancedJaro = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jaro");
        double advancedJaroWinkler = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jarowinkler");
        double advancedJaroWinklerSorted = WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "sortedjarowinkler");
        
        constructPrecisionMap(cLevenPrecisionMap, acceptance, cLeven);
        constructPrecisionMap(cNGramPrecisionMap, acceptance, cNGram);
        constructPrecisionMap(cCosinePrecisionMap, acceptance, cCosine);
        constructPrecisionMap(cLqsPrecisionMap, acceptance, cLqs);
                
        if((aLeven > threshold && acceptance.equals(ACCEPT)) || aLeven < threshold && acceptance.equals(REJECT)){
            aAccuracy.setLevenshteinCount(aAccuracy.getLevenshteinCount() + 1);
        }

        if((aNGram > threshold && acceptance.equals(ACCEPT)) || aNGram < threshold && acceptance.equals(REJECT)){
            aAccuracy.setNgram2Count(aAccuracy.getNgram2Count() + 1);
        } 

        if((aCosine > threshold && acceptance.equals(ACCEPT)) || aCosine < threshold && acceptance.equals(REJECT)){
            aAccuracy.setCosineCount(aAccuracy.getCosineCount() + 1);
        } 

        if((aLqs > threshold && acceptance.equals(ACCEPT)) || aLqs < threshold && acceptance.equals(REJECT)){
            aAccuracy.setLqsCount(aAccuracy.getLqsCount() + 1);
        } 

        if((jac > threshold && acceptance.equals(ACCEPT)) || jac < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaccardCount(aAccuracy.getJaccardCount() + 1);
        }
        
        if((jaro > threshold && acceptance.equals(ACCEPT)) || jaro < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaroCount(aAccuracy.getJaroCount() + 1);
        } 

        if((jaroWinkler > threshold && acceptance.equals(ACCEPT)) || jaroWinkler < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaroWinklerCount(aAccuracy.getJaroWinklerCount() + 1);
        } 

        if((jaroWinklerSorted > threshold && acceptance.equals(ACCEPT)) || jaroWinklerSorted < threshold && acceptance.equals(REJECT)){
            aAccuracy.setJaroWinklerSortedCount(aAccuracy.getJaroWinklerSortedCount() + 1);
        } 
        
        
        
        //basic norm
        
        if((bLeven > threshold && acceptance.equals(ACCEPT)) || bLeven < threshold && acceptance.equals(REJECT)){
            bAccuracy.setLevenshteinCount(bAccuracy.getLevenshteinCount() + 1);
        }

        if((bNGram > threshold && acceptance.equals(ACCEPT)) || bNGram < threshold && acceptance.equals(REJECT)){
            bAccuracy.setNgram2Count(bAccuracy.getNgram2Count() + 1);
        } 

        if((bCosine > threshold && acceptance.equals(ACCEPT)) || bCosine < threshold && acceptance.equals(REJECT)){
            bAccuracy.setCosineCount(bAccuracy.getCosineCount() + 1);
        } 

        if((bLqs > threshold && acceptance.equals(ACCEPT)) || bLqs < threshold && acceptance.equals(REJECT)){
            bAccuracy.setLqsCount(bAccuracy.getLqsCount() + 1);
        } 
        
        if((basicJac > threshold && acceptance.equals(ACCEPT)) || basicJac < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaccardCount(bAccuracy.getJaccardCount() + 1);
        }
        
        if((basicJaro > threshold && acceptance.equals(ACCEPT)) || basicJaro < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaroCount(bAccuracy.getJaroCount() + 1);
        } 

        if((basicJaroWinkler > threshold && acceptance.equals(ACCEPT)) || basicJaroWinkler < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaroWinklerCount(bAccuracy.getJaroWinklerCount() + 1);
        } 

        if((basicJaroWinklerSorted > threshold && acceptance.equals(ACCEPT)) || basicJaroWinklerSorted < threshold && acceptance.equals(REJECT)){
            bAccuracy.setJaroWinklerSortedCount(bAccuracy.getJaroWinklerSortedCount() + 1);
        } 
        
        
        
        //advanced norm
        
        if((cLeven > threshold && acceptance.equals(ACCEPT)) || cLeven < threshold && acceptance.equals(REJECT)){
            cAccuracy.setLevenshteinCount(cAccuracy.getLevenshteinCount() + 1);
        }

        if((cNGram > threshold && acceptance.equals(ACCEPT)) || cNGram < threshold && acceptance.equals(REJECT)){
            cAccuracy.setNgram2Count(cAccuracy.getNgram2Count() + 1);
        } 

        if((cCosine > threshold && acceptance.equals(ACCEPT)) || cCosine < threshold && acceptance.equals(REJECT)){
            cAccuracy.setCosineCount(cAccuracy.getCosineCount() + 1);
        } 

        if((cLqs > threshold && acceptance.equals(ACCEPT)) || cLqs < threshold && acceptance.equals(REJECT)){
            cAccuracy.setLqsCount(cAccuracy.getLqsCount() + 1);
        } 
        
        if((advancedJac > threshold && acceptance.equals(ACCEPT)) || advancedJac < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaccardCount(cAccuracy.getJaccardCount() + 1);
        }
        
        if((advancedJaro > threshold && acceptance.equals(ACCEPT)) || advancedJaro < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaroCount(cAccuracy.getJaroCount() + 1);
        } 

        if((advancedJaroWinkler > threshold && acceptance.equals(ACCEPT)) || advancedJaroWinkler < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaroWinklerCount(cAccuracy.getJaroWinklerCount() + 1);
        } 

        if((advancedJaroWinklerSorted > threshold && acceptance.equals(ACCEPT)) || advancedJaroWinklerSorted < threshold && acceptance.equals(REJECT)){
            cAccuracy.setJaroWinklerSortedCount(cAccuracy.getJaroWinklerSortedCount() + 1);
        } 
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
    
    private void constructPrecisionMap(Map<String, List<Double>> precisionMap, String acceptance, double similarity) {
        if(acceptance.equals(ACCEPT)){
            precisionMap.get(ACCEPT).add(similarity);
        } else {
            precisionMap.get(REJECT).add(similarity);
        }
    }    

    private void clearPrecisionMapLists(Map<String, List<Double>> precisionMap){
        precisionMap.get(ACCEPT).clear();
        precisionMap.get(REJECT).clear();
    }
    private double calculateAccuracy(int count, int rows) {
        if(rows == 0){
            logger.warn("Could not calculate accuracy. No rows found.");
            return 0;
        } else {
            return roundHalfUp(count / (double)rows);
        } 
    }
    
    private double calculatePrecision(int acceptCounter, int rejectCounter) {
        double result;
        
        if(acceptCounter == 0 && rejectCounter == 0){
            result = 0;
        } else {
            result = roundHalfUp(acceptCounter / (double)(acceptCounter + rejectCounter));
        }
        return result;
    }

    private double calculateRecall(int acceptCounter, Map<String, List<Double>> precisionMap) {
        double result;
        
        if(acceptCounter == 0){
            result = 0;
        } else {
            result = roundHalfUp(acceptCounter / (double) precisionMap.get(ACCEPT).size());
        }
        
        return result;

    }

    private double calculateHarmonicMean(double precision, double recall) {
        double result;
        
        if(precision == 0 || recall == 0){
            result = 0;
        } else {
            result = roundHalfUp(2 * precision * recall / (double)(precision + recall));
        }
        
        return result;
    }
    
    private double roundHalfUp(double d){
        return new BigDecimal(d).setScale(SpecificationConstants.ROUND_DECIMALS, RoundingMode.HALF_UP).doubleValue();        
    }
    
    private int count(List<Double> accepted, double threshold) {
        int counter = 0;
        for(Double elem : accepted){
            if(elem > threshold){
                counter++;
            }
        }
        return counter;
    }
}
