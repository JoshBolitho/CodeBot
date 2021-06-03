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
        //If currently within a function, add this variable within the function's scope
        if(functionVariables != null){
            functionVariables.put(name,value.evaluate(programState, functionVariables));
        }
        //otherwise, add this variable in the global scope.
        else {
            programState.addProgramVariable(name, value.evaluate(programState, functionVariables));
        }
    }

    @Override
    public String toString() {
        return "VariableAssignmentNode{" +
                name + ',' +
                value + '}';
    }

    public String display(int depth) {
        StringBuilder res = new StringBuilder();
        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append("variable "+name +" = "+value+"\n");

        return res.toString();
    }
}
