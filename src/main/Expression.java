package main;

import java.awt.*;
import java.awt.image.BufferedImage;
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

            case Value:
                //Arrays are parsed as an array of Expressions, which must be
                //evaluated to an array of Variables before they are accessible.
                if(value.getType() == VariableType.ARRAY){
                    ((ArrayVariable)value).evaluateArray(programState,functionVariables);
                }
                return value;

            case Reference:

                Variable v;
                if(functionVariables != null && functionVariables.containsKey(variableReference)){
                    v = functionVariables.get(variableReference);
                }else{
                    v = programState.getProgramVariable(variableReference);
                }
                //Arrays are parsed as an array of Expressions, which must be
                //evaluated to an array of Variables before they are accessible.
                if (v.getType()==VariableType.ARRAY){
                    ((ArrayVariable) v).evaluateArray(programState, functionVariables);
                }
                return v;

            case Operation:

                //Evaluate expression1 to a Variable
                Variable value1 = expression1==null? null: expression1.evaluate(programState, functionVariables);
                Variable value2 = expression2==null? null: expression2.evaluate(programState, functionVariables);

                switch (operation) {
                    case greaterThan -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if(value1.isType(FLOAT) || value2.isType(FLOAT)){
                                return new BooleanVariable(value1.castFloat() > value2.castFloat());
                            }
                            return new BooleanVariable(value1.castInteger() > value2.castInteger());
                        }
                        failOperation(value1,value2);
                    }
                    case lessThan -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if(value1.isType(FLOAT) || value2.isType(FLOAT)){
                                return new BooleanVariable(value1.castFloat() < value2.castFloat());
                            }
                            return new BooleanVariable(value1.castInteger() < value2.castInteger());
                        }
                        failOperation(value1,value2);
                    }
                    case equals -> {
                        if(notNull(value1) && notNull(value2)){
                            if(value1.isType(INTEGER) && value2.isType(INTEGER)){
                                return new BooleanVariable(value1.castInteger().equals(value2.castInteger()));
                            }
                            if(value1.isType(FLOAT) && value2.isType(FLOAT)){
                                return new BooleanVariable(value1.castFloat().equals(value2.castFloat()));
                            }
                            if(value1.isType(BOOLEAN) && value2.isType(BOOLEAN)){
                                return new BooleanVariable(value1.castBoolean() == value2.castBoolean());
                            }
                            if(value1.isType(STRING) && value2.isType(STRING)){
                                return new BooleanVariable(value1.castString().equals(value2.castString()));
                            }
                            if(value1.isType(NULL) && value2.isType(NULL)){
                                return new BooleanVariable(true);
                            }
                            if(value1.isType(ARRAY) && value2.isType(ARRAY)){

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

                                    if ( !equalityTester.castBoolean() ) {
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
                            if(value1.isType(FLOAT) || value2.isType(FLOAT)){
                                return new FloatVariable(value1.castFloat() + value2.castFloat());
                            }
                            return new IntegerVariable(value1.castInteger() + value2.castInteger());
                        }

                        if( notNull(value1) && notNull(value2)
                                && (value1.isType(STRING) || value2.isType(STRING))
                        ){
                            return new StringVariable(value1.castString() + value2.castString());
                        }

                        failOperation(value1,value2);
                    }
                    case minus -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if(value1.isType(FLOAT) || value2.isType(FLOAT)){
                                return new FloatVariable(value1.castFloat() - value2.castFloat());
                            }
                            return new IntegerVariable(value1.castInteger() - value2.castInteger());
                        }
                        failOperation(value1,value2);
                    }
                    case times -> {
                        if (bothValuesAreNumbers(value1, value2)) {
                            if(value1.isType(FLOAT) || value2.isType(FLOAT)){
                                return new FloatVariable(value1.castFloat() * value2.castFloat());
                            }
                            //both are ints
                            return new IntegerVariable(value1.castInteger() * value2.castInteger());
                        }
                        failOperation(value1,value2);
                    }
                    case divide -> {
                        if (bothValuesAreNumbers(value1, value2)) {

                            if(value1.isType(FLOAT) || value2.isType(FLOAT)){
                                if(value2.castFloat()==0f || value2.castFloat().isNaN()){ failOperation(value1,value2); }
                                return new FloatVariable(value1.castFloat() / value2.castFloat());
                            }else{
                                if(value2.castInteger()==0){ failOperation(value1,value2); }
                                //if both are integers: only return float if they don't divide to a whole number.
                                if (value1.castInteger() % value2.castInteger() == 0) {
                                    return new IntegerVariable((value1.castInteger() / value2.castInteger()));
                                }
                                return new FloatVariable(value1.castFloat() / value2.castFloat());
                            }
                        }
                        failOperation(value1,value2);
                    }
                    case modulo -> {
                        if ( notNull(value1) && notNull(value2)
                             && value1.isType(INTEGER) && value2.isType(INTEGER)
                        ) {
                            if(value2.castInteger()==0){ failOperation(value1,value2); }
                            return new IntegerVariable(value1.castInteger() % value2.castInteger());
                        }
                        failOperation(value1,value2);
                    }
                    case and -> {
                        if (bothValuesAreBooleans(value1, value2)) {
                            return new BooleanVariable((Boolean) value1.getValue() && (Boolean) value2.getValue());
                        }
                        failOperation(value1,value2);
                    }
                    case or -> {
                        if (bothValuesAreBooleans(value1, value2)) {
                            return new BooleanVariable((Boolean) value1.getValue() || (Boolean) value2.getValue());
                        }
                        failOperation(value1,value2);
                    }
                    case not -> {
                        if (notNull(value1) && value1.isType(BOOLEAN)) {
                            return new BooleanVariable(!value1.castBoolean());
                        }
                        failOperation(value1,value2);
                    }
                }
                //error
                failOperation(value1,value2);


            case Function:
                //execute the function.
                if(programState.hasProgramFunction(functionName)){
                    return programState.getProgramFunction(functionName)
                            .executeFunction(parameters,programState, functionVariables);
                }

                throw new ScriptException("No such function Exists: \""+functionName+"\"");


            case InternalFunction:
                switch (functionName){
                    case "print":
                        checkParameters(1);

                        Expression value = parameters.get(0);

                        programState.print(value.evaluate(programState, functionVariables).castString());

                        //print() returns nothing
                        return new NullVariable();

                    case "add":
                        checkParameters(2);

                        ArrayVariable addArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        Variable addValue = parameters.get(1).evaluate(programState, functionVariables);

                        addArray.addElement(addValue);

                        //add() returns nothing
                        return new NullVariable();

                    case "remove":
                        checkParameters(2);

                        ArrayVariable removeArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        int removeInt = parameters.get(1).evaluate(programState, functionVariables).castInteger();
                        removeArray.removeElement(removeInt);

                        //remove() returns nothing
                        return new NullVariable();

                    case "set":
                        checkParameters(3);

                        ArrayVariable setArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        int setInt = parameters.get(1).evaluate(programState, functionVariables).castInteger();
                        Variable setValue = parameters.get(2).evaluate(programState, functionVariables);

                        setArray.setElement(setInt, setValue);

                        //set() returns nothing
                        return new NullVariable();

                    case "castString":
                        checkParameters(1);
                        Variable stringCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new StringVariable(stringCast.castString());
                    case "castInteger":
                        checkParameters(1);
                        Variable integerCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new IntegerVariable(integerCast.castInteger());
                    case "castFloat":
                        checkParameters(1);
                        Variable floatCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new FloatVariable(floatCast.castFloat());
                    case "castBoolean":
                        checkParameters(1);
                        Variable booleanCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new BooleanVariable(booleanCast.castBoolean());
                    case "random":
                        checkParameters(0);
                        return new FloatVariable((float)Math.random());

                    case "length":
                        checkParameters(1);
                        Variable lengthVariable = parameters.get(0).evaluate(programState, functionVariables);
                        VariableType lengthType = lengthVariable.getType();

                        if(lengthType==VariableType.STRING){
                            return new IntegerVariable(lengthVariable.castString().length());
                        }else if(lengthType==VariableType.ARRAY){
                            return new IntegerVariable(lengthVariable.castArray().size());
                        }
                        failParameters(new Variable[]{lengthVariable});

                    case "charAt":
                        checkParameters(2);

                        Variable charAtString = parameters.get(0).evaluate(programState, functionVariables);
                        Variable charAtInteger = parameters.get(1).evaluate(programState, functionVariables);

                        if(charAtString.getType() == VariableType.STRING && charAtInteger.getType() == VariableType.INTEGER){
                            String s = charAtString.castString();
                            Integer i = charAtInteger.castInteger();
                            return new StringVariable(Character.toString(s.charAt(i)));
                        }
                        failParameters(new Variable[]{charAtString,charAtInteger});

                    case "get":
                        checkParameters(2);

                        Variable getArray = parameters.get(0).evaluate(programState, functionVariables);
                        Variable getInteger = parameters.get(1).evaluate(programState, functionVariables);
                        if(getArray.getType() == VariableType.ARRAY && getInteger.getType() == VariableType.INTEGER){

                            Integer i = getInteger.castInteger();

                            //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                            if (getArray.getValue() == null){
                                ArrayList<Variable> variables = new ArrayList<>();
                                ArrayVariable thisArrayVariable = (ArrayVariable)getArray;
                                for (Expression exp : thisArrayVariable.getExpressionArray()){
                                    variables.add(exp.evaluate(programState, functionVariables));
                                }
                                ((ArrayVariable) getArray).setValueArray(variables);
                            }

                            return getArray.castArray().get(i);
                        }
                        failParameters(new Variable[]{getArray,getInteger});

                    case "type":
                        checkParameters(1);
                        Variable typeVariable = parameters.get(0).evaluate(programState, functionVariables);
                        return new StringVariable(typeVariable.toString());

                    case "createImage":
                        checkParameters(2);

                        IntegerVariable x = (IntegerVariable)parameters.get(0).evaluate(programState, functionVariables);
                        IntegerVariable y = (IntegerVariable)parameters.get(1).evaluate(programState, functionVariables);

                        //createImage() returns an image variable
                        return new ImageVariable((int)x.getValue(),(int)y.getValue());

                    case "setPixel":
                        checkParameters(6);

                        ImageVariable setPixel_img = (ImageVariable) parameters.get(0).evaluate(programState, functionVariables);
                        IntegerVariable setPixel_x = (IntegerVariable)parameters.get(1).evaluate(programState, functionVariables);
                        IntegerVariable setPixel_y = (IntegerVariable)parameters.get(2).evaluate(programState, functionVariables);

                        IntegerVariable setPixel_r = (IntegerVariable)parameters.get(3).evaluate(programState, functionVariables);
                        IntegerVariable setPixel_g = (IntegerVariable)parameters.get(4).evaluate(programState, functionVariables);
                        IntegerVariable setPixel_b = (IntegerVariable)parameters.get(5).evaluate(programState, functionVariables);

                        Color setPixel_colour = new Color(
                                setPixel_r.castInteger(),
                                setPixel_g.castInteger(),
                                setPixel_b.castInteger()
                        );
                        setPixel_img.setPixel((int)setPixel_x.getValue(),(int)setPixel_y.getValue(),setPixel_colour);

                        //setPixel() returns nothing
                        return new NullVariable();

                    case "getPixel":
                        checkParameters(3);

                        ImageVariable getPixel_img = (ImageVariable) parameters.get(0).evaluate(programState, functionVariables);
                        Integer getPixel_x = parameters.get(1).evaluate(programState, functionVariables).castInteger();
                        Integer getPixel_y = parameters.get(2).evaluate(programState, functionVariables).castInteger();

                        return getPixel_img.getPixel(getPixel_x, getPixel_y);

                    case "setCanvas":
                        checkParameters(1);

                        ImageVariable setCanvas_img = (ImageVariable) parameters.get(0).evaluate(programState, functionVariables);
                        programState.addProgramVariable("_canvas",setCanvas_img);
                        programState.addProgramVariable("_canvasVisibility",new BooleanVariable(true));

                        //setCanvas() returns nothing
                        return new NullVariable();

                    case "canvasVisible":
                        checkParameters(1);

                        Boolean canvasVisible_bool = parameters.get(0).evaluate(programState, functionVariables).castBoolean();
                        programState.addProgramVariable("_canvasVisibility",new BooleanVariable(canvasVisible_bool));

                        //canvasVisible() returns nothing
                        return new NullVariable();

                    case "getDimensions":
                        checkParameters(1);

                        BufferedImage sizeImage = parameters.get(0).evaluate(programState, functionVariables).castImage();
                        ArrayList<Expression> sizeArray = new ArrayList<>(Arrays.asList(
                                new Expression(new IntegerVariable(sizeImage.getWidth())),
                                new Expression(new IntegerVariable(sizeImage.getHeight()))
                        ));
                        return new ArrayVariable(sizeArray);

                    case "sin":
                        checkParameters(1);
                        float sinFloat = parameters.get(0).evaluate(programState, functionVariables).castFloat();
                        return new FloatVariable((float)Math.sin(sinFloat));

                    case "cos":
                        checkParameters(1);
                        float cosFloat = parameters.get(0).evaluate(programState, functionVariables).castFloat();
                        return new FloatVariable((float)Math.cos(cosFloat));

                    case "pow":
                        checkParameters(2);
                        Variable powBase = parameters.get(0).evaluate(programState, functionVariables);
                        Variable powExponent = parameters.get(1).evaluate(programState, functionVariables);
                        float result = (float)Math.pow(powBase.castFloat(),powExponent.castFloat());

                        if(Float.isNaN(result)){failParameters(new Variable[]{powBase,powExponent});}
                        if(Float.isInfinite(result)){failParameters(new Variable[]{powBase,powExponent});}
                        return new FloatVariable(result);

                    default:
                        return new NullVariable();
                }

            default:
                //error
                return new NullVariable();
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
        System.out.println(operation);
        System.out.println(expression1);
        System.out.println(expression2);
        throw new ScriptException(String.format("Failed to evaluate %s %s %s", v1, operations.get(operation), v2));
    }

    //Internal Function helper methods
    private void checkParameters(int n){
        //check the correct number of parameters have been supplied
        if(parameters.size() != n){
            throw new ScriptException(
                    String.format("Wrong number of parameters: expecting %s, received %s", n ,parameters.size() )
            );
        }
    }
    private void failParameters(Variable[] params){
        String parameterTypes = "";

        for(int i=0; i<params.length-1; i++){
            parameterTypes += params[i].getType()+", ";
        }
        parameterTypes += params[params.length-1].getType();

        throw new ScriptException(String.format("Unable to call %s() with parameters of type: %s",functionName,parameterTypes));
    }


    @Override
    public String toString() {
        switch (myMode){
            case Value :
                return value.toString();
            case Function:
                StringBuilder res = new StringBuilder(functionName+"(");
                for(int i = 0; i<parameters.size();i++){
                    res.append(parameters.get(i));
                    if(i != parameters.size()-1){
                        res.append(", ");
                    }
                }
                res.append(")");
                return res.toString();

            case InternalFunction:
                StringBuilder res2 = new StringBuilder("[Internal]"+functionName+"(");
                for(int i = 0; i<parameters.size();i++){
                    res2.append(parameters.get(i));
                    if(i != parameters.size()-1){
                        res2.append(", ");
                    }
                }
                res2.append(")");
                return res2.toString();

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
        }
        return "Expression(" +
                myMode +
                ')';
    }
}
