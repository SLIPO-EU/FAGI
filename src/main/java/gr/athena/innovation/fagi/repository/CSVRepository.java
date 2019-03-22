package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.Link;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.specification.Configuration;
import gr.athena.innovation.fagi.specification.EnumOutputMode;
import gr.athena.innovation.fagi.specification.Namespace;
import gr.athena.innovation.fagi.utils.RDFUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Repository class for parsing and reading CSV files. 
 * This repository currently supports only CSV files that contain linked entities with confidence score.
 * 
 * @author nkarag
 */
public class CSVRepository extends AbstractRepository{

    private static final Logger LOG = LogManager.getLogger(CSVRepository.class);
    private static int initialCount;
    private static int uniqueCount;
    private static int ensemblesCount;

    @Override
    public void parseLeft(String filepath) throws WrongInputException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void parseRight(String filepath) throws WrongInputException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void parseFused(String filepath) throws WrongInputException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void parseLinks(String filepath) throws ParseException, WrongInputException {
        LOG.debug("Loading links file:\" " + filepath + "\" with CSV Loader");
        if(!isValidPath(filepath)){
            throw new WrongInputException("Invalid path for Links file: " + filepath + ". Check the config file.");
        }

        try {
            parseLinksCSVFile(filepath);
        } catch (IOException ex) {
            throw new WrongInputException(ex.getMessage());
        }
    }

