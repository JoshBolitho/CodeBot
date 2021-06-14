package main;

import java.util.ArrayList;
import java.util.HashMap;

//Attempting to make function inputs more loosely typed, no requirement define what type the inputs of a function are.

public class Function {

    //Initialised when function is defined
    String name;
    String[] parameterNames;
    ProgramNode functionScript;

    public Function(String name, String[] parameterNames, ProgramNode functionScript) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.functionScript = functionScript;
    }

    //executeFunction() represents A called instance of this function, functionParameters are the inputs.
    //If this function call was called from within another function call, parentFunctionVariables are
    //any variables defined in the scope of the parent function call.

    public Variable executeFunction( ArrayList<Expression> functionParameters, ProgramState programState, HashMap<String,Variable> parentFunctionVariables){
        //functionVariables
        HashMap<String,Variable> functionVariables = new HashMap<>();

        //Ensure the right number of parameters were passed
        if(functionParameters.size() != parameterNames.length){
            throw new ScriptException(name+": Wrong number of parameters: expecting "+ parameterNames.length+", received "+functionParameters.size());
        }

        //functionVariables tracks functionParameters + any variables defined within the function.
        //Evaluate and add parameters to functionVariables
        for (int i = 0; i< parameterNames.length; i++){
            functionVariables.put(parameterNames[i],functionParameters.get(i).evaluate(programState,parentFunctionVariables));
        }

        //Now the parameters are ready to use, run the script.
        functionScript.execute(programState, functionVariables);

        //return the output of execution if it exists.
        if(functionVariables.containsKey("_return")){
            return functionVariables.get("_return");
        }
        //If this function has no return value, return null.
        return new NullVariable();
    }


    public String toString() {
        String out = name+" (";
        for (int i=0;i<parameterNames.length;i++){
            out += parameterNames[i];
            if(i< parameterNames.length-1) out += ", ";
        }
        out += "){\n";
        out += functionScript.toString();
        out+="\n}";

        return out;
    }

    public String display(int depth) {
        StringBuilder res = new StringBuilder();

        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append("function "+name+"(");
        for(int i=0; i< parameterNames.length;i++){
            res.append(parameterNames[i]);
            if(i != parameterNames.length-1){
                res.append(", ");
            }
        }
        res.append("){\n");

        res.append(functionScript.display(depth+1));

        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append("}\n");
        return res.toString();
    }
}