package gr.athena.innovation.fagi.preview;

import com.vividsolutions.jts.io.ParseException;
import gr.athena.innovation.fagi.core.normalizer.BasicGenericNormalizer;
import gr.athena.innovation.fagi.core.similarity.Cosine;
import gr.athena.innovation.fagi.core.similarity.Jaro;
import gr.athena.innovation.fagi.core.similarity.JaroWinkler;
import gr.athena.innovation.fagi.core.similarity.Levenshtein;
import gr.athena.innovation.fagi.core.similarity.LongestCommonSubsequenceMetric;
import gr.athena.innovation.fagi.core.similarity.NGram;
import gr.athena.innovation.fagi.core.similarity.SortedJaroWinkler;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.RightDataset;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.io.BufferedWriter;
import java.io.File;
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
public class RDFInputSimilarityViewer {

    private static final Logger LOG = LogManager.getLogger(RDFInputSimilarityViewer.class);
    private final FusionSpecification fusionSpecification;

    public RDFInputSimilarityViewer() {

        this.fusionSpecification = FusionSpecification.getInstance();
    }

    public void printRDFSimilarityResults(List<String> rdfProperties) throws ParseException, IOException {

        int index = 0;
        for (String rdfProperty : rdfProperties) {
            String propertyPath;

            //Create user friendly filename for property.
            String filename;
            if (rdfProperty.lastIndexOf("#") != -1) {
                filename = rdfProperty.substring(rdfProperty.lastIndexOf("#") + 1);
            } else if (rdfProperty.lastIndexOf("/") != -1) {
                filename = rdfProperty.substring(rdfProperty.lastIndexOf("/") + 1);
            } else {
                filename = "_" + index;
            }

            propertyPath = fusionSpecification.getOutputDir() + "similarityMetrics/" + filename + ".txt";

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

        Model left = LeftDataset.getLeftDataset().getModel();
        Model right = RightDataset.getRightDataset().getModel();
        LinksModel links = LinksModel.getLinksModel();

        try (BufferedWriter output = new BufferedWriter(new FileWriter(propertyPath, true))) {
            for (Link link : links.getLinks()) {

                Model modelA = constructEntityDataModel(link.getNodeA(), left, fusionSpecification.getOptionalDepth());
                Model modelB = constructEntityDataModel(link.getNodeB(), right, fusionSpecification.getOptionalDepth());

                String literalA = SparqlRepository.getObjectOfProperty(rdfProperty, modelA);
                String literalB = SparqlRepository.getObjectOfProperty(rdfProperty, modelB);
                
                if(literalA == null && literalB == null){
                    continue;
                }

                LOG.info("Literals: {}, {}", literalA, literalB);

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

    private Model constructEntityDataModel(String node, Model sourceModel, int depth) {

        String q = SparqlConstructor.constructNodeQueryWithDepth(node, depth);
        Query query = QueryFactory.create(q);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, sourceModel);
        Model model = queryExecution.execConstruct();

        return model;
    }
}
