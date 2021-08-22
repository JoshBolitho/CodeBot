package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BooleanValue implements Value {

    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    @Override
    public ValueType getType() {
        return ValueType.BOOLEAN;
    }

    @Override
    public boolean isType(ValueType v) {
        return v == ValueType.BOOLEAN;
    }

    @Override
    public String castString() {
        return Boolean.toString(value);
    }

    @Override
    public Integer castInteger() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast %s to integer",value));
    }

    @Override
    public Float castFloat() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast %s to float",value));
    }

    @Override
    public Boolean castBoolean() {
        return value;
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
        return new BooleanValue(value);
    }
}
