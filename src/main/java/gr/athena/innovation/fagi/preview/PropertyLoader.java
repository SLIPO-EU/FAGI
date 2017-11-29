package gr.athena.innovation.fagi.preview;

import com.google.common.io.Files;
import gr.athena.innovation.fagi.exception.ApplicationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * File that loads RDF properties from file. It expects each property at a separate line.
 * These properties are selected for producing quality metrics between the corresponding literals.
 * 
 * @author nkarag
 */
public class PropertyLoader {
    public List<String> getRDFProperties() throws IOException, ApplicationException{

        return Files.readLines(new File("/path/to/file.txt"), Charset.forName("utf-8"));        
    }    
}
