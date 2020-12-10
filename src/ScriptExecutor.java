//Parse the script, execute the parsed program, and return the console result
public class ScriptExecutor {

    private static String script;

    private static ProgramState programState;
    private static Parser parser;
    private static ProgramNode program;

    //Initialise ScriptExecutor
    public ScriptExecutor(String script) {
        ScriptExecutor.script = script;

        programState = new ProgramState();
        parser = new Parser();
        program = new ProgramNode();
    }

    //Parse script to ProgramNode
    public void parseScript(){
        try {
            program = parser.parseScript(script);
        }catch (CompilerException e){
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
        String testScript = "variable testa = 3 + (integer) \"6\"\n" +
                "print(\"Expecting 9: \"+testa)\n" +
                "\n" +
                "variable testb = ((string) 6) + 7 \n" +
                "print(\"Expecting 67: \"+testb)\n" +
                "\n" +
                "variable testc = ((boolean) 6.2 ) + \" or false = \" + true\n" +
                "print(\"Expecting  true or false = true: \"+testc)\n" +
                "\n" +
                "variable testd = \"\"\n" +
                "print(\"Expecting []: [\"+testd+\"]\")\n" +
                "\n" +
                "variable testf = ((boolean) 1 )| false\n" +
                "print(\"Expecting true: \"+testf)\n" +
                "\n" +
                "variable testg = (integer) 3.4\n" +
                "print(\"Expecting 3: \"+testg)\n" +
                "\n" +
                "variable testh = (float) 3\n" +
                "print(\"Expecting 3.0: \"+testh)";

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
