package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.core.Fuser;
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
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.preview.FrequencyCalculationProcess;
import gr.athena.innovation.fagi.preview.RDFInputSimilarityViewer;
import gr.athena.innovation.fagi.preview.RDFStatisticsCollector;
import gr.athena.innovation.fagi.preview.statistics.StatisticsCollector;
import gr.athena.innovation.fagi.preview.statistics.StatisticsContainer;
import gr.athena.innovation.fagi.preview.statistics.StatisticsExporter;
import gr.athena.innovation.fagi.repository.AbstractRepository;
import gr.athena.innovation.fagi.repository.GenericRDFRepository;
import gr.athena.innovation.fagi.rule.RuleSpecification;
import gr.athena.innovation.fagi.rule.RuleProcessor;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import gr.athena.innovation.fagi.specification.ConfigurationParser;
import gr.athena.innovation.fagi.utils.InputValidator;
import gr.athena.innovation.fagi.repository.ResourceFileLoader;
import gr.athena.innovation.fagi.specification.Namespace;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
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
    private final boolean exportStatistics = true;
    private final boolean exportSimilaritiesPerLink = false;
    private final boolean train = false;
    private final boolean fuse = true;

    /**
     * FagiInstance Constructor. Expects absolute paths of specification XML and rules XML.
     * 
     * @param config The path of the specification file.
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
        genericRDFRepository.parseLinks(configuration.getPathLinks());

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

        if(exportFrequencies){
            LOG.info("Exporting frequencies...");
            FrequencyCalculationProcess freqProcess = new FrequencyCalculationProcess();
            freqProcess.run(configuration, rdfProperties);
        }

        long startTimeComputeStatistics = System.currentTimeMillis(); 

        if (exportStatistics) {
            LOG.info("Calculating statistics...");
            //statistics obtained using RDF
            StatisticsCollector collector = new RDFStatisticsCollector();
            StatisticsContainer container = collector.collect();
            StatisticsExporter exporter = new StatisticsExporter();
            
            if(!container.isValid() && !container.isComplete()){
                LOG.warn("Could not export statistics. Input dataset(s) do not contain " 
                        + Namespace.SOURCE + " property that is being used to count the entities."); 
            }

            exporter.exportStatistics(container.toJsonMap(), configuration.getStatsFilepath());
        }
        
        long stopTimeComputeStatistics = System.currentTimeMillis();
        
        if(exportSimilaritiesPerLink){
            //similarity viewer for each pair and a,b,c normalization
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
            Trainer trainer = new Trainer(configuration);
            trainer.train();
        }

        //Start fusion process
        long startTimeFusion = System.currentTimeMillis();        

        if(fuse){
            LOG.info("Initiating fusion process...");

            Fuser fuser = new Fuser();
            Map<String, IFunction> functionRegistryMap = functionRegistry.getFunctionMap();

            List<LinkedPair> fusedEntities = fuser.fuseAll(configuration, ruleSpec, functionRegistryMap);

            long stopTimeFusion = System.currentTimeMillis();

            //Combine result datasets and write to file
            long startTimeWrite = System.currentTimeMillis();

            LOG.info("Writing results...");
            fuser.combine(configuration, fusedEntities, ruleSpec.getDefaultDatasetAction());

            long stopTimeWrite = System.currentTimeMillis();

            LOG.info(configuration.toString());
            
            LOG.info("####### ###### ##### #### ### ## # Results # ## ### #### ##### ###### #######");
            LOG.info("Interlinked: " + fusedEntities.size() 
                    + ", Fused: " + fuser.getFusedPairsCount() 
                    + ", Rejected links: " + LinksModel.getLinksModel().getRejected().size() 
                    + ", Linked Entities not found: " + fuser.getLinkedEntitiesNotFoundInDataset());
            LOG.info("Analyzing/validating input and configuration completed in " + (stopTimeInput - startTimeInput) + "ms.");
            LOG.info("Datasets loaded in " + (stopTimeReadFiles - startTimeReadFiles) + "ms.");
            LOG.info("Statistics computed in " + (stopTimeComputeStatistics - startTimeComputeStatistics) + "ms.");
            LOG.info("Fusion completed in " + (stopTimeFusion - startTimeFusion) + "ms.");
            LOG.info("Combining files and write to disk completed in " + (stopTimeWrite - startTimeWrite) + "ms.");
            LOG.info("Total time {}ms.", stopTimeWrite - startTimeInput);
            LOG.info("####### ###### ##### #### ### ## # # # # # # ## ### #### ##### ###### #######");            
        }
    }

    public String computeStatistics(List<String> selected) 
            throws WrongInputException, ParserConfigurationException, SAXException, IOException, ParseException{
        LOG.info("calculating statistics...");
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
        
        long stopTimeInput = System.currentTimeMillis();

        //Load datasets
        long startTimeReadFiles = System.currentTimeMillis();

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(configuration.getPathDatasetA());
        genericRDFRepository.parseRight(configuration.getPathDatasetB());
        genericRDFRepository.parseLinks(configuration.getPathLinks());

        //todo: enumMap for stats

        LOG.info("Calculating statistics...");

        StatisticsCollector collector = new RDFStatisticsCollector();
        StatisticsContainer container = collector.collect(selected);

        if(!container.isValid() && !container.isComplete()){
            LOG.warn("Could not export statistics. Input dataset(s) do not contain " 
                    + Namespace.SOURCE + " property that is being used to count the entities."); 
        }

        LOG.info(container.toJsonMap());

        return container.toJsonMap();
    }
    
    public static String getFormattedTime(long millis) {
        String time = String.format("%02d min, %02d sec", 
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        ); 
        return time;
    }    
}
