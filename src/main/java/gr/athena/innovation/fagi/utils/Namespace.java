package gr.athena.innovation.fagi.utils;

/**
 * Class containing namespaces and other constants to be used globally.
 * 
 * @author Nikos Karagiannakis
 */
public class Namespace {
    
    public static final String LGD2 = "http://linkedgeodata.org/triplify";
    public static final String LGD = "<http://linkedgeodata.org/triplify/>";
    public static final String GEOSPARQL = "<http://www.opengis.net/ont/geosparql/>";
    public static final String GEOSPARQL_HAS_GEOMETRY = "http://www.opengis.net/ont/geosparql#hasGeometry";
    public static final String WKT = "http://www.opengis.net/ont/geosparql#asWKT";
    public static final String WKT2 = "http://www.opengis.net/ont/geosparql#asWKT";
    
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
