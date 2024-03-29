package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import static java.util.Map.entry;

public class Parser {

    ArrayList<String> variableNames = new ArrayList<>();
    ArrayList<String> functionNames = new ArrayList<>();

    enum Operation{
        greaterThan,
        lessThan,
        equals,

        plus,
        minus,
        times,
        divide,
        modulo,

        and,
        or,
        not
    }

    static String defaultDelimiter = "[^\\S\\r\\n]+|(?=[{}()\\[\\],;\"+\\-*/%#&|!<>=\\n])|(?<=[{}()\\[\\],;\"+\\-*/%#&|!<>=\\n])";

    static Pattern OpenParenthesis = Pattern.compile("\\(");
    static Pattern CloseParenthesis = Pattern.compile("\\)");
    static Pattern OpenBrace = Pattern.compile("\\{");
    static Pattern CloseBrace = Pattern.compile("}");
    static Pattern OpenSquare = Pattern.compile("\\[");
    static Pattern CloseSquare = Pattern.compile("]");

    static Pattern NewLine = Pattern.compile("\n");
    static Pattern DoubleQuotes = Pattern.compile("\"");
    static Pattern Not = Pattern.compile("!");

    static Pattern While = Pattern.compile("while",Pattern.CASE_INSENSITIVE);
    static Pattern If = Pattern.compile("if",Pattern.CASE_INSENSITIVE);
    static Pattern Else = Pattern.compile("else",Pattern.CASE_INSENSITIVE);
    static Pattern Function = Pattern.compile("function",Pattern.CASE_INSENSITIVE);
    static Pattern Comma =  Pattern.compile(",");
    static Pattern Return =  Pattern.compile("return",Pattern.CASE_INSENSITIVE);

    static Pattern Times = Pattern.compile("\\*");
    static Pattern Divide = Pattern.compile("/");
    static Pattern Modulo = Pattern.compile("%");
    static Pattern Plus = Pattern.compile("\\+");
    static Pattern Minus = Pattern.compile("-");
    static Pattern GreaterThan = Pattern.compile(">");
    static Pattern LessThan = Pattern.compile("<");
    static Pattern Equals = Pattern.compile("=");
    static Pattern And = Pattern.compile("&");
    static Pattern Or = Pattern.compile("\\|");

    static Pattern IntegerPattern = Pattern.compile("\\d+");
    static Pattern FloatPattern = Pattern.compile("\\d+\\.\\d+");
    static Pattern BooleanPattern = Pattern.compile("true|false");

    static Pattern Name = Pattern.compile("[a-zA-Z\\d]+");

    static Map<Pattern,String> patternDisplayStrings = Map.ofEntries(
        entry(OpenParenthesis, "("),
        entry(CloseParenthesis, ")"),
        entry(OpenBrace, "{"),
        entry(CloseBrace, "}"),
        entry(OpenSquare, "["),
        entry(CloseSquare, "]"),

        entry(NewLine, "\\n"),
        entry(DoubleQuotes, "\""),
        entry(Not, "!"),

        entry(While, "while"),
        entry(If, "if"),
        entry(Else, "else"),
        entry(Function, "function"),
        entry(Comma, ","),
        entry(Return, "return"),

        entry(Times, "*"),
        entry(Divide, "/"),
        entry(Modulo, "%"),
        entry(Plus, "+"),
        entry(Minus, "-"),
        entry(GreaterThan, ">"),
        entry(LessThan, "<"),
        entry(Equals, "="),
        entry(And, "&"),
        entry(Or, "|"),

        entry(IntegerPattern, "Integer"),
        entry(FloatPattern, "Float"),
        entry(BooleanPattern, "Boolean")
    );

    static ArrayList<Operator> operators = new ArrayList<>(
            Set.of(
                    new Operator(Times,Operation.times,5),
                    new Operator(Divide,Operation.divide,5),
                    new Operator(Modulo,Operation.modulo,5),
                    new Operator(Plus,Operation.plus,4),
                    new Operator(Minus,Operation.minus,4),
                    new Operator(GreaterThan,Operation.greaterThan,3),
                    new Operator(LessThan,Operation.lessThan,3),
                    new Operator(Equals,Operation.equals,2),
                    new Operator(And,Operation.and,1),
                    new Operator(Or,Operation.or,0)
            )
    );

