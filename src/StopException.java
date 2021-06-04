//This exception is thrown to stop parsing or execution of a BotScript program
public class StopException extends RuntimeException{
    public StopException(String message) {
        super(message);
    }
}
