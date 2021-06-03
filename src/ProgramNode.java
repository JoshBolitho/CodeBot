import java.util.ArrayList;
import java.util.HashMap;

public class ProgramNode implements ExecutableNode{

    // A ProgramNode is an executable node which stores an array of nodes.
    // When executed, this node executes all its sub-nodes in order.
    // This is how a script is stored and run using a ProgramNode.
    public ProgramNode() {}

    private ArrayList<ExecutableNode> executableNodes = new ArrayList<>();

    public ArrayList<ExecutableNode> getExecutableNodes() {
        return executableNodes;
    }

    public void addExecutableNode(ExecutableNode e){
        executableNodes.add(e);
    }

    @Override
    public void execute(ProgramState programState, HashMap<String,Variable> functionVariables) {
        for(ExecutableNode e : executableNodes){
            //If in a function, and a return statement has been reached, break from the function execution.
            if(functionVariables != null && functionVariables.containsKey("_return")){
                return;
            }
            //Catch errors and print current line with error message to programState.
            //Also print stack trace and end execution with an EndExecutionException.
            try {
                e.execute(programState, functionVariables);
            }catch (ScriptException err){
                programState.print("Execution error at: "+e.display(0).split("\n")[0]);
                programState.print(err.getMessage());
                err.printStackTrace();
                throw new EndExecutionException(err.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("ProgramNode{");
        for(ExecutableNode ex : executableNodes){
            res.append("\n    ").append(ex);
        }
        return res + "\n}";
    }

    public String display(int depth){
        StringBuilder res = new StringBuilder();
        for(ExecutableNode ex : executableNodes){
            res.append(ex.display(depth));

        }
        return res.toString();
    }
}
