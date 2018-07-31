package gr.athena.innovation.fagi.core.function.date;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionFiveStringParameters;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class DatesAreSame implements IFunction, IFunctionFiveStringParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(DatesAreSame.class);

    /**
     *
     * @param dateA
     * @param dateFormatA
     * @param dateB
     * @param dateFormatB
     * @param tolerance
     * @return 
     */
    @Override
    public boolean evaluate(String dateA, String dateFormatA, String dateB, String dateFormatB, String tolerance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
