package gr.athena.innovation.fagi.utils;

/**
 * Utilities for constructing SPARQL queries
 * 
 * @author nkarag
 */
public class SparqlConstructor {
    
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
    
    public static String countDistinctProperties(String countVar){
        String query = "SELECT (COUNT(DISTINCT ?p) AS ?" + countVar + ")\n" +
                       "WHERE\n" +
                       "{?s ?p ?o}";
        return query;
    }    
}
