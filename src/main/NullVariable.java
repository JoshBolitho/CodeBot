package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class NullVariable implements Variable{

    public NullVariable() {}

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Object getValue() {
        return "null";
    }

    @Override
    public VariableType getType() {
        return VariableType.NULL;
    }

    @Override
    public boolean isType(VariableType v) {
        return v == VariableType.NULL;
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
    public Float castFloat() throws ScriptException{
        throw new ScriptException("Failed to cast null to float");
    }

    @Override
    public Boolean castBoolean() throws ScriptException{
        throw new ScriptException("Failed to cast null to boolean");
    }

    @Override
    public ArrayList<Variable> castArray() throws ScriptException {
        throw new ScriptException("Failed to cast null to array");
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException("Failed to cast null to image");
    }
}
