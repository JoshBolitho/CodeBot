package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static main.VariableType.*;

public class Expression {

    enum Mode{
        Value,
        Function,
        InternalFunction,
        Operation,
        Reference
    }
    Mode myMode;

    //Each of the following groups are initialised exclusively depending on what kind of expression this is (Mode enum).

    //Value
    Variable value;

    //Reference to a Function or an Internal Function (Internal Functions are operations not directly available to users)
    String functionName;
    ArrayList<Expression> parameters;

    //Operation
    Expression expression1;
    Expression expression2;
    Parser.Operation operation;
    Map<Parser.Operation, String> operations = Map.ofEntries(
            entry(Parser.Operation.equals, "="),
            entry(Parser.Operation.plus, "+"),
            entry(Parser.Operation.minus, "-"),
            entry(Parser.Operation.times, "*"),
            entry(Parser.Operation.divide, "/"),
            entry(Parser.Operation.modulo, "%"),
            entry(Parser.Operation.and, "&"),
            entry(Parser.Operation.or, "|"),
            entry(Parser.Operation.lessThan, "<"),
            entry(Parser.Operation.greaterThan, ">")
    );

    //Reference
    String variableReference;


    //Expression is simply a value
    public Expression(Variable value){
        this.value = value;
        myMode = Mode.Value;
    }

    //Expression represents a function call
    public Expression(String functionName, ArrayList<Expression> parameters, boolean isInternalFunction){
        this.functionName = functionName;
        this.parameters = parameters;

        myMode = isInternalFunction? Mode.InternalFunction : Mode.Function;
    }

