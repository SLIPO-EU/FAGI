package gr.athena.innovation.fagi.fusers;

import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * Date fusion class. Contains methods for checking and transforming date known formats.
 * 
 * @author nkarag
 */
public class DateFuser {
    
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
                    Date date = simpleDateFormat.parse(dateString);
                    isKnown = true;
                } catch (ParseException ex) {
                    //When parsing throws an exception it does not belong to the current format.
                    //It is not possible to know from before the input date format
                    //logger.error("Error parsing date format: " + dateString, ex);
                }
            }
        }
        return isKnown;
    }
    
    /**
     * Validates the date range of the given date string using java.util.Calendar
     * 
     * @param dateString the date string
     * @param format the SimpleDateFormat of the date string
     * @return true if the date is valid and false if the date is invalid or it does not agree with the given format.
     */
    public boolean isValidDate(String dateString, String format){

        boolean isValid;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException ex) {
            logger.error("Error parsing date: " + date + " with format: " + format);
            logger.error(ex);
            return false;
        }
        
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setLenient(false);
            calendar.setTime(date);
        
        
            calendar.getTime();
            isValid = true;
        }
        catch (Exception e) {
            logger.debug("invalid date ", e);
            isValid = false;
            return false;
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

    public String getName(){
        return "isDateKnownFormat";
    }
}
