import java.util.ArrayList;

//Attempting to make function inputs more loosely typed, no requirement define what type the inputs of a function are.

public class Function {

    //Initialised when function is defined
    String[] variableNames;
    ArrayList<StatementNode> statementNodes;
    VariableType returnType;

    public Function (String[] variableNames, ArrayList<StatementNode> statementNodes, VariableType returnType) {
        this.variableNames = variableNames;
        this.statementNodes = statementNodes;
        this.returnType = returnType;
    }

    //Initialised when function is called
    ArrayList<Variable> functionVariables;
    Variable returnVariable;

    public Variable executeFunction( ArrayList<Variable> functionVariables, Variable returnVariable){
        return new NullVariable();
    }

    public Function(String[] variableNames) {
        this.variableNames = variableNames;
    }

}