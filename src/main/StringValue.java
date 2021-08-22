package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class StringValue implements Value {

    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public boolean isType(ValueType v) {
        return v == ValueType.STRING; }

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
        if(value.toLowerCase().equals("true")){
            return true;
        }
        if(value.toLowerCase().equals("false")){
            return false;
        }
        throw new ScriptException(String.format("Failed to cast %s to boolean",value));
    }

    @Override
    public ArrayList<Value> castArray() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to array",value));
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to image",value));
    }

    @Override
    public Value clone() {
        return new StringValue(value);
    }
}
