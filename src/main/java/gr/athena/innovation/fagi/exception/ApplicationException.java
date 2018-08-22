package gr.athena.innovation.fagi.exception;

/**
 * Application exception of FAGI. 
 * 
 * @author nkarag
 */
public class ApplicationException extends RuntimeException{
    public ApplicationException() {}

    public ApplicationException(String message){
       super(message);
    }
}
