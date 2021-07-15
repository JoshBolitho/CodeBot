package main;

import java.util.HashMap;

public class VariableAssignmentNode implements ExecutableNode {

    String name;
    Expression value;

    public VariableAssignmentNode(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(ProgramState programState, HashMap<String, Value> functionVariables) throws InterruptedException{
        //Stop execution if the thread is interrupted (program has taken too long to complete execution)
        if (Thread.interrupted()){
            Thread.currentThread().interrupt();
            throw new InterruptedException("Thread Interrupted");
        }

        //If currently within a function, add this variable within the function's scope
        if(functionVariables != null){
            functionVariables.put(name,value.evaluate(programState, functionVariables));
        }
        //otherwise, add this variable in the global scope.
        else {
            programState.addProgramVariable(name, value.evaluate(programState, functionVariables));
        }
    }

    @Override
    public String toString() {
        return "VariableAssignmentNode{" +
                name + ',' +
                value + '}';
    }

    public String display(int depth) {

        StringBuilder res = new StringBuilder();
        for(int i=0; i<depth; i++){
            res.append("    ");
        }

        //don't call value.tostring() on an internal function. instead, display a simple view of it.
        if(value.getClass().equals(InternalFunctionExpression.class)){
            return res.toString()+((InternalFunctionExpression)value).functionName+"()\n";
        }

        //don't display pre-defined variables that have the underscore prefix "_"
        if(value.getClass().equals(ValueExpression.class) && name.charAt(0)=='_'){
            return "";
        }


        if(name.equals("_return")){
            res.append("return "+value+"\n");
        }else {
            res.append(name + " = " + value + "\n");
        }

        return res.toString();
    }
}
