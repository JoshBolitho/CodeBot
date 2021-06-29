package main;

import java.util.HashMap;

public class ReferenceExpression implements Expression {

    String variableReference;

    public ReferenceExpression(String variableReference) {
        this.variableReference = variableReference;
    }

    @Override
    public Variable evaluate(ProgramState programState, HashMap<String, Variable> functionVariables) {
        Variable v;
        if (functionVariables != null && functionVariables.containsKey(variableReference)) {
            v = functionVariables.get(variableReference);
        } else {
            v = programState.getProgramVariable(variableReference);
        }
        //Arrays are parsed as an array of Expressions, which must be
        //evaluated to an array of Variables before they are accessible.
        if (v.getType() == VariableType.ARRAY) {
            ((ArrayVariable) v).evaluateArray(programState, functionVariables);
        }
        return v;
    }

    @Override
    public String toString() {
        return variableReference;
    }
}