    static ArrayList<String> reservedKeywords = new ArrayList<>(Arrays.asList(
            "if",
            "else",
            "while",

            "function",
            "return"
    ));

    //Keep track of what line the parser is up to
    int linesParsed = 0;

    //Require scanner to have the following pattern.
    String require (Pattern p, Scanner s) throws ScriptException {
        if (s.hasNext(p)) {
            if(p.equals(NewLine)){linesParsed++;}
            return s.next();
        }
        //if the require fails:
        if(patternDisplayStrings.containsKey(p)) {
            throw new ScriptException("Expected \"" + patternDisplayStrings.get(p) + "\"");
        }else{
            throw new ScriptException("Expected \"" + p + "\"");
        }
    }

    //Return true if scanner has the following pattern.
    //Consumes the pattern if it exists.
    boolean optionalRequire(Pattern p, Scanner s) {
        if (s.hasNext(p)) {
            if(p.equals(NewLine)){linesParsed++;}
            s.next();
            return true;
        }
        return false;
    }

    static String require(String str, Scanner s) throws ScriptException {
        if (s.hasNext(str)) {
            return s.next();
        }
        //if the require fails:
        StringBuilder log = new StringBuilder("Tokens received:");
        for(int i=0;i<5;i++){
            if(s.hasNext()){
                log.append("[").append(s.next()).append("], ");}
        }
        System.out.println(log);
        throw new ScriptException("Expected \""+str+"\"");
    }

    //Creates a function assignment node, and adds it to the program.
    //As a result, executing the program will add the function to the scripting environment.
    public void addInternalFunction(ProgramNode program, String functionName, String[] arguments, boolean hasReturnValue){

        //Create a new program node to hold the function code block.
        ProgramNode myFunction = new ProgramNode();

        //Convert the String[] to an ArrayList of variable reference expressions.
        //(As required in the internal function expression below)
        ArrayList<Expression> args = new ArrayList<>();
        for (String argument : arguments) {
            args.add(new ReferenceExpression(argument));
        }

        //Create a variable assignment node which accesses and runs an internal function,
        //and either returns the result by setting "_return", or sets it to a throwaway variable "_".
        myFunction.addExecutableNode(
                //Assigns "_return" or "_" to the output of the internal function,
                //depending on whether we expect a return value from it.
                new VariableAssignmentNode(
                        hasReturnValue? "_return":"_",
                        new InternalFunctionExpression(
                                functionName,
                                args
                        )
                )
        );

        //Creates and adds a function assignment node to the program.
        // The function assigned is given the name functionName, the String[] arguments,
        // and the program block we have just created.
        program.addExecutableNode(
                new FunctionAssignmentNode(
                        functionName,
                        new Function(functionName,arguments,myFunction)
                )
        );
        //Ensure the program knows it now has the function functionName
        functionNames.add(functionName);
    }

    //Creates a variable assignment node, and adds it to the program.
    //As a result, executing the program will add the variable to the scripting environment.
    public void addProgramVariable(ProgramNode program, String name, Value value){
        program.addExecutableNode(
                new VariableAssignmentNode(name,
                        new ValueExpression(value)
                )
        );
        variableNames.add(name);
    }

    //Load an image as a BufferedImage
    public BufferedImage loadImage(String source){
        BufferedImage bi;
        try {
            bi = ImageIO.read(new File(source));
        } catch (IOException e) {
            System.out.println("loadImage failed:" + source);
            return null;
        }
        if(bi==null){
            System.out.println("Warning: Loaded image is null: " + source);
            return null;
        }
        return bi;
    }

