package gr.athena.innovation.fagi.core.transform.date;

import gr.athena.innovation.fagi.core.specification.SpecificationConstants;
import gr.athena.innovation.fagi.core.transform.ITransform;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for date format transformations.
 * 
 * @author nkarag
 */
public class TransformToFormat implements ITransform{
    /**
     * Transforms the given date String to the provided format.
     * @param dateString
     * @param targetFormat the target date format
     * @return the String date formatted with the given date format.
     */
    public String transform(String dateString, String targetFormat){
        
        if (!StringUtils.isBlank(dateString)) {
            for (String tempFormat : SpecificationConstants.DATE_FORMATS) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tempFormat);
                simpleDateFormat.setLenient(false);
                try {
                    Date date = simpleDateFormat.parse(dateString);

                    SimpleDateFormat formatter = new SimpleDateFormat(targetFormat);
                    
                    String transformedDate = formatter.format(date);
                    
                    return transformedDate;
                } catch (ParseException ex) {
                    //do nothing
                    //When simpleDateFormat.parse throws an exception the format does not match with the date.
                    //The check is done for each known format with the hope that it does not raise an exception
                    //which means that the format is belongs to the known formats
                    //logger.error("Error parsing date format: " + dateString, ex);
                }
            }
        }        

        return dateString;
    }    

    @Override
    public String getName(){
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
