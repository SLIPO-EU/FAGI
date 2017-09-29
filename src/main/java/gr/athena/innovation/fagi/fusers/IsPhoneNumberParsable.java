package gr.athena.innovation.fagi.fusers;

/**
 *
 * @author nkarag
 */
public class IsPhoneNumberParsable {
    
    /**
     * Checks if the given number is represented as an integer. 
     * (Contains only numeric characters and no other symbols or spaces)
     * 
     * @param number
     * @return true if the telephone number representation can be parsed as an integer and false otherwise.
     * 
     */
    public static boolean isPhoneNumberParsable(String number){
        
        boolean parsable = true;
        
        try {
            
            Integer.parseInt(number);
            
        }catch(NumberFormatException e){
            //logger.debug("Number is not parsable, but it is ok. \n");
            parsable = false;
        }           

        return parsable;
    }    
}
