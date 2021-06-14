package main;

import java.util.HashMap;

public interface ExecutableNode {
    public void execute(ProgramState programState, HashMap<String,Variable> functionVariables);
    public String display(int depth);
}
