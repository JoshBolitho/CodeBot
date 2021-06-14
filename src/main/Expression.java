package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

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

                //Initialise value1, type1, value2, and type2

                //Sometimes, Expression1 and Expression2 can be null as they may not be used by the operator.
                //e.g. ! operator only takes one expression, so expression2 would be null.
                //random() takes no expressions, so expression1 and expression2 would both be null.

                Variable value1;
                VariableType type1;
                if(expression1==null){
                    value1 = null;
                }else{
                    value1 = expression1.evaluate(programState, functionVariables);
                }

                if(value1==null){
                    type1 = VariableType.NULL;
                }else{
                    type1 = value1.getType();
                }

                Variable value2;
                VariableType type2;
                if(expression2==null){
                    value2 = null;
                }else{
                    value2 = expression2.evaluate(programState, functionVariables);
                }

                if(value2==null){
                    type2 = VariableType.NULL;
                }else{
                    type2 = value2.getType();
                }

                switch (operation) {
                    case greaterThan -> {
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new BooleanVariable((float) value1.getValue() > (float) value2.getValue());
                            } else if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new BooleanVariable((float) value1.getValue() > (int) value2.getValue());
                            } else if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new BooleanVariable((int) value1.getValue() > (float) value2.getValue());
                            } else {
                                return new BooleanVariable((int) value1.getValue() > (int) value2.getValue());
                            }
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case lessThan -> {
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new BooleanVariable((float) value1.getValue() < (float) value2.getValue());
                            } else if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new BooleanVariable((float) value1.getValue() < (int) value2.getValue());
                            } else if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new BooleanVariable((int) value1.getValue() < (float) value2.getValue());
                            } else {
                                return new BooleanVariable((int) value1.getValue() < (int) value2.getValue());
                            }
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case equals -> {
                        if (type1 == VariableType.INTEGER && type2 == VariableType.INTEGER) {
                            return new BooleanVariable((int) value1.getValue() == (int) value2.getValue());
                        }
                        if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                            return new BooleanVariable((float) value1.getValue() == (float) value2.getValue());
                        }
                        if (type1 == VariableType.STRING && type2 == VariableType.STRING) {
                            return new BooleanVariable(
                                    value1.getValue().equals(value2.getValue())
                            );
                        }
                        if (type1 == VariableType.BOOLEAN && type2 == VariableType.BOOLEAN) {
                            return new BooleanVariable(value1.getValue() == value2.getValue());
                        }
                        if (type1 == VariableType.NULL && type2 == VariableType.NULL) {
                            return new BooleanVariable(true);
                        }
                        if (type1 == VariableType.ARRAY && type2 == VariableType.ARRAY) {
                            if (value1.castArray().size() != value2.castArray().size()) {
                                return new BooleanVariable(false);
                            }

                            for (int i = 0; i < value1.castArray().size(); i++) {
                                Variable v1 = value1.castArray().get(i);
                                Variable v2 = value2.castArray().get(i);
                                Variable equalityTester = new Expression(new Expression(v1), new Expression(v2), Parser.Operation.equals).evaluate(programState, functionVariables);

                                if (equalityTester.getType() != VariableType.BOOLEAN ||
                                        !equalityTester.castBoolean()
                                ) {
                                    return new BooleanVariable(false);
                                }
                            }
                            //every element of the two arrays have been tested and match.
                            return new BooleanVariable(true);
                        }
                        return new BooleanVariable(false);
                    }
                    case plus -> {
                        if (bothValuesAreNumbers(type1, type2)) {

                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() + (float) value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int) value1.getValue() + (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() + (float) (int) value2.getValue());
                            }
                            return new IntegerVariable((int) value1.getValue() + (int) value2.getValue());
                        }
                        if (type1 == VariableType.STRING || type2 == VariableType.STRING) {
                            return new StringVariable(value1.toString() + value2.toString());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case minus -> {
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() - (float) value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int) value1.getValue() - (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() - (float) (int) value2.getValue());
                            }
                            //both are ints
                            return new IntegerVariable((int) value1.getValue() - (int) value2.getValue());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case times -> {
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() * (float) value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int) value1.getValue() * (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() * (float) (int) value2.getValue());
                            }
                            //both are ints
                            return new IntegerVariable((int) value1.getValue() * (int) value2.getValue());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case divide -> {
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() / (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() / (float) (int) value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int) value1.getValue() / (float) value2.getValue());
                            }

                            //if both are integers: only return float if they don't divide to a whole number.
                            if ((int) value1.getValue() % (int) value2.getValue() == 0) {
                                return new IntegerVariable((int) value1.getValue() / (int) value2.getValue());
                            }
                            return new FloatVariable((float) (int) value1.getValue() / (float) (int) value2.getValue());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case modulo -> {
                        if (type1 == VariableType.INTEGER && type2 == VariableType.INTEGER) {
                            return new IntegerVariable((int) value1.getValue() % (int) value2.getValue());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case and -> {
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() && (Boolean) value2.getValue());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case or -> {
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() || (Boolean) value2.getValue());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s %s", value1, operation, value2));
                    }
                    case not -> {
                        if (type1 == VariableType.BOOLEAN) {
                            return new BooleanVariable(!(Boolean) value1.getValue());
                        }
                        throw new ScriptException(String.format("Failed to evaluate %s %s", operation, value1));
                    }
                }
                //error
                return new NullVariable();

            case Function:
                //evaluate the execution of the function.
                if(programState.hasProgramFunction(functionName)){
                    Variable var = programState.getProgramFunction(functionName).executeFunction(parameters,programState, functionVariables);
//                System.out.println("v: "+v);
                    return var;
                }

                throw new ScriptException("No such function Exists: \""+functionName+"\"");


            case InternalFunction:
                switch (functionName){
                    case "print":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 1){
                            throw new ScriptException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Expression value = parameters.get(0);

                        programState.print(value.evaluate(programState, functionVariables).castString());

                        //print() returns nothing
                        return new NullVariable();

                    case "add":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 2){
                            throw new ScriptException("Wrong number of parameters: expecting 2 received "+parameters.size());
                        }
                        ArrayVariable addArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        Variable addValue = parameters.get(1).evaluate(programState, functionVariables);

                        addArray.addElement(addValue);

                        //add() returns nothing
                        return new NullVariable();

                    case "remove":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 2){
                            throw new ScriptException("Wrong number of parameters: expecting 2, received "+parameters.size());
                        }
                        ArrayVariable removeArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        int removeInt = (Integer) ((IntegerVariable)parameters.get(1).evaluate(programState, functionVariables)).getValue();
                        removeArray.removeElement(removeInt);

                        //remove() returns nothing
                        return new NullVariable();

                    case "set":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 3){
                            throw new ScriptException("Wrong number of parameters: expecting 3, received "+parameters.size());
                        }
                        ArrayVariable setArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        int setInt = (Integer) ((IntegerVariable)parameters.get(1).evaluate(programState, functionVariables)).getValue();
                        Variable setValue = parameters.get(2).evaluate(programState, functionVariables);

                        setArray.setElement(setInt, setValue);

                        //set() returns nothing
                        return new NullVariable();

                    case "castString":
                        if(parameters.size() != 1){
                            throw new ScriptException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Variable stringCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new StringVariable(stringCast.castString());
                    case "castInteger":
                        if(parameters.size() != 1){
                            throw new ScriptException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Variable integerCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new IntegerVariable(integerCast.castInteger());
                    case "castFloat":
                        if(parameters.size() != 1){
                            throw new ScriptException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Variable floatCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new FloatVariable(floatCast.castFloat());
                    case "castBoolean":
                        if(parameters.size() != 1){
                            throw new ScriptException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Variable booleanCast = parameters.get(0).evaluate(programState, functionVariables);
                        return new BooleanVariable(booleanCast.castBoolean());
                    case "random":
                        if(parameters.size() != 0){
                            throw new ScriptException("Wrong number of parameters: expecting 0, received "+parameters.size());
                        }
                        return new FloatVariable((float)Math.random());
                    case "length":
                        if(parameters.size() != 1){
                            throw new ScriptException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Variable lengthVariable = parameters.get(0).evaluate(programState, functionVariables);
                        VariableType lengthType = lengthVariable.getType();

                        if(lengthType==VariableType.STRING){
                            return new IntegerVariable(lengthVariable.castString().length());
                        }else if(lengthType==VariableType.ARRAY){
                            return new IntegerVariable(lengthVariable.castArray().size());
                        }
                        throw new ScriptException("Unable to get length of "+lengthType+" object");
                    case "charAt":
                        if(parameters.size() != 2){
                            throw new ScriptException("Wrong number of parameters: expecting 2, received "+parameters.size());
                        }
                        Variable charAtString = parameters.get(0).evaluate(programState, functionVariables);
                        Variable charAtInteger = parameters.get(1).evaluate(programState, functionVariables);

                        if(charAtString.getType() == VariableType.STRING && charAtInteger.getType() == VariableType.INTEGER){
                            String s = charAtString.castString();
                            Integer i = charAtInteger.castInteger();
                            return new StringVariable(Character.toString(s.charAt(i)));
                        }
                        throw new ScriptException("Unable to call charAt() with parameters of type: "+charAtString.getType()+", "+charAtInteger.getType());
                    case "get":
                        if(parameters.size() != 2){
                            throw new ScriptException("Wrong number of parameters: expecting 2, received "+parameters.size());
                        }
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
                        throw new ScriptException("Unable to call get() with parameters of type: "+getArray.getType()+", "+getInteger.getType());
                    case "type":
                        if(parameters.size() != 1){
                            throw new ScriptException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Variable typeVariable = parameters.get(0).evaluate(programState, functionVariables);
                        return new StringVariable(typeVariable.toString());
                    case "createImage":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 2){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 2, received "+parameters.size());
                        }
                        IntegerVariable x = (IntegerVariable)parameters.get(0).evaluate(programState, functionVariables);
                        IntegerVariable y = (IntegerVariable)parameters.get(1).evaluate(programState, functionVariables);

                        //createImage() returns an image variable
                        return new ImageVariable((int)x.getValue(),(int)y.getValue());

                    case "setPixel":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 6){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 6, received "+parameters.size());
                        }
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
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 3){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 3, received "+parameters.size());
                        }

                        ImageVariable getPixel_img = (ImageVariable) parameters.get(0).evaluate(programState, functionVariables);
                        Integer getPixel_x = parameters.get(1).evaluate(programState, functionVariables).castInteger();
                        Integer getPixel_y = parameters.get(2).evaluate(programState, functionVariables).castInteger();

                        return getPixel_img.getPixel(getPixel_x, getPixel_y);

                    case "setCanvas":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 1){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        ImageVariable setCanvas_img = (ImageVariable) parameters.get(0).evaluate(programState, functionVariables);
                        programState.addProgramVariable("_canvas",setCanvas_img);

                        //setCanvas() returns nothing
                        return new NullVariable();

                    case "canvasVisible":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 1){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Boolean canvasVisible_bool = parameters.get(0).evaluate(programState, functionVariables).castBoolean();
                        programState.addProgramVariable("_canvasVisibility",new BooleanVariable(canvasVisible_bool));

                        //canvasVisible() returns nothing
                        return new NullVariable();

                    case "sin":
                        if(parameters.size() != 1){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        float sinFloat = parameters.get(0).evaluate(programState, functionVariables).castFloat();
                        return new FloatVariable((float)Math.sin(sinFloat));

                    case "cos":
                        if(parameters.size() != 1){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        float cosFloat = parameters.get(0).evaluate(programState, functionVariables).castFloat();
                        return new FloatVariable((float)Math.cos(cosFloat));

                    case "pow":
                        if(parameters.size() != 2){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 2, received "+parameters.size());
                        }
                        float powBase = parameters.get(0).evaluate(programState, functionVariables).castFloat();
                        float powExponent = parameters.get(1).evaluate(programState, functionVariables).castFloat();
                        return new FloatVariable((float)Math.pow(powBase,powExponent));

                    case "getDimensions":
                        if(parameters.size() != 1){
                            throw new ScriptException(functionName+" Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        BufferedImage sizeImage = parameters.get(0).evaluate(programState, functionVariables).castImage();
                        ArrayList<Expression> sizeArray = new ArrayList<>(Arrays.asList(
                                new Expression(new IntegerVariable(sizeImage.getWidth())),
                                new Expression(new IntegerVariable(sizeImage.getHeight()))
                        ));
                        return new ArrayVariable(sizeArray);
                    default:
                        return new NullVariable();
                }

            default:
                //error
                return new NullVariable();
        }

    }


    //helper methods
    private boolean bothValuesAreNumbers(VariableType a, VariableType b){
        return  (a == VariableType.FLOAT || a == VariableType.INTEGER)
                &&
                (b == VariableType.FLOAT || b == VariableType.INTEGER);
    }
    private boolean bothValuesAreBooleans(VariableType a, VariableType b){
        return  (a == VariableType.BOOLEAN)
                &&
                (b == VariableType.BOOLEAN);
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
