package gr.athena.innovation.fagi.exception;

/**
 * Wrapper exception for wrong input related exceptions.
 * 
 * @author nkarag
 */
public class WrongInputException extends Exception{

    /**
     * Empty constructor of a Wrong Input Exception. 
     */
    public WrongInputException() {}

    /**
     * Constructor of a Wrong Input Exception with the message. 
     * Calls the super constructor with the message (RuntimeException -> Exception -> Throwable).
     * 
     * @param message the exception message as a string.
     */
    public WrongInputException(String message){
         super(message);
      }
}
