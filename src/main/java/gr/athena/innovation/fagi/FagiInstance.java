package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.core.POIFuser;
import gr.athena.innovation.fagi.core.function.FunctionRegistry;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.literal.AbbreviationAndAcronymResolver;
import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.core.function.phone.CallingCodeResolver;
import gr.athena.innovation.fagi.evaluation.Evaluation;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.LinkedPair;
import gr.athena.innovation.fagi.learning.Trainer;
import gr.athena.innovation.fagi.model.AmbiguousDataset;
import gr.athena.innovation.fagi.preview.FrequencyCalculationProcess;
import gr.athena.innovation.fagi.preview.LightContainer;
import gr.athena.innovation.fagi.preview.LightStatisticsProcessor;
import gr.athena.innovation.fagi.preview.RDFInputSimilarityViewer;
import gr.athena.innovation.fagi.preview.RDFStatisticsCollector;
import gr.athena.innovation.fagi.preview.statistics.StatisticsContainer;
import gr.athena.innovation.fagi.preview.statistics.StatisticsExporter;
import gr.athena.innovation.fagi.repository.AbstractRepository;
import gr.athena.innovation.fagi.repository.CSVRepository;
import gr.athena.innovation.fagi.repository.GenericRDFRepository;
import gr.athena.innovation.fagi.rule.RuleSpecification;
import gr.athena.innovation.fagi.rule.RuleProcessor;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.specification.ConfigurationParser;
import gr.athena.innovation.fagi.utils.InputValidator;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Reads the specification and rules, validates input and starts the fusion process.
 *
 * @author nkarag
 */
public class FagiInstance {

    private static final Logger LOG = LogManager.getLogger(FagiInstance.class);
    private final String config;

    private final boolean runEvaluation = false;
    private final boolean exportFrequencies = false;
    private final boolean exportSimilaritiesPerLink = false;
    private final boolean train = false;
    private final boolean fuse = true;

    /**
     * FagiInstance Constructor. Expects the absolute path of the configuration XML file.
     * 
     * @param config The path of the configuration file.
     */
    public FagiInstance(String config) {
        this.config = config;
    }

