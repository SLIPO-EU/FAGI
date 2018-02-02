package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.preview.Frequency;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class SparqlRepository {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(SparqlRepository.class);

    public static String getObjectOfProperty(Property p, Model model) {
        String rdfObjectValue = null;

        List<RDFNode> objectList = model.listObjectsOfProperty(p).toList();

        if (objectList.size() == 1) {
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.toString();
            } else {
                logger.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else if (objectList.size() > 1) {
            //Possible duplicate triple. Happens with synthetic data. Returns the first literal
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.toString();
            } else {
                logger.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else {
            logger.debug("Problem finding unique result with property: " + p + "\nObjects returned: " + objectList.size());
        }
        return rdfObjectValue;
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
                logger.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else if (objectList.size() > 1) {
            //Possible duplicate triple. Happens with synthetic data. Return the first literal
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.toString();
            } else {
                logger.fatal("Object is not a Literal! " + object.toString());
                return null;
            }
        } else {
            logger.debug("Problem finding unique result with property: " + p + "\nObjects returned: " + objectList.size());
        }
        return rdfObjectValue;
    }

    public static Literal getObjectOfProperty(Resource r, Property p, Model model) {

        Statement statement = model.getProperty(r, p);

        return statement.getLiteral();
    }

    public static Literal getObjectOfProperty(String resource, String property, Model model) {

        Resource s = ResourceFactory.createResource(resource);
        Property p = ResourceFactory.createProperty(property);
        Statement statement = model.getProperty(s, p);

        if (statement == null) {
            logger.debug("Could not find " + property + " for " + resource);
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
}
