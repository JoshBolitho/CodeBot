package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public interface Variable {

    //Helpful methods which allow testing of a Variable's type.
    VariableType getType();
    boolean isType(VariableType v);

    // Casting methods are used to retrieve the Variable's value.
    // If we know a Variable is a StringVariable type by calling Variable.isType(STRING),
    //we can call Variable.castString() with certainty that the value will be returned.

    //We can also attempt to cast it to another type using the other casting methods.
    //If our StringVariable's value is "12", we could call castInteger() and it would succeed.
    String castString() throws ScriptException;
    Integer castInteger() throws ScriptException;
    Float castFloat() throws ScriptException;
    Boolean castBoolean() throws ScriptException;
    ArrayList<Variable> castArray() throws ScriptException;
    BufferedImage castImage() throws ScriptException;

}