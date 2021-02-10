import java.util.HashMap;

public class PrintNode implements ExecutableNode{
    Expression value;

    public PrintNode(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(ProgramState programState, HashMap<String,Variable> functionVariables) {
        programState.print(value.evaluate(programState, functionVariables).toString());
    }

    @Override
    public String toString() {
        return "PrintNode{" + value + '}';
    }
}
