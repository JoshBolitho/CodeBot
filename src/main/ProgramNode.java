package main;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgramNode implements ExecutableNode{

    // A ProgramNode is an executable node which stores an array of nodes.
    // When executed, this node executes all its sub-nodes in order.
    // This is how a script is stored and run using a ProgramNode.
    public ProgramNode() {}

    private ArrayList<ExecutableNode> executableNodes = new ArrayList<>();

    public ArrayList<ExecutableNode> getExecutableNodes() {
        return executableNodes;
    }

    public void addExecutableNode(ExecutableNode e){
        executableNodes.add(e);
    }

    @Override
    public void execute(ProgramState programState, HashMap<String, Value> functionVariables) throws InterruptedException {
        //Stop execution if the thread is interrupted (program has taken too long to complete execution)
        if (Thread.interrupted()){
            Thread.currentThread().interrupt();
            throw new InterruptedException("Thread Interrupted");
        }

        //return if the program node is empty
        if(executableNodes.size()==0){return;}

        for(ExecutableNode ex : executableNodes){
            //If in a function, and a return statement has been reached, break from the function execution.
            if(functionVariables != null && functionVariables.containsKey("_return")){
                return;
            }
            //Catch errors and print current line with error message to programState.
            //Also print stack trace and end execution with an EndExecutionException.
            try {
                //Stop execution if the thread is interrupted (program has taken too long to complete execution)
                if (Thread.interrupted()){
                    Thread.currentThread().interrupt();
                    throw new InterruptedException("Thread Interrupted");
                }

                ex.execute(programState, functionVariables);

            } catch (ScriptException e){
                handleThrowable(e,programState,ex,e.getMessage());
            } catch (StackOverflowError e){
                handleThrowable(e,programState,ex,"Recursion error: Stack overflow");
            } catch (OutOfMemoryError e){
                handleThrowable(e,programState,ex,"Memory error: Out of memory");
            }
        }
    }

    //where Throwable t is either an Error, or a ScriptException
    public void handleThrowable(Throwable e, ProgramState programState, ExecutableNode ex, String message) throws Error {
        //re-throw the throwable if it is from an internal function
        if (ex.getClass().equals(VariableAssignmentNode.class) && ((VariableAssignmentNode) ex).value.getClass().equals(InternalFunctionExpression.class)){
            if(e.getClass().equals(ScriptException.class)){throw (ScriptException)e;}
            if(e.getClass().equals(Error.class)){ throw (Error)e; }
        }
        //Otherwise, handle the exception and throw a StopException
        programState.printError("Execution error at: "+ex.display(0).split("\n")[0]);
        programState.printError(message);
        programState.printError("[^_^] Check out the guide for help with programming! https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md");

        throw new StopException(message);
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("ProgramNode{");
        for(ExecutableNode ex : executableNodes){
            res.append("\n    ").append(ex);
        }
        return res + "\n}";
    }

    public String display(int depth){
        StringBuilder res = new StringBuilder();
        for(ExecutableNode ex : executableNodes){
            res.append(ex.display(depth));

        }
        return res.toString();
    }

    @Override
    public ExecutableNode clone() {
        ProgramNode newProgramNode = new ProgramNode();
        for (ExecutableNode e : executableNodes){
            newProgramNode.addExecutableNode(e.clone());
        }
        return newProgramNode;
    }
}