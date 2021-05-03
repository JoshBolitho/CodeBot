import java.util.ArrayList;
import java.util.HashMap;

public class ProgramNode implements ExecutableNode{

    // A ProgramNode is an executable node which stores an array of nodes.
    // When executed, this node executes all its sub-nodes in order.
    // This is how a script is stored and run using a ProgramNode.
    public ProgramNode() {}

    public ArrayList<ExecutableNode> executableNodes = new ArrayList<>();
    public void addExecutableNode(ExecutableNode e){
        executableNodes.add(e);
    }

    @Override
    public void execute(ProgramState programState, HashMap<String,Variable> functionVariables) {
        for(ExecutableNode e : executableNodes){
            e.execute(programState, functionVariables);
        }
//        System.out.println("in prognode execute: "+functionVariables);
    }

    @Override
    public String toString() {
        String res = "ProgramNode{";
        for(ExecutableNode ex : executableNodes){
            res += "\n    "+ex;
        }
        return res + "\n}";
    }
}
