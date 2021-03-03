import java.util.*;
import java.util.regex.*;

public class Parser {

    ArrayList<String> scriptVariableNames = new ArrayList<>();
    ArrayList<String> scriptFunctionNames = new ArrayList<>();

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
        type
    }


//    static Pattern IntegerPattern = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
    static Pattern OpenParenthesis = Pattern.compile("\\(");
    static Pattern CloseParenthesis = Pattern.compile("\\)");
    static Pattern OpenBrace = Pattern.compile("\\{");
    static Pattern CloseBrace = Pattern.compile("}");
    static Pattern OpenSquare = Pattern.compile("\\[");
    static Pattern CloseSquare = Pattern.compile("]");

    static Pattern NewLine = Pattern.compile("\n");
//    static Pattern SemiColon = Pattern.compile(";");

    static Pattern Plus = Pattern.compile("\\+");
    static Pattern Minus = Pattern.compile("-");
    static Pattern Times = Pattern.compile("\\*");
    static Pattern Divide = Pattern.compile("/");
    static Pattern Modulo = Pattern.compile("%");

    static Pattern And = Pattern.compile("&");
    static Pattern Or = Pattern.compile("\\|");
    static Pattern Not = Pattern.compile("!");

    static Pattern DoubleQuotes = Pattern.compile("\"");

    static Pattern Equals = Pattern.compile("=");

    static Pattern GreaterThan = Pattern.compile(">");
    static Pattern LessThan = Pattern.compile("<");

    static Pattern StringCast = Pattern.compile("string");
    static Pattern IntegerCast= Pattern.compile("integer");
    static Pattern FloatCast = Pattern.compile("float");
    static Pattern BooleanCast = Pattern.compile("boolean");


    static Pattern While = Pattern.compile("while");
    static Pattern If = Pattern.compile("if");
    static Pattern Else = Pattern.compile("else");
    static Pattern Function = Pattern.compile("function");
    static Pattern Variable = Pattern.compile("variable");
    static Pattern Comma =  Pattern.compile(",");

    static Pattern Random =  Pattern.compile("random");
    static Pattern Length =  Pattern.compile("length");
    static Pattern CharAt =  Pattern.compile("charAt");
    static Pattern Get =  Pattern.compile("get");
    static Pattern Type =  Pattern.compile("type");

    static Pattern Return =  Pattern.compile("return");

    static String defaultDelimiter = "[^\\S\\r\\n]|(?=[{}()\\[\\],;\"+\\-*/%#&|!<>=\\n])|(?<=[{}()\\[\\],;\"+\\-*/%#&|!<>=\\n])";

    //Require scanner to have the following pattern.
    static String require(Pattern p, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        //if the require fails:
        String log = "Tokens received:";
        for(int i=0;i<5;i++){
            log += "["+ s.next() + "], ";
        }
        System.out.println(log);
        throw new CompilerException("Expected "+p.toString());
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

    static String require(String str, Scanner s) {
        if (s.hasNext(str)) {
            return s.next();
        }
        //if the require fails:
        String log = "Tokens received:";
        for(int i=0;i<5;i++){
            log += "["+ s.next() + "], ";
        }
        System.out.println(log);
        throw new CompilerException("Expected "+str);
    }

    public ProgramNode parseScript(String script){

        //Remove any lines beginning with "#". These lines are treated as comments by the parser.
        String[] lines = script.split("\n");
        String commentRemovedScript = "";
        for(String s : lines){
            if( s.length()>0 && ! (s.charAt(0) == '#') ){
                commentRemovedScript = commentRemovedScript + s + "\n";
            }
        }

        System.out.println("\n=====================Parsing======================== \n"+commentRemovedScript);

        ProgramNode program = new ProgramNode();
        Scanner scanner = new Scanner(commentRemovedScript);

        //New delimiter that cuts each token before the \n character without consuming it.
        scanner.useDelimiter(defaultDelimiter);

        //Load Default functions
        //Print(x)
        ProgramNode printFunction = new ProgramNode();
        printFunction.addExecutableNode(
                //Assigns a placeholder variable to the output of the internal function print()
                new VariableAssignmentNode("_", new Expression(
                        "print",
                        new ArrayList<Expression>(Arrays.asList(new Expression("x"))),
                        true)));

        program.addExecutableNode(new FunctionAssignmentNode("print", new Function("print",new String[]{"x"},printFunction)));
        scriptFunctionNames.add("print");

        //add(array,value)
        ProgramNode addFunction = new ProgramNode();
        addFunction.addExecutableNode(
                //Assigns a placeholder variable to the output of the internal function add()
                new VariableAssignmentNode("_", new Expression(
                        "add",
                        new ArrayList<Expression>(Arrays.asList(new Expression("array"), new Expression("value"))),
                        true)));

        program.addExecutableNode(new FunctionAssignmentNode("add", new Function("add",new String[]{"array","value"},addFunction)));
        scriptFunctionNames.add("add");

        //remove(array,int)
        ProgramNode removeFunction = new ProgramNode();
        removeFunction.addExecutableNode(
                //Assigns a placeholder variable to the output of the internal function remove()
                new VariableAssignmentNode("_", new Expression(
                        "remove",
                        new ArrayList<Expression>(Arrays.asList(new Expression("array"), new Expression("int"))),
                        true)));

        program.addExecutableNode(new FunctionAssignmentNode("remove", new Function("remove",new String[]{"array","int"},removeFunction)));
        scriptFunctionNames.add("remove");

        //set(array,int,value)
        ProgramNode setFunction = new ProgramNode();
        setFunction.addExecutableNode(
                //Assigns a placeholder variable to the output of the internal function set()
                new VariableAssignmentNode("_", new Expression(
                        "set",
                        new ArrayList<Expression>(Arrays.asList(new Expression("array"), new Expression("int"), new Expression("value"))),
                        true)));

        program.addExecutableNode(new FunctionAssignmentNode("set", new Function("set",new String[]{"array","int","value"},setFunction)));
        scriptFunctionNames.add("set");


        while(scanner.hasNext()){
            program.addExecutableNode(parseExecutableNode(scanner, null));
        }

        return program;
    }


    public ExecutableNode parseExecutableNode(Scanner s, ArrayList<String> functionVariables) {
        if(s.hasNext(Variable)){
            return parseVariableAssignment(s, functionVariables);
        }else if(s.hasNext(If)){
            return parseIfNode(s, functionVariables);
        }else if(s.hasNext(While)){
            return parseWhileNode(s, functionVariables);
        }else if(s.hasNext(Function)){
            return parseFunctionAssignment(s);
        }else if(s.hasNext(Return)){
            if(functionVariables!=null){
                return parseReturn(s, functionVariables);
            }
            throw new CompilerException("Can't return when not inside a function");
        }else{

            //test whether scanner.next() has a variable name already defined in the function
            if(functionVariables != null){
                for(String str : functionVariables){
                    if(s.hasNext(str)){
                        require(str, s);
                        require(Equals, s);
                        VariableAssignmentNode variableAssignmentNode = new VariableAssignmentNode(str,parseExpression(s,false, functionVariables));
                        require(NewLine, s);
                        return variableAssignmentNode;
                    }
                }
            }

            //test whether scanner.next() has a variable name already defined in the script
            for(String str : scriptVariableNames){
                if(s.hasNext(str)){
                    require(str, s);
                    require(Equals, s);
                    VariableAssignmentNode variableAssignmentNode = new VariableAssignmentNode(str,parseExpression(s,false, functionVariables));
                    require(NewLine, s);
                    return variableAssignmentNode;
                }
            }

            //check for function names
            for(String str : scriptFunctionNames){
                if(s.hasNext(str)){
                    require(str, s);
                    require(OpenParenthesis,s);
                    ArrayList<Expression> parameters = new ArrayList<>();
                    if(!s.hasNext(CloseParenthesis)){
                        parameters.add(parseExpression(s,false,null));
                    }
                    while(s.hasNext(Comma)){
                        require(Comma,s);
                        parameters.add(parseExpression(s,false,null));
                    }
                    require(CloseParenthesis,s);
                    require(NewLine,s);

                    return new FunctionExecutionNode(str, parameters);
                }
            }
            //error
            throw new CompilerException("Invalid statement");
        }
    }

    public VariableAssignmentNode parseReturn(Scanner s, ArrayList<String> functionVariables){
        require(Return,s);
        Expression value = parseExpression(s,false, functionVariables);
        require(NewLine, s);

        return new VariableAssignmentNode("_return",value);
    }

    public VariableAssignmentNode parseVariableAssignment(Scanner s, ArrayList<String> functionVariables) throws CompilerException{
        String variableName;
        Expression value;

        require(Variable, s);
        if(s.hasNext("[a-z,A-Z]+")){
            variableName = s.next();

            require(Equals, s);

            value = parseExpression(s,false, functionVariables);

            require(NewLine, s);

            if(!scriptVariableNames.contains(variableName)){
                //Add new variable name to list of recognised variables, so the compiler can reference them later
                scriptVariableNames.add(variableName);
            }
//            System.out.println("setting "+variableName+" to "+value.myMode);
            return new VariableAssignmentNode(variableName,value);
        }else{
            throw new CompilerException("Invalid variable name (Upper/Lower case alphabet characters only): "+s.next());
        }
    }

    public IfNode parseIfNode(Scanner s, ArrayList<String> functionVariables){
        require(If, s);
        require(OpenParenthesis, s);

        Expression condition = parseExpression(s,false, functionVariables);

        require(CloseParenthesis, s);
        require(OpenBrace, s);
        optionalRequire(NewLine, s);

        //while the scanner doesn't have close brace:
        ProgramNode ifBlock = new ProgramNode();
        while(!s.hasNext(CloseBrace)){
            ifBlock.addExecutableNode(parseExecutableNode(s, functionVariables));
        }
        require(CloseBrace, s);

        //parse else statement if it exists
        if(s.hasNext(Else)){
            require(Else, s);
            require(OpenBrace, s);

            //while the scanner doesn't have close brace:
            ProgramNode elseBlock = new ProgramNode();
            while(!s.hasNext(CloseBrace)){
                elseBlock.addExecutableNode(parseExecutableNode(s, functionVariables));
            }
            require(CloseBrace, s);
            require(NewLine, s);
            return new IfNode(condition, ifBlock, elseBlock);
        }else{
            require(NewLine, s);
            return new IfNode(condition, ifBlock);
        }
    }

    public WhileNode parseWhileNode(Scanner s, ArrayList<String> functionVariables){
        require(While, s);
        require(OpenParenthesis, s);

        Expression condition = parseExpression(s,false, functionVariables);

        require(CloseParenthesis, s);
        require(OpenBrace, s);
        optionalRequire(NewLine, s);

//        while scanner doesn't have close brace
        ProgramNode whileBlock = new ProgramNode();
        while(!s.hasNext(CloseBrace)){
            whileBlock.addExecutableNode(parseExecutableNode(s, functionVariables));
        }
        require(CloseBrace, s);
        require(NewLine, s);

        return new WhileNode(condition,whileBlock);
    }

    public FunctionAssignmentNode parseFunctionAssignment(Scanner s){
        String name;
        ProgramNode functionScript = new ProgramNode();

        require(Function,s);
        if(s.hasNext("[a-z,A-Z]+")) {
            name = s.next();
        }else{
            throw new CompilerException("Invalid function name (Upper/Lower case alphabet characters only): "+s.next());
        }
        require(OpenParenthesis,s);
        ArrayList<String> functionVariables = new ArrayList<>();
        if(s.hasNext("[a-z,A-Z]+")) {
            functionVariables.add(s.next());
            while(s.hasNext(Comma)){
                require(Comma,s);
                functionVariables.add(require(Pattern.compile("[a-z,A-Z]+"), s));
            }
        }
        String[] parameters = new String[functionVariables.size()];
        parameters = functionVariables.toArray(parameters);

        require(CloseParenthesis,s);
        require(OpenBrace,s);
        optionalRequire(NewLine,s);
        while(!s.hasNext(CloseBrace)){
            //Add new function name to list of recognised variables, so the compiler can reference them later.
            functionScript.addExecutableNode(parseExecutableNode(s, functionVariables));
        }
        require(CloseBrace,s);
        require(NewLine,s);

        if(!scriptFunctionNames.contains(name)){
            scriptFunctionNames.add(name);

        }
        return new FunctionAssignmentNode(name, new Function(name,parameters, functionScript));
    }


    //operationPriority defines the behaviour for parsing lower priority operations (+,-)
    //true = ignore lower priority operations
    public Expression parseExpression(Scanner s, boolean operationPriority, ArrayList<String> functionVariables){
//        System.out.println("entering parseExp1");

//      Parse the left side of the expression.

        Expression firstExpression = new Expression(new NullVariable());
//        Expression firstExpression;

        //Parse n "!" characters
        boolean hasNotOperator = false;
        while (s.hasNext(Not)){
            require(Not, s);
            hasNotOperator = !hasNotOperator;
        }

        if(s.hasNext(OpenParenthesis)){
            require(OpenParenthesis, s);

            if(!hasNotOperator) {
                if (s.hasNext(StringCast)) {
                    require(StringCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(parseExpression(s, false, functionVariables), null, Operation.castString);
                } else if (s.hasNext(IntegerCast)) {
                    require(IntegerCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(parseExpression(s, false, functionVariables), null, Operation.castInteger);
                } else if (s.hasNext(FloatCast)) {
                    require(FloatCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(parseExpression(s, false, functionVariables), null, Operation.castFloat);
                } else if (s.hasNext(BooleanCast)) {
                    require(BooleanCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(parseExpression(s, false, functionVariables), null, Operation.castBoolean);
                } else {
                    firstExpression = parseExpression(s, false, functionVariables);
                    require(CloseParenthesis, s);
                }
            }else{
                if (s.hasNext(StringCast)) {
                    require(StringCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(new Expression(parseExpression(s, false, functionVariables), null, Operation.castString), null, Operation.not);
                } else if (s.hasNext(IntegerCast)) {
                    require(IntegerCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(new Expression(parseExpression(s, false, functionVariables), null, Operation.castInteger), null, Operation.not);
                } else if (s.hasNext(FloatCast)) {
                    require(FloatCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(new Expression(parseExpression(s, false, functionVariables), null, Operation.castFloat), null, Operation.not);
                } else if (s.hasNext(BooleanCast)) {
                    require(BooleanCast, s);
                    require(CloseParenthesis, s);
                    return new Expression(new Expression(parseExpression(s, false, functionVariables), null, Operation.castBoolean), null, Operation.not);
                } else {
                    firstExpression = new Expression(parseExpression(s, false, functionVariables), null, Operation.not);
                    require(CloseParenthesis, s);
                    return firstExpression;
                }

            }

        }

        else if (s.hasNext(OpenSquare)) { firstExpression = parseArrayExpression(s, functionVariables); }
        else if (s.hasNextInt()){firstExpression = new Expression(new IntegerVariable(s.nextInt()));}
        else if (s.hasNextFloat()){
            firstExpression = new Expression(new FloatVariable(s.nextFloat()));}
        else if (s.hasNextBoolean()){firstExpression = new Expression(new BooleanVariable(s.nextBoolean()));}
        else if (s.hasNext(Random)){
            //random()
            require(Random,s);
            require(OpenParenthesis,s);
            require(CloseParenthesis,s);
            firstExpression = new Expression(null,null,Operation.random);

        }
        else if (s.hasNext(Length)){
            //length(string)
            require(Length,s);
            require(OpenParenthesis,s);
            firstExpression = new Expression(parseExpression(s,false, functionVariables),null,Operation.length);
            require(CloseParenthesis,s);
        }
        else if (s.hasNext(CharAt)){
            //charAt(string,int)
            require(CharAt,s);
            require(OpenParenthesis,s);
            Expression expression1 = parseExpression(s,false, functionVariables);
            require(Comma,s);
            Expression expression2 = parseExpression(s,false, functionVariables);
            require(CloseParenthesis,s);
            firstExpression = new Expression(expression1,expression2,Operation.charAt);
        }
        else if (s.hasNext(Get)){
            //get(array,int)
            require(Get,s);
            require(OpenParenthesis,s);
            Expression expression1 = parseExpression(s,false, functionVariables);
            require(Comma,s);
            Expression expression2 = parseExpression(s,false, functionVariables);
            require(CloseParenthesis,s);
            firstExpression = new Expression(expression1,expression2,Operation.get);
        }
        else if (s.hasNext(Type)){
            //type(variable)
            require(Type,s);
            require(OpenParenthesis,s);
            firstExpression = new Expression(parseExpression(s,false, functionVariables),null,Operation.type);
            require(CloseParenthesis,s);
        }
        else {
            boolean firstVariableSet = false;

            if(functionVariables != null) {
                for (String functionVariable : functionVariables) {
                    if (s.hasNext(functionVariable)) {
                        String recognisedVariableName = s.next();
                        firstExpression = new Expression(recognisedVariableName);
                        firstVariableSet = true;
                        break;
                    }
                }
            }
            for(String variableName : scriptVariableNames){
                if(s.hasNext(variableName)){
                    String recognisedVariableName = s.next();
                    firstExpression = new Expression(recognisedVariableName);
                    firstVariableSet = true;
                    break;
                }
            }

            for(String functionName : scriptFunctionNames){
                if(s.hasNext(functionName)){
                    require(functionName, s);
                    require(OpenParenthesis,s);
                    ArrayList<Expression> parameters = new ArrayList<>();
                    if(!s.hasNext(CloseParenthesis)){
                        parameters.add(parseExpression(s,false,null));
                    }
                    while(s.hasNext(Comma)){
                        require(Comma,s);
                        parameters.add(parseExpression(s,false,null));
                    }
                    require(CloseParenthesis,s);

                    return new Expression(functionName, parameters, false);
                }
            }

            if(!firstVariableSet){

                if (s.hasNext(DoubleQuotes)){
                    require(DoubleQuotes, s);
                    //end token just before the next " character, and just after it aswell
                    s.useDelimiter("(?=[\"])|(?<=[\"])");
                    String nextString = s.next();

                    //This case catches an empty string
                    if(nextString.equals("\"")){
                        firstExpression = new Expression(new StringVariable(""));
                    }
                    //this case catches literally any other possible string
                    else {
                        firstExpression = new Expression(new StringVariable(nextString));
                        s.useDelimiter(defaultDelimiter);
                        require(DoubleQuotes, s);
                    }
                    //replace original delimiter regex
                    s.useDelimiter(defaultDelimiter);

                }else{
                    throw new CompilerException("Unrecognised Expression: "+s.next());
                }
            }
        }

        if(hasNotOperator){
            firstExpression = new Expression(firstExpression,firstExpression,Operation.not);
        }


        //Detect the end of the expression if it exists. Otherwise, parse operators in the expression if it continues.
        if(s.hasNext(CloseBrace)){return firstExpression;}
        if(s.hasNext(CloseSquare)){return firstExpression;}
        else if(s.hasNext(CloseParenthesis)){return firstExpression;}
        else if(s.hasNext(NewLine)){return firstExpression;}
        else if(s.hasNext(Comma)){return firstExpression;}

        else if(s.hasNext(Plus)){
            //if operationPriority is true, we are only accepting operations higher in the order of operations.
            //this means + and - are ignored and not consumed by the parser.
            if(operationPriority){
                return firstExpression;
            }else{
                require(Plus, s);
                return new Expression(firstExpression,parseExpression(s,false, functionVariables), Operation.plus);
            }
        }
        else if(s.hasNext(Minus)){
            //if operationPriority is true, we are only accepting operations higher in the order of operations.
            //this means + and - are ignored and not consumed by the parser.
            if(operationPriority){
                return firstExpression;
            }else {
                require(Minus, s);
                return new Expression(firstExpression, parseExpression(s, false, functionVariables), Operation.minus);
            }
        }

        else if(s.hasNext(Times)){
            require(Times, s);
            Expression intermediateExpression =  new Expression(firstExpression, parseExpression(s, true, functionVariables), Operation.times);
            return parseExpression(s,intermediateExpression, functionVariables);
        }

        else if(s.hasNext(Divide)){
            require(Divide, s);
            Expression intermediateExpression = new Expression(firstExpression, parseExpression(s, true, functionVariables), Operation.divide);
            return parseExpression(s, intermediateExpression, functionVariables);
        }
        else if(s.hasNext(Modulo)){
            require(Modulo, s);
            Expression intermediateExpression = new Expression(firstExpression, parseExpression(s, true, functionVariables), Operation.modulo);
            return parseExpression(s, intermediateExpression, functionVariables);
        }

        else if(s.hasNext(And)){
            require(And, s);
            return new Expression(firstExpression, parseExpression(s,false, functionVariables), Operation.and);
        }

        else if(s.hasNext(Or)){
            require(Or, s);
            return new Expression(firstExpression, parseExpression(s,false, functionVariables), Operation.or);
        }

        else if (s.hasNext(Equals)){
            require(Equals, s);
            return new Expression(firstExpression,parseExpression(s,false, functionVariables),Operation.equals);
        }

        else if (s.hasNext(GreaterThan)){
            require(GreaterThan, s);
            return new Expression(firstExpression,parseExpression(s,false, functionVariables),Operation.greaterThan);
        }

        else if (s.hasNext(LessThan)){
            require(LessThan, s);
            return new Expression(firstExpression,parseExpression(s,false, functionVariables),Operation.lessThan);
        }

        //Error
        throw new CompilerException("Unrecognised operation");
    }


    //Alternate form of parseExpression which takes the left side of the expression already made, and only parses the next operation.
    //providedExpression takes the place of firstExpression in the other version of this method.
    //This is used after a higher priority order of operations call of parseExpression is made for multiplication, division or modulo operation.
    //Because the higher priority call ignores + and -, we must use this method, ensuring that if it hasn't finished parsing, it can continue.
    //the boolean operationPriority used by the other version of this method is redundant here, as the priority will be false by default.
    public Expression parseExpression(Scanner s, Expression providedExpression, ArrayList<String> functionVariables){
//        System.out.println("entering parseExp2");

        if(s.hasNext(CloseBrace)){return providedExpression;}
        if(s.hasNext(CloseSquare)){return providedExpression;}
        else if(s.hasNext(CloseParenthesis)){return providedExpression;}
        else if(s.hasNext(NewLine)){return providedExpression;}
        else if(s.hasNext(Comma)){return providedExpression;}


        else if(s.hasNext(Plus)){
            require(Plus, s);
            return new Expression(providedExpression,parseExpression(s,false, functionVariables), Operation.plus);
        }

        else if(s.hasNext(Minus)){
            require(Minus, s);
            return new Expression(providedExpression,parseExpression(s,false, functionVariables), Operation.minus);
        }

        else if(s.hasNext(Times)) {
            require(Times, s);
            Expression intermediateExpression = new Expression(providedExpression, parseExpression(s, true, functionVariables), Operation.times);
            return parseExpression(s, intermediateExpression, functionVariables);
        }

        else if(s.hasNext(Divide)){
            require(Divide, s);
            Expression intermediateExpression = new Expression(providedExpression, parseExpression(s, true, functionVariables), Operation.divide);
            return parseExpression(s, intermediateExpression, functionVariables);
        }

        else if(s.hasNext(Modulo)){
            require(Modulo, s);
            Expression intermediateExpression = new Expression(providedExpression, parseExpression(s, true, functionVariables), Operation.modulo);
            return parseExpression(s, intermediateExpression, functionVariables);
        }

        else if(s.hasNext(And)){
            require(And, s);
            return new Expression(providedExpression, parseExpression(s,false, functionVariables), Operation.and);
        }

        else if(s.hasNext(Or)){
            require(Or, s);
            return new Expression(providedExpression, parseExpression(s,false, functionVariables), Operation.or);
        }

        else if (s.hasNext(Equals)){
            require(Equals, s);
            return new Expression(providedExpression,parseExpression(s,false, functionVariables),Operation.equals);
        }

        else if (s.hasNext(GreaterThan)){
            require(GreaterThan, s);
            return new Expression(providedExpression,parseExpression(s,false, functionVariables),Operation.greaterThan);
        }

        else if (s.hasNext(LessThan)){
            require(LessThan, s);
            return new Expression(providedExpression,parseExpression(s,false, functionVariables),Operation.lessThan);
        }


        //Error
        throw new CompilerException("Unrecognised operation");
    }

    public Expression parseArrayExpression(Scanner s, ArrayList<String> functionVariables){
        require(OpenSquare,s);

        ArrayList<Expression> elements = new ArrayList<>();

        while (true){
            //ignore Newlines
            if(s.hasNext(NewLine)){ require(NewLine,s);}

            if(s.hasNext(CloseSquare)){
                break;
            }
            elements.add(parseExpression(s,false, functionVariables));

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