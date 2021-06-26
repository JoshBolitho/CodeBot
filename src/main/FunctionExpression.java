package main;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionExpression implements Expression {
    String functionName;
    ArrayList<Expression> parameters;

    public FunctionExpression(String functionName, ArrayList<Expression> parameters){
        this.functionName = functionName;
        this.parameters = parameters;
    }

    @Override
    public Variable evaluate(ProgramState programState, HashMap<String, Variable> functionVariables) {
        if (programState.hasProgramFunction(functionName)) {
            return programState.getProgramFunction(functionName)
                    .executeFunction(parameters, programState, functionVariables);
        }

        throw new ScriptException("No such function Exists: \"" + functionName + "\"");
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(functionName+"(");
        for(int i = 0; i<parameters.size();i++){
            res.append(parameters.get(i));
            if(i != parameters.size()-1){
                res.append(", ");
            }
        }
        res.append(")");
        return res.toString();
    }
}
