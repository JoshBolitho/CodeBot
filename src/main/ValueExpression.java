package main;

import java.util.HashMap;

import static main.ValueType.ARRAY;

public class ValueExpression implements Expression{

    Value value;

    public ValueExpression(Value value){
        this.value = value;
    }

    @Override
    public Value evaluate(ProgramState programState, HashMap<String, Value> functionVariables) {
        //Arrays are parsed as an array of Expressions, which must be
        //evaluated to an array of Variables before they are accessible.
        if (value.isType(ARRAY)) {
            ((ArrayValue) value).evaluateArray(programState, functionVariables);
        }
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
