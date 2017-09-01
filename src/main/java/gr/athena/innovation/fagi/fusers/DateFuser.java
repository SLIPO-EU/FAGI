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
    
    public boolean isDateKnownFormat(String date){

        if (!StringUtils.isBlank(date)) {
            for (String parse : SpecificationConstants.DATE_FORMATS) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parse);

                try {
                    Date la = simpleDateFormat.parse(date);
                } catch (ParseException ex) {
                    logger.error("Error parsing date format: " + date, ex);
                }

            }
            return true;
        } else {
            return false;
        }
        
    }
    
    public String transformDateToFormat(String date, String format){
        SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        
        return "";
    }
    
    public String getName(){
        return "isDateKnownFormat";
    }
}
