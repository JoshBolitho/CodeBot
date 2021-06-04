import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//Parse the script, execute the parsed program, and return the console result
public class ScriptExecutor {

    private final String script;

    private final ProgramState programState;
    private final Parser parser;
    private ProgramNode program;

    //Initialise ScriptExecutor
    public ScriptExecutor(String script) {
        this.script = script;

        programState = new ProgramState();
        parser = new Parser();
        program = new ProgramNode();
    }

    //Parse script to ProgramNode
    public void parseScript() throws StopException{
        program = parser.parseScript(script);
    }

    public void displayProgram(){
        System.out.println("\n=====================Program========================");
        ArrayList<ExecutableNode> nodes = program.getExecutableNodes();

        //Print the last Executable node of program, which stores the parsed script.
        //This skips printing all the internally/pre defined functions and variables.
        System.out.println(nodes.get(nodes.size()-1).display(0));
    }
    //execute parsed ProgramNode
    public void executeProgram(){
        try { program.execute(programState, null); }
        catch (StopException e){ }
    }

    //retrieve program console output
    public String getConsoleOutput(){
        return programState.getConsoleOutput();
    }

    public ProgramState getProgramState() {
        return programState;
    }

    //Test code in testScript.txt, rather than on facebook.
    //output canvas is saved to canvas.png.
    public static void main (String[] Args){
        StringBuilder testScript = new StringBuilder();
        try {
            File myObj = new File("src\\testScript.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                testScript.append(data).append("\n");
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("error loading testScript.txt");
            e.printStackTrace();
        }

        ScriptExecutor scriptExecutor = new ScriptExecutor(testScript.toString());
        try { scriptExecutor.parseScript();
            try{
                scriptExecutor.displayProgram();
                System.out.println("\n=====================Execute========================");
                scriptExecutor.executeProgram();
            } catch (StopException e){}

        } catch (StopException err){
//            err.printStackTrace();
        }

        String result = scriptExecutor.getConsoleOutput();
        System.out.println(result);

        if( scriptExecutor.getProgramState().hasProgramVariable("_canvasVisibility")
                && scriptExecutor.getProgramState().getProgramVariable("_canvasVisibility").castBoolean()
                && scriptExecutor.getProgramState().hasProgramVariable("_canvas")
        ){
            try {
                File outputFile = new File("src\\canvas.png");
                ImageIO.write(((ImageVariable) scriptExecutor.getProgramState().getProgramVariable("_canvas")).getImage(), "png", outputFile);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
