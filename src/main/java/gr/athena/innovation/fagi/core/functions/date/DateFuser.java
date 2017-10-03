package gr.athena.innovation.fagi.core.functions.date;

import gr.athena.innovation.fagi.core.functions.IFunction;
import gr.athena.innovation.fagi.core.functions.IFunctionSingleParameter;
import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * Date fusion class. Contains methods for checking and transforming date known formats.
 * 
 * @author nkarag
 */
public class DateFuser implements IFunction, IFunctionSingleParameter{
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DateFuser.class);
    
    /**
     *
     * @param dateString
     * @return
     */
    public boolean isDateKnownFormat(String dateString){

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
     * Validates the date range of the given date string using the lenient property of date.
     * 
     * @param dateString the date string
     * @param format the SimpleDateFormat of the date string
     * @return true if the date is valid and false if the date is invalid or it does not agree with the given format.
     */
    public boolean isValidDate(String dateString, String format){

        //TODO - consider using https://github.com/joestelmach/natty for parsing unknown formats
        boolean isValid = false;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setLenient(false);
        Date date = null;
        try {
            
            simpleDateFormat.parse(dateString);
            isValid = true;

        } catch (ParseException ex) {
            //logger.error("Error parsing date: " + date + " with format: " + format);
            //logger.error(ex);
            isValid = false;
        }

        return isValid;
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
        String className = this.getClass().getSimpleName();
        return className;
    }

    @Override
    public boolean evaluate(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
