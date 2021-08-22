package main;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionExecutionNode implements ExecutableNode{

    String name;
    ArrayList<Expression> parameters;

    public FunctionExecutionNode(String name, ArrayList<Expression> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public void execute(ProgramState programState, HashMap<String, Value> functionVariables) throws InterruptedException{
        //Stop execution if the thread is interrupted (program has taken too long to complete execution)
        if (Thread.interrupted()){
                    Thread.currentThread().interrupt();
                    throw new InterruptedException("Thread Interrupted");
                }

        if(programState.hasProgramFunction(name)) {
            programState.getProgramFunction(name).executeFunction(parameters, programState, functionVariables);
        }else{
            throw new ScriptException("Unable to find reference to function \""+name+"\" in program");
        }
    }

    @Override
    public String toString() {
        String out = name + "(";
        for(int i=0;i<parameters.size();i++){
            out += parameters.get(i);
            if(i< parameters.size()-1){
                out += ", ";
            }
        }
        return out + ")\n";
    }

    public String display(int depth) {
        StringBuilder res = new StringBuilder();
        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append(name+"(");
        for(int i=0; i< parameters.size();i++){
            res.append(parameters.get(i));
            if(i != parameters.size()-1){
                res.append(", ");
            }
        }
        res.append(")\n");
        return res.toString();
    }

    @Override
    public ExecutableNode clone() {

        ArrayList<Expression> newParameters = new ArrayList<>();
        for(Expression e : parameters){
            newParameters.add(e.clone());
        }

        return new FunctionExecutionNode(name,newParameters);
    }
}
