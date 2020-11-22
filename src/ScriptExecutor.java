//Parse the script, execute the parsed program, and return the console result
public class ScriptExecutor {

    private ProgramState programState = new ProgramState();
    private Parser parser = new Parser();
    private String script;
    private ProgramNode program = new ProgramNode();

    //Initialise ScriptExecutor
    public ScriptExecutor(String script) {
        this.script = script;
    }

    //Parse script to ProgramNode
    public void parseScript(){
        this.program = parser.parseScript(script);
    }

    //execute parsed ProgramNode
    public void executeProgram(){
        program.execute();
    }

    //retrieve program console output
    public String getConsoleOutput(){
        return programState.getConsoleOutput();
    }

    public static void main (String[] Args){
        ScriptExecutor myScriptExecutor = new ScriptExecutor("");
        myScriptExecutor.parseScript();
        myScriptExecutor.executeProgram();
        System.out.println("Console output:"+myScriptExecutor.getConsoleOutput());
    }

}
