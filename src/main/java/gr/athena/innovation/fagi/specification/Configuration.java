package gr.athena.innovation.fagi.specification;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class holding configuration parameters from the "fusion.spec" input file.
 * 
 * @author nkarag
 */
public class Configuration {
    
    private static final Logger LOG = LogManager.getLogger(Configuration.class);
    
    private static Configuration configuration;
    
    private String idA;
    private String endpointA;
    private String pathDatasetA;
    private String pathDatasetB;
    private String idB;
    private String endpointB;
    private String pathLinks;
    private String idLinks;
    private String endpointLinks;
    private String linksFormat;
    private String outputDir;
    private String statsFilepath;
    private String fused;
    private String remaining;
    private String ambiguousDatasetFilepath;
    private String idOutput;
    private String endpointOutput;
    private String categoriesA;
    private String categoriesB;
    private String propertyFrequencyA;
    private String propertyFrequencyB;
    private Date dateA;
    private Date dateB;    
    private EnumOutputMode outputMode;
    private String outputRDFFormat;
    private String inputRDFFormat;
    private String rulesPath;
    private Locale locale = null;
    private String similarity;
    private String fusionLog;
    private String trainingSetCsvPath;
    private String nameModelPath;
    private String addressModelPath;
    private String phoneModelPath;
    private String websiteModelPath;
    private String emailModelPath;
    private String geoModelPath;
    private int optionalDepth = 1; //depth of optional in sparql queries
    private final int maxOptionalDepth = 4;
    private final int minOptionalDepth = 1;
    
    private boolean verbose;

    private Configuration() {
    }

    /**
     * 
     * @return the configuration instance.
     * @throws ApplicationException application exception.
     */
    public static Configuration getInstance() throws ApplicationException {
        //lazy init
        if (configuration == null) {
            configuration = new Configuration();
        }

        return configuration;
    }
    
    /**
     * 
     * @param pathDatasetA the path of dataset A (left).
     * @throws WrongInputException error with input.
     */
    public void setPathDatasetA(String pathDatasetA) throws WrongInputException {
        
        if(StringUtils.isBlank(pathDatasetA)){
            throw new WrongInputException("Dataset A path is blank!");
        }
        
        this.pathDatasetA = pathDatasetA;
    }
    
    /**
     *
     * @return the path of dataset A (left).
     */
    public String getPathDatasetA() {
        return pathDatasetA;
    }

    /**
     *
     * @param pathDatasetB the path of dataset B (right).
     * @throws WrongInputException error with input.
     */
    public void setPathDatasetB(String pathDatasetB) throws WrongInputException {
        
        if(StringUtils.isBlank(pathDatasetB)){
            throw new WrongInputException("Dataset B path is blank!");
        }

        this.pathDatasetB = pathDatasetB;
    }

    /**
     *
     * @return the path of dataset B (right).
     */
    public String getPathDatasetB() {
        return pathDatasetB;
    }

    /**
     *
     * @return the path of the links file.
     */
    public String getPathLinks() {
        return pathLinks;
    }

    /**
     *
     * @param pathLinks the path of the links file.
     * @throws WrongInputException error with input.
     */
    public void setPathLinks(String pathLinks) throws WrongInputException {
        if(StringUtils.isBlank(pathLinks)){
            throw new WrongInputException("Links path is blank!");
        }        
        this.pathLinks = pathLinks;
    }

    /**
     *
     * @return the output RDF format.
     */
    public String getOutputRDFFormat() {
        return outputRDFFormat;
    }

    /**
     *
     * @param outputRDFFormat the output RDF format.
     */
    public void setOutputRDFFormat(String outputRDFFormat) {
        this.outputRDFFormat = outputRDFFormat;
    }

    /**
     *
     * @return the input RDF format.
     */
    public String getInputRDFFormat() {
        return inputRDFFormat;
    }

    /**
     *
     * @param inputRDFFormat the input RDF format.
     */
    public void setInputRDFFormat(String inputRDFFormat) {
        this.inputRDFFormat = inputRDFFormat;
    }

    /**
     *
     * @return the option depth. The depth defines the depth of <code>OPTIONAL</code> operators in SPARQL queries.
     */
    public int getOptionalDepth() {
        return optionalDepth;
    }

    /**
     *
     * @param optionalDepth the optional depth. The depth defines the depth of <code>OPTIONAL</code> operators in SPARQL queries. Default is 2.
     */
    public void setOptionalDepth(int optionalDepth) {
        if(minOptionalDepth <= optionalDepth && optionalDepth <= maxOptionalDepth){
            this.optionalDepth = optionalDepth;
        } else {
            LOG.warn("Optional Depth: " + optionalDepth + " is not allowed. Setting default value.");
            this.optionalDepth = 2;
        }
    }

