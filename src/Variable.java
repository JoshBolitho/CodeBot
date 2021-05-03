import java.awt.image.BufferedImage;
import java.util.ArrayList;

public interface Variable {
    public Object getValue();
    public VariableType getType();

    //Used for casting attempts, if failed, these will throw errors to the user.
    public String castString() throws ScriptException;
    public Integer castInteger() throws ScriptException;
    public Float castFloat() throws ScriptException;
    public Boolean castBoolean() throws ScriptException;
    public ArrayList<Variable> castArray() throws ScriptException;
    public BufferedImage castImage() throws ScriptException;

}