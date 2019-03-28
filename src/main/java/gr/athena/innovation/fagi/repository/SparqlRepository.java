package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.model.NameAttribute;
import gr.athena.innovation.fagi.model.NameModel;
import gr.athena.innovation.fagi.model.TypedNameAttribute;
import gr.athena.innovation.fagi.preview.Frequency;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.utils.SparqlConstructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.logging.log4j.LogManager;

/**
 * Class for querying RDF models.
 * 
 * @author nkarag
 */
public class SparqlRepository {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(SparqlRepository.class);

    /**
     * Return the literal value of the given property in this model.
     * 
     * @param p the RDF property.
     * @param model the model.
     * @return the literal.
     */
    public static Literal getLiteralOfProperty(Property p, Model model) {

        List<RDFNode> objectList = model.listObjectsOfProperty(p).toList();

        if (objectList.size() == 1) {
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.asLiteral();
            } else {
                return ResourceFactory.createStringLiteral(object.toString());
                //LOG.warn("Object is not a Literal! " + object.toString());
                //return null;
            }
        } else if (objectList.size() > 1) {
            //Possible duplicate triple. Happens with synthetic data. Returns the first literal
            RDFNode object = objectList.get(0);
            if (object.isLiteral()) {
                return object.asLiteral();
            } else {
                return ResourceFactory.createStringLiteral(object.toString());
                //LOG.warn("Object is not a Literal! " + object.toString());
                //return null;
            }
        } else {
            LOG.debug("Problem finding unique result with property: " + p + "\nObjects returned: " + objectList.size());
            return null;
        }
    }

    /**
     * Return the object of the given property in this model.
     * 
     * @param p RDF the property.
     * @param model the model.
     * @return the object as RDFNode.
     */
    public static RDFNode getObjectOfProperty(Property p, Model model) {

        List<RDFNode> objectList = model.listObjectsOfProperty(p).toList();

        if (objectList.size() == 1) {
            RDFNode object = objectList.get(0);
            return object;

        } else if (objectList.size() > 1) {
            //Possible duplicate triple. Happens with synthetic data. Returns the first literal
            LOG.trace("found more than one object of property " + p);
            RDFNode object = objectList.get(0);
            return object;
        } else {
            LOG.debug("Problem finding unique result with property: " + p + "\nObjects returned: " + objectList.size());
            return null;
        }
    }

    /**
     * Return the literal of a property chain from this model.
     * 
     * @param p1 the parent property.
     * @param p2 the value property.
     * @param model the model.
     * @param checkOfficial flag for prioritizing literals that are tagged official.
     * @return the literal.
     */
    public static Literal getLiteralFromPropertyChain(String p1, String p2, Model model, boolean checkOfficial) {

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
                        //todo: check why this was returned as lexical form and remove redundancy
                        result = c.asLiteral();
                    } else {
                        result = c.asLiteral();
                    }
                } else {
                    LOG.warn("Expected literal but found resource " + c);
                }
            }
        }
        return result;
    }

    /**
     * Count objects of a property chain in this model.
     * 
     * @param p1 the parent property.
     * @param p2 the value property.
     * @param model the model.
     * @return the count.
     */
    public static int countObjectsOfPropertyChain(String p1, String p2, Model model) {
        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countObjectsOfPropertyChain(countVar, p1, p2);
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

    /**
     * Return the literals of a property chain in this model as a list of Strings.
     * 
     * @param p1 the parent property.
     * @param p2 the value property.
     * @param model the model.
     * @return the literals as a list of strings.
     */
    public static List<String> getLiteralStringsFromPropertyChain(String p1, String p2, Model model) {
        List<String> literals = new ArrayList<>();
        String var = "o2";
        String queryString = SparqlConstructor.selectObjectFromChainQuery(p1, p2);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(var);
                if (c.isLiteral()) {
                    literals.add(c.asLiteral().getLexicalForm());
                } else {
                    LOG.warn("Expected literal but found resource " + c);
                }
            }
        }
        return literals;
    }

    /**
     * Return the list of literal values of the given property in this model.
     * 
     * @param p the RDF property.
     * @param model the model.
     * @return the list of literals.
     */
    public static List<Literal> getLiteralsOfProperty(Property p, Model model) {

        List<Literal> literals = new ArrayList<>();
        List<RDFNode> objectList = model.listObjectsOfProperty(p).toList();
        
        for(RDFNode object : objectList){
            if(object.isLiteral()){
                literals.add(object.asLiteral());
            }
        }
        
        if(literals.isEmpty()){
            LOG.trace("No literals were found. Property: " + p.toString());
        }
        
        return literals;
    }
    
    /**
     * Return the list of literals of the given properties from the model.
     * 
     * @param p1 the parent property.
     * @param p2 the value property.
     * @param model the model.
     * @return the literals.
     */
    public static List<Literal> getLiteralsFromPropertyChain(Property p1, Property p2, Model model) {
        List<Literal> literals = new ArrayList<>();
        String var = "o2";
        String queryString = SparqlConstructor.selectObjectFromChainQuery(p1.toString(), p2.toString());
        
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(var);
                if (c.isLiteral()) {
                    literals.add(c.asLiteral());
                } else {
                    LOG.warn("Expected literal but found resource " + c);
                }
            }
        }
        return literals;
    }

    /**
     * Return the literal of the property chain in this model.
     * 
     * @param p1 the parent property.
     * @param p2 the value property.
     * @param model the model.
     * @return the literal.
     */
    public static Literal getLiteralFromPropertyChain(String p1, String p2, Model model) {
        String var = "o2";
        String queryString = SparqlConstructor.selectObjectFromChainQuery(p1, p2);

        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(var);
                if (c.isLiteral()) {
                    return c.asLiteral();
                } else {
                    LOG.warn("Expected literal but found resource " + c);
                }
            }
        }
        return null;
    }

    /**
     * Return the object of a property chain in this model as RDFNode.
     * 
     * @param p1 the parent property.
     * @param p2 the value property.
     * @param model the model.
     * @param checkOfficial flag for prioritizing literals that are tagged official.
     * @return the object as RDFNode.
     */
    public static RDFNode getObjectOfPropertyChain(String p1, String p2, Model model, boolean checkOfficial) {

        String var = "o2";
        Literal result = null;
        String queryString = SparqlConstructor.selectObjectFromChainQuery(p1, p2, checkOfficial);

        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                RDFNode c = soln.get(var);
                return c;
            }
        }
        return result;
    }

    /**
     * Return the subject of the given property from the model.
     * 
     * @param property the property.
     * @param model the model.
     * @return the subject as a resource.
     */
    public static Resource getSubjectOfSingleProperty(String property, Model model) {

        String var = "s";
        String queryString = SparqlConstructor.selectSubjectOfSinglePropertyQuery(property);
        
        Query query = null;
        try {
            query = QueryFactory.create(queryString);
        } catch (org.apache.jena.query.QueryParseException ex){
            LOG.warn("Query parse exception with query:\n" + queryString);
        }
        LOG.trace(queryString);
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

    /**
     * Return the object of the given resource and property from the model.
     * 
     * @param resource the resource.
     * @param property the property.
     * @param model the model.
     * @return the object as RDFNode.
     */
    public static RDFNode getObjectOfProperty(Resource resource, Property property, Model model) {

        Statement statement = model.getProperty(resource, property);

        if (statement == null) {
            LOG.debug("Could not find " + property + " for " + resource);
            return null;
        }

        return statement.getObject();
    }

    /**
     * Return the object as String of the given property from the model.
     * 
     * @param property the property.
     * @param model the model.
     * @return the object as String.
     */
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

    /**
     * Retrieve the object-literal from the given subject and property chain in the model.
     * 
     * @param subject the subject as a String.
     * @param property1 the parent property.
     * @param property2 the value property.
     * @param model the model.
     * @return the literal.
     */
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

    /**
     * Count distinct properties in the given model.
     * 
     * @param model the model.
     * @return the count of the distinct properties.
     */
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

    /**
     * Calculate the average properties per POI for the given model.
     * 
     * @param model the model.
     * @param distinctProperties the distinct properties.
     * @return the average properties.
     */
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

    /**
     * Calculate the empty properties per POI in this model.
     * 
     * @param model the model.
     * @param distinctProperties the distinct properties.
     * @return the average empty properties.
     */
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
    
    /**
     * Count the distinct properties of the given resource in this model.
     * 
     * @param model the model.
     * @param resource the resource.
     * @return the count.
     */
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

    /**
     * Retrieves the distinct properties of a resource in this model.
     * 
     * @param model the model.
     * @param resource the resource.
     * @return the set of the distinct properties.
     */
    public static Set<Property> getDistinctPropertiesOfResource(Model model, Resource resource) {
        StmtIterator props = model.listStatements(resource, (Property) null, (RDFNode) null);
        Set<Property> set = new HashSet<>();
        while(props.hasNext()){
            set.add(props.nextStatement().getPredicate());
        }
        return set;
    }

    /**
     * Counts the POIs in the given model.
     * 
     * @param model the model.
     * @return the number of the POIs.
     */
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

    /**
     * Counts the POIs of the left (A) dataset that are linked.
     * 
     * @param linksModel the linksModel.
     * @return the number of linked POIs in A.
     */
    public static int countLinkedPOIsA(Model linksModel) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedPOIsA(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, linksModel)) {
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

    /**
     * Counts the POIs of the right (B) dataset that are linked.
     * 
     * @param linksModel the linksModel.
     * @return the number of linked POIs in B.
     */
    public static int countLinkedPOIsB(Model linksModel) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedPOIsB(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, linksModel)) {
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
    
    /**
     * Counts the linked triples of the left dataset.
     * 
     * @param unionLinksWithLeftModel the model. This model is a union of the left dataset with the model of the links.
     * @return the number of linked triples.
     */
    public static int countLinkedTriplesA(Model unionLinksWithLeftModel) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedTriplesA(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, unionLinksWithLeftModel)) {
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

    /**
     * Counts the linked triples of the right dataset.
     * 
     * @param unionLinksWithRightModel the model. This model is a union of the right dataset with the model of the links.
     * @return the number of linked triples.
     */
    public static int countLinkedTriplesB(Model unionLinksWithRightModel) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedTriplesB(countVar);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, unionLinksWithRightModel)) {
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
    
    /**
     * Counts distinct subjects in this model.
     * 
     * @param model the model.
     * @return the number of subjects.
     */
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

    /**
     * Counts the distinct objects in this model.
     * 
     * @param model the model.
     * @return the number of distinct objects.
     */
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
    
    /**
     *Counts the number of properties with the given object in this model.
     * 
     * @param model the model.
     * @param property the property.
     * @param object the object.
     * @return the number of properties with the given object.
     */
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
    
    /**
     * Counts the given property in this model.
     * 
     * @param model the model.
     * @param property the property.
     * @return the number of occurrences of this property in the model.
     */
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
    
    /**
     * Counts the number of the given property chain in the model.
     * 
     * @param model the model.
     * @param property1 the parent property.
     * @param property2 the value property.
     * @return the number of occurrences of the given chain property in the model.
     */
    public static int countPropertyChain(Model model, String property1, String property2) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countPropertyChains(countVar, property1, property2);
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

    /**
     * Count the subjects of dataset A (left) of the given property chain that are also linked. 
     * 
     * @param unionLeftwithLinks the model. This model is the union of model A with the model of the links.
     * @param property1 the parent property.
     * @param property2 the value property.
     * @return the count.
     */
    public static int countLinkedWithPropertyA(Model unionLeftwithLinks, String property1, String property2) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyA(countVar, property1, property2);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, unionLeftwithLinks)) {
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

    /**
     * Count the subjects of dataset B (right) of the given property chain that are also linked. 
     * 
     * @param unionRightWithLinks the model. This model is the union of model B with the model of the links.
     * @param property1 the parent property.
     * @param property2 the value property.
     * @return the count.
     */
    public static int countLinkedWithPropertyB(Model unionRightWithLinks, String property1, String property2) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyB(countVar, property1, property2);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, unionRightWithLinks)) {
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

    /**
     * Count the subjects of dataset A (left) of the given property that are also linked. 
     * 
     * @param unionLeftWithLinks the model. This model is the union of model A with the model of the links.
     * @param property the property.
     * @return the count.
     */
    public static int countLinkedWithPropertyA(Model unionLeftWithLinks, String property) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyA(countVar, property);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, unionLeftWithLinks)) {
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

    /**
     * Count the subjects of dataset B (right) of the given property that are also linked. 
     * 
     * @param unionRightWithLinks the model. This model is the union of model A with the model of the links.
     * @param property the property.
     * @return the count.
     */
    public static int countLinkedWithPropertyB(Model unionRightWithLinks, String property) {

        int count = 0;

        String countVar = "cnt";
        String queryString = SparqlConstructor.countLinkedWithPropertyB(countVar, property);
        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, unionRightWithLinks)) {
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
    
    /**
     * Return a Frequency object of the given category on the model.
     * 
     * @param model the model.
     * @param category the category.
     * @return the frequency object.
     */
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
    
    /**
     * Retrieve the objects of the given property in the model as an Node iterator.
     * 
     * @param prop the property as a String.
     * @param model the model.
     * @return the objects as a node iterator.
     */
    public static NodeIterator getObjectsOfProperty(String prop, Model model){
        Property property = ResourceFactory.createProperty(prop);
        
        NodeIterator objects = model.listObjectsOfProperty(property);
        
        return objects;
    }
    
    /**
     * Retrieve the objects of the given property in the model as an Node iterator.
     * 
     * @param property the RDF property.
     * @param model the model.
     * @return the objects as a node iterator.
     */
    public static NodeIterator getObjectsOfProperty(Property property, Model model){

        NodeIterator objects = model.listObjectsOfProperty(property);
        
        return objects;
    }
    
    /**
     * Delete any triples of the given property.
     * 
     * @param property the RDF property.
     * @param model the model.
     */
    public static void deleteProperty(String property, Model model){
        String queryString = SparqlConstructor.deletePropertyQuery(property);
        LOG.debug(queryString);
        UpdateRequest q = UpdateFactory.create(queryString);
        UpdateAction.execute(q, model);
    }

    /**
     * Delete any triples of the given property chain.
     * 
     * @param property1 the parent RDF property.
     * @param property2 the value RDF property.
     * @param model the model.
     */
    public static void deleteProperty(String property1, String property2,  Model model){
        String queryString = SparqlConstructor.deletePropertyChainQuery(property1, property2);
        LOG.debug(queryString);
        UpdateRequest q = UpdateFactory.create(queryString);
        UpdateAction.execute(q, model);
    }

    /**
     * Retrieve the name model object from the given RDF model.
     * 
     * @param model the model.
     * @return the name model object.
     */
    public static NameModel getNameAttributes(Model model) {

        String nameValue = "?nameValue";
        String nameType = "?nameType";
        String language = "?language";
        String poi = "?poi";
        String o = "?o";

        String queryString = SparqlConstructor.getNameModel(nameType, language, poi, o, nameValue);
        
        LinkedHashSet<TypedNameAttribute> typedNameAttributes = new LinkedHashSet<>();
        LinkedHashSet<NameAttribute> nameAttributes = new LinkedHashSet<>();

        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                Resource poiURI = soln.get(poi).asResource();
                RDFNode nameVal = soln.get(nameValue);
                RDFNode nType = soln.get(nameType);
                RDFNode lang = soln.get(language);
                RDFNode obURI = soln.get(o);
                
                if(nType == null){
                    NameAttribute nameAttribute = new NameAttribute(poiURI, nameVal, lang, obURI);
                    nameAttributes.add(nameAttribute);
                } else {
                    TypedNameAttribute typedNameAttribute = new TypedNameAttribute(poiURI, nameVal, nType, lang, obURI);
                    typedNameAttributes.add(typedNameAttribute);
                }
            }
        }
        NameModel nameModel = new NameModel();
        nameModel.setTyped(typedNameAttributes);
        nameModel.setWithoutType(nameAttributes);

        return nameModel;
    }

    /**
     * Retrieve the previous score literal.
     * 
     * @param model the model.
     * @param scoreProperty the score property.
     * @return the score literal.
     */
    public static Literal getPreviousScore(Model model, Property scoreProperty) {
        NodeIterator previousScores = model.listObjectsOfProperty(scoreProperty);
        while(previousScores.hasNext()){
            RDFNode n = previousScores.next();
            return n.asLiteral();
        }
        return null;
    }
}
