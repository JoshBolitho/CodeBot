package main;

import java.util.HashMap;

public interface Expression {
    Value evaluate(ProgramState programState, HashMap<String, Value> functionVariables);

    Expression clone();
}
