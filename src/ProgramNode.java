import java.util.ArrayList;

public class ProgramNode implements ExecutableNode{

    public ProgramNode() {}

    public ArrayList<StatementNode> StatementNodes = new ArrayList<>();

    @Override
    public void execute() {
        for(StatementNode s : StatementNodes){
            s.execute();
        }
    }

    public void addStatementNode(StatementNode s){
        StatementNodes.add(s);
    }
}
