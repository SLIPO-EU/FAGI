package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.core.Fuser;
import gr.athena.innovation.fagi.core.function.FunctionRegistry;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.literal.AbbreviationAndAcronymResolver;
import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.core.function.phone.CallingCodeResolver;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.evaluation.SimilarityCalculator;
import gr.athena.innovation.fagi.evaluation.MetricProcessor;
import gr.athena.innovation.fagi.learning.Trainer;
import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.RightModel;
import gr.athena.innovation.fagi.preview.FileFrequencyCounter;
import gr.athena.innovation.fagi.preview.FrequencyExtractor;
import gr.athena.innovation.fagi.preview.RDFInputSimilarityViewer;
import gr.athena.innovation.fagi.preview.RDFStatisticsCollector;
import gr.athena.innovation.fagi.preview.StatisticsCollector;
import gr.athena.innovation.fagi.preview.StatisticsContainer;
import gr.athena.innovation.fagi.preview.StatisticsExporter;
import gr.athena.innovation.fagi.repository.AbstractRepository;
import gr.athena.innovation.fagi.repository.GenericRDFRepository;
import gr.athena.innovation.fagi.rule.RuleCatalog;
import gr.athena.innovation.fagi.rule.RuleProcessor;
import gr.athena.innovation.fagi.specification.FusionSpecification;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.specification.SpecificationParser;
import gr.athena.innovation.fagi.utils.InputValidator;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Reads the specification and rules, validates input and starts the fusion process.
 *
 * @author nkarag
 */
public class FagiInstance {

    private static final Logger logger = LogManager.getLogger(FagiInstance.class);
    private final String specXml;
    private final String rulesXml;

    private final boolean runEvaluation = false;
    
    private final boolean exportFrequencies = false;
    private final boolean exportStatistics = false;

    private final boolean train = false;

    public FagiInstance(String specXml, String rulesXml) {
        this.specXml = specXml;
        this.rulesXml = rulesXml;
    }

