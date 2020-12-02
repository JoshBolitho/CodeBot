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

    public void displayProgram(){
        System.out.println("\n"+program+"\n");
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
            "variable testA = 3+5\n" +
            "print(\"Expecting 8: \"+testA)\n" +
            "\n" +
            "variable testB = 3*5\n" +
            "print(\"Expecting 15: \"+testB)\n" +
            "\n" +
            "variable testC = 3 + 5 * 2\n" +
            "print(\"Expecting 13: \"+testC)\n" +
            "\n" +
            "variable testD = 3 * 5 + 2\n" +
            "print(\"Expecting 17: \"+testD)\n" +
            "\n" +
            "variable testE = (3+5)*2\n" +
            "print(\"Expecting 16: \"+testE)\n" +
            "\n" +
            "variable testF = true\n" +
            "print(\"Expecting true: \"+testF)\n" +
            "\n" +
            "variable testG = true | false\n" +
            "print(\"Expecting true: \"+testG)\n" +
            "\n" +
            "variable testH = true & true\n" +
            "print(\"Expecting true: \"+testH)\n" +
            "\n" +
            "variable testI = true & false | true\n" +
            "print(\"Expecting true: \"+testI)\n" +
            "\n" +
            "variable testJ = (true & false) | true\n" +
            "print(\"Expecting true: \"+testJ)\n" +
            "\n" +
            "variable testK = 15/3\n" +
            "print(\"Expecting 5: \"+testK)\n" +
            "\n" +
            "variable testL = 15/2\n" +
            "print(\"Expecting 7.5: \"+testL)\n" +
            "\n" +
            "variable testM = 15.0 / 3\n" +
            "print(\"Expecting 5.0: \"+testM)\n" +
            "\n" +
            "variable testN = \"hello world\"\n" +
            "print(\"Expecting hello world: \"+testN)\n" +
            "\n" +
            "variable testO = \"hello\" + \"world\"\n" +
            "print(\"Expecting helloworld: \"+testO)\n" +
            "\n" +
            "variable testP = \"1\" + \"1\"\n" +
            "print(\"Expecting 11: \"+testP)\n" +
            "\n" +
            "variable testQ = \"1\" + 1\n" +
            "print(\"Expecting 11: \"+testQ)\n" +
            "\n" +
            "variable testR = \"1\" + 1.0\n" +
            "print(\"Expecting 11.0: \"+testR)\n" +
            "\n" +
            "variable testS = \"1\" + (2-1)\n" +
            "print(\"Expecting 11: \"+testS)\n" +
            "\n" +
            "variable testT = true + \"false\" + (\"true\" + false)\n" +
            "print(\"Expecting truefalsetruefalse: \"+testT)\n" +
            "\n" +
            "variable testV = false | 3 < 4\n" +
            "print(\"Expecting true: \"+testV)\n" +
            "\n" +
            "variable testW = 4 < 3.0\n" +
            "print(\"Expecting false: \"+testW)\n" +
            "\n" +
            "variable testX = 5 = 5\n" +
            "print(\"Expecting true: \"+testX)\n" +
            "\n" +
            "variable testY = 5 = 10 / 2\n" +
            "print(\"Expecting true: \"+testY)\n" +
            "\n" +
            "#variable testU = !false & !(false) & !!true\n" +
            "#print(\"Expecting : true\"+testU)\n" +
            "\n" +
            "#variable testZ = 3.0 = 9 /3.0 & !false = true\n" +
            "#print(\"Expecting : \"+testZ)";

        ScriptExecutor myScriptExecutor = new ScriptExecutor(testScript);
        myScriptExecutor.parseScript();
        myScriptExecutor.displayProgram();
        myScriptExecutor.executeProgram();

        System.out.println("\n=============================================\nConsole output:\n"+myScriptExecutor.getConsoleOutput());

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