    //Expression is an operation on two expressions
    //this can form a tree of expression nodes.
    public Expression(Expression expression1, Expression expression2, Parser.Operation operation) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.operation = operation;
        myMode = Mode.Operation;
    }

    //Expression is a String reference to a variable value which can be retrieved on runtime
    public Expression(String variableReference){
        this.variableReference = variableReference;
        myMode = Mode.Reference;
    }


    public Variable evaluate(ProgramState programState, HashMap<String,Variable> functionVariables) throws RuntimeException{

        switch (myMode) {

            case Value: {
                //Arrays are parsed as an array of Expressions, which must be
                //evaluated to an array of Variables before they are accessible.
                if (value.isType(ARRAY)) {
                    ((ArrayVariable) value).evaluateArray(programState, functionVariables);
                }
                return value;
            }

            case Reference: {

                Variable v;
                if (functionVariables != null && functionVariables.containsKey(variableReference)) {
                    v = functionVariables.get(variableReference);
                } else {
                    v = programState.getProgramVariable(variableReference);
                }
                //Arrays are parsed as an array of Expressions, which must be
                //evaluated to an array of Variables before they are accessible.
                if (v.getType() == VariableType.ARRAY) {
                    ((ArrayVariable) v).evaluateArray(programState, functionVariables);
                }
                return v;
            }

            case Operation: {

                //Evaluate expression1 to a Variable
                Variable value1 = expression1 == null ? null : expression1.evaluate(programState, functionVariables);
                Variable value2 = expression2 == null ? null : expression2.evaluate(programState, functionVariables);

                switch (operation) {
                    case greaterThan -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                                return new BooleanVariable(value1.castFloat() > value2.castFloat());
                            }
                            return new BooleanVariable(value1.castInteger() > value2.castInteger());
                        }
                        failOperation(value1, value2);
                    }
                    case lessThan -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                                return new BooleanVariable(value1.castFloat() < value2.castFloat());
                            }
                            return new BooleanVariable(value1.castInteger() < value2.castInteger());
                        }
                        failOperation(value1, value2);
                    }
                    case equals -> {
                        if (notNull(value1) && notNull(value2)) {
                            if (value1.isType(INTEGER) && value2.isType(INTEGER)) {
                                return new BooleanVariable(value1.castInteger().equals(value2.castInteger()));
                            }
                            if (value1.isType(FLOAT) && value2.isType(FLOAT)) {
                                return new BooleanVariable(value1.castFloat().equals(value2.castFloat()));
                            }
                            if (value1.isType(BOOLEAN) && value2.isType(BOOLEAN)) {
                                return new BooleanVariable(value1.castBoolean() == value2.castBoolean());
                            }
                            if (value1.isType(STRING) && value2.isType(STRING)) {
                                return new BooleanVariable(value1.castString().equals(value2.castString()));
                            }
                            if (value1.isType(NULL) && value2.isType(NULL)) {
                                return new BooleanVariable(true);
                            }
                            if (value1.isType(ARRAY) && value2.isType(ARRAY)) {

                                //ensure both arrays are the same length
                                if (value1.castArray().size() != value2.castArray().size()) {
                                    return new BooleanVariable(false);
                                }

                                //test whether each element in both arrays match up.
                                for (int i = 0; i < value1.castArray().size(); i++) {
                                    Variable v1 = value1.castArray().get(i);
                                    Variable v2 = value2.castArray().get(i);

                                    Variable equalityTester = new Expression(
                                            new Expression(v1),
                                            new Expression(v2),
                                            Parser.Operation.equals
                                    ).evaluate(programState, functionVariables);

                                    if (!equalityTester.castBoolean()) {
                                        return new BooleanVariable(false);
                                    }
                                }
                                //every element of the two arrays have been tested and match.
                                return new BooleanVariable(true);
                            }
                        }

                        return new BooleanVariable(false);
                    }
                    case plus -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                                return new FloatVariable(value1.castFloat() + value2.castFloat());
                            }
                            return new IntegerVariable(value1.castInteger() + value2.castInteger());
                        }

                        if (notNull(value1) && notNull(value2)
                                && (value1.isType(STRING) || value2.isType(STRING))
                        ) {
                            return new StringVariable(value1.castString() + value2.castString());
                        }

                        failOperation(value1, value2);
                    }
                    case minus -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                                return new FloatVariable(value1.castFloat() - value2.castFloat());
                            }
                            return new IntegerVariable(value1.castInteger() - value2.castInteger());
                        }
                        failOperation(value1, value2);
                    }
                    case times -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                                return new FloatVariable(value1.castFloat() * value2.castFloat());
                            }
                            //both are ints
                            return new IntegerVariable(value1.castInteger() * value2.castInteger());
                        }
                        failOperation(value1, value2);
                    }
                    case divide -> {
                        if (bothValuesAreNumbers(value1, value2)) {

                            if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                                if (value2.castFloat() == 0f || value2.castFloat().isNaN()) {
                                    failOperation(value1, value2);
                                }
                                return new FloatVariable(value1.castFloat() / value2.castFloat());
                            } else {
                                if (value2.castInteger() == 0) {
                                    failOperation(value1, value2);
                                }
                                //if both are integers: only return float if they don't divide to a whole number.
                                if (value1.castInteger() % value2.castInteger() == 0) {
                                    return new IntegerVariable((value1.castInteger() / value2.castInteger()));
                                }
                                return new FloatVariable(value1.castFloat() / value2.castFloat());
                            }
                        }
                        failOperation(value1, value2);
                    }
                    case modulo -> {
                        if (notNull(value1) && notNull(value2)
                                && value1.isType(INTEGER) && value2.isType(INTEGER)
                        ) {
                            if (value2.castInteger() == 0) {
                                failOperation(value1, value2);
                            }
                            return new IntegerVariable(value1.castInteger() % value2.castInteger());
                        }
                        failOperation(value1, value2);
                    }
                    case and -> {
                        if (bothValuesAreBooleans(value1, value2)) {
                            return new BooleanVariable((Boolean) value1.getValue() && (Boolean) value2.getValue());
                        }
                        failOperation(value1, value2);
                    }
                    case or -> {
                        if (bothValuesAreBooleans(value1, value2)) {
                            return new BooleanVariable((Boolean) value1.getValue() || (Boolean) value2.getValue());
                        }
                        failOperation(value1, value2);
                    }
                    case not -> {
                        if (notNull(value1) && value1.isType(BOOLEAN)) {
                            return new BooleanVariable(!value1.castBoolean());
                        }
                        failOperation(value1, value2);
                    }
                }
                //error
                failOperation(value1, value2);
            }

            case Function: {
                //execute the function.
                if (programState.hasProgramFunction(functionName)) {
                    return programState.getProgramFunction(functionName)
                            .executeFunction(parameters, programState, functionVariables);
                }

                throw new ScriptException("No such function Exists: \"" + functionName + "\"");
            }

            case InternalFunction: {
                switch (functionName) {
                    case "print": {
                        assertParameters(1);

                        Variable x = getParameter(0, programState, functionVariables);

                        programState.print(x.castString());

                        //print() returns nothing
                        return new NullVariable();
                    }
                    case "add": {
                        assertParameters(2);

                        Variable arr = getParameter(0, programState, functionVariables);
                        Variable element = getParameter(1, programState, functionVariables);

                        assertType(arr, ARRAY);
                        ((ArrayVariable) arr).addElement(element);

                        //add() returns nothing
                        return new NullVariable();

                    }
                    case "remove": {
                        assertParameters(2);

                        Variable arr = getParameter(0, programState, functionVariables);
                        Variable i = getParameter(1, programState, functionVariables);

                        assertType(arr, ARRAY);
                        assertType(i, INTEGER);

                        ArrayVariable array = (ArrayVariable) arr;
                        int index = i.castInteger();

                        //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                        if (!array.hasValue()) {
                            array.evaluateArray(programState, functionVariables);
                        }

                        assertRange(index, 0, array.castArray().size());

                        array.removeElement(index);

                        //remove() returns nothing
                        return new NullVariable();
                    }
                    case "set": {
                        assertParameters(3);

                        Variable arr = getParameter(0, programState, functionVariables);
                        Variable i = getParameter(1, programState, functionVariables);
                        Variable element = getParameter(2, programState, functionVariables);

                        assertType(arr, ARRAY);
                        assertType(i, INTEGER);

                        ArrayVariable array = (ArrayVariable) arr;
                        int index = i.castInteger();

                        //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                        if (!array.hasValue()) {
                            array.evaluateArray(programState, functionVariables);
                        }

                        assertRange(index, 0, array.castArray().size());

                        array.setElement(index, element);

                        //set() returns nothing
                        return new NullVariable();
                    }
                    case "get": {
                        assertParameters(2);

                        Variable arr = getParameter(0, programState, functionVariables);
                        Variable i = getParameter(1, programState, functionVariables);

                        assertType(arr, ARRAY);
                        assertType(i, INTEGER);

                        ArrayVariable array = (ArrayVariable) arr;
                        int index = i.castInteger();

                        //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                        if (!array.hasValue()) {
                            array.evaluateArray(programState, functionVariables);
                        }

                        assertRange(index, 0, array.castArray().size());

                        return array.castArray().get(index);
                    }
                    case "castString": {
                        assertParameters(1);
                        Variable variable = getParameter(0, programState, functionVariables);
                        return new StringVariable(variable.castString());
                    }
                    case "castInteger": {
                        assertParameters(1);
                        Variable variable = getParameter(0, programState, functionVariables);
                        return new IntegerVariable(variable.castInteger());
                    }
                    case "castFloat": {
                        assertParameters(1);
                        Variable variable = getParameter(0, programState, functionVariables);
                        return new FloatVariable(variable.castFloat());
                    }
                    case "castBoolean": {
                        assertParameters(1);
                        Variable variable = getParameter(0, programState, functionVariables);
                        return new BooleanVariable(variable.castBoolean());
                    }
                    case "random": {
                        assertParameters(0);
                        return new FloatVariable((float) Math.random());
                    }
                    case "length": {
                        assertParameters(1);
                        Variable variable = getParameter(0, programState, functionVariables);

                        if (variable.isType(STRING)) {
                            return new IntegerVariable(variable.castString().length());
                        }
                        if (variable.isType(ARRAY)) {
                            ArrayVariable array = (ArrayVariable) variable;

                            //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                            if (!array.hasValue()) {
                                array.evaluateArray(programState, functionVariables);
                            }

                            return new IntegerVariable(variable.castArray().size());
                        }
                        failParameters();
                    }
                    case "charAt": {
                        assertParameters(2);

                        Variable str = getParameter(0, programState, functionVariables);
                        Variable i = getParameter(1, programState, functionVariables);

                        assertType(str, STRING);
                        assertType(i, INTEGER);

                        String string = str.castString();
                        int index = i.castInteger();

                        assertRange(index, 0, string.length() - 1);

                        return new StringVariable(Character.toString(string.charAt(index)));
                    }
                    case "type": {
                        assertParameters(1);
                        Variable variable = getParameter(0, programState, functionVariables);
                        return new StringVariable(variable.getType().toString());
                    }
                    case "createImage": {
                        assertParameters(2);

                        int x = getParameter(0, programState, functionVariables).castInteger();
                        int y = getParameter(1, programState, functionVariables).castInteger();

                        //createImage() returns an image variable
                        return new ImageVariable(x, y);
                    }
                    case "setPixel": {
                        assertParameters(6);

                        Variable img = getParameter(0, programState, functionVariables);

                        int x = getParameter(1, programState, functionVariables).castInteger();
                        int y = getParameter(2, programState, functionVariables).castInteger();

                        int r = getParameter(3, programState, functionVariables).castInteger();
                        int g = getParameter(4, programState, functionVariables).castInteger();
                        int b = getParameter(5, programState, functionVariables).castInteger();

                        assertType(img, IMAGE);
                        ImageVariable image = (ImageVariable) img;

                        assertRange(x, 0, image.getWidth() - 1);
                        assertRange(y, 0, image.getWidth() - 1);

                        assertRange(r, 0, 255);
                        assertRange(g, 0, 255);
                        assertRange(b, 0, 255);

                        Color colour = new Color(r, g, b);
                        image.setPixel(x, y, colour);

                        //setPixel() returns nothing
                        return new NullVariable();
                    }
                    case "getPixel": {
                        assertParameters(3);

                        Variable img = getParameter(0, programState, functionVariables);

                        int x = getParameter(1, programState, functionVariables).castInteger();
                        int y = getParameter(2, programState, functionVariables).castInteger();

                        assertType(img, IMAGE);
                        ImageVariable image = (ImageVariable) img;

                        assertRange(x, 0, image.getWidth() - 1);
                        assertRange(y, 0, image.getWidth() - 1);

                        return image.getPixel(x, y);
                    }
                    case "setCanvas": {
                        assertParameters(1);

                        Variable img = getParameter(0, programState, functionVariables);
                        assertType(img, IMAGE);
                        ImageVariable image = (ImageVariable) img;

                        programState.addProgramVariable("_canvas", image);
                        programState.addProgramVariable("_canvasVisibility", new BooleanVariable(true));

                        //setCanvas() returns nothing
                        return new NullVariable();
                    }
                    case "canvasVisible": {
                        assertParameters(1);

                        Variable b = getParameter(0, programState, functionVariables);
                        assertType(b, BOOLEAN);
                        BooleanVariable bool = (BooleanVariable) b;

                        programState.addProgramVariable("_canvasVisibility", bool);

                        //canvasVisible() returns nothing
                        return new NullVariable();
                    }
                    case "getDimensions": {
                        assertParameters(1);

                        Variable img = getParameter(0, programState, functionVariables);
                        assertType(img, IMAGE);
                        ImageVariable image = (ImageVariable) img;

                        ArrayList<Expression> array = new ArrayList<>(Arrays.asList(
                                new Expression(new IntegerVariable(image.getWidth())),
                                new Expression(new IntegerVariable(image.getHeight())))
                        );
                        return new ArrayVariable(array);
                    }
                    case "sin": {
                        assertParameters(1);

                        float x = getParameter(0, programState, functionVariables).castFloat();
                        return new FloatVariable((float) Math.sin(x));
                    }
                    case "cos": {
                        assertParameters(1);

                        float x = getParameter(0, programState, functionVariables).castFloat();
                        return new FloatVariable((float) Math.cos(x));
                    }
                    case "pow": {
                        assertParameters(2);
                        float base = getParameter(0, programState, functionVariables).castFloat();
                        float exponent = getParameter(1, programState, functionVariables).castFloat();

                        float result = (float) Math.pow(base, exponent);

                        if (Float.isNaN(result)) {
                            failParameters();
                        }
                        if (Float.isInfinite(result)) {
                            failParameters();
                        }

                        return new FloatVariable(result);
                    }
                    default:
                        failParameters();
                }
            }

            default: {
                //error
                return new NullVariable();
            }
        }

    }

    //Operation helper methods
    private boolean bothValuesAreNumbers(Variable a, Variable b){
        if(a==null || b==null){return false;}
        return  (a.getType() == FLOAT || a.getType() == VariableType.INTEGER)
             && (b.getType() == FLOAT || b.getType() == VariableType.INTEGER);
    }
    private boolean bothValuesAreBooleans(Variable a, Variable b){
        if(a==null || b==null){return false;}
        return  (a.getType() == VariableType.BOOLEAN) && (b.getType() == VariableType.BOOLEAN);
    }
    private boolean notNull(Variable v){
        return v != null;
    }
    private void failOperation(Variable v1, Variable v2){
        if(operation == Parser.Operation.not){
            throw new ScriptException(String.format("Failed to evaluate ! %s", v1));
        }
        throw new ScriptException(String.format("Failed to evaluate %s %s %s", v1, operations.get(operation), v2));
    }

    //Internal Function helper methods
    private Variable getParameter(int i, ProgramState programState, HashMap<String,Variable> functionVariables ){
        return parameters.get(i).evaluate(programState, functionVariables);
    }
    private void failParameters(){
//    private void failParameters(Variable[] params){
//        String parameterTypes = "";
//
//        for(int i=0; i<params.length-1; i++){
//            parameterTypes += params[i].getType()+", ";
//        }
//        parameterTypes += params[params.length-1].getType();

//        throw new ScriptException(String.format("Unable to call %s() with parameters of type: %s",functionName,parameterTypes));

        StringBuilder res = new StringBuilder("Function call failed: "+functionName+"(");
        for(int i = 0; i<parameters.size();i++){
            res.append(parameters.get(i));
            if(i != parameters.size()-1){
                res.append(", ");
            }
        }
        res.append(")");
        throw new ScriptException(res.toString());
//        throw new ScriptException("Function call failed: "+this.toString());

    }
    private void assertParameters(int n){
        //check the correct number of parameters have been supplied
        if(parameters.size() != n){
            throw new ScriptException(
                    String.format("Wrong number of parameters: expecting %s, received %s", n ,parameters.size() )
            );
        }
    }
    private void assertType(Variable variable,VariableType variableType){
        if( variable.getType() != variableType ){failParameters();}
    }
    private void assertRange(int i, int min, int max){
        //boundary inclusive
        if(i<min || i>max){failParameters();}
    };

    @Override
    public String toString() {
        switch (myMode){
            case Value :
                return value.toString();
            case Function, InternalFunction:
                StringBuilder res = new StringBuilder(functionName+"(");
                for(int i = 0; i<parameters.size();i++){
                    res.append(parameters.get(i));
                    if(i != parameters.size()-1){
                        res.append(", ");
                    }
                }
                res.append(")");
                return res.toString();

            case Operation:
                if (operation == Parser.Operation.not) {
                    return "!(" + expression1 + ")";
                }
                if (operations.containsKey(operation)) {
                    return "(" + expression1 + " " + operations.get(operation) + " " + expression2 + ")";
                }
                return "(" + expression1 + " " + operation + " " + expression2 + ")";

            case Reference:
                return variableReference;

            default:
                return "Expression(" +
                        myMode +
                        ')';
        }
    }
}
