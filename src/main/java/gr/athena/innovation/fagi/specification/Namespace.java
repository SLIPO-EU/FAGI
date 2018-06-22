package gr.athena.innovation.fagi.specification;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;

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
    public static final String WKT_DATATYPE = "^^<http://www.opengis.net/ont/geosparql#wktLiteral>";
    public static final String WKT_DATATYPE_NAME = "http://www.opengis.net/ont/geosparql#wktLiteral";
    
    public static final RDFDatatype WKT_RDF_DATATYPE = TypeMapper.getInstance().getSafeTypeByName(Namespace.WKT_DATATYPE_NAME);
    public static final String CRS_4326 = "<http://www.opengis.net/def/crs/EPSG/0/4326>";

    public static final String LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    public static final String DATE_OSM_MODIFIED = "http://purl.org/dc/terms/modified";
    
    public static final String SLIPO_PREFIX = "http://slipo.eu/def#";
    public static final String SAME_AS = "<http://www.w3.org/2002/07/owl#sameAs>";
    
    public static final String CATEGORY_VALUE = "<http://slipo.eu/def#value>";
    public static final String CATEGORY = "<http://slipo.eu/def#category>";
    
    //TODO: define/select date property from ontology
    public static final String DATE = "<http://slipo.eu/def#date>";
    
    public static final String TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
    //public static final String POI = "<http://slipo.eu/def#POI>";

    public static final String LON = "<http://www.w3.org/2003/01/geo/wgs84_pos#lon>";
    public static final String LAT = "<http://www.w3.org/2003/01/geo/wgs84_pos#lat>";

    public static final String NAME = "<http://slipo.eu/def#name>";
    public static final String NAME_VALUE = "<http://slipo.eu/def#nameValue>";
    public static final String NAME_TYPE = "<http://slipo.eu/def#nameType>";
    public static final String OFFICIAL_LITERAL = "official";

    public static final String SOURCE = "<http://slipo.eu/def#source>";
    public static final String LOCALITY = "<http://slipo.eu/def#locality>";
    public static final String ADDRESS = "<http://slipo.eu/def#address>";
    public static final String STREET = "<http://slipo.eu/def#street>";
    public static final String STREET_NUMBER = "<http://slipo.eu/def#number>";
    public static final String HOMEPAGE = "<http://slipo.eu/def#homepage>";
    public static final String EMAIL = "<http://slipo.eu/def#email>";
    public static final String PHONE = "<http://slipo.eu/def#phone>";
    public static final String CONTACT_VALUE = "<http://slipo.eu/def#contactValue>";

    public static final String LINKED_AMBIGUOUSLY = "http://slipo.eu/def#linked-ambiguously-with";
    public static final String HAS_AMBIGUOUS_PROPERTY = "http://slipo.eu/def#has-ambiguous-property";
    public static final String HAS_AMBIGUOUS_SUB_PROPERTY = "http://slipo.eu/def#has-ambiguous-sub-property";

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