    public ProgramNode parseScript(String script, BufferedImage inputImage) throws StopException{

        //Remove any lines beginning with "%". These lines are treated as comments by the parser.
        String[] lines = script.split("\n");
        StringBuilder commentRemovedScript = new StringBuilder();
        for(String s : lines){
            //remove all tab characters (\t) from each line.
            s = s.replace("\t","");

            //replace curved quotes with plain quotes
            s = s.replace('”','"');
            s = s.replace('“','"');

            if( s.length()>0 && ! (s.charAt(0) == '%') ){
                commentRemovedScript.append(s).append("\n");
            }
        }

        ProgramNode program = new ProgramNode();
        Scanner scanner = new Scanner(commentRemovedScript.toString());

        //New delimiter that cuts each token before the \n character without consuming it.
        scanner.useDelimiter(defaultDelimiter);

        //general functions
        addInternalFunction(program,"print",new String[]{"x"},false);
        addInternalFunction(program,"length",new String[]{"x"},true);
        addInternalFunction(program,"charAt",new String[]{"string","int"},true);
        addInternalFunction(program,"type",new String[]{"x"},true);

        //array functions
        addInternalFunction(program,"add",new String[]{"array","value"},false);
        addInternalFunction(program,"remove",new String[]{"array","int"},false);
        addInternalFunction(program,"get",new String[]{"array","int"},true);
        addInternalFunction(program,"set",new String[]{"array","int","value"},false);

        //image functions
        addInternalFunction(program,"createImage",new String[]{"x","y"},true);
        addInternalFunction(program,"setPixel",new String[]{"image","x","y","r","g","b"},false);
        addInternalFunction(program,"getPixel",new String[]{"image","x","y"},true);
        addInternalFunction(program,"getDimensions",new String[]{"image"},true);
        addInternalFunction(program,"setCanvas",new String[]{"image"},false);
        addInternalFunction(program,"canvasVisible",new String[]{"boolean"},false);

        //casting functions
        addInternalFunction(program,"castString",new String[]{"x"},true);
        addInternalFunction(program,"castInteger",new String[]{"x"},true);
        addInternalFunction(program,"castFloat",new String[]{"x"},true);
        addInternalFunction(program,"castBoolean",new String[]{"x"},true);

        //maths functions
        addInternalFunction(program,"random",new String[]{},true);
        addInternalFunction(program,"sin",new String[]{"x"},true);
        addInternalFunction(program,"cos",new String[]{"x"},true);
        addInternalFunction(program,"pow",new String[]{"b","p"},true);

        //Initialise canvas as ImageValue called "_canvas"
        //default size is 100x100
        addProgramVariable(program, "_canvas",new ImageValue(100,100));

        //Initialise canvas visibility as boolean called "_canvasVisibility"
        addProgramVariable(program, "_canvasVisibility",new BooleanValue(false));

        //Add input image
        if(inputImage==null){
            inputImage = loadImage("src\\main\\Images\\input.png");
        }
        addProgramVariable(program,"input",new ImageValue(inputImage));

        //Add monky image
        BufferedImage monkyImage = loadImage("src\\main\\Images\\monky.png");
        addProgramVariable(program,"monky",new ImageValue(monkyImage));

        //Add sun image
        BufferedImage sunImage = loadImage("src\\main\\Images\\sun.png");
        addProgramVariable(program,"sun",new ImageValue(sunImage));

        //Add alphabet image
        BufferedImage alphabetImage = loadImage("src\\main\\Images\\alphabet.png");
        addProgramVariable(program,"alphabet",new ImageValue(alphabetImage));

        //More images...
        BufferedImage fiordlandImage = loadImage("src\\main\\Images\\fiordland.png");
        addProgramVariable(program,"fiordland",new ImageValue(fiordlandImage));

        BufferedImage kapitiImage = loadImage("src\\main\\Images\\kapiti.png");
        addProgramVariable(program,"kapiti",new ImageValue(kapitiImage));

        BufferedImage kiwiImage = loadImage("src\\main\\Images\\kiwi.png");
        addProgramVariable(program,"kiwi",new ImageValue(kiwiImage));

        BufferedImage pohutukawaImage = loadImage("src\\main\\Images\\pohutukawa.png");
        addProgramVariable(program,"pohutukawa",new ImageValue(pohutukawaImage));

        BufferedImage tuiImage = loadImage("src\\main\\Images\\tui.png");
        addProgramVariable(program,"tui",new ImageValue(tuiImage));

        System.out.println(commentRemovedScript);

        //Parse the user's script in a separate ProgramNode "scriptNode" and append to the main ProgramNode, "program"
        //to distinguish internal functions/variables from the user's script.
        //(helps when printing the program)
        ProgramNode scriptNode = new ProgramNode();
        while (scanner.hasNext()) {
            try {
                scriptNode.addExecutableNode(parseExecutableNode(scanner));
            } catch (ScriptException err){
                String[] scriptArray = commentRemovedScript.toString().split("\n");
                //throw a StopException to end parsing and add the line which
                //failed parsing in the error message.
                if (linesParsed<scriptArray.length){
                    String currentLine = scriptArray[linesParsed];
                    throw new StopException("Parsing error at: "+currentLine+"\n"+err.getMessage());
                }else{
                    throw new StopException("Parsing error\n"+err.getMessage());
                }

            }
        }
        program.addExecutableNode(scriptNode);

        return program;
    }


