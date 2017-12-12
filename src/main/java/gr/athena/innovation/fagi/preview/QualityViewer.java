package gr.athena.innovation.fagi.preview;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.normalizer.MultipleGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.Cosine;
import gr.athena.innovation.fagi.core.similarity.Jaro;
import gr.athena.innovation.fagi.core.similarity.JaroWinkler;
import gr.athena.innovation.fagi.core.similarity.Levenshtein;
import gr.athena.innovation.fagi.core.similarity.LongestCommonSubsequenceMetric;
import gr.athena.innovation.fagi.core.similarity.NGram;
import gr.athena.innovation.fagi.core.similarity.PermutedJaroWinkler;
import gr.athena.innovation.fagi.core.similarity.SortedJaroWinkler;
import gr.athena.innovation.fagi.core.similarity.WeightedSimilarity;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.model.WeightedLiteral;
import gr.athena.innovation.fagi.quality.MetricSelector;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.rule.RuleCatalog;
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
import java.util.List;
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

    public QualityViewer(List<InterlinkedPair> interlinkedEntitiesList, RuleCatalog ruleCatalog,
            MetricSelector metricSelector, FusionSpecification fusionSpecification) {

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
                MultipleGenericNormalizer mgn = new MultipleGenericNormalizer();
                String a = mgn.normalize(literalA, literalB);
                String b = mgn.normalize(literalB, literalA);

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

    public void fromCSV(String path, String outputPath) throws FileNotFoundException, IOException {
        String csvFile = path;
        String line = "";
        String cvsSplitBy = "#";

        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        int i = 0;

        String propertyPath = outputPath + "name.txt";
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
        //try {
        BufferedWriter namesWriter = new BufferedWriter(new FileWriter(file, true));
        l = 0;
        while ((line = br.readLine()) != null) {

            //skip first line of csv
            if (l == 0) {
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

            String namesLine = getPropertyLine(idA, idB, nameA, nameB, acceptance);

            namesWriter.append(namesLine);
            namesWriter.newLine();

            l++;
        }
        //} catch(Exception ex){  
        //}
        logger.info("Total lines: " + l);
    }

    private String getPropertyLine(String idA, String idB, String propertyA, String propertyB, String acceptance) {

        String a = getNormalized(propertyA, propertyB);
        String b = getNormalized(propertyB, propertyA);
        
        WeightedLiteral weightedA = getWeightedLiteral(a, b);
        WeightedLiteral weightedB = getWeightedLiteral(b, a);
        
        String namesLine = idA + " " + idB + " Name\n" + propertyA + " <-> " + propertyB
                + " \nLevenstein           :" + Levenshtein.computeSimilarity(propertyA, propertyB, null)
                + " \n2Gram                :" + NGram.computeSimilarity(propertyA, propertyB, 2)
                + " \nCosine               :" + Cosine.computeSimilarity(propertyA, propertyB)
                + " \nLongestCommonSubseq  :" + LongestCommonSubsequenceMetric.computeSimilarity(propertyA, propertyB)
                + " \nJaro                 :" + Jaro.computeSimilarity(propertyA, propertyB)
                + " \nJaroWinkler          :" + JaroWinkler.computeSimilarity(propertyA, propertyB)
                + " \nSortedJaroWinkler    :" + SortedJaroWinkler.computeSimilarity(propertyA, propertyB)
                //+ " \nPermJaroWinkler      :" + per.computeDistance(literalA, literalB) //too slow                        
                + " \nnormalized: " + a + " <--> " + b
                + " \nJaroWinklerNormalized:" + JaroWinkler.computeSimilarity(a, b)
                + " \nJaroNormalized       :" + Jaro.computeSimilarity(a, b)
                + " \nweighted: " + weightedA.toString() + " <--> " + weightedB.toString()
                + " \nLevenstein           :" + WeightedSimilarity.computeDistance(weightedA, weightedB, "levenshtein")
                + " \n2Gram                :" + WeightedSimilarity.computeDistance(weightedA, weightedB, "2gram")
                + " \nCosine               :" + WeightedSimilarity.computeDistance(weightedA, weightedB, "cosine")
                + " \nLongestCommonSubseq  :" + WeightedSimilarity.computeDistance(weightedA, weightedB, "longestcommonsubsequence")
                + " \nJaro                 :" + WeightedSimilarity.computeDistance(weightedA, weightedB, "jaro")
                + " \nJaroWinkler          :" + WeightedSimilarity.computeDistance(weightedA, weightedB, "jarowinkler")
                + " \nSortedJaroWinkler    :" + WeightedSimilarity.computeDistance(weightedA, weightedB, "sortedjarowinkler")                
                + " \n" + acceptance + "\n";
        return namesLine;
    }

    private String getNormalized(String literalA, String literalB) {
        MultipleGenericNormalizer mgn = new MultipleGenericNormalizer();
        return mgn.normalize(literalA, literalB);
    }

    private WeightedLiteral getWeightedLiteral(String normalizedA, String normalizedB) {
        MultipleGenericNormalizer mgn = new MultipleGenericNormalizer();
        
        return mgn.getWeightedLiteral(normalizedA, normalizedB);
    }
    
    private Model constructEntityMetadataModel(String node, Model sourceModel, int depth) {

        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct();

        return model;
    }
}
