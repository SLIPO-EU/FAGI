package gr.athena.innovation.fagi.core.function.date;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionSingleParameter;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class IsDatePrimaryFormat implements IFunction, IFunctionSingleParameter {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsDatePrimaryFormat.class);
    
    /**
     * Checks if the given date String is written as a primary format as defined in the specification.
     * See <code> SpecificationConstants.PRIMARY_DATE_FORMATS</code> class.
     * 
     * @param dateString The date string.
     * @return True if the date belongs to a primary format as described in the specification, false otherwise.
     */
    @Override
    public boolean evaluate(String dateString){

        boolean isKnown = false;
        if (!StringUtils.isBlank(dateString)) {

            for (String format : SpecificationConstants.PRIMARY_DATE_FORMATS) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                simpleDateFormat.setLenient(false);
                try {
                    simpleDateFormat.parse(dateString);
                    return true;
                } catch (ParseException ex) {
                    //do nothing
                    //When simpleDateFormat.parse throws an exception the format does not match with the date.
                    //The check is done for each known format with the hope that it does not raise an exception
                    //which means that the format is belongs to the known formats
                    //logger.error("Error parsing date format: " + dateString, ex);
                }
            }
        }
        return isKnown;
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }    
}
