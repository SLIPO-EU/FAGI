package gr.athena.innovation.fagi.fusers;

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
public class DateFuser {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DateFuser.class);
    
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
    
    public String transformDateToFormat(String date, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        
        String transformedDate = formatter.format(date);

        return transformedDate;
    }
    
    public String getName(){
        return "isDateKnownFormat";
    }
}