    /**
     *
     * @return the ID of dataset A (left). 
     */
    public String getIdA() {
        return idA;
    }

    /**
     *
     * @param idA the ID of dataset A (left).
     */
    public void setIdA(String idA) {
        this.idA = idA;
    }

    /**
     *
     * @return the SPARQL endpoint of dataset A. 
     */
    public String getEndpointA() {
        return endpointA;
    }

    /**
     *
     * @param endpointA the SPARQL endpoint of dataset A. 
     */
    public void setEndpointA(String endpointA) {
        this.endpointA = endpointA;
    }

    /**
     *
     * @return the ID of dataset B (right). 
     */
    public String getIdB() {
        return idB;
    }

    /**
     *
     * @param idB the ID of dataset B (right). 
     */
    public void setIdB(String idB) {
        this.idB = idB;
    }

    /**
     *
     * @return the SPARQL endpoint of dataset B (right). 
     */
    public String getEndpointB() {
        return endpointB;
    }

    /**
     *
     * @param endpointB the SPARQL endpoint of dataset B (right). 
     */
    public void setEndpointB(String endpointB) {
        this.endpointB = endpointB;
    }

    /**
     *
     * @return the ID of the links file.
     */
    public String getIdLinks() {
        return idLinks;
    }

    /**
     *
     * @param idLinks the ID of the links file.
     */
    public void setIdLinks(String idLinks) {
        this.idLinks = idLinks;
    }

    /**
     *
     * @return the SPARQL endpoint of the links.
     */
    public String getEndpointLinks() {
        return endpointLinks;
    }

    /**
     *
     * @param endpointLinks the SPARQL endpoint of the links.
     */
    public void setEndpointLinks(String endpointLinks) {
        this.endpointLinks = endpointLinks;
    }

    /**
     *
     * @return the ID of the output. 
     */
    public String getIdOutput() {
        return idOutput;
    }

    /**
     *
     * @param idOutput the ID of the output. 
     */
    public void setIdOutput(String idOutput) {
        this.idOutput = idOutput;
    }

    /**
     *
     * @return the SPARQL endpoint of the output.
     */
    public String getEndpointOutput() {
        return endpointOutput;
    }

    /**
     *
     * @param endpointOutput the SPARQL endpoint of the output.
     */
    public void setEndpointOutput(String endpointOutput) {
        this.endpointOutput = endpointOutput;
    }

    /**
     *
     * @return the locale.
     */
    public Locale getLocale() {
        if(locale == null){
            return Locale.ENGLISH;
        }
        return locale;
    }

    /**
     *
     * @param locale the locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     *
     * @return the path of the categories mapping file for dataset A (left). NT format expected.
     */
    public String getCategoriesA() {
        return categoriesA;
    }

    /**
     *
     * @param categoriesA the path of the categories mapping file for dataset A (left). NT format expected.
     */
    public void setCategoriesA(String categoriesA) {
        this.categoriesA = categoriesA;
    }

    /**
     *
     * @return the path of the categories mapping file for dataset B (right). NT format expected.
     */
    public String getCategoriesB() {
        return categoriesB;
    }

    /**
     *
     * @param categoriesB the path of the categories mapping file for dataset B (right). NT format expected.
     */
    public void setCategoriesB(String categoriesB) {
        this.categoriesB = categoriesB;
    }

    /**
     *
     * @return the path of the property frequencies file of dataset A.
     */
    public String getPropertyFrequencyA() {
        return propertyFrequencyA;
    }

    /**
     *
     * @param propertyFrequencyA the path of the property frequencies file of dataset A.
     */
    public void setPropertyFrequencyA(String propertyFrequencyA) {
        this.propertyFrequencyA = propertyFrequencyA;
    }

    /**
     *
     * @return the path of the property frequencies file of dataset B.
     */
    public String getPropertyFrequencyB() {
        return propertyFrequencyB;
    }

    /**
     *
     * @param propertyFrequencyB the path of the property frequencies file of dataset B.
     */
    public void setPropertyFrequencyB(String propertyFrequencyB) {
        this.propertyFrequencyB = propertyFrequencyB;
    }

    /**
     * Date reference for dataset A.
     * @return the date.
     */
    public Date getDateA() {
        return dateA;
    }

    /**
     * Date reference for dataset A.
     * @param dateA the date.
     */
    public void setDateA(Date dateA) {
        this.dateA = dateA;
    }
    