    /**
     *
     * Initiates the fusion process. 
     * 
     * @throws javax.xml.parsers.ParserConfigurationException Indicates configuration error.
     * @throws org.xml.sax.SAXException Encapsulate a general SAX error or warning.
     * @throws java.io.IOException Signals that an I/O exception of some sort has occurred.
     * @throws java.text.ParseException Signals that an error has been reached unexpectedly while parsing.
     * @throws gr.athena.innovation.fagi.exception.WrongInputException Wrong input exception.
     * @throws org.json.simple.parser.ParseException Json parsing exception.
     */
    public void run() throws ParserConfigurationException, SAXException, IOException, ParseException, WrongInputException,
            ApplicationException, org.json.simple.parser.ParseException {

        long startTimeInput = System.currentTimeMillis();
        //Validate input
        FunctionRegistry functionRegistry = new FunctionRegistry();
        functionRegistry.init();
        Set<String> functionSet = functionRegistry.getFunctionMap().keySet();

        InputValidator validator = new InputValidator(config, functionSet);

        LOG.info("Validating input..");

        if (!validator.isValidConfigurationXSD()) {
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }

        //Parse specification and rules
        ConfigurationParser configurationParser = new ConfigurationParser();
        Configuration configuration = configurationParser.parse(config);
        
        if (!validator.isValidRulesWithXSD(configuration.getRulesPath())) {
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }
        
        if (!validator.isValidFunctions(configuration.getRulesPath())) {
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }

        //validate output filepath:
        if(!validator.isValidOutputDirPath(configuration.getOutputDir())){
            LOG.info("Please specify a file output in specification.");
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }
        
        LOG.info("XML files seem syntactically valid.");
        LOG.info("Rules: " + configuration.getRulesPath());
        
        RuleProcessor ruleProcessor = new RuleProcessor();
        RuleSpecification ruleSpec = ruleProcessor.parseRules(configuration.getRulesPath());    
        ruleSpec.setFunctionRegistry(functionRegistry);

        long stopTimeInput = System.currentTimeMillis();

        //Load datasets
        long startTimeReadFiles = System.currentTimeMillis();

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        
        genericRDFRepository.parseLeft(configuration.getPathDatasetA());
        genericRDFRepository.parseRight(configuration.getPathDatasetB());

        LOG.info("Source datasets loaded.");
        
        Integer initialLinksCount = 0;
        switch(configuration.getLinksFormat()){
            case SpecificationConstants.Config.NT: {
                genericRDFRepository.parseLinks(configuration.getPathLinks());
                initialLinksCount = AbstractRepository.getInitialCount();
                break;
            }
            case SpecificationConstants.Config.CSV: {
                CSVRepository csvRepository = new CSVRepository();
                csvRepository.parseLinks(configuration.getPathLinks());
                initialLinksCount = CSVRepository.getInitialCount();
                break;
            }
            case SpecificationConstants.Config.CSV_UNIQUE_LINKS:{
                CSVRepository.extractUniqueLinks(configuration.getPathLinks());
                initialLinksCount = CSVRepository.getInitialCount();
                break;
            }
            case SpecificationConstants.Config.CSV_ENSEMBLES:{
                CSVRepository.extractEnsembles(configuration.getPathLinks());
                initialLinksCount = CSVRepository.getInitialCount();
                break;
            }
        }

        LOG.info("Links file loaded.");
        
        AmbiguousDataset.getAmbiguousDataset().getModel();

        long stopTimeReadFiles = System.currentTimeMillis();

        //Load resources
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        Map<String, String> knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();
        List<String> rdfProperties = resourceFileLoader.getRDFProperties();
        Set<String> specialTerms = resourceFileLoader.getSpecialTerms();
        Map<String, String> codes = resourceFileLoader.getExitCodes();

        AbbreviationAndAcronymResolver.setKnownAbbreviationsAndAcronyms(knownAbbreviations);
        AbbreviationAndAcronymResolver.setLocale(configuration.getLocale());
        TermResolver.setTerms(specialTerms);
        CallingCodeResolver.setCodes(codes);

        LOG.info("Resource files loaded.");
        
        if(exportFrequencies){
            LOG.info("Exporting frequencies...");
            FrequencyCalculationProcess freqProcess = new FrequencyCalculationProcess();
            freqProcess.run(configuration, rdfProperties);
        }

        if(exportSimilaritiesPerLink){
            //similarity viewer for each pair and a, b, c, d normalization
            RDFInputSimilarityViewer qualityViewer = new RDFInputSimilarityViewer();

            try {

                qualityViewer.printRDFSimilarityResults(rdfProperties);

            } catch (com.vividsolutions.jts.io.ParseException | IOException ex) {
                LOG.error(ex);
                throw new ApplicationException(ex.getMessage());
            }            
        }

        //Produce quality metric results for previewing, if enabled
        if (runEvaluation) {
            LOG.info("Running evaluation...");
            Evaluation evaluation = new Evaluation();
            String csvPath = "";
            evaluation.run(configuration, csvPath);
        }

        if(train){
            LOG.info("Training...");
            String trainingCsvPath = "";
            configuration.setTrainingSetCsvPath(trainingCsvPath);
            Trainer trainer = new Trainer(configuration);
            trainer.train();
        }

        //Start fusion process
        long startTimeFusion = System.currentTimeMillis();        

        if(fuse){
            LOG.info("Initiating fusion process...");

            POIFuser fuser = new POIFuser();
            Map<String, IFunction> functionRegistryMap = functionRegistry.getFunctionMap();

            List<LinkedPair> fusedEntities = fuser.fuseAll(configuration, ruleSpec, functionRegistryMap);

            long stopTimeFusion = System.currentTimeMillis();

            //Combine result datasets and write to file
            long startTimeWrite = System.currentTimeMillis();

            LOG.info("Writing results...");
            fuser.combine(configuration, fusedEntities, ruleSpec.getDefaultDatasetAction());

            long stopTimeWrite = System.currentTimeMillis();

            long startTimeComputeStatistics = System.currentTimeMillis(); 

            Integer rejected = fuser.getRejectedCount();
            Integer fused = fuser.getFusedPairsCount();

            StatisticsContainer container;
            StatisticsExporter exporter = new StatisticsExporter();
            
            LightContainer lightContainer = new LightContainer();
            LightStatisticsProcessor lightStatProcessor = new LightStatisticsProcessor(lightContainer);

            String finalStats = "";

            if (configuration.getStats().equals(SpecificationConstants.Config.DETAILED_STATS)) {
                LOG.info("Calculating statistics...");
                //statistics obtained using RDF
                RDFStatisticsCollector collector = new RDFStatisticsCollector();
                container = collector.collect();

                if(!container.isValid() && !container.isComplete()){
                    LOG.warn("Could not export statistics. Input dataset(s) do not contain " 
                            + Namespace.SOURCE + " property that is being used to count the entities."); 
                }

                finalStats = container.toJsonMap();

            } else if(configuration.getStats().equals(SpecificationConstants.Config.LIGHT_STATS)){

                lightContainer.setAverageConfidence(fuser.getAverageConfidence());
                lightContainer.setAverageGain(fuser.getAverageGain());
                lightContainer.setMaxGain(fuser.getMaxGain());
                lightContainer.setInitialLinks(initialLinksCount.toString());
                lightContainer.setFusedPOIs(fused.toString());
                
                lightContainer.setFusedPath(configuration.getFused());
                lightContainer.setPathA(configuration.getPathDatasetA());
                lightContainer.setPathB(configuration.getPathDatasetB());

                
                if(configuration.getLinksFormat().equals(SpecificationConstants.Config.CSV_UNIQUE_LINKS)){
                    LOG.info("Unique links: " + CSVRepository.getUniqueCount());
                    Integer uniqueLinks = CSVRepository.getUniqueCount();
                    lightContainer.setUniqueLinks(uniqueLinks.toString());
                } else {
                    lightContainer.setUniqueLinks(initialLinksCount.toString());
                }
            
                lightContainer.setRejectedLinks(rejected.toString());
                
                lightStatProcessor = new LightStatisticsProcessor(lightContainer);
                lightStatProcessor.compute();

            } else {
                finalStats = "";
                LOG.info("Statistics will not be computed. Select a value in the \"stats\" field.");
            }
            
            long stopTimeComputeStatistics = System.currentTimeMillis();

            Long datasetLoadTime = stopTimeReadFiles - startTimeReadFiles;
            Long statisticsTime = stopTimeComputeStatistics - startTimeComputeStatistics;
            Long fusionTime = stopTimeFusion - startTimeFusion;
            Long totalTime = stopTimeWrite - startTimeInput;

            Properties prop = new Properties();
            prop.setProperty("fused", fused.toString());
            prop.setProperty("rejected", rejected.toString());
            prop.setProperty("dataset-load-time(ms)", datasetLoadTime.toString());
            prop.setProperty("statistics-time(ms)", statisticsTime.toString());
            prop.setProperty("fusion(ms)", fusionTime.toString());
            prop.setProperty("total-time(ms)", totalTime.toString());

            if(configuration.isVerbose()){
                prop.setProperty("average-gain", fuser.getAverageGain().toString());
                prop.setProperty("max-gain", fuser.getMaxGain().toString());
                prop.setProperty("average-confidence", fuser.getAverageConfidence().toString());
            } else {
                prop.setProperty("average-gain", "available in verbose execution");
                prop.setProperty("max-gain", "available in verbose execution");
                prop.setProperty("average-confidence", "available in verbose execution");
            }

            OutputStream st = new FileOutputStream(configuration.getOutputDir() + "/" + "fusion.properties", false);
            prop.store(st, null);

            if(configuration.getStats().equals("light")){
                lightStatProcessor.updateExecutionTimes(datasetLoadTime.toString(), fusionTime.toString(), statisticsTime.toString());
                finalStats = lightStatProcessor.getStats();
            }

            exporter.exportStatistics(finalStats, configuration.getStatsFilepath());
            
            LOG.info(configuration.toString());

            LOG.info("####### ###### ##### #### ### ## # Results # ## ### #### ##### ###### #######");
            LOG.info("Interlinked (might contain multiples): " + initialLinksCount);

            if(configuration.getLinksFormat().equals(SpecificationConstants.Config.CSV_UNIQUE_LINKS)){
                LOG.info("Unique links: " + CSVRepository.getUniqueCount());
            } else if(configuration.getLinksFormat().equals(SpecificationConstants.Config.CSV_ENSEMBLES)){
                LOG.info("Ensembles (including one-to-one): " + CSVRepository.getEnsemblesCount());
                LOG.info("Initial links: " + CSVRepository.getInitialCount());
            }

            LOG.info("Fused: " + fused + ", Rejected links: " + rejected);
            LOG.info("Linked Entities not found: " + fuser.getLinkedEntitiesNotFoundInDataset());
            LOG.info("Analyzing/validating input and configuration completed in " + (stopTimeInput - startTimeInput) + "ms.");
            LOG.info("Datasets loaded in " + datasetLoadTime + "ms.");
            LOG.info("Statistics computed in " + statisticsTime + "ms.");
            LOG.info("Fusion completed in " + fusionTime + "ms.");
            LOG.info("Combining files and write to disk completed in " + (stopTimeWrite - startTimeWrite) + "ms.");
            LOG.info("Total time {}ms.", totalTime);
            LOG.info("####### ###### ##### #### ### ## # # # # # # ## ### #### ##### ###### #######");            
        }
    }

