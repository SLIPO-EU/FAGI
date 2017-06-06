package gr.athena.innovation.fagi.configuration;

import gr.athena.innovation.fagi.model.EnumFuseIntoDataset;
import gr.athena.innovation.fagi.model.EnumGeometricActions;
import gr.athena.innovation.fagi.model.EnumMetadataActions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class holding fusion actions and configuration.
 * 
 * @author nkarag
 */
public class FusionConfig {
    
    private static final Logger logger = LogManager.getLogger(FusionConfig.class);
    
    private String pathA;
    private String pathB;
    private String pathLinks;
    private String pathOutput;
    private EnumFuseIntoDataset finalDataset;
    private String outputRDFFormat;
    private String inputRDFFormat;
    
    private EnumGeometricActions geoAction;
    private EnumMetadataActions metaAction;

    private int optionalDepth = 1; //depth of optional in sparql queries
    private final int maxOptionalDepth = 3;
    private final int minOptionalDepth = 1;
    
    public void setPathA(String pathA) {
        
        if(StringUtils.isBlank(pathA)){
            logger.fatal("Dataset A path is blank!");
            throw new RuntimeException();
        }
        
        this.pathA = pathA;
    }
    
    public String getPathA() {
        return pathA;
    }

    public void setPathB(String pathB) {
        
        if(StringUtils.isBlank(pathB)){
            logger.fatal("Dataset B path is blank!");
            throw new RuntimeException();
        }
        
        this.pathB = pathB;
    }
    
    public String getPathB() {
        return pathB;
    }
    
    public String getPathLinks() {
        return pathLinks;
    }

    public void setPathLinks(String pathLinks) {
        if(StringUtils.isBlank(pathLinks)){
            logger.fatal("Links path is blank!");
            throw new RuntimeException();
        }        
        this.pathLinks = pathLinks;
    }
    
    public String getPathOutput() {
        return pathOutput;
    }

    public void setPathOutput(String pathOutput) {
        if(StringUtils.isBlank(pathOutput)){
            logger.fatal("Output path is blank!");
            throw new RuntimeException();
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
    
    public EnumGeometricActions getGeoAction() {
        return geoAction;
    }

    public void setGeoAction(EnumGeometricActions geoAction) {
        this.geoAction = geoAction;
    }

    public EnumMetadataActions getMetaAction() {
        return metaAction;
    }

    public void setMetaAction(EnumMetadataActions metaAction) {
        this.metaAction = metaAction;
    }

    public int getOptionalDepth() {
        return optionalDepth;
    }

    public void setOptionalDepth(int optionalDepth) {
        if(minOptionalDepth <= optionalDepth && optionalDepth <= maxOptionalDepth){
            this.optionalDepth = optionalDepth;
        } else {
            logger.warn("Optional Depth: " + optionalDepth + " is not allowed. Setting default value.");
            this.optionalDepth = 1;
        }
    }

    public EnumFuseIntoDataset getFinalDataset() {
        return finalDataset;
    }

    public void setFinalDataset(EnumFuseIntoDataset finalDataset) {
        this.finalDataset = finalDataset;
    }

    @Override
    public String toString() {
        return "Fusion Configuration: {" + 
                "\npathA=" + pathA + 
                ", \npathB=" + pathB + 
                ", \npathLinks=" + pathLinks + 
                ", \npathOutput=" + pathOutput + 
                ", \nfinalDataset=" + finalDataset + 
                ", \noutputRDFFormat=" + outputRDFFormat + 
                ", \ninputRDFFormat=" + inputRDFFormat + 
                ", \ngeoAction=" + geoAction + 
                ", \nmetaAction=" + metaAction + 
                ", \noptionalDepth=" + optionalDepth + 
                "\n}";
    }
}
