public class PrintNode implements ExecutableNode{
    Expression value;

    public PrintNode(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(ProgramState programState) {
        programState.print(value.evaluate(programState).asString());
    }

    @Override
    public String toString() {
        return "PrintNode{" + value + '}';
    }
}
