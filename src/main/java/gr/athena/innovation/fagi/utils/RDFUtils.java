package gr.athena.innovation.fagi.utils;

import gr.athena.innovation.fagi.model.CustomRDFProperty;
import gr.athena.innovation.fagi.model.Entity;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class RDFUtils {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RDFUtils.class);
    
    public static String getIdFromResource(String resourceString) {

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

    public static String getLocalName(CustomRDFProperty property) {
        String localName;
        if(property.isSingleLevel()){
            localName = SpecificationConstants.Mapping.myMap.get(property.getValueProperty().toString());
        } else {
            localName = SpecificationConstants.Mapping.myMap.get(property.getParent().toString());
        }
        if(localName == null){
            LOG.warn("fail fail with property " + property.getParent() + " " + property.getValueProperty());
        }
        return localName;
    }
    
    
    public static Resource getRootResource(Entity leftNode, Entity rightNode) {

        EnumOutputMode mode = Configuration.getInstance().getOutputMode();

        switch (mode) {
            case AA_MODE:
            case AB_MODE:
            case A_MODE:
            case L_MODE:
            case DEFAULT:
                return SparqlRepository.getSubjectOfProperty(Namespace.SOURCE_NO_BRACKETS, leftNode.getEntityData().getModel());
            case BB_MODE:
            case BA_MODE:
            case B_MODE:
                return SparqlRepository.getSubjectOfProperty(Namespace.SOURCE_NO_BRACKETS, rightNode.getEntityData().getModel());
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
                resource = SparqlRepository.getSubjectOfProperty(Namespace.SOURCE_NO_BRACKETS, leftNode.getEntityData().getModel());
                String resourceString = resource.toString() +"/"+ RDFUtils.getLocalName(property);
                return ResourceFactory.createResource(resourceString);
            }
            case BB_MODE:
            case BA_MODE:
            case B_MODE:{
                resource = SparqlRepository.getSubjectOfProperty(Namespace.SOURCE_NO_BRACKETS, rightNode.getEntityData().getModel());
                String resourceString = resource.toString() +"/"+ RDFUtils.getLocalName(property);
                return ResourceFactory.createResource(resourceString);
            }
            default:
                LOG.fatal("Cannot resolved fused Entity's URI. Check Default fused output mode.");
                throw new IllegalArgumentException();
        }
    }
}
