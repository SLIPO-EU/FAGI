package gr.athena.innovation.fagi.model;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Resource;

/**
 * Class representing a pair of interlinked RDF entities.
 * 
 * @author Nikos Karagiannakis
 */
public class RdfPair extends Pair{
    private Resource left;
    private Resource right;

    @Override
    public Resource getLeft() {
        return left;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Resource getRight() {
        return right;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Resource setValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setLeft(){
        
    }
}
