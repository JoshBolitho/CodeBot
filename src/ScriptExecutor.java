//Parse the script, execute the parsed program, and return the console result
public class ScriptExecutor {

    private static ProgramState programState = new ProgramState();
    private static Parser parser = new Parser();
    private static String script;
    private static ProgramNode program = new ProgramNode();

    //Initialise ScriptExecutor
    public ScriptExecutor(String script) {
        ScriptExecutor.script = script;
    }

    //Parse script to ProgramNode
    public void parseScript(){
        try {
            program = parser.parseScript(script);
        }catch (CompilerException e){
            String message = e.getMessage();
            if(message != null){
                programState.print(e.getMessage());
            }else{
                programState.print("Unspecified Compilation error.");
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
                programState.print(e.getMessage());
            }else{
                programState.print("Unspecified Execution error.");
            }
        }
    }

    //retrieve program console output
    public String getConsoleOutput(){
        return programState.getConsoleOutput();
    }

    public static void main (String[] Args){
        String testScript = "variable a = \"6\"\n" +
                "variable b = (integer) a\n" +
                "variable c = 3 + b\n" +
                "print(\"Expecting 9: \"+c)";

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
