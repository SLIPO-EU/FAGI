package gr.athena.innovation.fagi.specification;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class holding configuration parameters from the "fusion.spec" input file.
 * 
 * @author nkarag
 */
public class FusionSpecification {
    
    private static final Logger LOG = LogManager.getLogger(FusionSpecification.class);
    
    private static FusionSpecification fusionSpecification;
    
    private String idA;
    private String endpointA;
    private String pathDatasetA;
    private String pathDatasetB;
    private String idB;
    private String endpointB;
    private String pathLinks;
    private String idLinks;
    private String endpointLinks;
    private String outputDir;
    private String statsFilepath;
    private String fileA;
    private String fileB;
    private String fileC;
    private String ambiguousDatasetFilepath;
    private String idOutput;
    private String endpointOutput;
    
    private String categoriesA;
    private String categoriesB;

    private EnumOutputMode outputMode;
    private String outputRDFFormat;
    private String inputRDFFormat;
    
    private Locale locale = null;
    private String similarity;

    private int optionalDepth = 2; //depth of optional in sparql queries
    private final int maxOptionalDepth = 4;
    private final int minOptionalDepth = 1;    

    private FusionSpecification() {
    }

    public static FusionSpecification getInstance() throws ApplicationException {
        //lazy init
        if (fusionSpecification == null) {
            fusionSpecification = new FusionSpecification();
        }

        return fusionSpecification;
    }
    
    public void setPathDatasetA(String pathDatasetA) throws WrongInputException {
        
        if(StringUtils.isBlank(pathDatasetA)){
            throw new WrongInputException("Dataset A path is blank!");
        }
        
        this.pathDatasetA = pathDatasetA;
    }
    
    public String getPathDatasetA() {
        return pathDatasetA;
    }

    public void setPathDatasetB(String pathDatasetB) throws WrongInputException {
        
        if(StringUtils.isBlank(pathDatasetB)){
            throw new WrongInputException("Dataset B path is blank!");
        }

        this.pathDatasetB = pathDatasetB;
    }

    public String getPathDatasetB() {
        return pathDatasetB;
    }

    public String getPathLinks() {
        return pathLinks;
    }

    public void setPathLinks(String pathLinks) throws WrongInputException {
        if(StringUtils.isBlank(pathLinks)){
            throw new WrongInputException("Links path is blank!");
        }        
        this.pathLinks = pathLinks;
    }

    public String getOutputRDFFormat() {
        return outputRDFFormat;
    }

    public void setOutputRDFFormat(String outputRDFFormat) {
        this.outputRDFFormat = outputRDFFormat;
    }

    public String getInputRDFFormat() {
        return inputRDFFormat;
    }

    public void setInputRDFFormat(String inputRDFFormat) {
        this.inputRDFFormat = inputRDFFormat;
    }

    public int getOptionalDepth() {
        return optionalDepth;
    }

    public void setOptionalDepth(int optionalDepth) {
        if(minOptionalDepth <= optionalDepth && optionalDepth <= maxOptionalDepth){
            this.optionalDepth = optionalDepth;
        } else {
            LOG.warn("Optional Depth: " + optionalDepth + " is not allowed. Setting default value.");
            this.optionalDepth = 2;
        }
    }

