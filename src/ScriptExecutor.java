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
    public void parseScript(){
        try {
            program = parser.parseScript(script);
        }catch (CompilerException e){
            System.out.println(e);
            String message = e.getMessage();
            if(message != null){
                programState.print("Error: "+e.getMessage());
            }else{
                programState.print("Error: Unspecified Compilation error.");
            }
        }
    }

    public void displayProgram(){
        System.out.println("\n=====================Program========================");
        System.out.println(program+"\n");
    }
    //execute parsed ProgramNode
    public void executeProgram(){
        try {
            program.execute(programState);
        }catch (ExecutionException e){
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

    public static void main (String[] Args){
        String testScript = "";

        ScriptExecutor myScriptExecutor = new ScriptExecutor(testScript);
        myScriptExecutor.parseScript();
        myScriptExecutor.displayProgram();
        myScriptExecutor.executeProgram();

        System.out.println("\n===================Console Output==========================\n"+myScriptExecutor.getConsoleOutput());

//        System.out.println("Variables assigned:");
//        for(String s : programState.getProgramVariables().keySet()){
//            System.out.println(s+" : " + programState.getProgramVariables().get(s).asString());
//        }
//        System.out.println();
//
//        System.out.println("Functions assigned:");
//        for(String s : programState.getProgramFunctions().keySet()){
//            System.out.println(s+" : " + programState.getProgramFunctions().get(s).asString());
//        }
    }



}
