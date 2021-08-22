package main;

import java.util.HashMap;

public class FunctionAssignmentNode implements ExecutableNode {

    String name;
    Function value;

    public FunctionAssignmentNode(String name, Function value) {
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

        programState.addProgramFunction(name,value);
    }

    @Override
    public String display(int depth) {
        return value.display(depth);
    }

    @Override
    public ExecutableNode clone() {
        //TODO unsure whether Function value should be deep cloned
        return new FunctionAssignmentNode(name,value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
