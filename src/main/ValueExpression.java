package main;

import java.util.HashMap;

import static main.VariableType.ARRAY;

public class ValueExpression implements Expression{

    Variable value;

    public ValueExpression(Variable value){
        this.value = value;
    }

    @Override
    public Variable evaluate(ProgramState programState, HashMap<String, Variable> functionVariables) {
        //Arrays are parsed as an array of Expressions, which must be
        //evaluated to an array of Variables before they are accessible.
        if (value.isType(ARRAY)) {
            ((ArrayVariable) value).evaluateArray(programState, functionVariables);
        }
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
