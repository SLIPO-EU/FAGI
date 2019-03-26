package gr.athena.innovation.fagi.utils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import gr.athena.innovation.fagi.core.action.EnumFusionAction;
import gr.athena.innovation.fagi.core.function.geo.MinimumOrthodromicDistance;
import gr.athena.innovation.fagi.core.normalizer.phone.PhoneNumberNormalizer;
import gr.athena.innovation.fagi.core.similarity.JaroWinkler;
import gr.athena.innovation.fagi.core.similarity.Levenshtein;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.CustomRDFProperty;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;

/**
 * Utilities for String/RDF manipulation.
 * 
 * @author nkarag
 */
public class RDFUtils {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RDFUtils.class);
    
    /**
     * Returns the alphanumeric ID of a URI resource as String.
     * 
     * @param resourceString the resource string.
     * @return the ID string.
     */
    public static String getIdFromResource(String resourceString) {
        //example
        //input: http://slipo.eu/id/poi/0d1bb367-f3a5-10c1-b33c-381ef1e2f041
        //returns 0d1bb367-f3a5-10c1-b33c-381ef1e2f041
        int startPosition = StringUtils.ordinalIndexOf(resourceString, "/", 5) + 1;
        String id = resourceString.subSequence(startPosition, resourceString.length()).toString();

        return id;
    }

    /**
     * Returns the alphanumeric ID of a URI resource object.
     * 
     * @param resource the resource.
     * @return the ID string.
     */
    public static String getIdFromResource(Resource resource) {

        String resourceString = resource.toString();
        int startPosition = StringUtils.ordinalIndexOf(resourceString, "/", 5) + 1;
        String id = resourceString.subSequence(startPosition, resourceString.length()).toString();

        return id;
    }

    /**
     * Returns the alphanumeric ID of a URI resource-part as String. 
     * Resource-part can be any string that comes from splitting a triple on white-space.
     * 
     * @param resourcePart the resource part.
     * @return the ID as a string.
     */
    public static String getIdFromResourcePart(String resourcePart) {
        //expects: <namespace:id> or <namespace:id/localname>
        int endPosition = StringUtils.lastIndexOf(resourcePart, "/");
        int startPosition = StringUtils.ordinalIndexOf(resourcePart, "/", 5) + 1;
        String res;
        if(resourcePart.substring(startPosition).contains("/")){
            res = resourcePart.subSequence(startPosition, endPosition).toString();
        } else {
            res = resourcePart.subSequence(startPosition, resourcePart.length()-1).toString();
        }

        return res;
    }

    /**
     * Returns the local-name of a custom RDF property.
     * @param property the property.
     * @return the local-name.
     */
    public static String getLocalName(CustomRDFProperty property) {
        String localName;
        if(property.isSingleLevel()){
            localName = SpecificationConstants.Mapping.PROPERTY_MAPPINGS.get(property.getValueProperty().toString());
        } else {
            localName = SpecificationConstants.Mapping.PROPERTY_MAPPINGS.get(property.getParent().toString());
        }
        if(localName == null){
            LOG.warn("Failed to retrieve mapping with property " + property.getParent() + " " + property.getValueProperty());
            //do not stop fusion due to this. Single level properties do not have localnames
            //throw new ApplicationException("Property mapping does not exist.");
        }
        return localName;
    }
    
    /**
     * Adds brackets to a String node.
     * @param node the node as a string.
     * @return the string node with brackets.
     */
    public static String addBrackets(String node){
        return "<" + node + ">";
    }

    /**
     * Removes brackets of the given string node.
     * @param node the node as a string.
     * @return the string node without brackets.
     */
    public static String removeBrackets(String node){
        return node.substring(1, node.length()-1);
    }

    /**
     * This method basically removes the CRS prefix from a WKT Literal.
     * 
     * @param wkt the literal that contains the geometry.
     * @return the WKT literal with the CRS prefix removed
     */
    public static Literal extractGeometry(Literal wkt){
        String lexicalForm = wkt.getLexicalForm();
        RDFDatatype geometryDatatype = Namespace.WKT_RDF_DATATYPE;
        
        if(lexicalForm.startsWith(Namespace.CRS_4326)){
            lexicalForm = lexicalForm.replaceAll(Namespace.CRS_4326, "").trim();
            Literal wktLiteral = ResourceFactory.createTypedLiteral(lexicalForm, geometryDatatype);
            
            return wktLiteral;
        } else {
            return wkt;
        }
    }

    /**
     * Extracts a geometry from a WKT string by checking the CRS prefix.
     * 
     * @param wkt the WKT.
     * @return the WKT without CRS prefix.
     */
    public static String extractGeometry(String wkt){
        if(wkt.startsWith(Namespace.CRS_4326)){
            String targetWKT = wkt.replaceAll(Namespace.CRS_4326, "").trim();
            return targetWKT;
        } else {
            return wkt;
        }
    }

    /**
     * Returns the root resource based on the fusion mode.
     * 
     * @param leftNode the left node.
     * @param rightNode the right node.
     * @return the root resource.
     */
    public static Resource getRootResource(Entity leftNode, Entity rightNode) {

        EnumOutputMode mode = Configuration.getInstance().getOutputMode();

        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                return SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, leftNode.getEntityData().getModel());
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                return SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, rightNode.getEntityData().getModel());
            default:
                LOG.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
    }
    
    /**
     * Returns the resource based on the fusion mode and the given property.
     * 
     * @param leftNode the left node.
     * @param rightNode the right node.
     * @param property the custom RDF property.
     * @return the resource.
     */
    public static Resource resolveResource(Entity leftNode, Entity rightNode, CustomRDFProperty property) {

        EnumOutputMode mode = Configuration.getInstance().getOutputMode();
        Resource resource;
        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
            {
                resource = SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, leftNode.getEntityData().getModel());
                String localName = RDFUtils.getLocalName(property);
                if(localName == null){
                    //todo: Single level properties do not have localnames. Check if is single level and avoid null check
                    return ResourceFactory.createResource(resource.toString());
                }
                String resourceString = resource.toString() +"/"+ RDFUtils.getLocalName(property);
                return ResourceFactory.createResource(resourceString);
            }
            case BB_MODE:
            case BA_MODE:
            case B_MODE:{
                resource = SparqlRepository.getSubjectOfSingleProperty(Namespace.SOURCE_NO_BRACKETS, rightNode.getEntityData().getModel());
                String localName = RDFUtils.getLocalName(property);
                if(localName == null){
                    return ResourceFactory.createResource(resource.toString());
                }
                String resourceString = resource.toString() +"/"+ RDFUtils.getLocalName(property);
                return ResourceFactory.createResource(resourceString);
            }
            default:
                LOG.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
    }
    
    /**
     * Checks if the given model is rejected from a rule previously. 
     * A rejected model is always empty, so this method simply checks the size of the model.
     * 
     * @param model the model.
     * @return true if the model is rejected, else otherwise.
     */
    public static boolean isRejectedByPreviousRule(Model model) {
        //the link has been rejectedFromRule (or rejectedFromRule and marked ambiguous) by previous rule.
        //TODO: if size is 1, maybe strict check if the triple contains the ambiguity

        return model.isEmpty() || model.size() == 1;
    }

    /**
     * Constructs a statement of an ambiguous link.
     * 
     * @param uri1 the first URI.
     * @param uri2 the second URI.
     * @return the statement.
     */
    public static Statement getAmbiguousLinkStatement(String uri1, String uri2) {

        Property ambiguousLink = ResourceFactory.createProperty(Namespace.LINKED_AMBIGUOUSLY);

        Resource resource1 = ResourceFactory.createResource(uri1);
        Resource resource2 = ResourceFactory.createResource(uri2);

        Statement statement = ResourceFactory.createStatement(resource1, ambiguousLink, resource2);

        return statement;
    }

    /**
     * Constructs a statement of an ambiguous property.
     * 
     * @param uri the URI.
     * @param property the property.
     * @return the statement.
     */
    public static Statement getAmbiguousPropertyStatement(String uri, Property property) {

        Property ambiguousProperty = ResourceFactory.createProperty(Namespace.HAS_AMBIGUOUS_PROPERTY);

        Resource resource = ResourceFactory.createResource(uri);

        Statement statement = ResourceFactory.createStatement(resource, ambiguousProperty, property);

        return statement;
    }

    /**
     * Constructs a statement of an ambiguous sub-property.
     * 
     * @param uri the URI.
     * @param subProperty the sub-property.
     * @return the statement.
     */
    public static Statement getAmbiguousSubPropertyStatement(String uri, Property subProperty) {

        Property ambiguousSubProperty = ResourceFactory.createProperty(Namespace.HAS_AMBIGUOUS_SUB_PROPERTY);

        Resource resource = ResourceFactory.createResource(uri);

        Statement statement = ResourceFactory.createStatement(resource, ambiguousSubProperty, subProperty);

        return statement;
    }
    
    /**
     * Renames an entity based of the URI of another,
     * 
     * @param entityToBeRenamed the entity to be renamed.
     * @param entity the base entity.
     */
    public static void renameResourceURIs(Entity entityToBeRenamed, Entity entity) {
        Model original = entityToBeRenamed.getEntityData().getModel();
        Iterator<Statement> statementIterator = original.listStatements().toList().iterator();

        Model newModel = ModelFactory.createDefaultModel();
        while (statementIterator.hasNext()) {

            Statement statement = statementIterator.next();

            String newSub = statement.getSubject().toString().replaceAll(entityToBeRenamed.getResourceURI(), entity.getResourceURI());
            String newPred = statement.getPredicate().toString().replaceAll(entityToBeRenamed.getResourceURI(), entity.getResourceURI());
            String newOb = statement.getObject().toString().replaceAll(entityToBeRenamed.getResourceURI(), entity.getResourceURI());

            Resource subject = ResourceFactory.createResource(newSub);
            Property predicate = ResourceFactory.createProperty(newPred);

            RDFNode object;
            if(statement.getObject().isResource()){
                object = ResourceFactory.createResource(newOb);
            } else if(statement.getObject().isLiteral()){
                object = statement.getObject();
            } else {
                object = statement.getObject();
            }

            Statement newStatement = ResourceFactory.createStatement(subject, predicate, object);

            newModel.add(newStatement);
        }
        entityToBeRenamed.getEntityData().setModel(newModel);
    }
    
    /**
     * Parses a geometry from a string literal.
     * 
     * @param literal the literal.
     * @return the geometry object.
     */
    public static Geometry parseGeometry(String literal) {

        String literalWithoutCRS = literal.replace(Namespace.CRS_4326 + " ", "");

        int sourceSRID = 4326; //All features assumed in WGS84 lon/lat coordinates

        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(), sourceSRID);

        WKTReader wellKnownTextReader = new WKTReader(geomFactory);

        Geometry geometry = null;
        try {
            geometry = wellKnownTextReader.read(literalWithoutCRS);
        } catch (ParseException ex) {
            LOG.fatal("Error parsing geometry literal " + literalWithoutCRS);
            LOG.fatal(ex);
        }

        return geometry;
    }

    /**
     * Returns the RDF node of the given property in the model.
     * 
     * @param property the property.
     * @param model the model.
     * @return the RDF node.
     */
    public static RDFNode getRDFNode(String property, Model model) {
        Property propertyRDF = getRDFPropertyFromString(property);

        if (propertyRDF != null) {
            return SparqlRepository.getObjectOfProperty(propertyRDF, model);
        } else {
            LOG.warn("Could not find literal with property {}", property);
            throw new RuntimeException("cannot find object of " + property);
        }
    }

    /**
     * Returns the literal of the given property in the given model.
     * 
     * @param property the property.
     * @param model the model.
     * @return the literal object.
     */
    public static Literal getLiteralValue(String property, Model model) {
        Property propertyRDF = getRDFPropertyFromString(property);

        if (propertyRDF != null) {
            return SparqlRepository.getLiteralOfProperty(propertyRDF, model);
        } else {
            LOG.warn("Could not find literal with property {}", property);
            return ResourceFactory.createStringLiteral("");
        }
    }

    /**
     * Constructs a property from a string using the resource factory.
     * @param property the property.
     * @return the property.
     */
    public static Property getRDFPropertyFromString(String property) {

        if (StringUtils.isBlank(property)) {
            return null;
        }

        Property propertyRDF = ResourceFactory.createProperty(property);

        return propertyRDF;
    }

    /**
     * Constructs a custom RDF property based on the given string property.
     * 
     * @param property the property.
     * @return the custom RDF property.
     */
    public static CustomRDFProperty getCustomRDFPropertyFromString(String property) {

        if (StringUtils.isBlank(property)) {
            throw new IllegalArgumentException("Property string is empty or null.");
        }

        CustomRDFProperty customRDFProperty = new CustomRDFProperty();
        if(property.contains(" ")){
            String[] parts = property.split(" ");
            customRDFProperty.setSingleLevel(false);
            customRDFProperty.setParent(ResourceFactory.createProperty(parts[0]));
            customRDFProperty.setValueProperty(ResourceFactory.createProperty(parts[1]));
            
        } else {
            customRDFProperty.setSingleLevel(true);
            customRDFProperty.setValueProperty(ResourceFactory.createProperty(property));
        }

        return customRDFProperty;
    }

    /**
     * Returns the literal of the given property chain in the model.
     * 
     * @param property1 the parent property.
     * @param property2 the child property.
     * @param model the model.
     * @return the literal.
     */
    public static Literal getLiteralValueFromChain(String property1, String property2, Model model) {
        if (property1 != null) {
            Literal literal = SparqlRepository.getLiteralFromPropertyChain(property1, property2, model, true);
            if(literal == null){
                literal = SparqlRepository.getLiteralFromPropertyChain(property1, property2, model, false);
            }
            return literal;
        } else {
            LOG.warn("Could not find literal with properties {}", property1, property2);
            return ResourceFactory.createStringLiteral("");
        }
    }

    /**
     * Returns the RDF node of the given property chain in the model.
     * 
     * @param property1 the parent property.
     * @param property2 the child property.
     * @param model the model.
     * @return the RDF node.
     */
    public static RDFNode getRDFNodeFromChain(String property1, String property2, Model model) {
        if (property1 != null) {
            RDFNode node = SparqlRepository.getObjectOfPropertyChain(property1, property2, model, true);
            if(node == null){
                node = SparqlRepository.getObjectOfPropertyChain(property1, property2, model, false);
            }
            return node;
        } else {
            LOG.warn("Could not find literal with properties {}", property1, property2);
            throw new RuntimeException("cannot find object of " + property1 + " " + property2);
        }
    }

    /**
     * Returns the WKT literal given a geometry object.
     * 
     * @param geometry the geometry object.
     * @return the WKT string.
     */
    public static String getWKTLiteral(Geometry geometry) {

        WKTWriter wellKnownTextWriter = new WKTWriter();
        String wktString = wellKnownTextWriter.write(geometry);

        return wktString;
    }

    /**
     * Checks if the property can be used for geometric fusion.
     * 
     * @param property the property.
     * @param action the action.
     * @throws WrongInputException wrong input.
     */
    public static void validateGeometryProperty(Property property, EnumFusionAction action) throws WrongInputException {
        if (!property.toString().equals(Namespace.WKT)) {
            LOG.error("The selected action " + action.toString() + " applies only for WKT geometry literals");
            throw new WrongInputException("The selected action " + action.toString() + " applies only for WKT geometry literals");
        }
    }

    /**
     * Checks if the property refers to WKT and throws exception if it does not.
     * 
     * @param property the property.
     * @param action the action.
     * @throws WrongInputException wrong input.
     */
    public static void validateActionForProperty(Property property, EnumFusionAction action) throws WrongInputException {
        if (property.toString().equals(Namespace.WKT)) {
            LOG.error("The selected action " + action.toString() + " does not apply on geometries.");
            throw new WrongInputException("The selected action " + action.toString() + " does not apply on geometries.");
        }
    }
    
    /**
     * Checks if the property refers to a name based on the SLIPO-ontology.
     * @param property the property.
     * @throws WrongInputException wrong input.
     */
    public static void validateNameAction(CustomRDFProperty property) throws WrongInputException {
        if(property.isSingleLevel()){
            if(!property.getValueProperty().toString().equals(Namespace.NAME_NO_BRACKETS)){
            LOG.error("The \"keep-most-complete-name\" can be applied only on the name property.");
            throw new WrongInputException("The \"keep-most-complete-name\" can be applied only on the name property.");
            }
        } else {
            if(!property.getParent().toString().equals(Namespace.NAME_NO_BRACKETS)){
                LOG.error("The \"keep-most-complete-name\" can be applied only on the name property.");
                throw new WrongInputException("The \"keep-most-complete-name\" can be applied only on the name property.");
            }
        }
    }

    /**
     * Returns the statement of the interlinking score.
     * 
     * @param uri the URI.
     * @param score the score.
     * @param modelA the model of A.
     * @param modelB the model of B.
     * @return the statement.
     */
    public static Statement getInterlinkingScore(String uri, float score, Model modelA, Model modelB) {

        Resource resource = ResourceFactory.createResource(uri);
        Property scoreProperty = ResourceFactory.createProperty(Namespace.INTERLINKING_SCORE);

        Literal scoreA = SparqlRepository.getPreviousScore(modelA, scoreProperty);
        Literal scoreB = SparqlRepository.getPreviousScore(modelB, scoreProperty);
        Literal scoreLiteral = constructScoreLiteral(scoreA, scoreB, score);

        Statement statement = ResourceFactory.createStatement(resource, scoreProperty , scoreLiteral);

        return statement;
    }

    /**
     * Returns a string that denotes a fusion value for unlinked URI nodes.
     * @param uri the URI.
     * @return the unlinked flag.
     */
    public static String getUnlinkedFlag(String uri) {
        String flag = uri + " " + Namespace.FUSION_GAIN + Namespace.ORIGINAL_LITERAL;
        return flag;
    }

    /**
     * Returns the fusion confidence statement.
     * 
     * @param fusedUri the URI.
     * @param modelA the model of A.
     * @param modelB the model of B.
     * @param fusedModel the fused model.
     * @return the statement.
     */
    public static Statement getFusionConfidenceStatement(String fusedUri, Model modelA, Model modelB, Model fusedModel) {
        Resource fusedRes = ResourceFactory.createResource(fusedUri);
        Property confidenceProperty = ResourceFactory.createProperty(Namespace.FUSION_CONFIDENCE_NO_BRACKETS);

        Double conf = computeFusionConfidence(modelA, modelB);
        
        Literal confidenceLiteral = ResourceFactory.createTypedLiteral(conf.floatValue());

        Statement statement = ResourceFactory.createStatement(fusedRes, confidenceProperty , confidenceLiteral);

        return statement;
    }
    
    /**
     * Computes the fusion confidence.
     * 
     * @param modelA the model of A.
     * @param modelB the model of B.
     * @return the fusion confidence score.
     */
    public static Double computeFusionConfidence(Model modelA, Model modelB){
        
        List<Double> sims = new ArrayList<>();
        Double nameSimilarity = computeNameSimilarity(modelA, modelB);
        Double phoneSimilarity = computePhoneSimilarity(modelA, modelB);
        Double streetSimilarity = computeAddressStreetSimilarity(modelA, modelB);

        Double geoSimilarity = computeGeoSimilarity(modelA, modelB);

        if(nameSimilarity != null){
            sims.add(nameSimilarity);
        }

        if(streetSimilarity != null){
            sims.add(streetSimilarity);
        }

        if(phoneSimilarity != null){
            sims.add(phoneSimilarity);
        }

        if(geoSimilarity != null){
            sims.add(geoSimilarity);
        }

        double sum = sims.stream().mapToDouble(Double::doubleValue).sum();
        Double confidence =  sum / (double) sims.size(); 
        
        return confidence;
    }

    /**
     * Returns the fusion-gain statement.
     * 
     * @param fusedUri the URI.
     * @param nodeA the left node.
     * @param nodeB the right node.
     * @param modelA the left model.
     * @param modelB the right model.
     * @param fusedModel the fused model.
     * @return the statement.
     */
    public static Statement getFusionGainStatement(String fusedUri, String nodeA, String nodeB, Model modelA, 
            Model modelB, Model fusedModel) {
        //get previous score if exists. Append new score
        Resource resA = ResourceFactory.createResource(nodeA);
        Resource resB = ResourceFactory.createResource(nodeB);
        Resource fusedRes = ResourceFactory.createResource(fusedUri);

        float fusionGain = computeFusionGain(fusedRes, resA, resB, modelA, modelB, fusedModel);

        Property scoreProperty = ResourceFactory.createProperty(Namespace.FUSION_GAIN_NO_BRACKETS);

        Literal scoreA = SparqlRepository.getPreviousScore(modelA, scoreProperty);
        Literal scoreB = SparqlRepository.getPreviousScore(modelB, scoreProperty);
        Literal gainLiteral = constructScoreLiteral(scoreA, scoreB, fusionGain);

        Statement statement = ResourceFactory.createStatement(fusedRes, scoreProperty , gainLiteral);

        return statement;
    }

    /**
     * Computes the fusion-gain score.
     * 
     * @param fusedUri the fused URI.
     * @param nodeA the left node.
     * @param nodeB the right node.
     * @param modelA the left model.
     * @param modelB the right model.
     * @param fusedModel the fused model.
     * @return the fusion-gain score.
     */
    public static float computeFusionGain(Resource fusedUri, Resource nodeA, Resource nodeB, Model modelA, Model modelB, Model fusedModel) {
        Set<Property> propsA = SparqlRepository.getDistinctPropertiesOfResource(modelA, nodeA);
        Set<Property> propsB = SparqlRepository.getDistinctPropertiesOfResource(modelB, nodeB);
        Set<Property> fusedProps = SparqlRepository.getDistinctPropertiesOfResource(fusedModel, fusedUri);
        Collection common = CollectionUtils.intersection(propsA, propsB);

        if(propsA.isEmpty() && propsB.isEmpty()){
            return 0f;
        }

        Double score = (fusedProps.size() - common.size())/ (double)(propsA.size() + propsB.size());

        return score.floatValue();
    }

    /**
     * Returns the last computed fusion-gain score from the given literal.
     * Example of fusion gain literal:{scoreA: 1.0, scoreB: 1.0, score: 0.11111111}"
     * 
     * @param literal the literal.
     * @return the score.
     */
    public static Double getLastFusionGainFromLiteral(Literal literal) {

        String value = literal.getString();
        
        int startIndex = value.lastIndexOf(' '); 
        int endIndex = value.lastIndexOf('}');
        if(startIndex == -1 || endIndex == -1){
            LOG.warn("cannot parse fusion gain from: " + literal);
            throw new ApplicationException("cannot parse fusion gain from: " + literal);
        }

        String gainString = value.substring(startIndex, endIndex);
        
        Double gain = Double.parseDouble(gainString);
        return gain;
    }

    /**
     * Constructs an intermediate node for POI ensemble fusion.
     * 
     * @param resourceURI the resource URI.
     * @param localName the local-name.
     * @param i an integer value to construct different nodes for same properties.
     * @return the intermediate node as a string.
     */
    public static String constructIntermediateEnsembleNode(Resource resourceURI, String localName, int i) {
        String uri = resourceURI + "/" + localName + "_" + i;
        return uri;
    }

    private static Literal constructScoreLiteral(Literal scoreA, Literal scoreB, float score) {
        String a;
        String b;
        if(scoreA == null){
            a = "1.0";
        } else {
            a = scoreA.toString();
        }
        if(scoreB == null){
            b = "1.0";
        } else {
            b = scoreB.toString();
        }

        String scoreString = "{scoreA: " + a + ", scoreB: " + b + ", score: " + score + "}";
        return ResourceFactory.createTypedLiteral(scoreString);
    }

    private static Double computeNameSimilarity(Model modelA, Model modelB) {
        List<String> namesA = SparqlRepository.getLiteralStringsFromPropertyChain(Namespace.NAME_NO_BRACKETS, 
                Namespace.NAME_VALUE_NO_BRACKETS, modelA);
        List<String> namesB = SparqlRepository.getLiteralStringsFromPropertyChain(Namespace.NAME_NO_BRACKETS, 
                Namespace.NAME_VALUE_NO_BRACKETS, modelB);
        
        if(namesA.isEmpty() || namesB.isEmpty()){
            return null;
        }
        
        double maxSimilarity = 0;
        for (String stringA : namesA ) {
            for (String stringB : namesB) {
                double tempSimilarity = JaroWinkler.computeSimilarity(stringA, stringB);
                if(tempSimilarity > maxSimilarity){
                    maxSimilarity = tempSimilarity;
                }
            }
        }

        return maxSimilarity;
    }

    private static Double computePhoneSimilarity(Model modelA, Model modelB) {
        Literal phoneA = SparqlRepository.getLiteralFromPropertyChain(Namespace.PHONE_NO_BRACKETS, 
                Namespace.CONTACT_VALUE_NO_BRACKETS, modelA);
        Literal phoneB = SparqlRepository.getLiteralFromPropertyChain(Namespace.PHONE_NO_BRACKETS, 
                Namespace.CONTACT_VALUE_NO_BRACKETS, modelB);

        if(phoneA == null || phoneB == null){
            return null;
        }

        String a = PhoneNumberNormalizer.removeNonNumericCharacters(phoneA.getLexicalForm());
        String b = PhoneNumberNormalizer.removeNonNumericCharacters(phoneB.getLexicalForm());

        return Levenshtein.computeSimilarity(a, b, null);

    }

    private static Double computeAddressStreetSimilarity(Model modelA, Model modelB) {
        Literal streetA = SparqlRepository.getLiteralFromPropertyChain(Namespace.ADDRESS_NO_BRACKETS, 
                Namespace.STREET_NO_BRACKETS, modelA);
        Literal streetB = SparqlRepository.getLiteralFromPropertyChain(Namespace.ADDRESS_NO_BRACKETS, 
                Namespace.STREET_NO_BRACKETS, modelB);

        if(streetA == null || streetB == null){
            return null;
        }
        
        return JaroWinkler.computeSimilarity(streetA.toString(), streetB.toString());
    }

    private static Double computeGeoSimilarity(Model modelA, Model modelB) {
        Literal geoA = SparqlRepository.getLiteralFromPropertyChain(Namespace.GEOSPARQL_HAS_GEOMETRY, Namespace.WKT, modelA);
        Literal geoB = SparqlRepository.getLiteralFromPropertyChain(Namespace.GEOSPARQL_HAS_GEOMETRY, Namespace.WKT, modelB);

        if(geoA == null || geoB == null){
            return null;
        }

        Double distance = MinimumOrthodromicDistance.compute(geoA, geoB);
        
        if(distance == null){
            return 0.0;
        }
        //arbitrary value for normalizing to a similarity. Distance greater than 300 meters is considered as 0 similarity.
        if(distance > 300){
            return 0.0;
        } else {
            return 1 - (distance / (double) 300);
        }
    }
}