    public String getIdA() {
        return idA;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getEndpointA() {
        return endpointA;
    }

    public void setEndpointA(String endpointA) {
        this.endpointA = endpointA;
    }

    public String getIdB() {
        return idB;
    }

    public void setIdB(String idB) {
        this.idB = idB;
    }

    public String getEndpointB() {
        return endpointB;
    }

    public void setEndpointB(String endpointB) {
        this.endpointB = endpointB;
    }

    public String getIdLinks() {
        return idLinks;
    }

    public void setIdLinks(String idLinks) {
        this.idLinks = idLinks;
    }

    public String getEndpointLinks() {
        return endpointLinks;
    }

    public void setEndpointLinks(String endpointLinks) {
        this.endpointLinks = endpointLinks;
    }

    public String getIdOutput() {
        return idOutput;
    }

    public void setIdOutput(String idOutput) {
        this.idOutput = idOutput;
    }

    public String getEndpointOutput() {
        return endpointOutput;
    }

    public void setEndpointOutput(String endpointOutput) {
        this.endpointOutput = endpointOutput;
    }

    public Locale getLocale() {
        if(locale == null){
            return Locale.ENGLISH;
        }
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getCategoriesA() {
        return categoriesA;
    }

    public void setCategoriesA(String categoriesA) {
        this.categoriesA = categoriesA;
    }

    public String getCategoriesB() {
        return categoriesB;
    }

    public void setCategoriesB(String categoriesB) {
        this.categoriesB = categoriesB;
    }

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }


    public EnumOutputMode getOutputMode() {
        return outputMode;
    }

    public void setOutputMode(EnumOutputMode outputMode) {
        this.outputMode = outputMode;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) throws WrongInputException {
        LOG.info("output path: " + outputDir);
        if(StringUtils.isBlank(outputDir)){
            throw new WrongInputException("Output directory is blank! Add " 
                    + SpecificationConstants.Spec.OUTPUT_DIR + " tag in " + SpecificationConstants.Spec.SPEC_XML);
        }          
       
        if(outputDir.endsWith("/")){
            this.outputDir = outputDir;
        } else {
            this.outputDir = outputDir + "/";
        }
        
    }
    
    public String getFileA() {
        return fileA;
    }

    public void setFileA(String fileA) throws WrongInputException {
        if(StringUtils.isBlank(fileA)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Spec.FILE_A 
                        + " filepath after " + SpecificationConstants.Spec.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Spec.SPEC_XML);
            }
            
            this.fileA = outputDir + SpecificationConstants.Spec.DEFAULT_FILENAME_A;
        } else {
            this.fileA = fileA;
        }
    }

    public String getFileB() {
        return fileB;
    }

    public void setFileB(String fileB) throws WrongInputException {
        if(StringUtils.isBlank(fileB)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Spec.FILE_B 
                        + " filepath after " + SpecificationConstants.Spec.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Spec.SPEC_XML);
            }
            
            this.fileB = outputDir + SpecificationConstants.Spec.DEFAULT_FILENAME_B;
        } else {
            this.fileB = fileB;
        }
    }

    public String getFileC() {
        return fileC;
    }

    public void setFileC(String fileC) throws WrongInputException {
        if(StringUtils.isBlank(fileC)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Spec.FILE_C
                        + " filepath after " + SpecificationConstants.Spec.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Spec.SPEC_XML);
            }
            
            this.fileC = outputDir + SpecificationConstants.Spec.DEFAULT_FILENAME_C;
        } else {
            this.fileC = fileC;
        }
    }

    public String getStatsFilepath() {
        return statsFilepath;
    }

    public void setStatsFilepath(String statsFilepath) throws WrongInputException {
        if(StringUtils.isBlank(statsFilepath)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Spec.STATS_FILEPATH 
                        + " filepath after " + SpecificationConstants.Spec.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Spec.SPEC_XML);
            }

            this.statsFilepath = outputDir + SpecificationConstants.Spec.DEFAULT_STATS_FILENAME;
        } else {
            this.statsFilepath = statsFilepath;
        }
    }

    public String getAmbiguousDatasetFilepath() {
        return ambiguousDatasetFilepath;
    }

    public void setAmbiguousDatasetFilepath(String ambiguousDatasetFilepath) throws WrongInputException {
        if(StringUtils.isBlank(ambiguousDatasetFilepath)){
            if(StringUtils.isBlank(outputDir)){
                throw new WrongInputException("Define " + SpecificationConstants.Spec.AMBIGUOUS
                        + " filepath after " + SpecificationConstants.Spec.OUTPUT_DIR 
                        + " tag in " + SpecificationConstants.Spec.SPEC_XML);
            }
            
            this.ambiguousDatasetFilepath = outputDir + SpecificationConstants.Spec.DEFAULT_AMBIGUOUS_FILENAME;
        } else {
            this.ambiguousDatasetFilepath = ambiguousDatasetFilepath;
        }
    }

    @Override
    public String toString() {
        return "\nFusionSpecification{" + 
                    "\n idA=" + idA + 
                    //"\n endpointA=" + endpointA + 
                    "\n pathA=" + pathDatasetA + 
                    "\n categoriesA=" + categoriesA +
                    "\n\n pathB=" + pathDatasetB + 
                    "\n idB=" + idB + 
                    //"\n endpointB=" + endpointB + 
                    "\n categoriesB=" + categoriesB +
                    "\n\n pathLinks=" + pathLinks + 
                    "\n idLinks=" + idLinks + 
                    //"\n endpointLinks=" + endpointLinks + 
                    "\n\n outputMode=" + outputMode +
                    "\n outputDir=" + outputDir +
                    "\n fileA=" + fileA +
                    "\n fileB=" + fileB +
                    "\n fileC=" + fileC +
                    "\n ambiguous=" + ambiguousDatasetFilepath +
                    "\n stats=" + statsFilepath +
                    "\n\n outputRDFFormat=" + outputRDFFormat + 
                    "\n inputRDFFormat=" + inputRDFFormat + 
                    "\n locale=" + locale +
                    "\n similarity=" + similarity +
                    "\n optionalDepth=" + optionalDepth + 
                    "\n}";
    }
}
