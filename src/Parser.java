import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

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
        not,

        castString,
        castInteger,
        castFloat,
        castBoolean,

        random,
        length,
        charAt,
        get,
        type;

    }
    static String defaultDelimiter = "[^\\S\\r\\n]|(?=[{}()\\[\\],;\"+\\-*/%#&|!<>=\\n])|(?<=[{}()\\[\\],;\"+\\-*/%#&|!<>=\\n])";

    static Pattern OpenParenthesis = Pattern.compile("\\(");
    static Pattern CloseParenthesis = Pattern.compile("\\)");
    static Pattern OpenBrace = Pattern.compile("\\{");
    static Pattern CloseBrace = Pattern.compile("}");
    static Pattern OpenSquare = Pattern.compile("\\[");
    static Pattern CloseSquare = Pattern.compile("]");

    static Pattern NewLine = Pattern.compile("\n");
    static Pattern DoubleQuotes = Pattern.compile("\"");
    static Pattern Not = Pattern.compile("!");

    static Pattern While = Pattern.compile("while");
    static Pattern If = Pattern.compile("if");
    static Pattern Else = Pattern.compile("else");
    static Pattern Function = Pattern.compile("function");
    static Pattern Comma =  Pattern.compile(",");
    static Pattern Return =  Pattern.compile("return");

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

    //These should all be deprecated and be rewritten as internal functions.
    static Pattern Random =  Pattern.compile("random");
    static Pattern Length =  Pattern.compile("length");
    static Pattern CharAt =  Pattern.compile("charAt");
    static Pattern Get =  Pattern.compile("get");
    static Pattern Type =  Pattern.compile("type");

    //considering deprecating these and turning them into internal functions too.
    //e.g. users will call "castString(x)" instad of "(string)x"
    static Pattern StringCast = Pattern.compile("string");
    static Pattern IntegerCast= Pattern.compile("integer");
    static Pattern FloatCast = Pattern.compile("float");
    static Pattern BooleanCast = Pattern.compile("boolean");
    static HashMap<Pattern,Operation> castPatterns = new HashMap<>(Map.ofEntries(
            Map.entry(StringCast,Operation.castString),
            Map.entry(IntegerCast,Operation.castInteger),
            Map.entry(FloatCast,Operation.castFloat),
            Map.entry(BooleanCast,Operation.castBoolean)
    ));

    ArrayList<String> reservedKeywords = new ArrayList<>(Arrays.asList(
            "if",
            "else",
            "while",

            "function",

            "string",
            "integer",
            "boolean",
            "float",

            "random",
            "length",
            "charAt",
            "get",
            "type",
            "return"
    ));

    //Require scanner to have the following pattern.
    static String require (Pattern p, Scanner s) throws ScriptException {
        if (s.hasNext(p)) {
            return s.next();
        }
        //if the require fails:
        StringBuilder log = new StringBuilder("Tokens received:");
        for(int i=0;i<5;i++){
            if(s.hasNext()){ log.append("[").append(s.next()).append("], ");}
        }
        System.out.println(log);
        throw new ScriptException("Expected "+p.toString());
    }

    //Return true if scanner has the following pattern.
    //Consumes the pattern if it exists.
    static boolean optionalRequire(Pattern p, Scanner s) {
        if (s.hasNext(p)) {
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
        String log = "Tokens received:";
        for(int i=0;i<5;i++){
            if(s.hasNext()){log += "["+ s.next() + "], ";}
        }
        System.out.println(log);
        throw new ScriptException("Expected "+str);
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
            args.add(new Expression(argument));
        }

        //Create a variable assignment node which accesses and runs an internal function,
        //and either returns the result by setting "_return", or sets it to a throwaway variable "_".
        myFunction.addExecutableNode(
                //Assigns "_return" or "_" to the output of the internal function,
                //depending on whether we expect a return value from it.
                new VariableAssignmentNode(
                        hasReturnValue? "_return":"_",
                        new Expression(
                                functionName,
                                args,
                                true
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
    public void addProgramVariable(ProgramNode program, String name, Variable variable){
        ProgramNode myVariable = new ProgramNode();
        myVariable.addExecutableNode(
                new VariableAssignmentNode(name,
                        new Expression(variable)
                )
        );
        program.addExecutableNode(myVariable);
        variableNames.add(name);
    }

    //Load an image as a BufferedImage
    public BufferedImage loadImage(String source){
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File(source));
        } catch (IOException e) {
            System.out.println("loadImage failed:" + source);
        }
        if(bi==null){
            System.out.println("Warning: loaded image is null: " + source);
        }
        return bi;
    }

    public ProgramNode parseScript(String script){

        //Remove any lines beginning with "#". These lines are treated as comments by the parser.
        String[] lines = script.split("\n");
        StringBuilder commentRemovedScript = new StringBuilder();
        for(String s : lines){
            if( s.length()>0 && ! (s.charAt(0) == '#') ){
                commentRemovedScript.append(s).append("\n");
            }
        }

        ProgramNode program = new ProgramNode();
        Scanner scanner = new Scanner(commentRemovedScript.toString());

        //New delimiter that cuts each token before the \n character without consuming it.
        scanner.useDelimiter(defaultDelimiter);

        addInternalFunction(program,"print",new String[]{"x"},false);
        addInternalFunction(program,"add",new String[]{"array","value"},false);
        addInternalFunction(program,"remove",new String[]{"array","int"},false);
        addInternalFunction(program,"set",new String[]{"array","int","value"},false);

        addInternalFunction(program,"createImage",new String[]{"x","y"},true);
        addInternalFunction(program,"setPixel",new String[]{"image","x","y","r","g","b"},false);
        addInternalFunction(program,"setCanvas",new String[]{"image"},false);
        addInternalFunction(program,"canvasVisible",new String[]{"boolean"},false);
        addInternalFunction(program,"getPixel",new String[]{"image","x","y"},true);


        //Initialise canvas as ImageVariable called "_canvas"
        //default size is 100x100
        addProgramVariable(program, "_canvas",new ImageVariable(100,100));

        //Initialise canvas visibility as boolean called "_canvasVisibility"
        addProgramVariable(program, "_canvasVisibility",new BooleanVariable(false));

        //Add monky image
        BufferedImage monkyImage = loadImage("src\\Images\\monky.png");
        addProgramVariable(program,"monky",new ImageVariable(monkyImage));

        //Add sun image
        BufferedImage sunImage = loadImage("src\\Images\\sun.png");
        addProgramVariable(program,"sun",new ImageVariable(sunImage));

        //Parse the user's script
        System.out.println("\n=====================Parsing======================== \n"+commentRemovedScript);

        while (scanner.hasNext()) {
            program.addExecutableNode(parseExecutableNode(scanner));
        }
        return program;
    }


    public ExecutableNode parseExecutableNode (Scanner s) throws ScriptException {
//        if(s.hasNext(Variable)){
//            return parseVariableAssignment(s);
//        }else
        if(s.hasNext(If)){
            return parseIfNode(s);
        }else if(s.hasNext(While)){
            return parseWhileNode(s);
        }else if(s.hasNext(Function)){
            return parseFunctionAssignment(s);
        }else if(s.hasNext(Return)){
            //currently missing a test to see if parsing is currently in a function,
            //so in theory someone could add a return statement without being in a function
            return parseReturn(s);
//            throw new ScriptException("Can't return when not inside a function");
        }else{
            //test whether scanner.next() has a variable name already defined in the script
            for(String str : variableNames){
                if(s.hasNext(str)){
                    require(str, s);
                    require(Equals, s);
                    VariableAssignmentNode variableAssignmentNode = new VariableAssignmentNode(str,parseExpression(s,null,null));
                    require(NewLine, s);
                    return variableAssignmentNode;
                }
            }

            //check for function names
            for(String str : functionNames){
                if(s.hasNext(str)){
                    require(str, s);
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
                    require(NewLine,s);

                    return new FunctionExecutionNode(str, parameters);
                }
            }

            //Finally, if no known variable or function names are found, attempt to parse a new variable name.
            if(s.hasNext("[a-z,A-Z]+")){
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

        //deprecating the "variable" keyword
//        require(Variable, s);
        if(s.hasNext("[a-z,A-Z]+")){
            variableName = s.next();

            require(Equals, s);

            value = parseExpression(s,null,null);

            require(NewLine, s);

            if(reservedKeywords.contains(variableName)){
                throw new ScriptException("Invalid variable name (trying to use reserved keyword): "+variableName);
            }

            if(!variableNames.contains(variableName)){
                //Add new variable name to list of recognised variables, so the compiler can reference them later
                variableNames.add(variableName);
            }
//            System.out.println("setting "+variableName+" to "+value.myMode);
            return new VariableAssignmentNode(variableName,value);
        }else{
            throw new ScriptException("Invalid variable name (Upper/Lower case alphabet characters only)"+(s.hasNext() ? ": "+s.next() : ""));
        }
    }

    public IfNode parseIfNode(Scanner s){
        require(If, s);
        require(OpenParenthesis, s);

        Expression condition = parseExpression(s,null,null);

        require(CloseParenthesis, s);
        require(OpenBrace, s);
        optionalRequire(NewLine, s);

        //while the scanner doesn't have close brace:
        ProgramNode ifBlock = new ProgramNode();
        while(!s.hasNext(CloseBrace)){
            ifBlock.addExecutableNode(parseExecutableNode(s));
        }
        require(CloseBrace, s);

        //parse else statement if it exists
        if(s.hasNext(Else)){
            require(Else, s);
            require(OpenBrace, s);
            optionalRequire(NewLine,s);

            //while the scanner doesn't have close brace:
            ProgramNode elseBlock = new ProgramNode();
            while(!s.hasNext(CloseBrace)){
                elseBlock.addExecutableNode(parseExecutableNode(s));
            }
            require(CloseBrace, s);
            require(NewLine, s);
            return new IfNode(condition, ifBlock, elseBlock);
        }else{
            require(NewLine, s);
            return new IfNode(condition, ifBlock);
        }
    }

    public WhileNode parseWhileNode(Scanner s){
        require(While, s);
        require(OpenParenthesis, s);

        Expression condition = parseExpression(s,null,null);

        require(CloseParenthesis, s);
        require(OpenBrace, s);
        optionalRequire(NewLine, s);

//        while scanner doesn't have close brace
        ProgramNode whileBlock = new ProgramNode();
        while(!s.hasNext(CloseBrace)){
            whileBlock.addExecutableNode(parseExecutableNode(s));
        }
        require(CloseBrace, s);
        require(NewLine, s);

        return new WhileNode(condition,whileBlock);
    }

    public FunctionAssignmentNode parseFunctionAssignment(Scanner s) throws ScriptException {
        String name;
        ProgramNode functionScript = new ProgramNode();

        require(Function,s);
        if(s.hasNext("[a-z,A-Z]+")) {
            name = s.next();
        }else{
            throw new ScriptException("Invalid function name (Upper/Lower case alphabet characters only)"+(s.hasNext() ? ": "+s.next() : ""));
        }

        if(reservedKeywords.contains(name)){
            throw new ScriptException("Invalid function name (trying to use reserved keyword): "+name);
        }
        if(!functionNames.contains(name)){
            functionNames.add(name);
        }

        require(OpenParenthesis,s);
        ArrayList<String> parameters = new ArrayList<>();
        if(s.hasNext("[a-z,A-Z]+")) {
            parameters.add(s.next());
            while(s.hasNext(Comma)){
                require(Comma,s);
                parameters.add(require(Pattern.compile("[a-z,A-Z]+"), s));
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
        require(OpenBrace,s);
        optionalRequire(NewLine,s);
        while(!s.hasNext(CloseBrace)){
            //Add new function name to list of recognised variables, so the compiler can reference them later.
            functionScript.addExecutableNode(parseExecutableNode(s));
        }
        require(CloseBrace,s);
        require(NewLine,s);

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

        //Parse n "!" characters
        while (s.hasNext(Not)){
            require(Not, s);
            expression = new Expression(parseExpression(s,null,null), null, Operation.not);
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

        if (s.hasNextInt()){
            expression = new Expression(new IntegerVariable(s.nextInt()));
            return expression;
        }

        if (s.hasNextFloat()){
            expression = new Expression(new FloatVariable(s.nextFloat()));
            return expression;
        }

        if (s.hasNextBoolean()){
            expression = new Expression(new BooleanVariable(s.nextBoolean()));
            return expression;
        }

        if (s.hasNext(DoubleQuotes)){
            require(DoubleQuotes, s);
            //end token just before the next " character, and just after it as well
            s.useDelimiter("(?=[\"])|(?<=[\"])");
            String nextString = s.next();

            //This case catches an empty string
            if(nextString.equals("\"")){
                expression = new Expression(new StringVariable(""));
                s.useDelimiter(defaultDelimiter);
            }
            //this case catches literally any other possible string
            else {
                expression = new Expression(new StringVariable(nextString));
                s.useDelimiter(defaultDelimiter);
                require(DoubleQuotes, s);
            }
            return expression;

        }

        for(String variableName : variableNames){
            if(s.hasNext(variableName)){
                String recognisedVariableName = s.next();
                expression = new Expression(recognisedVariableName);
                return expression;
            }
        }

        for(String functionName : functionNames) {
            if (s.hasNext(functionName)) {
                require(functionName, s);
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

                expression =  new Expression(functionName, parameters, false);
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
//        System.out.println("");

        //This is run the first time parseExpression is called.
        //Initialises firstOperand and priority level for recursive calls.
        if(priorityLevel==null){ priorityLevel = 0; }
        if(firstOperand==null){ firstOperand = parseOperand(s); }
        if(expressionEndDetected(s)){ return firstOperand; }


        //Only parse operators of the correct priority level.
        Operator operator1 = parseOperator(s);

//        System.out.println("priority: "+priorityLevel);
//        System.out.println("op1: "+operator1.getPriority());

        if(operator1.getPriority() < priorityLevel) {
            //The first operator we parsed is of a lower priority, so we ignore it and
            //return firstOperand.
            return firstOperand;

        }
        else if(operator1.getPriority() == priorityLevel){
            //If the first operator we parse is of the correct priority level, we take it,
            //take the next operand, and then the following operator. We decide what to do
            //next, based on the following operator's relative priority
//            System.out.println("here");

            //this is the only case where we consume the operator1 token
            require(operator1.getPattern(),s);

            //get next operand
            Expression operand2 = parseOperand(s);

            //detect potential end of expression
            if(expressionEndDetected(s)){ return new Expression(firstOperand,operand2,operator1.getOperation()); }

            //get next operation
            Operator operator2 = parseOperator(s);
            if(operator2.getPriority() > operator1.getPriority()){
                //parse the higher priority operation in a new parseExpression() call with higher priority
                Expression nextExpression = new Expression(firstOperand,parseExpression(s,operand2,priorityLevel+1),operator1.getOperation());
//                System.out.println("nextExpression:"+nextExpression);
                return parseExpression(s,nextExpression,priorityLevel);
            }else if (operator2.getPriority() == operator1.getPriority()){
                //create an Expression to represented the equal quality operation,
                //then call parseExpression with this newly created expression as the parameter.
                Expression temp = new Expression(firstOperand,operand2,operator1.getOperation());
                return parseExpression(s,temp,priorityLevel);
            }else{
                //next operation is lower priority
                return new Expression(firstOperand,operand2,operator1.getOperation());
            }

        }
//        else if(operator1.getPriority() > priorityLevel){
        else {
            //If you are parsing at a lower priority level and you reach an operation
            //of a higher priority level, you must recursively call parseExpression() with
            //priorityLevel+1 until the right level is reached and it can be parsed.

            Expression operand1 = parseExpression(s,firstOperand,priorityLevel+1);

            if(expressionEndDetected(s)){ return operand1; }

            //After the higher priority level calls have completed, there may be more
            //operations of equal or lower priority to parse.
            Operator operator2 = parseOperator(s);

            if(operator2.getPriority() == priorityLevel){
                return parseExpression(s,operand1,priorityLevel);
            }

            //We are dropping down a priority level here so the "outer" parseExpression()
            // call can handle it. Just return what we parsed so far.
            else if(operator2.getPriority() < priorityLevel){
                return operand1;
            }else{
//                System.out.println("\n------\npriority: "+priorityLevel);
//                System.out.println("op2: "+operator2.getPriority()+"\n------");
                throw new ScriptException("Operation priority error!");
            }

        }

    }

    public Expression parseArrayExpression (Scanner s){
        require(OpenSquare,s);

        ArrayList<Expression> elements = new ArrayList<>();

        while (true){
            //ignore Newlines
            if(s.hasNext(NewLine)){ require(NewLine,s);}

            if(s.hasNext(CloseSquare)){
                break;
            }
            elements.add(parseExpression(s,null,null));

            //ignore Newlines
            if(s.hasNext(NewLine)){ require(NewLine,s);}

            if(s.hasNext(Comma)){
                require(Comma,s);
            }else{
                break;
            }
        }

        require(CloseSquare,s);

        return new Expression(new ArrayVariable(elements));
    }

    public Parser() {}
}