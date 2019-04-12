package gr.athena.innovation.fagi.core;

import gr.athena.innovation.fagi.model.CustomRDFProperty;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.repository.SparqlRepository;
import gr.athena.innovation.fagi.rule.RuleSpecification;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.utils.RDFUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;

/**
 * Class for fusing ensemble POIS.
 *
 * @author nkarag
 */
public class EnsembleFuser {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(EnsembleFuser.class);

    private final RuleSpecification ruleSpecification;

    public EnsembleFuser(RuleSpecification ruleSpecification) {
        this.ruleSpecification = ruleSpecification;
    }

    /**
     * Method for fusing an ensemble link.
     * 
     * @param link the link.
     * @param modelsA the models from A.
     * @param modelsB the models from B.
     * @return the fused model.
     */
    public Model fuseEnsemble(Link link, Map<String, Model> modelsA, Map<String, Model> modelsB) {

        Set<CustomRDFProperty> functionalProps = RDFUtils.convertToCustomRDFProperty(ruleSpecification.getFunctionalProperties());
        Set<CustomRDFProperty> nonFunctionalProps = RDFUtils.convertToCustomRDFProperty(ruleSpecification.getNonFunctionalProperties());
        
        Model fusedModel;
        final EnumOutputMode mode = Configuration.getInstance().getOutputMode();

        switch (mode) {
            case AA_MODE:
            case A_MODE:
            case AB_MODE:
            case L_MODE: {

                fusedModel = modelsA.values().iterator().next(); //modelsA contains a single model in A based modes.

                fuse(FusionStrategy.KEEP_UNIQUE_BY_VOTE, functionalProps, fusedModel, modelsB);
                fuse(FusionStrategy.KEEP_ALL, nonFunctionalProps, fusedModel, modelsB);

                break;
            }
            case BB_MODE:
            case B_MODE:
            case BA_MODE: {

                fusedModel = modelsB.values().iterator().next(); //modelsB contains a single model in B based modes.

                fuse(FusionStrategy.KEEP_UNIQUE_BY_VOTE, functionalProps, fusedModel, modelsA);
                fuse(FusionStrategy.KEEP_ALL, nonFunctionalProps, fusedModel, modelsA);

                break;
            }
            default:
                throw new UnsupportedOperationException("Wrong fusion mode!");
        }

        return fusedModel;
    }

    private void fuse(FusionStrategy strategy, Set<CustomRDFProperty> properties, Model fusedModel,
            Map<String, Model> models) {

        switch (strategy) {
            case KEEP_UNIQUE_BY_VOTE:
                for (CustomRDFProperty prop : properties) {
                    Resource resourceURI;
                    if (prop.isSingleLevel()) {
                        resourceURI = SparqlRepository.getSubjectOfSingleProperty(prop.getValueProperty().toString(), fusedModel);
                    } else {
                        resourceURI = SparqlRepository.getSubjectOfSingleProperty(prop.getParent().toString(), fusedModel);
                    }

                    keepUnique(resourceURI, prop, fusedModel, models);
                }
                break;
            case KEEP_ALL:
                for (CustomRDFProperty prop : properties) {
                    Resource resourceURI;
                    if (prop.isSingleLevel()) {
                        resourceURI = SparqlRepository.getSubjectOfSingleProperty(prop.getValueProperty().toString(), fusedModel);
                    } else {
                        resourceURI = SparqlRepository.getSubjectOfSingleProperty(prop.getParent().toString(), fusedModel);
                    }
                    
                    keepAll(resourceURI, prop, fusedModel, models);
                }
                break;
            case KEEP_ANY:
                for (CustomRDFProperty prop : properties) {
                    Resource resourceURI;
                    if (prop.isSingleLevel()) {
                        resourceURI = SparqlRepository.getSubjectOfSingleProperty(prop.getValueProperty().toString(), fusedModel);
                    } else {
                        resourceURI = SparqlRepository.getSubjectOfSingleProperty(prop.getParent().toString(), fusedModel);
                    }

                    keepAny(resourceURI, prop, fusedModel, models);
                }
                break;
        }
    }

    private void keepUnique(Resource subject, CustomRDFProperty prop, Model fusedModel, Map<String, Model> models) {

        if (prop.isSingleLevel()) {
            List<Literal> literals = SparqlRepository.getLiteralsOfProperty(prop.getValueProperty(), fusedModel);
            //voted value has been computed from both a, b models.
            Literal votedValue = getVotedValue(prop, models, literals);

            SparqlRepository.deleteProperty(prop.getValueProperty().toString(), fusedModel);

            LOG.trace(subject + " " + prop.getValueProperty() + " " + votedValue);
            if (subject != null && votedValue != null) {
                Statement statement = ResourceFactory.createStatement(subject, prop.getValueProperty(), votedValue);
                fusedModel.add(statement);
            }

        } else {

            List<Literal> literals = SparqlRepository
                    .getLiteralsFromPropertyChain(prop.getParent(), prop.getValueProperty(), fusedModel);
            //voted value has been computed from both a, b models.
            Literal votedValue = getVotedValue(prop, models, literals);

            RDFNode o1 = SparqlRepository.getObjectOfProperty(prop.getParent(), fusedModel);

            SparqlRepository.deleteProperty(prop.getParent().toString(), prop.getValueProperty().toString(), fusedModel);

            LOG.trace(subject + " " + prop.getValueProperty() + " " + votedValue);
            if (subject != null && votedValue != null) {
                Statement statement1 = ResourceFactory.createStatement(subject, prop.getParent(), o1.asResource());
                Statement statement2 = ResourceFactory.createStatement(o1.asResource(), prop.getValueProperty(), votedValue);
                fusedModel.add(statement1);
                fusedModel.add(statement2);
            }
        }
    }

