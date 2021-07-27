package main;

//Stores the current state of the program during execution.
//This includes all variables and functions, as well as program output (console or canvas)

//All operations executed by a CodeBot script are applied to the ProgramState.
//This includes getting/setting program variables, and calling functions that trigger output e.g. print("hello")

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ProgramState {

    private HashMap<String, Value> programVariables = new HashMap<>();
    private HashMap<String,Function> programFunctions = new HashMap<>();
    private String consoleOutput = "";


    //Print a string line to console output
    public void print(String s){
        if((consoleOutput+s).getBytes(StandardCharsets.UTF_8).length > ScriptExecutor.getMaxOutputLength()){
            throw new ScriptException("Print failed: Exceeded maximum output length ("+ScriptExecutor.getMaxOutputLength()+" characters)" );
        }

        consoleOutput = consoleOutput + s + "\n";
    }

    //Print an error to console output, and truncate if the string is too long rather than throwing another error.
    public void printError(String s){

        //Strip newlines from error message.
        String temp = "\n" + s.replace("\n","\\n");

        //ensure the error string isn't too long
        if(temp.getBytes(StandardCharsets.UTF_8).length >= ScriptExecutor.getMaxErrorLength()){return;}

        //ensure printing the error won't cause the consoleOutput to go over maxOutputLength
        while((consoleOutput+temp).getBytes(StandardCharsets.UTF_8).length > ScriptExecutor.getMaxOutputLength()){
            //safely shave off 100 chars at a time
            if(consoleOutput.length()<=100){return;}
            consoleOutput = consoleOutput.substring(0,consoleOutput.length()-100);
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
