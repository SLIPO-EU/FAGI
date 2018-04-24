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
    
    private static final Logger logger = LogManager.getLogger(FusionSpecification.class);
    
    private static FusionSpecification fusionSpecification;
    
    private String idA;
    private String endpointA;
    private String pathA;
    private String pathB;
    private String idB;
    private String endpointB;
    private String pathLinks;
    private String idLinks;
    private String endpointLinks;
    private String pathOutput;
    private String resourceUri;
    private String idOutput;
    private String endpointOutput;
    
    private String categoriesA;
    private String categoriesB;

    private EnumDataset finalDataset;
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
    
    public void setPathA(String pathA) throws WrongInputException {
        
        if(StringUtils.isBlank(pathA)){
            throw new WrongInputException("Dataset A path is blank!");
        }
        
        this.pathA = pathA;
    }
    
    public String getPathA() {
        return pathA;
    }

    public void setPathB(String pathB) throws WrongInputException {
        
        if(StringUtils.isBlank(pathB)){
            throw new WrongInputException("Dataset B path is blank!");
        }

        this.pathB = pathB;
    }

    public String getPathB() {
        return pathB;
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

    public String getPathOutput() {
        return pathOutput;
    }

    public void setPathOutput(String pathOutput) throws WrongInputException {
        if(StringUtils.isBlank(pathOutput)){
            throw new WrongInputException("Output path is blank!");
        }          
        this.pathOutput = pathOutput;
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
            logger.warn("Optional Depth: " + optionalDepth + " is not allowed. Setting default value.");
            this.optionalDepth = 2;
        }
    }

    public EnumDataset getFinalDataset() {
        return finalDataset;
    }

    public void setFinalDataset(EnumDataset finalDataset) {
        this.finalDataset = finalDataset;
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
    
    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
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
    
    @Override
    public String toString() {
        return "FusionSpecification{" + 
                    "\n idA=" + idA + 
                    "\n endpointA=" + endpointA + 
                    "\n pathA=" + pathA + 
                    "\n pathB=" + pathB + 
                    "\n idB=" + idB + 
                    "\n endpointB=" + endpointB + 
                    "\n pathLinks=" + pathLinks + 
                    "\n idLinks=" + idLinks + 
                    "\n endpointLinks=" + endpointLinks + 
                    "\n pathOutput=" + pathOutput + 
                    "\n resourceUri=" + resourceUri +
                    "\n finalDataset=" + finalDataset + 
                    "\n outputRDFFormat=" + outputRDFFormat + 
                    "\n inputRDFFormat=" + inputRDFFormat + 
                    "\n locale=" + locale +
                    "\n categoriesA=" + categoriesA +
                    "\n categoriesB=" + categoriesB +
                    "\n similarity=" + similarity +
                    "\n optionalDepth=" + optionalDepth + 
                    "\n maxOptionalDepth=" + maxOptionalDepth + 
                    "\n minOptionalDepth=" + minOptionalDepth + 
                    "\n}";
    }
}
