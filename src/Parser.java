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
        castBoolean
    }

    //need to rewrite as our own code
    //Require scanner to have the following pattern.
    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        //if the require fails:
        String log = "Tokens received:";
        for(int i=0;i<5;i++){
            log += "["+ s.next() + "], ";
        }
        System.out.println(log);
        throw new CompilerException(message);
    }

    static String require(String str, String message, Scanner s) {
        if (s.hasNext(str)) {
            return s.next();
        }
        throw new CompilerException(message);
    }

//    static Pattern IntegerPattern = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
    static Pattern OpenParenthesis = Pattern.compile("\\(");
    static Pattern CloseParenthesis = Pattern.compile("\\)");
    static Pattern OpenBrace = Pattern.compile("\\{");
    static Pattern CloseBrace = Pattern.compile("\\}");
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


    //    static Pattern Comma =  Pattern.compile(",");
    //    static Pattern While = Pattern.compile("while");
    //    static Pattern If = Pattern.compile("if");
    //    static Pattern Else = Pattern.compile("else");
    //    static Pattern Function = Pattern.compile("function");

    public ProgramNode parseScript(String script){

        //Remove any lines beginning with "#". These lines are treated as comments by the parser.
        String[] lines = script.split("\n");
        String commentRemovedScript = "";
        for(String s : lines){
            if( s.length()>0 && ! (s.charAt(0) == '#') ){
                commentRemovedScript = commentRemovedScript + s + "\n";
            }
        }
//        System.out.println(script);
        System.out.println("=================Parsing================== \n"+commentRemovedScript);

        ProgramNode program = new ProgramNode();
        Scanner scanner = new Scanner(commentRemovedScript);

        //New delimiter that cuts each token before the \n character without consuming it.
        scanner.useDelimiter("[^\\S\\r\\n]|(?=[{}(),;\"+\\-*\\/%#\\n])|(?<=[{}(),;\"+\\-*\\/%#\\n])");

        while(scanner.hasNext()){
            program.addExecutableNode(parseExecutableNode(scanner));
        }

        return program;
    }


    public ExecutableNode parseExecutableNode(Scanner s) {
        if(s.hasNext("variable")){
            return parseVariableAssignment(s);
        }
        else if(s.hasNext("print")){
            return parsePrintNode(s);
        }
        //check for function assignment
        //check for variable names
        //check for function names

        //error
        else{
            throw new CompilerException("Invalid statement");
        }

    }

    public VariableAssignmentNode parseVariableAssignment(Scanner s) throws CompilerException{
        String variableName;
        Expression value;

        require("variable","Expected \"variable\"",s);
        if(s.hasNext("[a-z,A-Z]+")){
            variableName = s.next();

            require(Equals,"Expected =",s);

            value = parseExpression(s,false);

            require(NewLine,"Expected NewLine",s);

            if(!scriptVariableNames.contains(variableName)){
                //Add new variable name to list of recognised variables.
                scriptVariableNames.add(variableName);
            }
            System.out.println("setting "+variableName+" to "+value.myMode);
            return new VariableAssignmentNode(variableName,value);
        }else{
            throw new CompilerException("Invalid variable name (Upper/Lower case alphabet characters only): "+s.next());
        }
    }

    public PrintNode parsePrintNode(Scanner s){
        require("print","expected \"print\"",s);
        require(OpenParenthesis,"Expected \"(\"",s);

        Expression value = parseExpression(s, false);

        require(CloseParenthesis,"Expected \")\"",s);
        require(NewLine,"Expected NewLine",s);

        return new PrintNode(value);
    }

    //operationPriority defines the behaviour for parsing lower priority operations (+,-)
    //true = ignore lower priority operations
    public Expression parseExpression(Scanner s, boolean operationPriority){
//        System.out.println("entering parseExp1");

//      Parse the left side of the expression.

        Expression firstExpression = new Expression(new NullVariable());
//        Expression firstExpression;


        //Parse n "!" characters
        boolean hasNotOperator = false;
        while (s.hasNext(Not)){
            hasNotOperator = !hasNotOperator;
        }

        if(s.hasNext(OpenParenthesis)){
            require(OpenParenthesis,"Expecting \"(\"",s);

            if(!hasNotOperator) {
                if (s.hasNext(StringCast)) {
                    require(StringCast, "Expecting \"string\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(parseExpression(s, false), null, Operation.castString);
                } else if (s.hasNext(IntegerCast)) {
                    require(IntegerCast, "Expecting \"integer\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(parseExpression(s, false), null, Operation.castInteger);
                } else if (s.hasNext(FloatCast)) {
                    require(FloatCast, "Expecting \"float\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(parseExpression(s, false), null, Operation.castFloat);
                } else if (s.hasNext(BooleanCast)) {
                    require(BooleanCast, "Expecting \"boolean\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(parseExpression(s, false), null, Operation.castBoolean);
                } else {
                    firstExpression = parseExpression(s, false);
                    require(CloseParenthesis, "Expecting \")\"", s);
                }
            }else{

                if (s.hasNext(StringCast)) {
                    require(StringCast, "Expecting \"string\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(new Expression(parseExpression(s, false), null, Operation.castString), null, Operation.not);
                } else if (s.hasNext(IntegerCast)) {
                    require(IntegerCast, "Expecting \"integer\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(new Expression(parseExpression(s, false), null, Operation.castInteger), null, Operation.not);
                } else if (s.hasNext(FloatCast)) {
                    require(FloatCast, "Expecting \"float\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(new Expression(parseExpression(s, false), null, Operation.castFloat), null, Operation.not);
                } else if (s.hasNext(BooleanCast)) {
                    require(BooleanCast, "Expecting \"boolean\"", s);
                    require(CloseParenthesis, "Expecting \")\"", s);
                    return new Expression(new Expression(parseExpression(s, false), null, Operation.castBoolean), null, Operation.not);
                } else {
                    firstExpression = new Expression(parseExpression(s, false), null, Operation.not);
                    require(CloseParenthesis, "Expecting \")\"", s);
                }

            }

        }


        else if (s.hasNextInt()){firstExpression = new Expression(new IntegerVariable(s.nextInt()));}
        else if (s.hasNextFloat()){
            firstExpression = new Expression(new FloatVariable(s.nextFloat()));}
        else if (s.hasNextBoolean()){firstExpression = new Expression(new BooleanVariable(s.nextBoolean()));}
        else {
            boolean firstVariableSet = false;
            for(String variableName : scriptVariableNames){
                if(s.hasNext(variableName)){
                    String recognisedVariableName = s.next();
                    firstExpression = new Expression(recognisedVariableName);
                    firstVariableSet = true;
                    break;
                }
            }
            if(!firstVariableSet){

                if (s.hasNext(DoubleQuotes)){
                    require(DoubleQuotes,"Expected \"",s);
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
                        s.useDelimiter("[^\\S\\r\\n]|(?=[{}(),;\"+\\-*\\/%#\\n])|(?<=[{}(),;\"+\\-*\\/%#\\n])");
                        require(DoubleQuotes,"Expected \"",s);
                    }
                    //replace original delimiter regex
                    s.useDelimiter("[^\\S\\r\\n]|(?=[{}(),;\"+\\-*\\/%#\\n])|(?<=[{}(),;\"+\\-*\\/%#\\n])");

                }
                else if (s.hasNext(OpenBrace)){
                    firstExpression = parseArrayExpression(s);
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
        else if(s.hasNext(CloseParenthesis)){return firstExpression;}
        else if(s.hasNext(NewLine)){return firstExpression;}

        else if(s.hasNext(Plus)){
            //if operationPriority is true, we are only accepting operations higher in the order of operations.
            //this means + and - are ignored and not consumed by the parser.
            if(operationPriority){
                return firstExpression;
            }else{
                require(Plus,"Expected +",s);
                return new Expression(firstExpression,parseExpression(s,false), Operation.plus);
            }
        }
        else if(s.hasNext(Minus)){
            //if operationPriority is true, we are only accepting operations higher in the order of operations.
            //this means + and - are ignored and not consumed by the parser.
            if(operationPriority){
                return firstExpression;
            }else {
                require(Minus,"Expected -",s);
                return new Expression(firstExpression, parseExpression(s, false), Operation.minus);
            }
        }

        else if(s.hasNext(Times)){
            require(Times, "Expected *", s);
            Expression intermediateExpression =  new Expression(firstExpression, parseExpression(s, true), Operation.times);
            return parseExpression(s,intermediateExpression);
        }

        else if(s.hasNext(Divide)){
            require(Divide, "Expected /", s);
            Expression intermediateExpression = new Expression(firstExpression, parseExpression(s, true), Operation.divide);
            return parseExpression(s, intermediateExpression);
        }
        else if(s.hasNext(Modulo)){
            require(Modulo, "Expected %", s);
            Expression intermediateExpression = new Expression(firstExpression, parseExpression(s, true), Operation.modulo);
            return parseExpression(s, intermediateExpression);
        }

        else if(s.hasNext(And)){
            require(And,"Expecting &",s);
            return new Expression(firstExpression, parseExpression(s,false), Operation.and);
        }

        else if(s.hasNext(Or)){
            require(Or,"Expecting |",s);
            return new Expression(firstExpression, parseExpression(s,false), Operation.or);
        }

        else if (s.hasNext(Equals)){
            require(Equals,"Expecting =",s);
            return new Expression(firstExpression,parseExpression(s,false),Operation.equals);
        }

        else if (s.hasNext(GreaterThan)){
            require(GreaterThan,"Expecting >",s);
            return new Expression(firstExpression,parseExpression(s,false),Operation.greaterThan);
        }

        else if (s.hasNext(LessThan)){
            require(LessThan,"Expecting <",s);
            return new Expression(firstExpression,parseExpression(s,false),Operation.lessThan);
        }


//        if(s.hasNext()){}
//        if(s.hasNext()){}
//        if(s.hasNext()){}



        //Error
        return new Expression(new NullVariable());
    }



    //Alternate form of parseExpression which takes the left side of the expression already made, and only parses the rest of the expression.
    //providedExpression takes the place of firstExpression in the other version of this method.
    //This is used after a higher priority order of operations call of parseExpression is made for multiplication, division or modulo operation.
    //Because the higher priority call ignores + and -, we must use this method, ensuring that if it hasn't finished parsing, it can continue.
    //the boolean operationPriority used by the other version of this method is redundant here, as the priority will be false by default.
    public Expression parseExpression(Scanner s, Expression providedExpression){
//        System.out.println("entering parseExp2");

        if(s.hasNext(CloseBrace)){return providedExpression;}
        else if(s.hasNext(CloseParenthesis)){return providedExpression;}
        else if(s.hasNext(NewLine)){return providedExpression;}


        else if(s.hasNext(Plus)){
            require(Plus,"Expected +",s);
            return new Expression(providedExpression,parseExpression(s,false), Operation.plus);
        }

        else if(s.hasNext(Minus)){
            require(Minus,"Expected -",s);
            return new Expression(providedExpression,parseExpression(s,false), Operation.minus);
        }

        else if(s.hasNext(Times)) {
            require(Times, "Expected *", s);
            Expression intermediateExpression = new Expression(providedExpression, parseExpression(s, true), Operation.times);
            return parseExpression(s, intermediateExpression);
        }

        else if(s.hasNext(Divide)){
            require(Divide, "Expected /", s);
            Expression intermediateExpression = new Expression(providedExpression, parseExpression(s, true), Operation.divide);
            return parseExpression(s, intermediateExpression);
        }

        else if(s.hasNext(Modulo)){
            require(Modulo, "Expected %", s);
            Expression intermediateExpression = new Expression(providedExpression, parseExpression(s, true), Operation.modulo);
            return parseExpression(s, intermediateExpression);
        }

        else if(s.hasNext(And)){
            require(And,"Expecting &",s);
            return new Expression(providedExpression, parseExpression(s,false), Operation.and);
        }

        else if(s.hasNext(Or)){
            require(Or,"Expecting |",s);
            return new Expression(providedExpression, parseExpression(s,false), Operation.or);
        }

        else if (s.hasNext(Equals)){
            require(Equals,"Expecting =",s);
            return new Expression(providedExpression,parseExpression(s,false),Operation.equals);
        }

        else if (s.hasNext(GreaterThan)){
            require(GreaterThan,"Expecting >",s);
            return new Expression(providedExpression,parseExpression(s,false),Operation.greaterThan);
        }

        else if (s.hasNext(LessThan)){
            require(LessThan,"Expecting <",s);
            return new Expression(providedExpression,parseExpression(s,false),Operation.lessThan);
        }


//        if(s.hasNext()){}
//        if(s.hasNext()){}
//        if(s.hasNext()){}


        //Error
        return new Expression(new NullVariable());
    }

    //TODO
    public Expression parseArrayExpression(Scanner s){
        return new Expression(new ArrayVariable(new ArrayList<Variable>()));
    }


    public Parser() {}
}