import java.util.HashMap;

public class VariableAssignmentNode implements ExecutableNode {

    String name;
    Expression value;

    public VariableAssignmentNode(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(ProgramState programState, HashMap<String,Variable> functionVariables) {
        if(functionVariables != null){
            functionVariables.put(name,value.evaluate(programState, functionVariables));
        }else {
            programState.addProgramVariable(name, value.evaluate(programState, functionVariables));
        }
    }

    @Override
    public String toString() {
        return "VariableAssignmentNode{" +
                name + ',' +
                value + '}';
    }
}