    /**
     * 
     * @return the dataset considered most recent based on the dates provided for each dataset. 
     */
    public EnumDataset getMostRecentDataset(){
        if(dateA != null && dateB != null){
            if(dateA.after(dateB)){
                return EnumDataset.LEFT;
            } else {
                return EnumDataset.RIGHT;
            }
        } else {
            return EnumDataset.UNDEFINED;
        }
    }

    /**
     * Date reference for dataset B.
     * @return the date.
     */
    public Date getDateB() {
        return dateB;
    }

    /**
     * Date reference for dataset B.
     * @param dateB the date.
     */
    public void setDateB(Date dateB) {
        this.dateB = dateB;
    }  
    
    /**
     *
     * @return the similarity name. Default is jaro-winkler.
     */
    public String getSimilarity() {
        return similarity;
    }

    /**
     *
     * @param similarity the similarity name.
     */
    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }

    /**
     *
     * @return the path of the rules specification XML file.
     */
    public String getRulesPath() {
        return rulesPath;
    }

    /**
     *
     * @param rulesPath the path of the rules specification XML file.
     */
    public void setRulesPath(String rulesPath) {
        this.rulesPath = rulesPath;
    }

    /**
     * Enumeration that defines the fusion mode. 
     * 
     * @return the output mode enumeration.
     */
    public EnumOutputMode getOutputMode() {
        return outputMode;
    }

    /**
     * Enumeration that defines the fusion mode.
     * 
     * @param outputMode the output mode enumeration.
     */
    public void setOutputMode(EnumOutputMode outputMode) {
        this.outputMode = outputMode;
    }

    /**
     * 
     * @return the output directory of the resulted files.
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     *
     * @param outputDir the output directory of the resulted files.
     * @throws WrongInputException error with input path.
     */
    public void setOutputDir(String outputDir) throws WrongInputException {
        LOG.info("output path: " + outputDir);
        if(StringUtils.isBlank(outputDir)){
            throw new WrongInputException("Output directory is blank! Add " 
                    + SpecificationConstants.Config.OUTPUT_DIR + " tag in " + SpecificationConstants.Config.CONFIG_XML);
        }          
       
        if(outputDir.endsWith("/")){
            this.outputDir = outputDir;
        } else {
            this.outputDir = outputDir + "/";
        }
        
    }
    
    /**
     *
     * @return the fused output file path.
     */
    public String getFused() {
        return fused;
    }

    /**
     *
     * @param fused the fused output file path.
     * @throws WrongInputException error with input path.
     */
    public void setFused(String fused) throws WrongInputException {
        if(StringUtils.isBlank(fused)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Config.FUSED 
                        + " filepath after " + SpecificationConstants.Config.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Config.CONFIG_XML);
            }
            
            this.fused = outputDir + SpecificationConstants.Config.DEFAULT_FUSED_FILENAME;
        } else {
            this.fused = fused;
        }
    }

    /**
     * Remaining dataset gets populated based on the fusion mode. 
     * 
     * @return the remaining output file path.
     */
    public String getRemaining() {
        return remaining;
    }

    /**
     * Remaining dataset gets populated based on the fusion mode. 
     * 
     * @param remaining the remaining output file path.
     * @throws WrongInputException error with input path.
     */
    public void setRemaining(String remaining) throws WrongInputException {
        if(StringUtils.isBlank(remaining)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Config.REMAINING 
                        + " filepath after " + SpecificationConstants.Config.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Config.CONFIG_XML);
            }
            
            this.remaining = outputDir + SpecificationConstants.Config.DEFAULT_REMAINING_FILENAME;
        } else {
            this.remaining = remaining;
        }
    }

    /**
     * 
     * @return the statistics path.
     */
    public String getStatsFilepath() {
        return statsFilepath;
    }

    /**
     *
     * @param statsFilepath the statistics path.
     * @throws WrongInputException error with input path.
     */
    public void setStatsFilepath(String statsFilepath) throws WrongInputException {
        if(StringUtils.isBlank(statsFilepath)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Config.STATISTICS 
                        + " filepath after " + SpecificationConstants.Config.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Config.CONFIG_XML);
            }

            this.statsFilepath = outputDir + SpecificationConstants.Config.DEFAULT_STATS_FILENAME;
        } else {
            this.statsFilepath = statsFilepath;
        }
    }

    /**
     * Ambiguous dataset contains entities and/or entity properties that were marked ambiguous at the fusion process based on the rules specification.
     * 
     * @return the ambiguous dataset file path.
     */
    public String getAmbiguousDatasetFilepath() {
        return ambiguousDatasetFilepath;
    }

    /**
     * Ambiguous dataset contains entities and/or entity properties that were marked ambiguous at the fusion process based on the rules specification.
     * 
     * @param ambiguousDatasetFilepath the ambiguous dataset file path.
     * @throws WrongInputException error with input path.
     */
    public void setAmbiguousDatasetFilepath(String ambiguousDatasetFilepath) throws WrongInputException {
        if(StringUtils.isBlank(ambiguousDatasetFilepath)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Config.AMBIGUOUS
                        + " filepath after " + SpecificationConstants.Config.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Config.CONFIG_XML);
            }
            
            this.ambiguousDatasetFilepath = outputDir + SpecificationConstants.Config.DEFAULT_AMBIGUOUS_FILENAME;
        } else {
            this.ambiguousDatasetFilepath = ambiguousDatasetFilepath;
        }
    }

    /**
     *
     * @return the path of the file containing the training examples (CSV format).
     */
    public String getTrainingSetCsvPath() {
        return trainingSetCsvPath;
    }

    /**
     *
     * @param trainingSetCsvPath the path of the file containing the training examples (CSV format).
     */
    public void setTrainingSetCsvPath(String trainingSetCsvPath) {
        this.trainingSetCsvPath = trainingSetCsvPath;
    }

    public String getFusionLog() {
        return fusionLog;
    }

    public void setFusionLog(String fusionLog) throws WrongInputException {
        if(StringUtils.isBlank(fusionLog)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Config.FUSION_LOG
                        + " filepath after " + SpecificationConstants.Config.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Config.CONFIG_XML);
            }

            this.fusionLog = outputDir + SpecificationConstants.Config.DEFAULT_FUSION_LOG_FILENAME;
        } else {
            this.fusionLog = fusionLog;
        }
    }

    public String getLinksFormat() {
        return linksFormat;
    }

    public void setLinksFormat(String linksFormat) {
        this.linksFormat = linksFormat.toLowerCase();
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public String getNameModelPath() {
        return nameModelPath;
    }

    public void setNameModelPath(String nameModelPath) {
        this.nameModelPath = nameModelPath;
    }

    public String getAddressModelPath() {
        return addressModelPath;
    }

    public void setAddressModelPath(String addressModelPath) {
        this.addressModelPath = addressModelPath;
    }

    public String getPhoneModelPath() {
        return phoneModelPath;
    }

    public void setPhoneModelPath(String phoneModelPath) {
        this.phoneModelPath = phoneModelPath;
    }

    public String getWebsiteModelPath() {
        return websiteModelPath;
    }

    public void setWebsiteModelPath(String websiteModelPath) {
        this.websiteModelPath = websiteModelPath;
    }

    public String getEmailModelPath() {
        return emailModelPath;
    }

    public void setEmailModelPath(String emailModelPath) {
        this.emailModelPath = emailModelPath;
    }

    public String getGeoModelPath() {
        return geoModelPath;
    }

    public void setGeoModelPath(String geoModelPath) {
        this.geoModelPath = geoModelPath;
    }

    @Override
    public String toString() {
        return "\nconfiguration{" + 
                    "\n rulesPath=" + rulesPath +                 
                    "\n idA=" + idA + 
                    //"\n endpointA=" + endpointA + 
                    "\n pathA=" + pathDatasetA + 
                    "\n categoriesA=" + categoriesA +
                    "\n dateA=" + dateA +
                    "\n\n pathB=" + pathDatasetB + 
                    "\n idB=" + idB + 
                    //"\n endpointB=" + endpointB + 
                    "\n categoriesB=" + categoriesB +
                    "\n dateB=" + dateB +                
                    "\n\n pathLinks=" + pathLinks + 
                    "\n idLinks=" + idLinks + 
                    "\n linksFormat=" + linksFormat +
                    "\n\n outputMode=" + outputMode +
                    "\n outputDir=" + outputDir +
                    "\n fused=" + fused +
                    "\n remaining=" + remaining +
                    "\n ambiguous=" + ambiguousDatasetFilepath +
                    "\n stats=" + statsFilepath +
                    "\n fusionLog=" + fusionLog +
                    "\n\n outputRDFFormat=" + outputRDFFormat + 
                    "\n inputRDFFormat=" + inputRDFFormat + 
                    "\n locale=" + locale +
                    "\n similarity=" + similarity +
                    "\n optionalDepth=" + optionalDepth + 
                    "\n}";
    }
}
