package main;

import java.util.HashMap;

public class ReferenceExpression implements Expression {

    String variableReference;

    public ReferenceExpression(String variableReference) {
        this.variableReference = variableReference;
    }

    @Override
    public Value evaluate(ProgramState programState, HashMap<String, Value> functionVariables) {
        if(variableReference==null){
            System.out.println("Warning: variableReference field in ReferenceExpression was null when evaluating. Returning new NullVariable");
            return new NullValue();
        }
        Value v;
        if (functionVariables != null && functionVariables.containsKey(variableReference)) {
            v = functionVariables.get(variableReference);
        } else {
            if(programState.hasProgramVariable(variableReference)){
                v = programState.getProgramVariable(variableReference);
            }else {
                System.out.println("Warning: couldn't find variable referenced by variableReference field in ReferenceExpression when evaluating. Returning new NullVariable");
                return new NullValue();
            }
        }
        //Arrays are parsed as an array of Expressions, which must be
        //evaluated to an array of Variables before they are accessible.
        if (v.getType() == ValueType.ARRAY) {
            ((ArrayValue) v).evaluateArray(programState, functionVariables);
        }
        return v;
    }

    @Override
    public String toString() {
        return variableReference;
    }
}