    /**
     * Parses the given CSV links file into a list of <code>Link</code> objects.
     * 
     * @param linksFile The file path of the initialLinks.
     * @return List of link objects.
     * @throws java.io.FileNotFoundException
     */    
    public static List<Link> parseLinksCSVFile(final String linksFile) throws FileNotFoundException, IOException {

        Model model = ModelFactory.createDefaultModel();
        List<Link> links = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(linksFile))) {
            for(String line; (line = br.readLine()) != null; ) {

                if(StringUtils.isBlank(line)){
                    continue;
                }

                initialCount++;
                
                String[] parts = line.split("\\s+");
                String nodeA = RDFUtils.removeBrackets(parts[0]);
                final String uriA = RDFUtils.getIdFromResource(nodeA);

                String nodeB = RDFUtils.removeBrackets(parts[1]);
                final String uriB = RDFUtils.getIdFromResource(nodeB);

                Float score = Float.parseFloat(parts[2]);
                Link link = new Link(nodeA, uriA, nodeB, uriB, score);
                links.add(link);

                Resource s = ResourceFactory.createResource(nodeA);
                Property p = ResourceFactory.createProperty(Namespace.SAME_AS_NO_BRACKETS);
                Resource o = ResourceFactory.createResource(nodeB);
                Statement statement = ResourceFactory.createStatement(s, p, o);
                model.add(statement);
            }
        }

        LinksModel linksModel = LinksModel.getLinksModel();
        linksModel.setModel(model);
        linksModel.setFilepath(linksFile);
        linksModel.setLinks(links);

        return links;       
    }

    /**
     * Parses and filters the given CSV links file into a list of <code>Link</code> objects.
     * The goal of the filtering is to keep only unique links with the highest score. 
     * Example input:
     * <code>
     * A1	B1	0.8
     * A1	B2	0.4
     * A1	B3	0.7
     * A2	B3	0.55
     * A3	B2	0.5
     * A3	B4	0.6
     * A4	B3	0.2
     * </code>
     * The above input will give the following output:
     * <code>
     * A1	B1	0.8
     * A3	B4	0.6
     * A2	B3	0.5
     * </code>
     * 
     * The B2 entity could not make it to the final list, 
     * because the A1 has a stronger link with B1 and A3 has a stronger link with B4.
     * 
     * The A4 entity could not make it to the final list also because B3 has stronger link with A1, 
     * but A1 has stronger link with B1.
     * 
     * @param linksFile The file path of the initialLinks.
     * @return List of link objects.
     * @throws java.io.FileNotFoundException
     */    
    public static List<Link> extractUniqueLinks(final String linksFile) throws FileNotFoundException, IOException {

        Model model = ModelFactory.createDefaultModel();
        List<Link> initialLinks = new ArrayList<>();
        List<Link> filteredLinks = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(linksFile))) {
            for(String line; (line = br.readLine()) != null; ) {

                if(StringUtils.isBlank(line)){
                    continue;
                }

                initialCount++;

                String[] parts = line.split("\\s+");
                String nodeA = RDFUtils.removeBrackets(parts[0]);
                String nodeB = RDFUtils.removeBrackets(parts[1]);

                Float score = Float.parseFloat(parts[2]);

                final String uriA = RDFUtils.getIdFromResource(nodeA);
                final String uriB = RDFUtils.getIdFromResource(nodeB);
                Link link = new Link(nodeA, uriA, nodeB, uriB, score);
                initialLinks.add(link);
            }
        }

        initialLinks.sort((o1, o2) -> -o1.getScore().compareTo(o2.getScore())); //descending

        for (Link initialLink : initialLinks) {
            if(!listContainsNodeA(filteredLinks, initialLink.getNodeA()) 
                    && !listContainsNodeB(filteredLinks, initialLink.getNodeB())){

                filteredLinks.add(initialLink);

                Resource s = ResourceFactory.createResource(initialLink.getNodeA());
                Property p = ResourceFactory.createProperty(Namespace.SAME_AS_NO_BRACKETS);
                Resource o = ResourceFactory.createResource(initialLink.getNodeB());
                Statement statement = ResourceFactory.createStatement(s, p, o);
                model.add(statement);
            }
        }

        LinksModel linksModel = LinksModel.getLinksModel();
        linksModel.setModel(model);
        linksModel.setFilepath(linksFile);
        linksModel.setLinks(filteredLinks);

        uniqueCount = filteredLinks.size();
        return filteredLinks;       
    }

    private static boolean listContainsNodeA(List<Link> links, String nodeA) {
        for(Link link : links) {
            if(link != null && link.getNodeA().equals(nodeA)) {
                return true;
            }
        }
        return false;
    }

    private static boolean listContainsNodeB(List<Link> links, String nodeB) {
        for(Link link : links) {
            if(link != null && link.getNodeB().equals(nodeB)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param linksFile The file path of the initialLinks.
     * @return List of link objects.
     * @throws java.io.FileNotFoundException
     */    
    public static List<Link> extractEnsembles(final String linksFile) throws FileNotFoundException, IOException {

        Model model = ModelFactory.createDefaultModel();

        List<Link> initialLinks = new ArrayList<>();
        List<Link> linksToReturn = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(linksFile))) {
            for(String line; (line = br.readLine()) != null; ) {

                if(StringUtils.isBlank(line)){
                    continue;
                }

                initialCount++;

                String[] parts = line.split("\\s+");
                String nodeA = RDFUtils.removeBrackets(parts[0]);
                String nodeB = RDFUtils.removeBrackets(parts[1]);

                Resource s = ResourceFactory.createResource(nodeA);
                Property p = ResourceFactory.createProperty(Namespace.SAME_AS_NO_BRACKETS);
                Resource o = ResourceFactory.createResource(nodeB);
                Statement statement = ResourceFactory.createStatement(s, p, o);
                model.add(statement);

                Float score = Float.parseFloat(parts[2]);

                final String uriA = RDFUtils.getIdFromResource(nodeA);
                final String uriB = RDFUtils.getIdFromResource(nodeB);

                Link link = new Link(nodeA, uriA, nodeB, uriB, score);

                initialLinks.add(link);
            }
        }

        initialLinks.sort((o1, o2) -> -o1.getScore().compareTo(o2.getScore())); //descending

        final EnumOutputMode mode = Configuration.getInstance().getOutputMode();
        Map<String, Link> ensembleMapA = new HashMap<>();
        Map<String, Link> ensembleMapB = new HashMap<>();
        Set<String> aNodes = new HashSet<>();
        Map<String, Link> ensembleCandidates = new HashMap<>();
        Map<String, Link> linkEnsemblesB = new HashMap<>();
        Set<String> bNodes = new HashSet<>();

        Map<String, Link> linkEnsemblesA = new HashMap<>();
        for(Link link : initialLinks){
            switch(mode){
                case AA_MODE:
                case A_MODE:
                case AB_MODE:
                case L_MODE: {
                    String nodeA = link.getNodeA();
                    String nodeB = link.getNodeB();
                    
                    if(ensembleMapA.containsKey(nodeA)){
                        //firstly, remove the link from the simple links
                        Link l = ensembleMapA.get(nodeA);

                        //node A already participates in another link. Resolve what to do with node B
                        if(bNodes.contains(nodeB)){
                            //node B is already assigned to another ensemble with higher score. skip link (do nothing)
                            LOG.trace("skiping: " + link.getKey());
                        } else {
                            //node B should be included in the ensemble. Get the ensemble link and add node B to it.
                            Link ensemble = ensembleMapA.get(nodeA);
                            ensemble.addEnsembleB(nodeB);

                            bNodes.add(nodeB);
                            ensemble.setEnsemble(true);

                            //get the first link that were found and should belong to this ensemble now
                            Link firstLink = ensembleCandidates.get(nodeA);
                            ensemble.addEnsembleB(firstLink.getNodeB());
                            
                            linkEnsemblesA.put(nodeA, ensemble);
                        }
                    } else {
                        //link is not an ensemble or is not an ensemble yet.
                        ensembleCandidates.put(nodeA, link);
                        bNodes.add(nodeB);
                        ensembleMapA.put(nodeA, link);
                        linkEnsemblesA.put(nodeA, link);
                    }
                    
                    break;
                }
                case BB_MODE:
                case B_MODE:
                case BA_MODE: {
                    LOG.trace("\n\nlink " + link.getKey());
 
                    String nodeA = link.getNodeA();
                    String nodeB = link.getNodeB();
                    if(ensembleMapB.containsKey(nodeB)){
                        //firstly, remove the link from the simple links
                        Link l = ensembleMapB.get(nodeB);

                        //node A already participates in another link. Resolve what to do with node B
                        if(aNodes.contains(nodeA)){
                            //node B is already assigned to another ensemble with higher score. skip link (do nothing)
                            LOG.trace("skiping: " + link.getKey());
                        } else {
                            //node B should be included in the ensemble. Get the ensemble link and add node B to it.
                            Link ensemble = ensembleMapB.get(nodeB);
                            ensemble.addEnsembleA(nodeA);

                            aNodes.add(nodeA);
                            ensemble.setEnsemble(true);

                            //get the first link that were found and should belong to this ensemble now
                            Link firstLink = ensembleCandidates.get(nodeB);
                            ensemble.addEnsembleA(firstLink.getNodeA());
                            
                            linkEnsemblesB.put(nodeB, ensemble);
                        }
                    } else {
                        //link is not an ensemble or is not an ensemble yet.
                        ensembleCandidates.put(nodeB, link);
                        aNodes.add(nodeA);
                        ensembleMapB.put(nodeB, link);
                        linkEnsemblesB.put(nodeB, link);
                    }

                    break;
                }    
            }
        }

        linksToReturn.addAll(new ArrayList(linkEnsemblesA.values()));

        LinksModel linksModel = LinksModel.getLinksModel();
        linksModel.setModel(model);
        linksModel.setFilepath(linksFile);
        linksModel.setLinks(linksToReturn);

        ensemblesCount = linksToReturn.size();

        return linksToReturn;
    }
    
    private boolean isValidPath(String filepath){
        File file = new File(filepath);
        return (file.exists() && !file.isDirectory());
    }
    
    /**
     * Return the number of initial links.
     * 
     * @return the initial links count.
     */
    public static int getInitialCount() {
        return initialCount;
    }
    
    /**
     * Return the number of unique links.
     * 
     * @return the number of unique links.
     */
    public static int getUniqueCount() {
        return uniqueCount;
    }
    
    /**
     * Return the number of ensembles. This count includes the simple one-to-one links.
     * 
     * @return the number of ensembles.
     */
    public static int getEnsemblesCount() {
        return ensemblesCount;
    }
}