    /**
     * Computes the selected statistics if the list. Returns a JSON string with the selected statistics.
     * 
     * @param selected the selected statistics as a list.
     * @return a JSON string containing the statistics. 
     * @throws WrongInputException wrong input exception.
     * @throws ParserConfigurationException error parsing the configuration.
     * @throws SAXException SAXException.
     * @throws IOException I/O exception.
     * @throws ParseException exception parsing the links file.
     */
    public String computeStatistics(List<String> selected) 
            throws WrongInputException, ParserConfigurationException, SAXException, IOException, ParseException{
        LOG.info("Initiating statistics process...");
        long startInit = System.currentTimeMillis();
        
        //Validate input
        FunctionRegistry functionRegistry = new FunctionRegistry();
        functionRegistry.init();
        Set<String> functionSet = functionRegistry.getFunctionMap().keySet();

        InputValidator validator = new InputValidator(config, functionSet);

        LOG.info("Validating input..");
        
        if (!validator.isValidConfigurationXSD()) {
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }

        //Parse specification and rules
        ConfigurationParser configurationParser = new ConfigurationParser();
        Configuration configuration = configurationParser.parse(config);
        
        if (!validator.isValidRulesWithXSD(configuration.getRulesPath())) {
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }
        
        if (!validator.isValidFunctions(configuration.getRulesPath())) {
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }

        //validate output filepath:
        if(!validator.isValidOutputDirPath(configuration.getOutputDir())){
            LOG.info("Please specify a file output in specification.");
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        }

        LOG.info("XML files seem syntactically valid.");

        long stopInit = System.currentTimeMillis();
        long initTime = stopInit - startInit;
        LOG.info("Initialization time: " + initTime + "ms.");
        //Load datasets
        long startTimeReadFiles = System.currentTimeMillis();

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(configuration.getPathDatasetA());
        genericRDFRepository.parseRight(configuration.getPathDatasetB());
        
        //genericRDFRepository.parseLinks(configuration.getPathLinks());

        switch(configuration.getLinksFormat()){
            case SpecificationConstants.Config.NT: {
                genericRDFRepository.parseLinks(configuration.getPathLinks());
                break;
            }
            case SpecificationConstants.Config.CSV: {
                CSVRepository csvRepository = new CSVRepository();
                csvRepository.parseLinks(configuration.getPathLinks());
                break;
            }
            case SpecificationConstants.Config.CSV_UNIQUE_LINKS:{
                CSVRepository.extractUniqueLinks(configuration.getPathLinks());
                break;
            }
        }

        long stopTimeReadFiles = System.currentTimeMillis();
        long readTime = stopTimeReadFiles - startTimeReadFiles;
        LOG.info("Datasets loaded in " + readTime + "ms.");
        LOG.info("Calculating statistics...");

        long startCompute = System.currentTimeMillis();
        
        RDFStatisticsCollector collector = new RDFStatisticsCollector();

        StatisticsContainer container = collector.collect(selected);
        
        long stopCompute = System.currentTimeMillis();
        long computeTime = stopCompute - startCompute;

        if(!container.isValid() && !container.isComplete()){
            LOG.warn("Could not export statistics. Input dataset(s) do not contain " 
                    + Namespace.SOURCE + " property that is being used to count the entities."); 
        }

        LOG.info(container.toJsonMap());
        LOG.info("Statistics computed in " + computeTime + "ms.");
        
        return container.toJsonMap();
    }
    
    /**
     * Produces formatted time from milliseconds.
     * 
     * @param millis the milliseconds.
     * @return the formatted time string.
     */
    public static String getFormattedTime(long millis) {
        String time = String.format("%02d min, %02d sec", 
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        ); 
        return time;
    }
}
