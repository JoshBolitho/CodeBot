package main;

import java.util.HashMap;

public interface ExecutableNode {
    void execute(ProgramState programState, HashMap<String, Variable> functionVariables);
    String display(int depth);
}