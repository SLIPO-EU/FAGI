package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Main class of the application.
 *
 * @author nkarag
 */
public class Fagi {

    private static final Logger LOG = LogManager.getRootLogger();

    /**
     *
     * Entry point of FAGI. Parses arguments from command line and initiates the fusion process.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String config = null;

        String arg;
        String value;

        int i = 0;

        while (i < args.length) {
            arg = args[i];
            if (arg.startsWith("-")) {
                if (arg.equals("-help")) {
                    LOG.info(SpecificationConstants.HELP);
                    System.exit(-1);
                }
            }
            value = args[i + 1];
            if (arg.equals("-spec")) {
                config = value;
                break;
            }
            i++;
        }

        try {

            FagiInstance fagi = new FagiInstance(config);

            fagi.run();

        } catch (WrongInputException e) {
            LOG.error(e.getMessage(), e);
            LOG.info(SpecificationConstants.HELP);
            System.exit(-1);
        } catch (ParserConfigurationException | SAXException | IOException | ParseException 
                | ApplicationException | org.json.simple.parser.ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }
}
