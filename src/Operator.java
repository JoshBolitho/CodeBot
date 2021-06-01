import java.util.regex.Pattern;

public class Operator {

    private final Pattern pattern;
    private final Parser.Operation operation;
    private final int priority;

    public Operator(Pattern pattern, Parser.Operation operation, int priority) {
        this.pattern = pattern;
        this.operation = operation;
        this.priority = priority;
    }

    public Parser.Operation getOperation() {
        return operation;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getPriority() {
        return priority;
    }
}
