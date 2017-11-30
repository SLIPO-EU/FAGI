package gr.athena.innovation.fagi;

import gr.athena.innovation.fagi.exception.WrongInputException;
import gr.athena.innovation.fagi.specification.SpecificationConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of the application.
 *
 * @author nkarag
 */
public class Fagi {

    private static final Logger logger = LogManager.getRootLogger();

    /**
     *
     * Entry point of FAGI. Parses arguments from command line and initiates the fusion process.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String specXml = null;
        String rulesXml = null;

        String arg;
        String value;

        int i = 0;

        while (i < args.length) {
            arg = args[i];
            if (arg.startsWith("-")) {
                if (arg.equals("-help")) {
                    logger.info(SpecificationConstants.HELP);
                    System.exit(-1);
                }
            }
            value = args[i + 1];
            if (arg.equals("-spec")) {
                specXml = value;
            } else if (arg.equals("-rules")) {
                rulesXml = value;
                break;
            }
            i++;
        }

        try {

            FagiInstance fagi = new FagiInstance(specXml, rulesXml);

            fagi.run();

        } catch (WrongInputException e) {
            logger.error(e.getMessage(), e);
            logger.info(SpecificationConstants.HELP);
            System.exit(-1);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }
}