    public ExecutableNode parseExecutableNode (Scanner s) throws ScriptException {
        if(s.hasNext(If)){
            return parseIfNode(s);
        }else if(s.hasNext(While)){
            return parseWhileNode(s);
        }else if(s.hasNext(Function)){
            return parseFunctionAssignment(s);
        }else if(s.hasNext(Return)){
            //currently missing a test to see if parsing is currently in a function,
            //so in theory someone could add a return statement without being in a function,
            //which would be redundant and harmless.
            return parseReturn(s);
        }else{
            //test whether scanner.next() has a variable name already defined in the script
            for(String str : variableNames){
                Pattern strPattern = Pattern.compile(str,Pattern.CASE_INSENSITIVE);
                if(s.hasNext(strPattern)){
                    require(strPattern, s);
                    require(Equals, s);
                    VariableAssignmentNode variableAssignmentNode = new VariableAssignmentNode(str,parseExpression(s,null,null));
                    optionalRequire(NewLine, s);
                    return variableAssignmentNode;
                }
            }

            //check for function names
            for(String str : functionNames){
                Pattern strPattern = Pattern.compile(str,Pattern.CASE_INSENSITIVE);
                if(s.hasNext(strPattern)){
                    require(strPattern, s);
                    require(OpenParenthesis,s);
                    ArrayList<Expression> parameters = new ArrayList<>();
                    if(!s.hasNext(CloseParenthesis)){
                        parameters.add(parseExpression(s,null,null));
                    }
                    while(s.hasNext(Comma)){
                        require(Comma,s);
                        parameters.add(parseExpression(s,null,null));
                    }
                    require(CloseParenthesis,s);

                    //if this line is within a code block, do not require a newLine character.
                    if(!s.hasNext(CloseBrace)){ require(NewLine,s); }

                    return new FunctionExecutionNode(str, parameters);
                }
            }

            //Finally, if no known variable or function names are found, attempt to parse a new variable name.
            if(s.hasNext(Name)){
                return parseVariableAssignment(s);
            }

            //error
            throw new ScriptException("Invalid statement"+(s.hasNext() ? ": "+s.next() : ""));
        }
    }

    public VariableAssignmentNode parseReturn(Scanner s){
        require(Return,s);
        Expression value = parseExpression(s,null,null);
        optionalRequire(NewLine, s);

        return new VariableAssignmentNode("_return",value);
    }

    public VariableAssignmentNode parseVariableAssignment(Scanner s) throws ScriptException {
        String variableName;
        Expression value;

        if(s.hasNext(Name)){
            variableName = s.next();

            require(Equals, s);

            value = parseExpression(s,null,null);

            optionalRequire(NewLine, s);

            if(reservedKeywords.contains(variableName)){
                throw new ScriptException("Invalid variable name (trying to use reserved keyword): "+variableName);
            }
            if(functionNames.contains(variableName)){
                throw new ScriptException("Invalid variable name (trying to use name of an already defined function): "+variableName);
            }
            if(!variableNames.contains(variableName)){
                //Add new variable name to list of recognised variables, so the compiler can reference them later
                variableNames.add(variableName);
            }

            return new VariableAssignmentNode(variableName,value);
        }else{
            throw new ScriptException("Invalid variable name (Alphanumerics only)"+(s.hasNext() ? ": "+s.next() : ""));
        }
    }

