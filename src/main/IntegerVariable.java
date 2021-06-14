package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class IntegerVariable implements Variable{

    private int value;

    public IntegerVariable(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VariableType getType() {
        return VariableType.INTEGER;
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
    public ArrayList<Variable> castArray() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to array",value));
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to image",value));
    }
}
