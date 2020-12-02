public class VariableAssignmentNode implements ExecutableNode {

    String name;
    Expression value;

    public VariableAssignmentNode(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(ProgramState programState) {
        programState.addProgramVariable(name,value.evaluate(programState));
    }

    @Override
    public String toString() {
        return "VariableAssignmentNode{" +
                name + ',' +
                value + '}';
    }
}