    public IfNode parseIfNode(Scanner s){
        require(If, s);

        //condition
        require(OpenParenthesis, s);
        Expression condition = parseExpression(s,null,null);
        require(CloseParenthesis, s);

        //open brace
        optionalRequire(NewLine,s);
        require(OpenBrace, s);
        optionalRequire(NewLine, s);

        //while the scanner doesn't have close brace:
        ProgramNode ifBlock = new ProgramNode();
        while(!s.hasNext(CloseBrace)){
            ifBlock.addExecutableNode(parseExecutableNode(s));
        }

        //close brace
        optionalRequire(NewLine,s);
        require(CloseBrace, s);
        optionalRequire(NewLine,s);

        //parse else statement if it exists
        if(s.hasNext(Else)){
            require(Else, s);

            //open brace
            optionalRequire(NewLine,s);
            require(OpenBrace, s);
            optionalRequire(NewLine,s);

            //while the scanner doesn't have close brace:
            ProgramNode elseBlock = new ProgramNode();
            while(!s.hasNext(CloseBrace)){
                elseBlock.addExecutableNode(parseExecutableNode(s));
            }

            //close brace
            optionalRequire(NewLine, s);
            require(CloseBrace, s);
            optionalRequire(NewLine, s);

            return new IfNode(condition, ifBlock, elseBlock);
        }else{
            return new IfNode(condition, ifBlock);
        }
    }

    public WhileNode parseWhileNode(Scanner s){
        require(While, s);

        //condition
        require(OpenParenthesis, s);
        Expression condition = parseExpression(s,null,null);
        require(CloseParenthesis, s);

        //open brace
        optionalRequire(NewLine, s);
        require(OpenBrace, s);
        optionalRequire(NewLine, s);

        //While scanner doesn't have close brace
        ProgramNode whileBlock = new ProgramNode();
        while(!s.hasNext(CloseBrace)){
            whileBlock.addExecutableNode(parseExecutableNode(s));
        }

        //close brace
        optionalRequire(NewLine, s);
        require(CloseBrace, s);
        optionalRequire(NewLine, s);

        return new WhileNode(condition,whileBlock);
    }

    public FunctionAssignmentNode parseFunctionAssignment(Scanner s) throws ScriptException {
        String name;
        ProgramNode functionScript = new ProgramNode();

        require(Function,s);
        if(s.hasNext(Name)) {
            name = s.next();
        }else{
            throw new ScriptException("Invalid function name (Alphanumerics only)"+(s.hasNext() ? ": "+s.next() : ""));
        }

        if(reservedKeywords.contains(name)){
            throw new ScriptException("Invalid function name (trying to use reserved keyword): "+name);
        }
        if(variableNames.contains(name)){
            throw new ScriptException("Invalid function name (trying to use name of already defined variable): "+name);
        }
        if(functionNames.contains(name)){
            throw new ScriptException("Invalid function name (trying to use name of already defined function): "+name);
        }
        //Add new function name to list of recognised function names, so the parser can reference it later.
        functionNames.add(name);

        require(OpenParenthesis,s);
        ArrayList<String> parameters = new ArrayList<>();
        if(s.hasNext(Name)) {
            parameters.add(s.next());
            while(s.hasNext(Comma)){
                require(Comma,s);
                parameters.add(require(Name, s));
            }
        }

        //ensure all the parameter names are legal, and add them to scriptVariableNames
        for(String variableName : parameters ){
            if(reservedKeywords.contains(variableName)){
                throw new ScriptException("Invalid variable name (trying to use reserved keyword): "+variableName);
            }
            if(!variableNames.contains(variableName)){
                //Add new variable name to list of recognised variables, so the compiler can reference them later
                variableNames.add(variableName);
            }
        }


        String[] parameterArray = new String[parameters.size()];
        parameterArray = parameters.toArray(parameterArray);

        require(CloseParenthesis,s);

        //open brace
        optionalRequire(NewLine,s);
        require(OpenBrace,s);
        optionalRequire(NewLine,s);

        while(!s.hasNext(CloseBrace)){
            functionScript.addExecutableNode(parseExecutableNode(s));
        }
        require(CloseBrace,s);
        optionalRequire(NewLine,s);

        return new FunctionAssignmentNode(name, new Function(name,parameterArray,functionScript));
    }
    public boolean expressionEndDetected(Scanner s){
        //Detect possible end of expression
        return  s.hasNext(CloseBrace) ||
                s.hasNext(CloseSquare) ||
                s.hasNext(CloseParenthesis) ||
                s.hasNext(NewLine) ||
                s.hasNext(Comma);
    }

