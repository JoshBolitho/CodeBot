import java.util.ArrayList;

public class ProgramNode implements ExecutableNode{

    public ProgramNode() {}

    public ArrayList<ExecutableNode> executableNodes = new ArrayList<>();

    @Override
    public void execute(ProgramState programState) {
        for(ExecutableNode e : executableNodes){
            e.execute(programState);
        }
    }

    @Override
    public String toString() {
        String res = "ProgramNode{";
        for(ExecutableNode ex : executableNodes){
            res += "\n    "+ex;
        }
        return res + "\n}";
    }

    public void addExecutableNode(ExecutableNode e){
        executableNodes.add(e);
    }
}
