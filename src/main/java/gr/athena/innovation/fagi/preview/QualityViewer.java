package gr.athena.innovation.fagi.preview;

import com.vividsolutions.jts.io.ParseException;
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
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.NormalizedLiteral;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.model.WeightedPairLiteral;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
public class QualityViewer {

    private static final Logger logger = LogManager.getLogger(QualityViewer.class);
    private final FusionSpecification fusionSpecification;

    public QualityViewer(FusionSpecification fusionSpecification) {

        this.fusionSpecification = fusionSpecification;
    }

    public void printSimilarityResults(List<String> rdfProperties) throws ParseException, IOException {

        int index = 0;
        for (String rdfProperty : rdfProperties) {
            String propertyPath;

            File propertyFile = new File(fusionSpecification.getPathOutput());
            File parentDir = propertyFile.getParentFile();

            String filename;
            if (rdfProperty.lastIndexOf("#") != -1) {
                filename = rdfProperty.substring(rdfProperty.lastIndexOf("#") + 1);
            } else if (rdfProperty.lastIndexOf("/") != -1) {
                filename = rdfProperty.substring(rdfProperty.lastIndexOf("/") + 1);
            } else {
                filename = "_" + index;
            }

            propertyPath = parentDir.getPath() + "/similarityMetrics/" + filename + ".txt";

            File file = new File(propertyPath);
            if (file.exists()) {
                //clear contents
                PrintWriter pw = new PrintWriter(propertyPath);
                pw.close();
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            computeSimilarityOnProperty(rdfProperty, propertyPath);
            index++;
        }
    }

    private void computeSimilarityOnProperty(String rdfProperty, String propertyPath) throws ParseException, IOException {

        Model left = LeftModel.getLeftModel().getModel();
        Model right = RightModel.getRightModel().getModel();
        LinksModel links = LinksModel.getLinksModel();

        try (BufferedWriter output = new BufferedWriter(new FileWriter(propertyPath, true))) {
            for (Link link : links.getLinks()) {

                Model modelA = constructEntityMetadataModel(link.getNodeA(), left, fusionSpecification.getOptionalDepth());
                Model modelB = constructEntityMetadataModel(link.getNodeB(), right, fusionSpecification.getOptionalDepth());

                String literalA = SparqlRepository.getObjectOfProperty(rdfProperty, modelA);
                String literalB = SparqlRepository.getObjectOfProperty(rdfProperty, modelB);

                //logger.info("Literals: {}, {}", literalA, literalB);

                BasicGenericNormalizer normalizer = new BasicGenericNormalizer();
                String a = normalizer.normalize(literalA, literalB);
                String b = normalizer.normalize(literalB, literalA);

                //PermutedJaroWinkler per = new PermutedJaroWinkler();
                String line = link.getNodeA() + " " + link.getNodeB() + "\n" + rdfProperty
                        + " \n" + literalA + " <--> " + literalB
                        + " \nLevenstein           :" + Levenshtein.computeSimilarity(literalA, literalB, null)
                        + " \n2Gram                :" + NGram.computeSimilarity(literalA, literalB, 2)
                        + " \nCosine               :" + Cosine.computeSimilarity(literalA, literalB)
                        + " \nLongestCommonSubseq  :" + LongestCommonSubsequenceMetric.computeSimilarity(literalA, literalB)
                        + " \nJaro                 :" + Jaro.computeSimilarity(literalA, literalB)
                        + " \nJaroWinkler          :" + JaroWinkler.computeSimilarity(literalA, literalB)
                        + " \nSortedJaroWinkler    :" + SortedJaroWinkler.computeSimilarity(literalA, literalB)
                        //+ " \nPermJaroWinkler      :" + per.computeDistance(literalA, literalB) //too slow                        
                        + " \n" + a + " <--> " + b
                        + " \nJaroWinklerNormalized:" + JaroWinkler.computeSimilarity(a, b)
                        + " \nJaro                 :" + Jaro.computeSimilarity(a, b);

                output.append(line);
                output.newLine();
            }
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