    //parse and consume a single operand
    public Expression parseOperand(Scanner s){
        Expression expression;

        //Parse n "-" characters
        if (s.hasNext(Minus)){
            require(Minus, s);
            expression = new OperationExpression(
                    new ValueExpression(new IntegerValue(0)),
                    parseOperand(s),
                    Operation.minus
            );
            return expression;
        }

        //Parse n "!" characters
        if (s.hasNext(Not)){
            require(Not, s);
            expression = new OperationExpression(
                    parseOperand(s),
                    null,
                    Operation.not
            );
            return expression;
        }

        if(s.hasNext(OpenParenthesis)) {
            require(OpenParenthesis, s);
            expression = parseExpression(s,null,null);
            require(CloseParenthesis,s);
            return expression;
        }

        if (s.hasNext(OpenSquare)) {
            expression = parseArrayExpression(s);
            return expression;
        }

        if (s.hasNext(FloatPattern)){
            if(s.hasNextFloat()){
                double next = s.nextDouble();
                if(Double.isNaN(next) || Double.isInfinite(next)){
                    throw new ScriptException("Failed to parse float: "+ next);
                }
                expression = new ValueExpression(new FloatValue(next));
                return expression;
            }
            throw new ScriptException("Invalid float value: "+s.next());
        }

        if (s.hasNext(IntegerPattern) ){
            if(s.hasNextInt()){
                Integer result = s.nextInt();
                expression = new ValueExpression(new IntegerValue(result));
                return expression;
            }
            throw new ScriptException("Invalid integer value: "+s.next());
        }

        if (s.hasNext(BooleanPattern)){
            if(s.hasNextBoolean()){
                boolean result = s.nextBoolean();
                expression = new ValueExpression(new BooleanValue(result));
                return expression;
            }
            throw new ScriptException("Invalid boolean value: "+s.next());
        }

        if (s.hasNext(DoubleQuotes)){
            require(DoubleQuotes, s);
            //end token just before the next " character, and just after it as well
            s.useDelimiter("(?=[\"])|(?<=[\"])");
            String nextString = s.next();

            //This case catches an empty string
            if(nextString.equals("\"")){
                expression = new ValueExpression(new StringValue(""));
                s.useDelimiter(defaultDelimiter);
            }
            //this case catches literally any other possible string
            else {
                expression = new ValueExpression(new StringValue(nextString));
                s.useDelimiter(defaultDelimiter);
                require(DoubleQuotes, s);

                //update linesParsed with number of newline characters in the new string
                for(int i=0;i<nextString.length();i++){
                    if(nextString.charAt(i)=='\n'){
                        linesParsed ++;
                    }
                }
            }

            return expression;
        }

        for(String variableName : variableNames){
            Pattern varNamePattern = Pattern.compile(variableName,Pattern.CASE_INSENSITIVE);
            if(s.hasNext(varNamePattern)){
                require(varNamePattern,s);
                expression = new ReferenceExpression(variableName);
                return expression;
            }
        }

        for(String functionName : functionNames) {
            Pattern funcNamePattern = Pattern.compile(functionName,Pattern.CASE_INSENSITIVE);
            if (s.hasNext(funcNamePattern)) {
                require(funcNamePattern, s);
                require(OpenParenthesis, s);
                ArrayList<Expression> parameters = new ArrayList<>();
                if (!s.hasNext(CloseParenthesis)) {
                    parameters.add(parseExpression(s,null,null));
                }
                while (s.hasNext(Comma)) {
                    require(Comma, s);
                    parameters.add(parseExpression(s,null,null));
                }
                require(CloseParenthesis, s);

                expression =  new FunctionExpression(functionName, parameters);
                return expression;
            }
        }

        throw new ScriptException("Unrecognised Expression"+(s.hasNext() ? ": "+s.next() : ""));
    }

