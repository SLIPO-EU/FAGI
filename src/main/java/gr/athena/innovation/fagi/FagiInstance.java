package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.core.Fuser;
import gr.athena.innovation.fagi.core.function.FunctionRegistry;
import gr.athena.innovation.fagi.core.function.literal.AbbreviationResolver;
import gr.athena.innovation.fagi.core.function.literal.TermResolver;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.InterlinkedPair;
import gr.athena.innovation.fagi.preview.QualityViewer;
import gr.athena.innovation.fagi.quality.MetricSelector;
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
    private final boolean qualityOn = false;

    public FagiInstance(String specXml, String rulesXml) {
        this.specXml = specXml;
        this.rulesXml = rulesXml;
    }

    public void run() throws ParserConfigurationException, SAXException, IOException, ParseException,
            com.vividsolutions.jts.io.ParseException, WrongInputException, ApplicationException {

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
        FusionSpecification fusionSpecification = specificationParser.parse(specXml);

        RuleProcessor ruleProcessor = new RuleProcessor();
        RuleCatalog ruleCatalog = ruleProcessor.parseRules(rulesXml);
        ruleCatalog.setFunctionRegistry(functionRegistry);

        long stopTimeInput = System.currentTimeMillis();

        //Load datasets
        long startTimeReadFiles = System.currentTimeMillis();

        AbstractRepository genericRDFRepository = new GenericRDFRepository();
        genericRDFRepository.parseLeft(fusionSpecification.getPathA());
        genericRDFRepository.parseRight(fusionSpecification.getPathB());
        genericRDFRepository.parseLinks(fusionSpecification.getPathLinks());

        long stopTimeReadFiles = System.currentTimeMillis();

        //Load resources
        ResourceFileLoader resourceFileLoader = new ResourceFileLoader();
        Map<String, String> knownAbbreviations = resourceFileLoader.getKnownAbbreviationsMap();
        List<String> rdfProperties = resourceFileLoader.getRDFProperties();
        Set<String> specialTerms = resourceFileLoader.getSpecialTerms();

        AbbreviationResolver.setKnownAbbreviations(knownAbbreviations);
        TermResolver.setTerms(specialTerms);

        //Start fusion process
        long startTimeFusion = System.currentTimeMillis();
        List<InterlinkedPair> interlinkedEntities = new ArrayList<>();

        //Produce quality metric results for previewing, if enabled
        if (qualityOn) {
            MetricSelector metricSelector = new MetricSelector();
            QualityViewer qualityViewer = new QualityViewer(interlinkedEntities, ruleCatalog, metricSelector, fusionSpecification);
            qualityViewer.printSimilarityResults(rdfProperties);
            
            //path of csv, path of similarity results
            //qualityViewer.fromCSV("", "");
        }

        Fuser fuser = new Fuser(interlinkedEntities);
        fuser.fuseAllWithRules(fusionSpecification, ruleCatalog, functionRegistry.getFunctionMap());

        long stopTimeFusion = System.currentTimeMillis();

        //Combine result datasets and write to file
        long startTimeWrite = System.currentTimeMillis();

        fuser.combineFusedAndWrite(fusionSpecification, interlinkedEntities, ruleCatalog.getDefaultDatasetAction());

        long stopTimeWrite = System.currentTimeMillis();

        logger.info(fusionSpecification.toString());

        logger.info("####### ###### ##### #### ### ## # Results # ## ### #### ##### ###### #######");
        logger.info("Interlinked: " + interlinkedEntities.size() + ", Fused: " + fuser.getFusedPairsCount()
                + ", Linked Entities not found: " + fuser.getLinkedEntitiesNotFoundInDataset());
        logger.info("Analyzing/validating input and configuration completed in " + (stopTimeInput - startTimeInput) + "ms.");
        logger.info("Datasets loaded in " + (stopTimeReadFiles - startTimeReadFiles) + "ms.");
        logger.info("Fusion completed in " + (stopTimeFusion - startTimeFusion) + "ms.");
        logger.info("Combining files and write to disk completed in " + (stopTimeWrite - startTimeWrite) + "ms.");
        logger.info("Total time {}ms.", stopTimeWrite - startTimeInput);
        logger.info("####### ###### ##### #### ### ## # # # # # # ## ### #### ##### ###### #######");
    }
}
