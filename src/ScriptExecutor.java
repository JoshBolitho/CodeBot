import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

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
        System.out.println(program.display(0));
    }
    //execute parsed ProgramNode
    public void executeProgram(){
        try {
            program.execute(programState, null);
        }catch (ScriptException e){
            e.printStackTrace();

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

    //Test code in testScript.txt, rather than on facebook.
    //output canvas is saved to canvas.png.
    public static void main (String[] Args){
        String testScript = "";
        try {
            File myObj = new File("src\\testScript.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                testScript += data +"\n";
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("error loading testScript.txt");
            e.printStackTrace();
        }

        ScriptExecutor scriptExecutor = new ScriptExecutor(testScript);
        try {
            scriptExecutor.parseScript();
            scriptExecutor.displayProgram();
            System.out.println("\n=====================Execute========================");
            scriptExecutor.executeProgram();

        } catch (ScriptException e){
            e.printStackTrace();
            String message = e.getMessage();
            if(message != null){
                scriptExecutor.getProgramState().print("Error: "+e.getMessage());
            }else{
                scriptExecutor.getProgramState().print("Error: Unspecified error.");
            }
        }

        String result = scriptExecutor.getConsoleOutput();
        System.out.println(result);
        System.out.println("Variables assigned: "+scriptExecutor.getProgramState().getProgramVariables().keySet());
        System.out.println("Functions assigned: "+scriptExecutor.getProgramState().getProgramFunctions().keySet()+"\n");


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
