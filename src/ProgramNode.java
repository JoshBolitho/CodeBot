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

    public void addExecutableNode(ExecutableNode e){
        executableNodes.add(e);
    }
}
