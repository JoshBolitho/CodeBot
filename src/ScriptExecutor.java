//Parse the script, execute the parsed program, and return the console result
public class ScriptExecutor {

    private String script;

    private ProgramState programState;
    private Parser parser;
    private ProgramNode program;

    //Initialise ScriptExecutor
    public ScriptExecutor(String script) {
        this.script = script;

        programState = new ProgramState();
        parser = new Parser();
        program = new ProgramNode();
    }

    //Parse script to ProgramNode
    public void parseScript() throws ScriptException{
        program = parser.parseScript(script);
    }

    public void displayProgram(){
        System.out.println("\n=====================Program========================");
        System.out.println(program+"\n");
    }
    //execute parsed ProgramNode
    public void executeProgram(){
        try {
            program.execute(programState, null);
        }catch (ScriptException e){
            String message = e.getMessage();
            if(message != null){
                programState.print("Error: "+e.getMessage());
            }else{
                programState.print("Error: Unspecified Execution error.");
            }
        }
    }

    //retrieve program console output
    public String getConsoleOutput(){
        return programState.getConsoleOutput();
    }

    public ProgramState getProgramState() {
        return programState;
    }

    public static void main (String[] Args){
        String testScript = "print(";
        ScriptExecutor myScriptExecutor = new ScriptExecutor(testScript);
        try{
            myScriptExecutor.parseScript();
            myScriptExecutor.displayProgram();
            myScriptExecutor.executeProgram();
        }catch (ScriptException e){
            e.printStackTrace();
            String message = e.getMessage();
            if(message != null){
                myScriptExecutor.getProgramState().print("Error: "+e.getMessage());
            }else{
                myScriptExecutor.getProgramState().print("Error: Unspecified error.");
            }
        }
        System.out.println("\n===================Console Output==========================\n"+myScriptExecutor.getConsoleOutput());

//        System.out.println("Variables assigned:");
//        for(String s : programState.getProgramVariables().keySet()){
//            System.out.println(s+" : " + programState.getProgramVariables().get(s).toString());
//        }
//        System.out.println();
//
//        System.out.println("Functions assigned:");
//        for(String s : programState.getProgramFunctions().keySet()){
//            System.out.println(s+" : " + programState.getProgramFunctions().get(s).toString());
//        }
    }



}
