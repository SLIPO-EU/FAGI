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
import gr.athena.innovation.fagi.preview.FrequencyCalculationProcess;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        AbbreviationAndAcronymResolver.setLocale(fusionSpec.getLocale());
        TermResolver.setTerms(specialTerms);
        CallingCodeResolver.setCodes(codes);

        //Start fusion process
        long startTimeFusion = System.currentTimeMillis();

        if(exportFrequencies){

            FrequencyCalculationProcess freqProcess = new FrequencyCalculationProcess();
            freqProcess.run(fusionSpec, rdfProperties);

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
            
            Evaluation evaluation = new Evaluation();
            String csvPath = "";
            evaluation.run(fusionSpec, csvPath);

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
}
