package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FloatValue implements Value {

    private final double value;

    public FloatValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public ValueType getType() {
        return ValueType.FLOAT;
    }

    @Override
    public boolean isType(ValueType v) {
        return v == ValueType.FLOAT;
    }

    @Override
    public String castString() {
        return String.valueOf(value);
    }

    @Override
    public Integer castInteger() {
        return (int)value;
    }

    @Override
    public Double castDouble() {
        return value;
    }

    @Override
    public Boolean castBoolean() {
        return (int)value != 0;
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
        return new FloatValue(value);
    }
}
