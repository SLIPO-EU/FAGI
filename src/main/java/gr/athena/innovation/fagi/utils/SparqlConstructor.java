package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.specification.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utilities for constructing SPARQL queries
 * 
 * @author nkarag
 */
public class SparqlConstructor {
    
    private static final Logger LOG = LogManager.getLogger(SparqlConstructor.class);
    
    public static String selectNodeQueryWithDepth(String nodeURI, int depth){
        String query = null;
        switch (depth){
            case 0:
                query = " SELECT ?p1 ?o1 "
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . }";
                break;
            case 1:
                query = " SELECT ?p1 ?o1 ?p2 ?o2 "
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 }}";
                break;
            case 2:
                query = " SELECT ?p1 ?o1 ?p2 ?o2 ?p3 ?o3"
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 }"
                + "OPTIONAL {?o2 ?p3 ?o3 }}";                
                break;
            case 3:
                query = " SELECT ?p1 ?o1 ?p2 ?o2 ?p3 ?o3 ?p4 ?o4"
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 }"
                + "OPTIONAL {?o2 ?p3 ?o3 }"
                + "OPTIONAL {?o3 ?p4 ?o4 }}";                     
                break;
            default:
                throw new java.lang.UnsupportedOperationException("Queries with OPTIONAL depth " + depth + " is not supported.");
        }

        return query;
    }

    public static String constructNodeQueryWithDepth(String nodeURI, int depth){
        String query = null;
        switch (depth){
            case 0:
                query = 
                "CONSTRUCT { <" + nodeURI + "> ?p1 ?o1 }"
                        + "} "
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . }";
                break;
            case 1:
                query = 
                " CONSTRUCT { <" + nodeURI + "> ?p1 ?o1 . ?o1 ?p2 ?o2 }"
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 }"
                + "}";
                break;
            case 2:
                query = 
                " CONSTRUCT { <" + nodeURI + "> ?p1 ?o1 . ?o1 ?p2 ?o2 . ?o2 ?p3 ?o3 . }"
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 . OPTIONAL {?o2 ?p3 ?o3 }}"   
                + "}";            
                break;
            case 3:
                query = 
                " CONSTRUCT { <" + nodeURI + "> ?p1 ?o1 . ?o1 ?p2 ?o2 . ?o2 ?p3 ?o3 . ?o3 ?p4 ?o4}"
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 . OPTIONAL {?o2 ?p3 ?o3 . OPTIONAL {?o3 ?p4 ?o4 }}}"   
                + "}";                    
                break;
            case 4:
                query = 
                " CONSTRUCT { <" + nodeURI + "> ?p1 ?o1 . ?o1 ?p2 ?o2 . ?o2 ?p3 ?o3 . ?o3 ?p4 ?o4 . ?o4 ?p5 ?o5}"
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 . OPTIONAL {?o2 ?p3 ?o3 . OPTIONAL {?o3 ?p4 ?o4 . OPTIONAL {?o4 ?p5 ?o5 }}}}"   
                + "}";                    
                break;                
            default:
                throw new java.lang.UnsupportedOperationException("Queries with OPTIONAL depth " + depth + " is not supported.");
        }

        return query;
    }

    public static String selectAll(int depth){
        String query = null;
        switch (depth){
            case 0:
                query = " SELECT ?s ?p1 ?o1 "
                + "WHERE {?s ?p1 ?o1 . }";
                break;
            case 1:
                query = " SELECT ?s ?p1 ?o1 ?p2 ?o2 "
                + "WHERE {?s ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 }}";
                break;
            case 2:
                query = " SELECT ?s ?p1 ?o1 ?p2 ?o2 ?p3 ?o3"
                + "WHERE {?s ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 . OPTIONAL {?o2 ?p3 ?o3 }}"
                + "}";
                break;
            case 3:
                query = " SELECT ?s ?p1 ?o1 ?p2 ?o2 ?p3 ?o3 ?p4 ?o4"
                + "WHERE {?s ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 . OPTIONAL {?o2 ?p3 ?o3 . OPTIONAL {?o3 ?p4 ?o4 }}}"
                + "}";
                break;
            case 4:
                query = " SELECT ?s ?p1 ?o1 ?p2 ?o2 ?p3 ?o3 ?p4 ?o4 ?p5 ?o5"
                + "WHERE {?s ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 . OPTIONAL {?o2 ?p3 ?o3 . OPTIONAL {?o3 ?p4 ?o4 . OPTIONAL {?o4 ?p5 ?o5 }}}}"
                + "}";
                break;                
            default:
                throw new java.lang.UnsupportedOperationException("Queries with OPTIONAL depth " + depth + " is not supported.");
        }
        return query; 
    }
    
    public static String askPredicateQuery(String predicate){
        return "ASK { ?s <" + predicate + "> ?o }";
    }
    
    public static String askSubjectPredicateQuery(String subject, String predicate){
        return "ASK { <" + subject + "> <" + predicate + "> ?o }";
    }    
    
    public static String selectObjectQuery(String predicate){
        String query = "SELECT ?o " 
                        + "WHERE {"
                        + "?s " + predicate + "?o"
                        + "}";
        return query;
    }
    
    public static String selectNodeWithLiteralQuery(String predicate, String literal){
        String query = "SELECT ?s " 
                        + "WHERE {"
                        + "?s <" + predicate + "> \"" + literal +"\" "
                        + "}";
        return query;
    }  
    
    public static String selectNodeWithGeometryQuery(String predicate, String literal){
        String query = "SELECT ?s " 
                        + "WHERE {"
                        + "?s <" + predicate + "> \"" + literal +"\"" + Namespace.WKT_DATATYPE + " "
                        + "}";
        return query;
    }     

    public static String selectObjectFromChainQuery(String predicate1, String predicate2){
        String query = "SELECT ?o2 " 
                        + "WHERE {"
                        + "?s <" + predicate1 + "> ?o1 . ?o1 <" + predicate2 + "> ?o2"
                        + "}";
        return query;
    }
    
    public static String countPOIs(String countVar){
        String query = "SELECT (COUNT(DISTINCT ?s) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s " + Namespace.SOURCE+ " ?o}";
        return query;
    }

    public static String countLinkedPOIsA(String countVar){
        String query = "SELECT (COUNT(DISTINCT ?s) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s ?p ?o }";
        return query;
    }
    
    public static String countLinkedPOIsB(String countVar){
        String query = "SELECT (COUNT(DISTINCT ?o) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s ?p ?o }";
        return query;
    }
    
    public static String countLinkedTriples(String countVar){
        String query = "SELECT (COUNT(?p) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s ?p ?o . ?s <http://www.w3.org/2002/07/owl#sameAs> ?o1 . }";
        return query;
    }
    
    public static String countPropertyWithObject(String countVar, String property, String object){
        String query = "SELECT (COUNT(DISTINCT ?s) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s " + property + " \"" + object + "\"}";
        return query;
    }
    
    public static String countDistinctSubjects(String countVar){
        String query = "SELECT (COUNT(DISTINCT ?s) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s ?p ?o}";
        return query;
    }

    public static String countDistinctObjects(String countVar){
        String query = "SELECT (COUNT(DISTINCT ?o) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s ?p ?o}";
        return query;
    }
    
    public static String countDistinctProperties(String countVar){
        String query = "SELECT (COUNT(DISTINCT ?p) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s ?p ?o}";
        return query;
    }

    public static String countProperties(String countVar, String predicate){
        String query = "SELECT (COUNT (?s) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s " + predicate + " ?o}";
        return query;
    }

    public static String countLinkedWithProperty(String countVar, String predicate){
        String query = "SELECT (COUNT (?s) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s " + predicate + " ?o . ?s <http://www.w3.org/2002/07/owl#sameAs> ?o1 . }";
        return query;
    }
    
    public static String countPropertyChain(String countVar, String predicate1, String predicate2){
        String query = "SELECT (COUNT (?o2) AS ?" + countVar + ")\n" +
                       "WHERE {" + 
                       "?s <" + predicate1 + "> ?o1 . ?o1 <" + predicate2 + "> ?o2" + 
                       "}";
        return query;
    }
}
