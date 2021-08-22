package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public interface Value {

    //Helpful methods which allow testing of a Value's type.
    ValueType getType();
    boolean isType(ValueType v);

    //Casting methods are used to retrieve the value.
    //If we know a Value is a StringValue type by calling Value.isType(STRING),
    //we can call Value.castString() with certainty that the value will be returned.

    //We can also attempt to cast it to another type using the other casting methods.
    //If our StringValue's value is "12", we could call castInteger() and it would succeed.
    String castString() throws ScriptException;
    Integer castInteger() throws ScriptException;
    Double castDouble() throws ScriptException;
    Boolean castBoolean() throws ScriptException;
    ArrayList<Value> castArray() throws ScriptException;
    BufferedImage castImage() throws ScriptException;

    Value clone();

}