package gr.athena.innovation.fagi.evaluation;

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
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.preview.RDFInputSimilarityViewer;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates similarities for each pair.
 *
 * @author nkarag
 */
public class SimilarityCalculator {

    private static final Logger LOG = LogManager.getLogger(RDFInputSimilarityViewer.class);
    private final Configuration configuration;

    public SimilarityCalculator(Configuration configuration) {

        this.configuration = configuration;
    }

    public void calculateCSVPairSimilarities(String path, String outputPath, String propertName){
        String csvFile = path;
        String line;
        String cvsSplitBy = "\\^";
        Locale locale = configuration.getLocale();
        
        BufferedWriter namesWriter = null;
        try {
            
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            int i = 0;

            String propertyPath = outputPath + propertName;
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
            
            namesWriter = new BufferedWriter(new FileWriter(file, true));
        
            while ((line = reader.readLine()) != null) {

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

                String namesLine = getPropertyLine(idA, idB, nameA, nameB, locale, acceptance);

                namesWriter.append(namesLine);
                namesWriter.newLine();

                l++;
            }
            
            namesWriter.close();
            
            LOG.info("Total lines: " + l);
            
        } catch(IOException ex){  
            LOG.error(ex);
            throw new ApplicationException(ex.getMessage());
        } finally {
            try {
                if(namesWriter != null){
                    namesWriter.close();
                }
            } catch (IOException ex) {
                LOG.error(ex);
                throw new ApplicationException(ex.getMessage());
            }
        }
    }

    private String getPropertyLine(String idA, String idB, String propertyA, String propertyB, 
            Locale locale, String acceptance) {
        
        NormalizedLiteral basicA = getBasicNormalization(propertyA, propertyB, locale);
        NormalizedLiteral basicB = getBasicNormalization(propertyB, propertyA, locale);

        WeightedPairLiteral normalizedPair = getAdvancedNormalization(basicA, basicB, locale);
        
        String namesLine = "id_a: " + idA + " id_b: " + idB + " property: Name"
            + " \n(a)"
            + " \nOriginal values: " + propertyA + " <--> " + propertyB
            + " \n\tLevenstein           :" + Levenshtein.computeSimilarity(propertyA, propertyB, null)
            + " \n\t2Gram                :" + NGram.computeSimilarity(propertyA, propertyB, 2)
            + " \n\tCosine               :" + Cosine.computeSimilarity(propertyA, propertyB)
            + " \n\tLongestCommonSubseq  :" + LongestCommonSubsequenceMetric.computeSimilarity(propertyA, propertyB)
            //+ " \n\tJaccard              :" + Jaccard.computeSimilarity(propertyA, propertyB)
            + " \n\tJaro                 :" + Jaro.computeSimilarity(propertyA, propertyB)
            + " \n\tJaroWinkler          :" + JaroWinkler.computeSimilarity(propertyA, propertyB)
            + " \n\tSortedJaroWinkler    :" + SortedJaroWinkler.computeSimilarity(propertyA, propertyB)
            //+ " \nPermJaroWinkler      :" + per.computeDistance(literalA, literalB) //too slow                        

            + " \n(b)"
            + " \nSimple normalization: " + basicA.getNormalized() + " <--> " + basicB.getNormalized()
            + " \n\tLevenstein           :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.LEVENSHTEIN)
            + " \n\t2Gram                :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.GRAM_2)
            + " \n\tCosine               :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.COSINE)
            + " \n\tLongestCommonSubseq  :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.LCS)
            //+ " \n\tJaccard              :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.JACC)
            + " \n\tJaro                 :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.JARO)
            + " \n\tJaroWinkler          :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.JARO_WINKLER)
            + " \n\tSortedJaroWinkler    :" + WeightedSimilarity.computeBSimilarity(basicA, basicB, SpecificationConstants.Similarity.SORTED_JARO_WINKLER)

            + " \n(c)"
            + " \nCustom normalization full: " + normalizedPair.getCompleteA() + " <--> " + normalizedPair.getCompleteB()
            + " \nBase: " + normalizedPair.getBaseValueA() + " <--> " + normalizedPair.getBaseValueB()
            + " \nMismatch: " + normalizedPair.mismatchToStringA() + " <--> " + normalizedPair.mismatchToStringB()
            + " \nSpecial terms: " + normalizedPair.specialTermsToStringA() + " <--> " + normalizedPair.specialTermsToStringB()
            + " \nCommon terms: " + normalizedPair.commonTermsToString()
            + " \n\tLevenstein           :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.LEVENSHTEIN)
            + " \n\t2Gram                :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.GRAM_2)
            + " \n\tCosine               :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.COSINE)
            + " \n\tLongestCommonSubseq  :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.LCS)
            //+ " \n\tJaccard              :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.JACC)
            + " \n\tJaro                 :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO)
            + " \n\tJaroWinkler          :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO_WINKLER)
            + " \n\tSortedJaroWinkler    :" + WeightedSimilarity.computeCSimilarity(normalizedPair, SpecificationConstants.Similarity.SORTED_JARO_WINKLER)                

            + " \n(d)"             
            + " \n\tLevenstein           :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.LEVENSHTEIN)
            + " \n\t2Gram                :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.GRAM_2)
            + " \n\tCosine               :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.COSINE)
            + " \n\tLongestCommonSubseq  :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.LCS)
            //+ " \n\tJaccard              :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.JACC)
            + " \n\tJaro                 :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO)
            + " \n\tJaroWinkler          :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.JARO_WINKLER)
            + " \n\tSortedJaroWinkler    :" + WeightedSimilarity.computeDSimilarity(normalizedPair, SpecificationConstants.Similarity.SORTED_JARO_WINKLER)     
                
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
}