    //parse the next operator token, without consuming it.
    public Operator parseOperator(Scanner s){
        for(Operator o : operators){
            if(s.hasNext(o.getPattern())){
                return o;
            }
        }
        throw new ScriptException("expected an operator");
    }

    //priorityLevel stores the operation priority level handled by the current function call.
    public Expression parseExpression(Scanner s, Expression firstOperand, Integer priorityLevel){

        //This is run the first time parseExpression is called.
        //Initialises firstOperand and priority level for recursive calls.
        if(priorityLevel==null){ priorityLevel = 0; }
        if(firstOperand==null){ firstOperand = parseOperand(s); }
        if(expressionEndDetected(s)){ return firstOperand; }

        //Only parse operators of the correct priority level.
        Operator operator1 = parseOperator(s);

        if(operator1.getPriority() < priorityLevel) {
            //The first operator we parsed is of a lower priority,
            //so we ignore it and return firstOperand.
            return firstOperand;

        }
        else if(operator1.getPriority() == priorityLevel){
            //If the first operator we parse is of the correct priority level, we take it,
            //take the next operand, and then the following operator. We decide what to do
            //next, based on the following operator's relative priority

            //this is the only case where we consume the operator1 token
            require(operator1.getPattern(),s);

            //get next operand
            Expression operand2 = parseOperand(s);

            //detect potential end of expression
            if(expressionEndDetected(s)){ return new OperationExpression(firstOperand,operand2,operator1.getOperation()); }

            //get next operation
            Operator operator2 = parseOperator(s);
            if(operator2.getPriority() > operator1.getPriority()){
                //parse the higher priority operation in a new parseExpression() call with higher priority
                Expression nextExpression = new OperationExpression(firstOperand,parseExpression(s,operand2,priorityLevel+1),operator1.getOperation());
                return parseExpression(s,nextExpression,priorityLevel);
            }else if (operator2.getPriority() == operator1.getPriority()){
                //create an Expression to represented the equal quality operation,
                //then call parseExpression with this newly created expression as the parameter.
                Expression temp = new OperationExpression(firstOperand,operand2,operator1.getOperation());
                return parseExpression(s,temp,priorityLevel);
            }else{
                //next operation is lower priority
                return new OperationExpression(firstOperand,operand2,operator1.getOperation());
            }

        }
        else {
            //If you are parsing at a lower priority level and you reach an operation
            //of a higher priority level, you must recursively call parseExpression() with
            //priorityLevel+1 until the right level is reached and it can be parsed.

            Expression operand1 = parseExpression(s,firstOperand,priorityLevel+1);

            if(expressionEndDetected(s)){ return operand1; }

            //After the higher priority level calls have completed, there may be more
            //operations of equal or lower priority to parse. They can be parsed using
            //another call of parseExpression() with the same priority level as this call.

            return parseExpression(s,operand1,priorityLevel);
        }
    }

    public Expression parseArrayExpression (Scanner s){
        require(OpenSquare,s);

        ArrayList<Expression> elements = new ArrayList<>();

        while (true){
            //ignore Newlines
            optionalRequire(NewLine,s);

            if(s.hasNext(CloseSquare)){
                break;
            }
            elements.add(parseExpression(s,null,null));

            //ignore Newlines
            optionalRequire(NewLine,s);

            if(s.hasNext(Comma)){
                require(Comma,s);
            }else{
                break;
            }
        }

        require(CloseSquare,s);

        return new ValueExpression(new ArrayValue(elements));
    }

    public Parser() {}
}