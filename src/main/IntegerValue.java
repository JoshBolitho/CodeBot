package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class IntegerValue implements Value {

    private final int value;

    public IntegerValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public ValueType getType() {
        return ValueType.INTEGER;
    }

    @Override
    public boolean isType(ValueType v) {
        return v == ValueType.INTEGER;
    }

    @Override
    public String castString() {
        return Integer.toString(value);
    }

    @Override
    public Integer castInteger() {
        return value;
    }

    @Override
    public Float castFloat() {
        return value * 1.0f;
    }

    @Override
    public Boolean castBoolean() {
        return value != 0;
    }

    @Override
    public ArrayList<Value> castArray() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to array",value));
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to image",value));
    }
}
