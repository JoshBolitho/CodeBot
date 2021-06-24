package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BooleanVariable implements Variable{

    private boolean value;

    public BooleanVariable(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VariableType getType() {
        return VariableType.BOOLEAN;
    }

    @Override
    public boolean isType(VariableType v) {
        return v == VariableType.BOOLEAN;
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
    public ArrayList<Variable> castArray() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to array",value));
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast %s to image",value));
    }
}
