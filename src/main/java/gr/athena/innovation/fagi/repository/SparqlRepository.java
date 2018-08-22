package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.preview.Frequency;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;

/**
 * Class for querying RDF models.
 * 
 * @author nkarag
 */
public class SparqlRepository {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(SparqlRepository.class);

    public static Literal getObjectOfProperty(Property p, Model model) {
        Literal rdfObjectValue = null;

        List<RDFNode> objectList = model.listObjectsOfProperty(p).toList();

        if (objectList.size() == 1) {
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.asLiteral();
            } else {
                LOG.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else if (objectList.size() > 1) {
            //Possible duplicate triple. Happens with synthetic data. Returns the first literal
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.asLiteral();
            } else {
                LOG.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else {
            LOG.debug("Problem finding unique result with property: " + p + "\nObjects returned: " + objectList.size());
        }
        return rdfObjectValue;
    }

    public static Literal getObjectOfPropertyChain(String p1, String p2, Model model, boolean checkOfficial) {

        String var = "o2";
        Literal result = null;
        String queryString = SparqlConstructor.selectObjectFromChainQuery(p1, p2, checkOfficial);

        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(var);
                if (c.isLiteral()) {
                    if(c.asLiteral().getDatatypeURI().equals(Namespace.WKT_DATATYPE_NAME)){
                        c.asLiteral().getLexicalForm();
                    } else {
                        result = c.asLiteral();
                    }
                }
            }
        }
        return result;
    }

    public static Resource getSubjectWithLiteral(String property, String literal, Model model) {

        String var = "s";
        String queryString = SparqlConstructor.selectNodeWithLiteralQuery(property, literal);
        Query query = null;
        try {
            query = QueryFactory.create(queryString);
        } catch (org.apache.jena.query.QueryParseException ex){
            LOG.warn("Query parse exception with query:\n" + queryString);
        }
        
        if(query == null){
            return null;
        }

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode result = soln.get(var);
                if (result.isResource()) {
                    return (Resource) result;
                }
            }
        }
        return null;
    }

    public static Resource getSubjectWithGeometry(String property, String literal, Model model) {

        String var = "s";
        String queryString = SparqlConstructor.selectNodeWithGeometryQuery(property, literal);

        Query query = null;
        try {
            query = QueryFactory.create(queryString);
        } catch (org.apache.jena.query.QueryParseException ex){
            LOG.warn("Query parse exception with query:\n" + queryString);
        }
        
        if(query == null){
            return null;
        }

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode result = soln.get(var);
                if (result.isResource()) {
                    return (Resource) result;
                }
            }
        }
        return null;
    }
    
    public static String getObjectOfProperty(String property, Model model) {
        String rdfObjectValue = null;

        Property p = ResourceFactory.createProperty(property);
        List<RDFNode> objectList = model.listObjectsOfProperty(p).toList();

        if (objectList.size() == 1) {
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.toString();
            } else {
                LOG.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else if (objectList.size() > 1) {
            //Possible duplicate triple. Happens with synthetic data. Return the first literal
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.toString();
            } else {
                LOG.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else {
            LOG.debug("Problem finding unique result with property: " + p + "\nObjects returned: " + objectList.size());
        }
        return rdfObjectValue;
    }

    public static Literal getObjectOfProperty(String subject, String property1, String property2, Model model) {

        String queryString = SparqlConstructor.selectObjectFromChain(subject, property1, property2);

        String var = "o2";
        Query query = null;
        try {
            query = QueryFactory.create(queryString);
        } catch (org.apache.jena.query.QueryParseException ex){
            LOG.warn("Query parse exception with query:\n" + queryString);
        }
        
        if(query == null){
            LOG.warn("null query");
            return null;
        }

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode result = soln.get(var);
                if (result.isLiteral()) {
                    return result.asLiteral();
                }
            }
        }
        return null;
    }

    public static Literal getObjectOfProperty(String resource, String property, Model model) {

        Resource s = ResourceFactory.createResource(resource);
        Property p = ResourceFactory.createProperty(property);
        Statement statement = model.getProperty(s, p);

        if (statement == null) {
            LOG.debug("Could not find " + property + " for " + resource);
            return null;
        }

        return statement.getLiteral();
    }

    public static int countDistinctProperties(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countDistinctProperties(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static double averagePropertiesPerPOI(Model model, int distinctProperties) {

        int sum = 0;
        int total = 0;

        String var = "s";
        String queryString = SparqlConstructor.selectAllPOIs(var);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                Resource c1 = soln.getResource(var);
                int c2 = countDistinctPropertiesOfResource(model, c1.toString());
                sum = sum + c2;
                total++;
            }
        }

        double res = sum/(double)total;

        return res;
    }

    public static double averageEmptyPropertiesPerPOI(Model model, int distinctProperties) {

        int emptyProps = 0;
        int total = 0;

        String var = "s";
        String queryString = SparqlConstructor.selectAllPOIs(var);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                Resource c1 = soln.getResource(var);
                int c2 = countDistinctPropertiesOfResource(model, c1.toString());
                emptyProps = emptyProps + (distinctProperties - c2);
                total++;
            }
        }

        double res = emptyProps/(double)total;
        return res;
    }
    
    public static int countDistinctPropertiesOfResource(Model model, String resource) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countDistinctPropertiesOfResource(countVar, resource);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }

        return count;
    }

    public static int countPOIs(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countPOIs(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                
                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countLinkedPOIsA(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedPOIsA(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                
                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countLinkedPOIsB(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedPOIsB(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                
                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }
    
    public static int countLinkedTriplesA(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedTriplesA(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                
                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countLinkedTriplesB(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedTriplesB(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                
                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }
    
    public static int countDistinctSubjects(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countDistinctSubjects(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                
                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        
        return count;
    }

    public static int countDistinctObjects(Model model) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countDistinctObjects(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                
                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }
    
    public static int countPropertyWithObject(Model model, String property, String object) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countPropertyWithObject(countVar, property, object);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }
    
    public static int countProperty(Model model, String property) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countProperties(countVar, property);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countPropertyChain(Model model, String property1, String property2) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countPropertyChain(countVar, property1, property2);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countLinkedWithPropertyA(Model model, String property1, String property2) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyA(countVar, property1, property2);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countLinkedWithPropertyB(Model model, String property1, String property2) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyB(countVar, property1, property2);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countLinkedWithPropertyA(Model model, String property) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyA(countVar, property);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }

    public static int countLinkedWithPropertyB(Model model, String property) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyB(countVar, property);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(countVar);
                if (c.isLiteral()) {
                    count = c.asLiteral().getInt();
                }
            }
        }
        return count;
    }
    
    public static Frequency selectCategories(Model model, String category) {

        Frequency frequency = new Frequency();
        
        String object = "o";
        String queryString = SparqlConstructor.selectObjectQuery(category);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode obj = soln.getResource(object);
                if (obj.isURIResource()) {
                    String categoryNode = obj.toString();
                    frequency.insert(new String[] {categoryNode});
                }
            }
        }
        return frequency;
    }
    
    public static NodeIterator getObjectsOfProperty(String prop, Model model){
        Property property = ResourceFactory.createProperty(prop);
        
        NodeIterator objects = model.listObjectsOfProperty(property);
        
        return objects;
    }
}
