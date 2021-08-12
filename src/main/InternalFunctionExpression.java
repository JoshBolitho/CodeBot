package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static main.ValueType.*;

public class InternalFunctionExpression implements Expression{

    String functionName;
    ArrayList<Expression> parameters;

    public InternalFunctionExpression(String functionName, ArrayList<Expression> parameters){
        this.functionName = functionName;
        this.parameters = parameters;
    }

    @Override
    public Value evaluate(ProgramState programState, HashMap<String, Value> functionVariables) {
        try {
            switch (functionName) {
                case "print": {
                    assertParameters(1);

                    Value x = getParameter(0, programState, functionVariables);

                    programState.print(x.castString());

                    //print() returns nothing
                    return new NullValue();
                }
                case "add": {
                    assertParameters(2);

                    Value arr = getParameter(0, programState, functionVariables);
                    Value element = getParameter(1, programState, functionVariables);

                    assertType(arr, ARRAY,"array");
                    ((ArrayValue) arr).addElement(element);

                    //add() returns nothing
                    return new NullValue();

                }
                case "remove": {
                    assertParameters(2);

                    Value arr = getParameter(0, programState, functionVariables);
                    Value i = getParameter(1, programState, functionVariables);

                    assertType(arr, ARRAY,"array");
                    assertType(i, INTEGER,"int");

                    ArrayValue array = (ArrayValue) arr;
                    int index = i.castInteger();

                    //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                    if (!array.hasValue()) {
                        array.evaluateArray(programState, functionVariables);
                    }

                    assertRange(index, 0, array.castArray().size(),"int");

                    array.removeElement(index);

                    //remove() returns nothing
                    return new NullValue();
                }
                case "set": {
                    assertParameters(3);

                    Value arr = getParameter(0, programState, functionVariables);
                    Value i = getParameter(1, programState, functionVariables);
                    Value element = getParameter(2, programState, functionVariables);

                    assertType(arr, ARRAY,"array");
                    assertType(i, INTEGER,"int");

                    ArrayValue array = (ArrayValue) arr;
                    int index = i.castInteger();

                    //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                    if (!array.hasValue()) {
                        array.evaluateArray(programState, functionVariables);
                    }

                    assertRange(index, 0, array.castArray().size(),"int");

                    array.setElement(index, element);

                    //set() returns nothing
                    return new NullValue();
                }
                case "get": {
                    assertParameters(2);

                    Value arr = getParameter(0, programState, functionVariables);
                    Value i = getParameter(1, programState, functionVariables);

                    assertType(arr, ARRAY,"array");
                    assertType(i, INTEGER,"int");

                    ArrayValue array = (ArrayValue) arr;
                    int index = i.castInteger();

                    //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                    if (!array.hasValue()) {
                        array.evaluateArray(programState, functionVariables);
                    }

                    assertRange(index, 0, array.castArray().size(),"int");

                    return array.castArray().get(index);
                }
                case "castString": {
                    assertParameters(1);
                    Value value = getParameter(0, programState, functionVariables);
                    return new StringValue(value.castString());
                }
                case "castInteger": {
                    assertParameters(1);
                    Value value = getParameter(0, programState, functionVariables);
                    return new IntegerValue(value.castInteger());
                }
                case "castFloat": {
                    assertParameters(1);
                    Value value = getParameter(0, programState, functionVariables);
                    return new FloatValue(value.castFloat());
                }
                case "castBoolean": {
                    assertParameters(1);
                    Value value = getParameter(0, programState, functionVariables);
                    return new BooleanValue(value.castBoolean());
                }
                case "random": {
                    assertParameters(0);
                    return new FloatValue((float) Math.random());
                }
                case "length": {
                    assertParameters(1);
                    Value value = getParameter(0, programState, functionVariables);

                    if (value.isType(STRING)) {
                        return new IntegerValue(value.castString().length());
                    }
                    if (value.isType(ARRAY)) {
                        ArrayValue array = (ArrayValue) value;

                        //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                        if (!array.hasValue()) {
                            array.evaluateArray(programState, functionVariables);
                        }

                        return new IntegerValue(value.castArray().size());
                    }
                    genericFail("Parameter \"value\" is the wrong type - Expected String or Array, received "+value.getType().name());
                }
                case "charAt": {
                    assertParameters(2);

                    Value str = getParameter(0, programState, functionVariables);
                    Value i = getParameter(1, programState, functionVariables);

                    assertType(str, STRING,"string");
                    assertType(i, INTEGER,"int");

                    String string = str.castString();
                    int index = i.castInteger();

                    assertRange(index, 0, string.length() - 1,"int");

                    return new StringValue(Character.toString(string.charAt(index)));
                }
                case "type": {
                    assertParameters(1);
                    Value value = getParameter(0, programState, functionVariables);
                    return new StringValue(value.getType().toString().toLowerCase());
                }
                case "createImage": {
                    assertParameters(2);

                    int x = getParameter(0, programState, functionVariables).castInteger();
                    int y = getParameter(1, programState, functionVariables).castInteger();

                    assertRange(x,0,ScriptExecutor.getMaxImageSize(),"x");
                    assertRange(y,0,ScriptExecutor.getMaxImageSize(),"y");

                    //createImage() returns an image variable
                    return new ImageValue(x, y);
                }
                case "setPixel": {
                    assertParameters(6);

                    Value img = getParameter(0, programState, functionVariables);

                    int x = getParameter(1, programState, functionVariables).castInteger();
                    int y = getParameter(2, programState, functionVariables).castInteger();

                    int r = getParameter(3, programState, functionVariables).castInteger();
                    int g = getParameter(4, programState, functionVariables).castInteger();
                    int b = getParameter(5, programState, functionVariables).castInteger();

                    assertType(img, IMAGE,"image");
                    ImageValue image = (ImageValue) img;

                    assertRange(x, 0, image.getWidth() - 1,"x");
                    assertRange(y, 0, image.getWidth() - 1,"y");

                    assertRange(r, 0, 255,"r");
                    assertRange(g, 0, 255,"g");
                    assertRange(b, 0, 255,"b");

                    Color colour = new Color(r, g, b);
                    image.setPixel(x, y, colour);

                    //setPixel() returns nothing
                    return new NullValue();
                }
                case "getPixel": {
                    assertParameters(3);

                    Value img = getParameter(0, programState, functionVariables);

                    int x = getParameter(1, programState, functionVariables).castInteger();
                    int y = getParameter(2, programState, functionVariables).castInteger();

                    assertType(img, IMAGE,"image");
                    ImageValue image = (ImageValue) img;

                    assertRange(x, 0, image.getWidth() - 1,"x");
                    assertRange(y, 0, image.getWidth() - 1,"y");

                    return image.getPixel(x, y);
                }
                case "setCanvas": {
                    assertParameters(1);

                    Value img = getParameter(0, programState, functionVariables);
                    assertType(img, IMAGE,"image");
                    ImageValue image = (ImageValue) img;

                    programState.addProgramVariable("_canvas", image);
                    programState.addProgramVariable("_canvasVisibility", new BooleanValue(true));

                    //setCanvas() returns nothing
                    return new NullValue();
                }
                case "canvasVisible": {
                    assertParameters(1);

                    Value b = getParameter(0, programState, functionVariables);
                    assertType(b, BOOLEAN,"boolean");
                    BooleanValue bool = (BooleanValue) b;

                    programState.addProgramVariable("_canvasVisibility", bool);

                    //canvasVisible() returns nothing
                    return new NullValue();
                }
                case "getDimensions": {
                    assertParameters(1);

                    Value img = getParameter(0, programState, functionVariables);
                    assertType(img, IMAGE,"image");
                    ImageValue image = (ImageValue) img;

                    ArrayList<Expression> array = new ArrayList<>(Arrays.asList(
                            new ValueExpression(new IntegerValue(image.getWidth())),
                            new ValueExpression(new IntegerValue(image.getHeight())))
                    );
                    return new ArrayValue(array);
                }
                case "sin": {
                    assertParameters(1);

                    float x = getParameter(0, programState, functionVariables).castFloat();
                    return new FloatValue((float) Math.sin(x));
                }
                case "cos": {
                    assertParameters(1);

                    float x = getParameter(0, programState, functionVariables).castFloat();
                    return new FloatValue((float) Math.cos(x));
                }
                case "pow": {
                    assertParameters(2);
                    float base = getParameter(0, programState, functionVariables).castFloat();
                    float exponent = getParameter(1, programState, functionVariables).castFloat();

                    float result = (float) Math.pow(base, exponent);

                    if (Float.isNaN(result)) {
                        genericFail("Result is not a number");
                    }
                    if (Float.isInfinite(result)) {
                        genericFail("Result is infinite");
                    }

                    return new FloatValue(result);
                }
                default:{ genericFail("Invalid function name");}
            }
        }catch (ScriptException | StopException e){ throw e; }
        //Best not to report to the user that a Java failure has occurred.
        catch (Exception e){ genericFail("Unknown error");}
        return new NullValue();
    }

    //Internal Function helper methods
    private Value getParameter(int i, ProgramState programState, HashMap<String, Value> functionVariables ){
        return parameters.get(i).evaluate(programState, functionVariables);
    }
    private void genericFail(String message) throws ScriptException{
        throw new ScriptException("Function " + this.toString() + " failed: " + message);
    }
    private void assertParameters(int n) throws ScriptException{
        //check the correct number of parameters have been supplied
        if(parameters.size() != n){
            genericFail(String.format(
                    "Wrong number of parameters - expected %s, received %s", n ,parameters.size()
            ));
        }
    }
    private void assertType(Value value, ValueType valueType, String name){
        if( value.getType() != valueType){
            genericFail(String.format(
                    "Parameter \"%s\" is the wrong type - expected %s, received %s",name, valueType, value.getType()
            ));
        }
    }
    private void assertRange(int i, int min, int max, String name) throws ScriptException {
        //boundary inclusive
        if(i<min || i>max){
            genericFail(String.format(
                    "Parameter \"%s\" = %s is out of bounds (%s,%s)",name, i, min, max
            ));
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(functionName+"(");
        for(int i = 0; i<parameters.size();i++){
            res.append(parameters.get(i));
            if(i != parameters.size()-1){
                res.append(", ");
            }
        }
        res.append(")");
        return res.toString();
    }
}
