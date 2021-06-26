package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static main.VariableType.*;
import static main.VariableType.IMAGE;

public class InternalFunctionExpression implements Expression{

    String functionName;
    ArrayList<Expression> parameters;

    public InternalFunctionExpression(String functionName, ArrayList<Expression> parameters){
        this.functionName = functionName;
        this.parameters = parameters;
    }

    @Override
    public Variable evaluate(ProgramState programState, HashMap<String, Variable> functionVariables) {
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
                        new ValueExpression(new IntegerVariable(image.getWidth())),
                        new ValueExpression(new IntegerVariable(image.getHeight())))
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
        return new NullVariable();
    }

    //Internal Function helper methods
    private Variable getParameter(int i, ProgramState programState, HashMap<String,Variable> functionVariables ){
        return parameters.get(i).evaluate(programState, functionVariables);
    }
    private void failParameters(){

        StringBuilder res = new StringBuilder("Function call failed: "+functionName+"(");
        for(int i = 0; i<parameters.size();i++){
            res.append(parameters.get(i));
            if(i != parameters.size()-1){
                res.append(", ");
            }
        }
        res.append(")");
        throw new ScriptException(res.toString());

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
