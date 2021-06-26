package main;

import java.util.HashMap;

public interface Expression {
    Variable evaluate(ProgramState programState, HashMap<String, Variable> functionVariables);
}
