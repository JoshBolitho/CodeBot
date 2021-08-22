package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class NullValue implements Value {

    public NullValue() {}

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public ValueType getType() {
        return ValueType.NULL;
    }

    @Override
    public boolean isType(ValueType v) {
        return v == ValueType.NULL;
    }

    @Override
    public String castString() {
        return "null";
    }

    @Override
    public Integer castInteger()throws ScriptException {
        throw new ScriptException("Failed to null to integer");
    }

    @Override
    public Double castDouble() throws ScriptException{
        throw new ScriptException("Failed to cast null to float");
    }

    @Override
    public Boolean castBoolean() throws ScriptException{
        throw new ScriptException("Failed to cast null to boolean");
    }

    @Override
    public ArrayList<Value> castArray() throws ScriptException {
        throw new ScriptException("Failed to cast null to array");
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException("Failed to cast null to image");
    }

    @Override
    public Value clone() {
        return new NullValue();
    }
}
