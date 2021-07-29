package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//Parse the script, execute the parsed program, and return the console result
public class ScriptExecutor {

    private final String script;
    private BufferedImage inputImage;

    private final ProgramState programState;
    private final Parser parser;
    private ProgramNode program;

    private String result = "";
    private boolean canvasVisibility = false;
    private BufferedImage canvas = null;


    private static final Integer maxImageSize = 2000;//pixels
    private static final Integer maxExecutionTime = 300;//seconds
    private static final Integer maxOutputLength = 4000;//characters
    private static final Integer maxErrorLength = 1000;//characters


    //Initialise ScriptExecutor
    public ScriptExecutor(String script, BufferedImage inputImage) {
        this.script = script;
        this.inputImage = inputImage;

        parser = new Parser();
        program = new ProgramNode();

        programState = new ProgramState();
    }

    //Parse script to ProgramNode
    public void parseScript() throws StopException{
        program = parser.parseScript(script, inputImage);
    }

    public void displayProgram(){
        ArrayList<ExecutableNode> nodes = program.getExecutableNodes();

        //Print the last Executable node of program, which stores the parsed script.
        //This skips printing all the internally/pre defined functions and variables.
        System.out.println(nodes.get(nodes.size()-1).display(0));

        //Alternatively, print the whole program
//        System.out.println(program.display(0));

    }
    //execute parsed ProgramNode
    public void executeProgram() throws InterruptedException {
        program.execute(programState, null);
    }

    //retrieve program console output
    private String getConsoleOutput(){
        return programState.getConsoleOutput();
    }

    public ProgramState getProgramState() {
        return programState;
    }

    public void run(){

        //Parse
        try {
            System.out.println("\n=====================Parsing========================\n");
            parseScript();
        } catch (StopException e){
            //if parsing fails, result is set to the error message output
            result = e.getMessage() + "\n[^_^] Check out the guide for help with programming! https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md";
            return;
        }

        System.out.println("\n=====================Program========================");
        displayProgram();

        //Execute
        try{
            System.out.println("\n=====================Execute========================");

            //Create new thread to run executeProgram()
            Runnable runnable = () -> {
                try{
                    executeProgram();
                }catch (InterruptedException e){
                    programState.printError("Program ran for too long");
                    programState.printError("[^_^] Check out the guide for help with programming! https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md");
                }catch (StopException e){
                    //StopException means that a ScriptException has already been caught and handled.
                    //We only have to end execution, no further processing is required.
                }catch (ScriptException e){
                    //ScriptExceptions should be already caught and handled by the time this block is reached.
                }
            };
            Thread executionThread = new Thread(runnable);
            executionThread.start();

            //Sleep main thread for maxExecutionTime seconds, checking each second whether execution is complete.
            for(int i=0; i<getMaxExecutionTime();i++){
                if( !executionThread.isAlive() ){ break; }
                Thread.sleep(1000L );
            }

            //Interrupt the execution thread if maxExecutionTime has elapsed and it is still running.
            if(executionThread.isAlive()){ executionThread.interrupt(); }

            //Give the execution thread another second to finish up
            Thread.sleep(1000L);
            if(executionThread.isAlive()){
                programState.printError("Program ran for too long");
                programState.printError("[^_^] Check out the guide for help with programming! https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md");
            }

            result = getConsoleOutput();

        } catch (InterruptedException e){
            //This shouldn't happen, the main thread doesn't get interrupted.
        }

        //Set canvas output
        if( getProgramState().hasProgramVariable("_canvasVisibility")){
            canvasVisibility = getProgramState().getProgramVariable("_canvasVisibility").castBoolean();
        }
        if(getProgramState().hasProgramVariable("_canvas")){
            canvas = getProgramState().getProgramVariable("_canvas").castImage();
        }

    }



    //Test code in testScript.txt, rather than on facebook.
    //output canvas is saved to canvas.png.
    public static void main (String[] Args){

        //Load test script
        StringBuilder testScript = new StringBuilder();
        try {
            File myObj = new File("src\\main\\testScript.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                testScript.append(data).append("\n");
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("error loading testScript.txt");
            e.printStackTrace();
            return;
        }

        ScriptExecutor scriptExecutor = new ScriptExecutor(testScript.toString(), null );
        scriptExecutor.run();
        String result = scriptExecutor.getResult();

        System.out.println(result);
        if(scriptExecutor.getCanvasVisibility() && scriptExecutor.getCanvas()!=null){
            try {
                File outputFile = new File("src\\main\\canvas.png");
                ImageIO.write(scriptExecutor.getCanvas(), "png", outputFile);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //Output getters
    public String getResult() {
        return result;
    }
    public boolean getCanvasVisibility() {
        return canvasVisibility;
    }
    public BufferedImage getCanvas() {
        return canvas;
    }


    //Config
    public static Integer getMaxImageSize() {
        return maxImageSize;
    }
    public static Integer getMaxExecutionTime() {
        return maxExecutionTime;
    }
    public static Integer getMaxOutputLength() {
        return maxOutputLength;
    }
    public static Integer getMaxErrorLength() {
        return maxErrorLength;
    }
}