    private void keepAny(Resource subject, CustomRDFProperty prop, Model fusedModel, Map<String, Model> models) {

        if (prop.isSingleLevel()) {
            List<Literal> literals = SparqlRepository.getLiteralsOfProperty(prop.getValueProperty(), fusedModel);

            Literal value;
            if (!literals.isEmpty()) {
                value = literals.get(0);
            } else {
                value = getAnyValue(prop, models);
            }

            SparqlRepository.deleteProperty(prop.getValueProperty().toString(), fusedModel);

            Statement statement = ResourceFactory.createStatement(subject, prop.getValueProperty(), value);

            fusedModel.add(statement);

        } else {
            List<Literal> literals = SparqlRepository
                    .getLiteralsFromPropertyChain(prop.getParent(), prop.getValueProperty(), fusedModel);

            Literal value;
            if (!literals.isEmpty()) {
                value = literals.get(0);
            } else {
                value = getAnyValue(prop, models);
            }

            RDFNode o1 = SparqlRepository.getObjectOfProperty(prop.getParent(), fusedModel);

            SparqlRepository.deleteProperty(prop.getParent().toString(), prop.getValueProperty().toString(), fusedModel);

            Statement statement1 = ResourceFactory.createStatement(subject, prop.getParent(), o1.asResource());
            Statement statement2 = ResourceFactory.createStatement(o1.asResource(), prop.getValueProperty(), value);

            fusedModel.add(statement1);
            fusedModel.add(statement2);
        }
    }

    private void keepAll(Resource resourceURI, CustomRDFProperty prop, Model fusedModel, Map<String, Model> models) {
        //keep fused model as is. (contains literals from base dataset.
        //add all literals from models in the fused by constructing intermediate nodes with counter if needed.

        //Single properties donnot use count, but they get multiple same properties in order not to break ontology alignment 
        Set<Literal> ensembleLiterals = new HashSet<>();

        for (Map.Entry<String, Model> entry : models.entrySet()) {
            if (prop.isSingleLevel()) {
                List<Literal> literals = SparqlRepository.getLiteralsOfProperty(prop.getValueProperty(), entry.getValue());

                ensembleLiterals.addAll(literals);
            } else {
                List<Literal> literals = SparqlRepository.getLiteralsFromPropertyChain(prop.getParent(),
                        prop.getValueProperty(), entry.getValue());
                ensembleLiterals.addAll(literals);
            }
        }

        if (prop.isSingleLevel()) {
            for (Literal l : ensembleLiterals) {
                Statement statement = ResourceFactory.createStatement(resourceURI, prop.getValueProperty(), l);
                fusedModel.add(statement);
            }
        } else {
            String propertyLocalName = RDFUtils.getLocalName(prop);
            int i = 0;
            for (Literal literal : ensembleLiterals) {
                Resource intermediateNode = ResourceFactory
                        .createResource(RDFUtils.constructIntermediateEnsembleNode(resourceURI, propertyLocalName, i));

                Statement statement1 = ResourceFactory.createStatement(resourceURI, prop.getParent(), intermediateNode);
                Statement statement2 = ResourceFactory.createStatement(intermediateNode, prop.getValueProperty(), literal);

                fusedModel.add(statement1);
                fusedModel.add(statement2);

                i++;
            }
        }
    }

    private Literal getVotedValue(CustomRDFProperty prop, Map<String, Model> models, List<Literal> literals) {
        List<Literal> list = new ArrayList<>();
        list.addAll(literals);

        if (prop.isSingleLevel()) {
            for (Map.Entry<String, Model> entry : models.entrySet()) {
                Model model = entry.getValue();
                List<Literal> tempLiterals = SparqlRepository.getLiteralsOfProperty(prop.getValueProperty(), model);

                list.addAll(tempLiterals);
            }
        } else {
            for (Map.Entry<String, Model> entry : models.entrySet()) {
                Model model = entry.getValue();
                List<Literal> tempLiterals = SparqlRepository
                        .getLiteralsFromPropertyChain(prop.getParent(), prop.getValueProperty(), model);

                list.addAll(tempLiterals);
            }
        }

        Map<Literal, Long> occurrences = list.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Literal mostCommonPropertyValue = null;
        long maxCount = -1;
        for (Map.Entry<Literal, Long> entry : occurrences.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostCommonPropertyValue = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mostCommonPropertyValue;
    }

    private Literal getAnyValue(CustomRDFProperty prop, Map<String, Model> models) {

        if (prop.isSingleLevel()) {
            for (Map.Entry<String, Model> entry : models.entrySet()) {
                Model model = entry.getValue();
                List<Literal> tempLiterals = SparqlRepository.getLiteralsOfProperty(prop.getValueProperty(), model);

                if (!tempLiterals.isEmpty()) {
                    return tempLiterals.get(0);
                }
            }
        } else {
            for (Map.Entry<String, Model> entry : models.entrySet()) {
                Model model = entry.getValue();
                List<Literal> tempLiterals = SparqlRepository
                        .getLiteralsFromPropertyChain(prop.getParent(), prop.getValueProperty(), model);

                if (!tempLiterals.isEmpty()) {
                    return tempLiterals.get(0);
                }
            }
        }

        throw new IllegalStateException("No literals found for property " + prop);
    }
}
