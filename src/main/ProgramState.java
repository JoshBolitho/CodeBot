package main;

//Stores the current state of the program during execution.
//This includes all variables and functions, as well as program output (console or canvas)

//All operations executed by the script are applied to the ProgramState.
//This includes getting/setting program variables, and calling functions that trigger output e.g. print("hello")

import java.util.HashMap;

public class ProgramState {

    private HashMap<String, Value> programVariables = new HashMap<>();
    private HashMap<String,Function> programFunctions = new HashMap<>();
    private String consoleOutput = "";


    //Print a string line to console output
    public void print(String s){
        if(consoleOutput.length()>ScriptExecutor.getMaxOutputLength()){
            throw new ScriptException("Print failed: Exceeded maximum output length ("+ScriptExecutor.getMaxOutputLength()+" characters)" );
        }
        consoleOutput = consoleOutput + s + "\n";
    }

    //Print an error to console output, and truncate if the string is too long rather than throwing another error.
    public void printError(String s){

        String temp = "\n" + s;

        //ensure the error string isn't too long
        if(temp.length()>=ScriptExecutor.getMaxOutputLength()){return;}

        //test whether printing the error will cause the consoleOutput to go over maxOutputLength
        if(consoleOutput.length()+temp.length() > ScriptExecutor.getMaxOutputLength()){
            //truncate the console output, and leave enough space for the temp string.
            consoleOutput = consoleOutput.substring(0,ScriptExecutor.getMaxOutputLength()-1-temp.length());
        }
        consoleOutput = consoleOutput + temp;
    }

    //Return console output after execution
    public String getConsoleOutput(){
        return consoleOutput;
    }

    //retrieve a variable by name
    public Value getProgramVariable(String key){
        if(programVariables.containsKey(key)){
            return programVariables.get(key);
        }
        throw new ScriptException("Unable to get program variable \""+key+"\" as it doesn't exist.");
    }

    public Boolean hasProgramVariable(String key){
        return programVariables.containsKey(key);
    }

    //add a variable
    public void addProgramVariable(String s, Value v){
        programVariables.put(s,v);
    }

    //retrieve a Function by name
    public Function getProgramFunction(String key){
        return programFunctions.get(key);
    }

    public boolean hasProgramFunction(String key){
        return programFunctions.containsKey(key);
    }



    public void addProgramFunction(String s, Function f){
        programFunctions.put(s,f);
    }


    //For testing only
    public HashMap<String, Value> getProgramVariables() {return programVariables; }
    public HashMap<String, Function> getProgramFunctions() {return programFunctions; }

    public ProgramState() {}

}
