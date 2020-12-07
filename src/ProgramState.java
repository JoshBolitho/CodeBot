//Stores the current state of the program during execution.
//This includes all variables and functions, as well as program output (console or canvas)

//All operations executed by the script are applied to the ProgramState.
//This includes getting/setting program variables, and calling functions that trigger output e.g. print("hello")

import java.util.HashMap;

public class ProgramState {

    private HashMap<String,Variable> programVariables = new HashMap<>();
    private HashMap<String,Function> programFunctions = new HashMap<>();
    private String consoleOutput = "";


    //Print a string line to console output
    public void print(String s){
//        System.out.println("Adding to console output: " + s);
        consoleOutput = consoleOutput + s + "\n";
    }

    //Return console output after execution
    public String getConsoleOutput(){
        return consoleOutput;
    }

    //retrieve a variable by name
    public Variable getProgramVariable(String key){
        return programVariables.get(key);
    }

    //add a variable,
    public void addProgramVariable(String s,Variable v){
        programVariables.put(s,v);
    }

    //retrieve a Function by name
    public Function getProgramFunction(String key){
        return programFunctions.get(key);
    }
    public void addProgramFunction(String s, Function f){
        programFunctions.put(s,f);
    }


    //For testing only
    public HashMap<String, Variable> getProgramVariables() {return programVariables; }
    public HashMap<String, Function> getProgramFunctions() {return programFunctions; }

    public ProgramState() {}

}
