package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class StringVariable implements Variable{

    private String value;

    public StringVariable(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VariableType getType() {
        return VariableType.STRING;
    }

    @Override
    public boolean isType(VariableType v) {
        return v == VariableType.STRING; }

    @Override
    public String castString() {
        return value;
    }

    @Override
    public Integer castInteger() throws ScriptException {
        try{Integer.parseInt(value);} catch (NumberFormatException e){
            throw new ScriptException(String.format("Failed to cast %s to integer",value));
        }
        return Integer.valueOf(value);
    }

    @Override
    public Float castFloat() throws ScriptException {
        //ensure this string can evaluate to a float
        try{Float.parseFloat(value);} catch (NumberFormatException e){
            throw new ScriptException(String.format("Failed to cast %s to float",value));
        }
        return Float.valueOf(value);
    }

    @Override
    public Boolean castBoolean() throws ScriptException {
        if(value.equals("true")){
            return true;
        }
        if(value.equals("false")){
            return false;
        }
        throw new ScriptException(String.format("Failed to cast %s to boolean",value));
    }

    @Override
    public ArrayList<Variable> castArray() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to array",value));
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to image",value));
    }
}
