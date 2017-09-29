package gr.athena.innovation.fagi.fusers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author nkarag
 */
public class IsValidDate {
    
    /**
     * Validates the date range of the given date string using the lenient property of date.
     * 
     * @param dateString the date string
     * @param format the SimpleDateFormat of the date string
     * @return true if the date is valid and false if the date is invalid or it does not agree with the given format.
     */
    public boolean isValidDate(String dateString, String format){

        //TODO - consider using https://github.com/joestelmach/natty for parsing unknown formats
        boolean isValid;
        
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
}
