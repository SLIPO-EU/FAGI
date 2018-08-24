package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.model.LeftDataset;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.RightDataset;
import static gr.athena.innovation.fagi.repository.AbstractRepository.parseLinksFile;
import java.io.File;
import java.text.ParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Repository class for parsing and reading RDF models. The generic description relies on file extension types.
 * 
 * @author nkarag
 */
public class GenericRDFRepository extends AbstractRepository{

    private static final Logger LOG = LogManager.getLogger(GenericRDFRepository.class);
    
    @Override
    public void parseLeft(String filepath) throws WrongInputException{
        LOG.debug("Loading left dataset file:\" " + filepath + "\" with Generic Loader");
        
        if(!isValidPath(filepath)){
            throw new WrongInputException("Invalid path for Left dataset: " + filepath + ". Check the config file.");
        }
        
        Model model = ModelFactory.createDefaultModel();
        model.read(filepath, null); //null base URI, since URIs are absolute

        LeftDataset leftModel = LeftDataset.getLeftDataset();
        LOG.debug("Jena model size for left dataset: " + model.size());
        leftModel.setModel(model);
        leftModel.setFilepath(filepath);
    }
    
    @Override
    public void parseRight(String filepath) throws WrongInputException {
        LOG.debug("Loading right dataset file:\" " + filepath + "\" with Generic Loader");
        
        if(!isValidPath(filepath)){
            throw new WrongInputException("Invalid path for Right dataset: " + filepath + ". Check the config file.");
        }
        
        Model model = ModelFactory.createDefaultModel();
        model.read(filepath, null); //null base URI, since URIs are absolute
        
        RightDataset rightModel = RightDataset.getRightDataset();
        LOG.debug("Jena model size for right dataset: " + model.size());
        rightModel.setModel(model);
        rightModel.setFilepath(filepath);
    }   
    
    @Override
    public void parseLinks(String filepath) throws ParseException, WrongInputException{
        
        LOG.debug("Loading links file:\" " + filepath + "\" with Generic Loader");
        
        if(!isValidPath(filepath)){
            throw new WrongInputException("Invalid path for Links file: " + filepath + ". Check the config file.");
        }
        
        loadLinksModel(filepath);
        loadLinksList(filepath);

    }
    
    private void loadLinksModel(String filepath){
        Model model = ModelFactory.createDefaultModel();
        model.read(filepath) ;
        LinksModel linksModel = LinksModel.getLinksModel();
        linksModel.setModel(model);
        linksModel.setFilepath(filepath);
    }
    
    private void loadLinksList(String filepath) throws ParseException{
        LinksModel linksModel = LinksModel.getLinksModel();
        linksModel.setLinks(parseLinksFile(filepath));
    }

    @Override
    public void readFile(String path) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    private boolean isValidPath(String filepath){
        File file = new File(filepath);
        return (file.exists() && !file.isDirectory());
    }
}