    public void run() throws ParserConfigurationException, SAXException, IOException, ParseException,
            com.vividsolutions.jts.io.ParseException, WrongInputException,
            ApplicationException, org.json.simple.parser.ParseException {

        Locale locale = Locale.GERMAN;

        long startTimeInput = System.currentTimeMillis();
        //Validate input
        FunctionRegistry functionRegistry = new FunctionRegistry();
        functionRegistry.init();
        Set<String> functionSet = functionRegistry.getFunctionMap().keySet();

        InputValidator validator = new InputValidator(rulesXml, specXml, functionSet);

        logger.info("Validating input..");

        if (!validator.isValidInput()) {
            logger.info(SpecificationConstants.HELP);
            System.exit(-1);
        }

        logger.info("XML files seem valid.");

        //Parse specification and rules
        SpecificationParser specificationParser = new SpecificationParser();
        FusionSpecification fusionSpec = specificationParser.parse(specXml);

        //TODO: remove setLocale as soon as locale is implemented in fusion specification parser
        fusionSpec.setLocale(locale);

        RuleProcessor ruleProcessor = new RuleProcessor();
        RuleCatalog ruleCatalog = ruleProcessor.parseRules(rulesXml);
        ruleCatalog.setFunctionRegistry(functionRegistry);

        long stopTimeInput = System.currentTimeMillis();

        //Load datasets
        long startTimeReadFiles = System.currentTimeMillis();

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(fusionSpec.getPathA());
        genericRDFRepository.parseRight(fusionSpec.getPathB());
        genericRDFRepository.parseLinks(fusionSpec.getPathLinks());

        long stopTimeReadFiles = System.currentTimeMillis();

        //Load resources
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        Map<String, String> knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();
        List<String> rdfProperties = resourceFileLoader.getRDFProperties();
        Set<String> specialTerms = resourceFileLoader.getSpecialTerms();
        Map<String, String> codes = resourceFileLoader.getExitCodes();

        AbbreviationAndAcronymResolver.setKnownAbbreviationsAndAcronyms(knownAbbreviations);
        AbbreviationAndAcronymResolver.setLocale(locale);
        TermResolver.setTerms(specialTerms);
        CallingCodeResolver.setCodes(codes);

        //Start fusion process
        long startTimeFusion = System.currentTimeMillis();

        if(exportFrequencies){

            //word frequencies using the RDF properties from file
            int topK = 0; //topK zero and negative values return the complete list

            //Frequent terms
            FileFrequencyCounter termFrequency = new FileFrequencyCounter(fusionSpec, topK);
            termFrequency.setLocale(locale);

            termFrequency.setProperties(rdfProperties);

            termFrequency.export(fusionSpec.getPathA());

            //category frequencies using the mapping (URI to literal value) of categories from external file
            //TODO: put optional field on spec.xml for this file
            String categoryMappingsNTPath = "";

            if(!StringUtils.isBlank(fusionSpec.getCategoriesA())){
                FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
                frequencyExtractor.extract(topK, fusionSpec.getCategoriesA(), LeftModel.getLeftModel().getModel(), 
                        fusionSpec, locale);                
            }
            
            if(!StringUtils.isBlank(fusionSpec.getCategoriesB())){

                FrequencyExtractor frequencyExtractor = new FrequencyExtractor();
                frequencyExtractor.extract(topK, fusionSpec.getCategoriesB(), RightModel.getRightModel().getModel(), 
                        fusionSpec, locale);                
            }            

            //similarity viewer for each pair and a,b,c normalization
            RDFInputSimilarityViewer qualityViewer = new RDFInputSimilarityViewer(fusionSpec);
            qualityViewer.printRDFSimilarityResults(rdfProperties);

        }

        if (exportStatistics) {
            //statistics obtained using RDF
            StatisticsCollector collector = new RDFStatisticsCollector();
            StatisticsExporter exporter = new StatisticsExporter();

            StatisticsContainer container = collector.collect();
            exporter.exportStatistics(container, fusionSpec.getPathOutput());
        }

        //Produce quality metric results for previewing, if enabled
        if (runEvaluation) {
            String csvPath = "";

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
            metricProcessor.executeEvaluation(csvPath, resultsPath, nameMetrics, thresholds, notes);
        }
        
        if(train){
            Trainer trainer = new Trainer(fusionSpec);
            trainer.train();
        }

        Fuser fuser = new Fuser();
        Map<String, IFunction> functionRegistryMap = functionRegistry.getFunctionMap();
        
        List<LinkedPair> fusedEntities = fuser.fuseAll(fusionSpec, ruleCatalog, functionRegistryMap);

        long stopTimeFusion = System.currentTimeMillis();

        //Combine result datasets and write to file
        long startTimeWrite = System.currentTimeMillis();

        fuser.combineFusedAndWrite(fusionSpec, fusedEntities, ruleCatalog.getDefaultDatasetAction());

        long stopTimeWrite = System.currentTimeMillis();

        logger.info(fusionSpec.toString());

        logger.info("####### ###### ##### #### ### ## # Results # ## ### #### ##### ###### #######");
        logger.info("Interlinked: " + fusedEntities.size() + ", Fused: " + fuser.getFusedPairsCount()
                + ", Linked Entities not found: " + fuser.getLinkedEntitiesNotFoundInDataset());
        logger.info("Analyzing/validating input and configuration completed in " + (stopTimeInput - startTimeInput) + "ms.");
        logger.info("Datasets loaded in " + (stopTimeReadFiles - startTimeReadFiles) + "ms.");
        logger.info("Fusion completed in " + (stopTimeFusion - startTimeFusion) + "ms.");
        logger.info("Combining files and write to disk completed in " + (stopTimeWrite - startTimeWrite) + "ms.");
        logger.info("Total time {}ms.", stopTimeWrite - startTimeInput);
        logger.info("####### ###### ##### #### ### ## # # # # # # ## ### #### ##### ###### #######");
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
