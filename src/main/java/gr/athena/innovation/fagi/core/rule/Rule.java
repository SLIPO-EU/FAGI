package gr.athena.innovation.fagi.core.rule;

import gr.athena.innovation.fagi.core.action.EnumGeometricActions;
import gr.athena.innovation.fagi.core.action.EnumMetadataActions;

/**
 * Class representing a rule for fusion. 
 * The rule is defined against a pair of properties and the method to apply for the fusion action selection. 
 * 
 * @author nkarag
 */
public class Rule {

    private String onProperty;
    private String propertyA;
    private String propertyB;
    private String method;
    private EnumGeometricActions geoAction;
    private EnumMetadataActions metaAction;
    private EnumGeometricActions defaultGeoAction;
    private EnumMetadataActions defaultMetaAction;
    
    public String getOnProperty() {
        return onProperty;
    }

    public void setOnProperty(String onProperty) {
        this.onProperty = onProperty;
    }
    
    public String getPropertyA() {
        return propertyA;
    }

    public void setPropertyA(String propertyA) {
        this.propertyA = propertyA;
    }

    public String getPropertyB() {
        return propertyB;
    }

    public void setPropertyB(String propertyB) {
        this.propertyB = propertyB;
    }
    
    public void setMethod(String method){
        this.method = method;
    }
    
    public String getMethod(){
        return method;
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

    public EnumGeometricActions getDefaultGeoAction() {
        return defaultGeoAction;
    }

    public void setDefaultGeoAction(EnumGeometricActions defaultGeoAction) {
        this.defaultGeoAction = defaultGeoAction;
    }

    public EnumMetadataActions getDefaultMetaAction() {
        return defaultMetaAction;
    }

    public void setDefaultMetaAction(EnumMetadataActions defaultMetaAction) {
        this.defaultMetaAction = defaultMetaAction;
    }

    
}
