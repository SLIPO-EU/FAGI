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
 *
 * @author nkarag
 */
public class RDFUtils {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RDFUtils.class);
    
    public static String getIdFromResource(String resourceString) {
        //example
        //input: http://slipo.eu/id/poi/0d1bb367-f3a5-10c1-b33c-381ef1e2f041
        //returns 0d1bb367-f3a5-10c1-b33c-381ef1e2f041
        int startPosition = StringUtils.ordinalIndexOf(resourceString, "/", 5) + 1;
        String id = resourceString.subSequence(startPosition, resourceString.length()).toString();

        return id;
    }

    public static String getIdFromResource(Resource resource) {

        String resourceString = resource.toString();
        int startPosition = StringUtils.ordinalIndexOf(resourceString, "/", 5) + 1;
        String id = resourceString.subSequence(startPosition, resourceString.length()).toString();

        return id;
    }

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
    
    public static String addBrackets(String node){
        return "<" + node + ">";
    }

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

    public static String extractGeometry(String wkt){

        if(wkt.startsWith(Namespace.CRS_4326)){
            String targetWKT = wkt.replaceAll(Namespace.CRS_4326, "").trim();
            return targetWKT;
        } else {
            return wkt;
        }
    }

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
    
    public static boolean isRejectedByPreviousRule(Model model) {
        //the link has been rejectedFromRule (or rejectedFromRule and marked ambiguous) by previous rule.
        //TODO: if size is 1, maybe strict check if the triple contains the ambiguity

        return model.isEmpty() || model.size() == 1;
    }

    public static Statement getAmbiguousLinkStatement(String uri1, String uri2) {

        Property ambiguousLink = ResourceFactory.createProperty(Namespace.LINKED_AMBIGUOUSLY);

        Resource resource1 = ResourceFactory.createResource(uri1);
        Resource resource2 = ResourceFactory.createResource(uri2);

        Statement statement = ResourceFactory.createStatement(resource1, ambiguousLink, resource2);

        return statement;
    }

    public static Statement getAmbiguousPropertyStatement(String uri, Property property) {

        Property ambiguousProperty = ResourceFactory.createProperty(Namespace.HAS_AMBIGUOUS_PROPERTY);

        Resource resource = ResourceFactory.createResource(uri);

        Statement statement = ResourceFactory.createStatement(resource, ambiguousProperty, property);

        return statement;
    }

    public static Statement getAmbiguousSubPropertyStatement(String uri, Property subProperty) {

        Property ambiguousSubProperty = ResourceFactory.createProperty(Namespace.HAS_AMBIGUOUS_SUB_PROPERTY);

        Resource resource = ResourceFactory.createResource(uri);

        Statement statement = ResourceFactory.createStatement(resource, ambiguousSubProperty, subProperty);

        return statement;
    }
    
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

    public static RDFNode getRDFNode(String property, Model model) {
        Property propertyRDF = getRDFPropertyFromString(property);

        if (propertyRDF != null) {
            return SparqlRepository.getObjectOfProperty(propertyRDF, model);
        } else {
            LOG.warn("Could not find literal with property {}", property);
            throw new RuntimeException("cannot find object of " + property);
        }
    }

    public static Literal getLiteralValue(String property, Model model) {
        Property propertyRDF = getRDFPropertyFromString(property);

        if (propertyRDF != null) {
            return SparqlRepository.getLiteralOfProperty(propertyRDF, model);
        } else {
            LOG.warn("Could not find literal with property {}", property);
            return ResourceFactory.createStringLiteral("");
        }
    }

    public static Property getRDFPropertyFromString(String property) {

        if (StringUtils.isBlank(property)) {
            return null;
        }

        Property propertyRDF = ResourceFactory.createProperty(property);

        return propertyRDF;
    }



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

    public static String getWKTLiteral(Geometry geometry) {

        WKTWriter wellKnownTextWriter = new WKTWriter();
        String wktString = wellKnownTextWriter.write(geometry);

        return wktString;
    }

    public static void validateGeometryProperty(Property property, EnumFusionAction action) throws WrongInputException {
        if (!property.toString().equals(Namespace.WKT)) {
            LOG.error("The selected action " + action.toString() + " applies only for WKT geometry literals");
            throw new WrongInputException("The selected action " + action.toString() + " applies only for WKT geometry literals");
        }
    }

    public static void validateActionForProperty(Property property, EnumFusionAction action) throws WrongInputException {
        if (property.toString().equals(Namespace.WKT)) {
            LOG.error("The selected action " + action.toString() + " does not apply on geometries.");
            throw new WrongInputException("The selected action " + action.toString() + " does not apply on geometries.");
        }
    }
    
    public static void validateNameAction(CustomRDFProperty property) throws WrongInputException {
        if(property.isSingleLevel()){
            if(!property.getValueProperty().toString().equals(Namespace.NAME_NO_BRACKETS)){
            LOG.error("The \"keep-most-complete-name\" can be applied only on the name property.");
            throw new WrongInputException("The \"keep-most-complete-name\" can be applied only on the name property.");
            }
        } else {
            LOG.error("The \"keep-most-complete-name\" can be applied only on name property as a parent");
            throw new WrongInputException("The \"keep-most-complete-name\" can be applied only on name property as a parent");
        }
    }

    public static Statement getInterlinkingScore(String uri, float score) {

        Property fusedPoiScoreProperty = ResourceFactory.createProperty(Namespace.INTERLINKING_SCORE);
        Resource resource = ResourceFactory.createResource(uri);
        Literal literal = ResourceFactory.createTypedLiteral(score);
        Statement statement = ResourceFactory.createStatement(resource, fusedPoiScoreProperty, literal);

        return statement;
    }

    public static String getUnlinkedFlag(String uri) {
        String flag = uri + " " + Namespace.FUSION_SCORE + Namespace.ORIGINAL_LITERAL;
        return flag;
    }

    public static Statement getFusionConfidenceStatement(String fusedUri, Model modelA, Model modelB, Model fusedModel) {

        Resource fusedRes = ResourceFactory.createResource(fusedUri);
        Property confidenceProperty = ResourceFactory.createProperty(Namespace.FUSION_CONFIDENCE_NO_BRACKETS);

        List<Double> sims = new ArrayList<>();
        Double nameSimilarity = computeNameSimilarity(modelA, modelB);
        Double geoSimilarity = computeGeoSimilarity(modelA, modelB);
        Double phoneSimilarity = computePhoneSimilarity(modelA, modelB);
        
        if(nameSimilarity != null){
            sims.add(nameSimilarity);
        }
        
        if(geoSimilarity != null){
            sims.add(geoSimilarity);
        }
        
        if(phoneSimilarity != null){
            sims.add(phoneSimilarity);
        }

        double sum = sims.stream().mapToDouble(Double::doubleValue).sum();
        Double confidence =  sum / (double) sims.size(); 

        Literal confidenceLiteral = ResourceFactory.createTypedLiteral(confidence.floatValue());

        Statement statement = ResourceFactory.createStatement(fusedRes, confidenceProperty , confidenceLiteral);

        return statement;
    }

    public static Statement getFusionScoreStatement(String fusedUri, String nodeA, String nodeB, Model modelA, 
            Model modelB, Model fusedModel) {
        //get previous score if exists. Append new score
        Resource resA = ResourceFactory.createResource(nodeA);
        Resource resB = ResourceFactory.createResource(nodeB);
        Resource fusedRes = ResourceFactory.createResource(fusedUri);

        float fusionScore = computeFusionScore(fusedRes, resA, resB, modelA, modelB, fusedModel);

        Property scoreProperty = ResourceFactory.createProperty(Namespace.FUSION_SCORE_NO_BRACKETS);

        Literal scoreA = SparqlRepository.getPreviousScore(resA, modelA);
        Literal scoreB = SparqlRepository.getPreviousScore(resB, modelB);
        Literal scoreLiteral = constructScoreString(scoreA, scoreB, fusionScore);

        Statement statement = ResourceFactory.createStatement(fusedRes, scoreProperty , scoreLiteral);

        return statement;
    }

    public static float computeFusionScore(Resource fusedUri, Resource nodeA, Resource nodeB, Model modelA, Model modelB, Model fusedModel) {
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

    private static Literal constructScoreString(Literal scoreA, Literal scoreB, float fusionScore) {
        String a;
        String b;
        if(scoreA == null){
            a = "original";
        } else {
            a = scoreA.toString();
        }
        if(scoreB == null){
            b = "original";
        } else {
            b = scoreB.toString();
        }

        String scoreString = "{scoreA: " + a + ", scoreB: " + b + ", fusionScore: " + fusionScore + "}";
        return ResourceFactory.createTypedLiteral(scoreString);
    }

    private static Double computeNameSimilarity(Model modelA, Model modelB) {
        List<String> namesA = SparqlRepository.getLiteralsFromPropertyChain(Namespace.NAME_NO_BRACKETS, 
                Namespace.NAME_VALUE_NO_BRACKETS, modelA);
        List<String> namesB = SparqlRepository.getLiteralsFromPropertyChain(Namespace.NAME_NO_BRACKETS, 
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

    private static Double computeGeoSimilarity(Model modelA, Model modelB) {
        Literal geoA = SparqlRepository.getLiteralFromPropertyChain(Namespace.GEOSPARQL_HAS_GEOMETRY, Namespace.WKT, modelA);
        Literal geoB = SparqlRepository.getLiteralFromPropertyChain(Namespace.GEOSPARQL_HAS_GEOMETRY, Namespace.WKT, modelB);

        if(geoA == null || geoB == null){
            return null;
        }

        Double distance = MinimumOrthodromicDistance.compute(geoA, geoB);
        
        //arbitrary value for normalizing to a similarity. Distance greater than 300 meters is considered as 0 similarity.
        if(distance > 300){
            return 0.0;
        } else {
            return 1 - (distance / (double) 300);
        }
    }
}
