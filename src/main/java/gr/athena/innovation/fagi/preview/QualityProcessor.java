package gr.athena.innovation.fagi.preview;

import gr.athena.innovation.fagi.core.normalizer.AdvancedGenericNormalizer;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.Cosine;
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
import java.util.Locale;
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

    public QualityProcessor(FusionSpecification fusionSpecification) {

        this.fusionSpecification = fusionSpecification;
    }

    public void executeEvaluation(String csvPath, String resultsPath, String propertyName) throws FileNotFoundException, IOException {

        //for (double threshold = 0.05; threshold < 1; threshold += 0.05) {

            //logger.warn(threshold);
            Accuracy accuracy = new Accuracy();
            
            executeThreshold(csvPath, resultsPath, propertyName, 0.5, accuracy);

        //}

        //executeThreshold(csvPath, resultsPath, propertyName);
    }

    private void executeThreshold(String csvPath, String resultsPath, String propertyName, double threshold, Accuracy accuracy) throws FileNotFoundException, IOException{
        String line;
        String cvsSplitBy = "\\^";
        Locale locale = fusionSpecification.getLocale();

        
        int i = 0;

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
        BufferedWriter namesWriter = new BufferedWriter(new FileWriter(file, true));
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

                String pairOutputResult = computePairOutputResult(idA, idB, nameA, nameB, locale, acceptance, threshold, accuracy);

                namesWriter.append(pairOutputResult);
                namesWriter.newLine();

                l++;
            }
            
            namesWriter.close();
            
        } catch(IOException | RuntimeException ex){  
            namesWriter.close();
            throw new RuntimeException();
        }
        logger.info("Total lines: " + l);        
    }

    private String computePairOutputResult(String idA, String idB, String valueA, String valueB, 
            Locale locale, String acceptance, double threshold, Accuracy accuracy) {
        
        NormalizedLiteral basicA = getBasicNormalization(valueA, valueB, locale);
        NormalizedLiteral basicB = getBasicNormalization(valueB, valueA, locale);

        WeightedPairLiteral normalizedPair = getAdvancedNormalization(basicA, basicB, locale);
        
        
        double leven = Levenshtein.computeSimilarity(valueA, valueB, null);
        double ngram2 = NGram.computeSimilarity(valueA, valueB, 2);
        double cosine = Cosine.computeSimilarity(valueA, valueB);
        double lqs = LongestCommonSubsequenceMetric.computeSimilarity(valueA, valueB);
        double jaro = Jaro.computeSimilarity(valueA, valueB);
        double jaroWinkler = JaroWinkler.computeSimilarity(valueA, valueB);
        double jaroWinklerSorted = SortedJaroWinkler.computeSimilarity(valueA, valueB);

        
        int levenCompl = 0;
        accuracy.setTotal(accuracy.getTotal()+1);
        

        if((leven > threshold && acceptance.equals("ACCEPT")) || leven < threshold && acceptance.equals("REJECT")){
            accuracy.setLevenAccur(accuracy.getLevenAccur() + 1);
        } else {
            levenCompl++;
        }
        
        if((ngram2 > threshold && acceptance.equals("ACCEPT")) || ngram2 < threshold && acceptance.equals("REJECT")){
            accuracy.setNgram2Accur(accuracy.getNgram2Accur() + 1);
        } 

        if((cosine > threshold && acceptance.equals("ACCEPT")) || cosine < threshold && acceptance.equals("REJECT")){
            accuracy.setCosineAccur(accuracy.getCosineAccur() + 1);
        } 

        if((lqs > threshold && acceptance.equals("ACCEPT")) || lqs < threshold && acceptance.equals("REJECT")){
            accuracy.setLqsAccur(accuracy.getLqsAccur() + 1);
        } 

        if((jaro > threshold && acceptance.equals("ACCEPT")) || jaro < threshold && acceptance.equals("REJECT")){
            accuracy.setJaroAccur(accuracy.getJaroAccur() + 1);
        } 

        if((jaroWinkler > threshold && acceptance.equals("ACCEPT")) || jaroWinkler < threshold && acceptance.equals("REJECT")){
            accuracy.setJaroWinklerAccur(accuracy.getJaroWinklerAccur() + 1);
        } 

        if((jaroWinklerSorted > threshold && acceptance.equals("ACCEPT")) || jaroWinklerSorted < threshold && acceptance.equals("REJECT")){
            accuracy.setJaroWinklerSortedAccur(accuracy.getJaroWinklerSortedAccur() + 1);
        } 
        
        
        String namesLine = "Threshold: " + threshold 
            +  "\nid_a: " + idA + " id_b: " + idB + " property: Name"             
            + " \nOriginal values: " + valueA + " <--> " + valueB
            + " \n\tLevenstein           :" + accuracy.getLevenAccur() + ", failed:" + levenCompl
            + " \n\t2Gram                :" + accuracy.getNgram2Accur()
            + " \n\tCosine               :" + accuracy.getCosineAccur()
            + " \n\tLongestCommonSubseq  :" + accuracy.getLqsAccur()
            + " \n\tJaro                 :" + accuracy.getJaroAccur()
            + " \n\tJaroWinkler          :" + accuracy.getJaroWinklerAccur()
            + " \n\tSortedJaroWinkler    :" + accuracy.getJaroWinklerSortedAccur();

//            + " \nSimple normalization: " + basicA.getNormalized() + " <--> " + basicB.getNormalized()
//            + " \n\tLevenstein           :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "levenshtein")
//            + " \n\t2Gram                :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "2Gram")
//            + " \n\tCosine               :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "cosine")
//            + " \n\tLongestCommonSubseq  :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "longestcommonsubsequence")
//            + " \n\tJaro                 :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "jaro")
//            + " \n\tJaroWinkler          :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "jarowinkler")
//            + " \n\tSortedJaroWinkler    :" + WeightedSimilarity.computeNormalizedSimilarity(basicA, basicB, "sortedjarowinkler")
//
//            + " \nCustom normalization full: " + normalizedPair.getCompleteA() + " <--> " + normalizedPair.getCompleteB()
//            + " \nBase: " + normalizedPair.getBaseValueA() + " <--> " + normalizedPair.getBaseValueB()
//            + " \nMismatch: " + normalizedPair.mismatchToStringA() + " <--> " + normalizedPair.mismatchToStringB()
//            + " \nSpecial terms: " + normalizedPair.specialTermsToStringA() + " <--> " + normalizedPair.specialTermsToStringB()
//            + " \nCommon terms: " + normalizedPair.commonTermsToString()
//            + " \n\tLevenstein           :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "levenshtein")
//            + " \n\t2Gram                :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "2Gram")
//            + " \n\tCosine               :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "cosine")
//            + " \n\tLongestCommonSubseq  :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "longestcommonsubsequence")
//            + " \n\tJaro                 :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jaro")
//            + " \n\tJaroWinkler          :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "jarowinkler")
//            + " \n\tSortedJaroWinkler    :" + WeightedSimilarity.computeAdvancedNormarizedSimilarity(normalizedPair, "sortedjarowinkler")                

//            + " \n" + acceptance + "\n";
        return namesLine;
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
