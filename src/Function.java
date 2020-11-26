import java.util.ArrayList;
import java.util.HashMap;

//Attempting to make function inputs more loosely typed, no requirement define what type the inputs of a function are.

public class Function {

    //Initialised when function is defined
    String[] variableNames;
    ArrayList<ExecutableNode> executableNodes;
    VariableType returnType;

    public Function (String[] variableNames, ArrayList<ExecutableNode> executableNodes, VariableType returnType) {
        this.variableNames = variableNames;
        this.executableNodes = executableNodes;
        this.returnType = returnType;
    }

    //Initialised when function is called
    HashMap<String,Variable> functionVariables;
    Variable returnVariable;

    //A called instance of this function. functionVariables are passed
    public Variable executeFunction( ArrayList<Variable> functionVariables, Variable returnVariable){
        return new NullVariable();
    }


    public String asString() {
        return "{TODO}";
    }

    public Function(String[] variableNames) {
        this.variableNames = variableNames;
    }

}