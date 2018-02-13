package gr.athena.innovation.fagi.specification;

/**
 * Class containing namespaces and other constants to be used globally.
 * 
 * @author nkarag
 */
public class Namespace {
    
    public static final String LGD2 = "http://linkedgeodata.org/triplify";
    public static final String LGD = "<http://linkedgeodata.org/triplify/>";
    public static final String GEOSPARQL = "<http://www.opengis.net/ont/geosparql/>";
    public static final String GEOSPARQL_HAS_GEOMETRY = "http://www.opengis.net/ont/geosparql#hasGeometry";
    public static final String WKT = "http://www.opengis.net/ont/geosparql#asWKT";
    public static final String WKT2 = "http://www.opengis.net/ont/geosparql#asWKT";
    
    public static final String CATEGORY_VALUE = "<http://slipo.eu/def#value>";
    public static final String CATEGORY = "<http://slipo.eu/def#category>";
    
    //TODO: define/select date property from ontology
    public static final String DATE = "<http://slipo.eu/def#date>";
    
    public static final String TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
    public static final String POI = "<http://slipo.eu/def#POI>";
    
    public static final String LON = "<http://www.w3.org/2003/01/geo/wgs84_pos#lon>";
    public static final String LAT = "<http://www.w3.org/2003/01/geo/wgs84_pos#lat>";
    
    public static final String NAME_VALUE = "<http://slipo.eu/def#nameValue>";
    public static final String NAME_TYPE = "<http://slipo.eu/def#nameType>";
    public static final String OFFICIAL_LITERAL = "official";
    
    public static final String LOCALITY = "<http://slipo.eu/def#locality>";
    public static final String STREET = "<http://slipo.eu/def#street>";
    public static final String STREET_NUMBER = "<http://slipo.eu/def#number>";
    public static final String WEBSITE = "<http://slipo.eu/def#homepage>";
    public static final String EMAIL = "<http://slipo.eu/def#email>";
    public static final String PHONE = "<http://slipo.eu/def#phone>";

    private String leftLinksNamespace;
    private String rightLinksNamespace;

    public String getLeftLinksNamespace() {
        return leftLinksNamespace;
    }

    public void setLeftLinksNamespace(String leftLinksNamespace) {
        this.leftLinksNamespace = leftLinksNamespace;
    }

    public String getRightLinksNamespace() {
        return rightLinksNamespace;
    }

    public void setRightLinksNamespace(String rightLinksNamespace) {
        this.rightLinksNamespace = rightLinksNamespace;
    }
    
}