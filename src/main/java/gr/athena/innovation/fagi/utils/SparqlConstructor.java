package gr.athena.innovation.fagi.utils;

/**
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
                " CONSTRUCT { <" + nodeURI + "> ?p1 ?o1 . ?o1 ?p2 ?o2 . ?o2 ?p3 ?o3 . }"
                + "WHERE {<" + nodeURI + "> ?p1 ?o1 . "
                + "OPTIONAL {?o1 ?p2 ?o2 . OPTIONAL {?o2 ?p3 ?o3 . OPTIONAL {?o3 ?p4 ?o4 }}}"   
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
}
