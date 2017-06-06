package gr.athena.innovation.fagi.repository;

import gr.athena.innovation.fagi.model.LeftModel;
import gr.athena.innovation.fagi.model.LinksModel;
import gr.athena.innovation.fagi.model.RightModel;
import static gr.athena.innovation.fagi.repository.AbstractRepository.parseLinksFile;
import java.text.ParseException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Nikos Karagiannakis
 */
public class GenericRDFRepository extends AbstractRepository{

    private static final Logger logger = LogManager.getLogger(GenericRDFRepository.class);
    
    @Override
    public void parseLeft(String filepath) {
        logger.debug("Loading left dataset file:\" " + filepath + "\" with Generic Loader");
        Model model = ModelFactory.createDefaultModel();
        model.read(filepath, null); //null base URI, since URIs are absolute

        LeftModel leftModel = LeftModel.getLeftModel();
        logger.debug("Jena model size for left dataset: " + model.size());
        leftModel.setModel(model);
    }
    
    @Override
    public void parseRight(String filepath) {
        logger.debug("Loading right dataset file:\" " + filepath + "\" with Generic Loader");
        Model model = ModelFactory.createDefaultModel();
        model.read(filepath, null); //null base URI, since URIs are absolute
        
        RightModel rightModel = RightModel.getRightModel();
        logger.debug("Jena model size for right dataset: " + model.size());
        rightModel.setModel(model);
    }   
    
    @Override
    public void parseLinks(String filepath) throws ParseException{
        
        logger.debug("Loading links file:\" " + filepath + "\" with Generic Loader");
        
        loadLinksModel(filepath);
        loadLinksList(filepath);

    }
    
    private void loadLinksModel(String filepath){
        Model model = ModelFactory.createDefaultModel();
        model.read(filepath) ;
        LinksModel linksModel = LinksModel.getLinksModel();
        linksModel.setModel(model);        
    }
    
    private void loadLinksList(String filepath) throws ParseException{
        LinksModel linksModel = LinksModel.getLinksModel();
        linksModel.setLinks(parseLinksFile(filepath));
    }

    @Override
    public void readFile(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}
