package gr.athena.innovation.fagi.exception;

/**
 * Application exception of FAGI. 
 * 
 * @author nkarag
 */
public class ApplicationException extends RuntimeException{

    /**
     * Empty constructor of an Application Exception. 
     */
    public ApplicationException() {}

    /**
     * Constructor of an Application Exception with the message. 
     * Calls the super constructor with the message (RuntimeException -> Exception -> Throwable).
     * 
     * @param message the exception message as a string.
     */
    public ApplicationException(String message){
       super(message);
    }
}
