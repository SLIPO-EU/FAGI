package gr.athena.innovation.fagi.core.functions.date;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.functions.IFunctionSingleParameter;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nkarag
 */
public class IsDateKnownFormat implements IFunction, IFunctionSingleParameter{
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IsDateKnownFormat.class);
    
    /**
     *
     * @param dateString
     * @return
     */
    @Override
    public boolean evaluate(String dateString){

        boolean isKnown = false;
        if (!StringUtils.isBlank(dateString)) {

            for (String format : SpecificationConstants.DATE_FORMATS) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
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
    
    /**
     *
     * @param date
     * @param format
     * @return
     */
    public String transformDateToFormat(String date, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        
        String transformedDate = formatter.format(date);

        return transformedDate;
    }

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
