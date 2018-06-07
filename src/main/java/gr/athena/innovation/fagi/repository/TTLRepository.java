package gr.athena.innovation.fagi.repository;

import java.text.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nkarag
 */
public class TTLRepository extends AbstractRepository{
    private static final Logger LOG = LogManager.getLogger(TTLRepository.class);
    
    @Override
    public void readFile(String path) {
        LOG.info("Reading from path", path);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void parseLeft(String filepath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void parseRight(String filepath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void parseLinks(String filepath) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
