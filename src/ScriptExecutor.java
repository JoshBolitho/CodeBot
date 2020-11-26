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
        program = parser.parseScript(script);
    }

    //execute parsed ProgramNode
    public void executeProgram(){
        program.execute(programState);
    }

    //retrieve program console output
    public String getConsoleOutput(){
        return programState.getConsoleOutput();
    }

    public static void main (String[] Args){
        String testScript =
            "variable testInt = 10\n" +
            "variable testString = \"Hello World\"\n" +
            "\n" +
            "variable testAddition = 10+5\n" +
            "print(testAddition)\n" +
            "\n" +
            "variable testConcat = \"10 \" + \"5\"\n" +
            "print(testConcat)\n" +
            "\n" +
            "#comment example\n" +
            "print(\"done\")"
        ;

        ScriptExecutor myScriptExecutor = new ScriptExecutor(testScript);
        myScriptExecutor.parseScript();
        myScriptExecutor.executeProgram();

        System.out.println("\n=============================================\nConsole output:\n"+myScriptExecutor.getConsoleOutput());

        System.out.println("Variables assigned:");
        for(String s : programState.getProgramVariables().keySet()){
            System.out.println(s+" : " + programState.getProgramVariables().get(s).asString());
        }
        System.out.println();

        System.out.println("Functions assigned:");
        for(String s : programState.getProgramFunctions().keySet()){
            System.out.println(s+" : " + programState.getProgramFunctions().get(s).asString());
        }
    }



}
