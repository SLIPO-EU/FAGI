package gr.athena.innovation.fagi.core.function.date;

import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.core.function.IFunctionFiveStringParameters;
import gr.athena.innovation.fagi.exception.ApplicationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.logging.log4j.LogManager;

/**
 * Class for comparing dates.
 * 
 * @author nkarag
 */
public class DatesAreSame implements IFunction, IFunctionFiveStringParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(DatesAreSame.class);

    /**
     * The method evaluates if the given dates are the same using a tolerance value in days.
     * 
     * @param dateALiteral the dateA literal.
     * @param dateBLiteral the dateB literal.
     * @param dateFormatA the date format of dateA.
     * @param dateFormatB the date format of dateB.
     * @param tolerance the tolerance value in days. Expects integer.
     * @return 
     */
    @Override
    public boolean evaluate(Literal dateALiteral, String dateFormatA, Literal dateBLiteral, String dateFormatB, String tolerance) {

        String dateAString = dateALiteral.getLexicalForm();
        String dateBString = dateBLiteral.getLexicalForm();

        int tlr = 0;
        if(!StringUtils.isBlank(tolerance)){
            try {
                tlr = Integer.parseInt(tolerance.trim());
            } catch(NumberFormatException ex){
                LOG.error(ex);
                throw new ApplicationException("Could not parse tolerance integer: " + tolerance);
            }
        }

        SimpleDateFormat simpleDateFormatA = new SimpleDateFormat(dateFormatA.trim());
        simpleDateFormatA.setLenient(false);
        
        SimpleDateFormat simpleDateFormatB = new SimpleDateFormat(dateFormatB.trim());
        simpleDateFormatB.setLenient(false);
        
        Date dateA;
        Date dateB;        
        try {
            dateA = simpleDateFormatA.parse(dateAString);
 
        } catch (ParseException ex) {
            LOG.warn("Could not parse date: " + dateAString + " with format: " + dateFormatA);
            return false;
        }

        try {
            dateB = simpleDateFormatB.parse(dateBString);
        } catch (ParseException ex) {
            LOG.warn("Could not parse date: " + dateBString + " with format: " + dateFormatB);
            return false;
        }

        long diff = dateA.getTime() - dateB.getTime();
        long dayDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        return dayDiff <= tlr;
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}
