//This exception is thrown to end the execution of a BotScript program
public class EndExecutionException extends RuntimeException{
    public EndExecutionException(String message) {
        super(message);
    }
}
