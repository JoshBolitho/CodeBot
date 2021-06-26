package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public interface Variable {
    Object getValue();
    VariableType getType();
    boolean isType(VariableType v);

    //Used for casting attempts, if failed, these will throw errors to the user.
    String castString() throws ScriptException;
    Integer castInteger() throws ScriptException;
    Float castFloat() throws ScriptException;
    Boolean castBoolean() throws ScriptException;
    ArrayList<Variable> castArray() throws ScriptException;
    BufferedImage castImage() throws ScriptException;

}