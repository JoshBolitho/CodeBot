import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FloatVariable implements Variable{

    private float value;

    public FloatVariable(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VariableType getType() {
        return VariableType.FLOAT;
    }

    @Override
    public String castString() {
        return Float.toString(value);
    }

    @Override
    public Integer castInteger() {
        return (int)value;
    }

    @Override
    public Float castFloat() {
        return value;
    }

    @Override
    public Boolean castBoolean() {
        return (int)value != 0;
    }

    @Override
    public ArrayList<Variable> castArray() throws ExecutionException {
        throw new ExecutionException(String.format("Failed to cast %s to array",value));
    }

    @Override
    public BufferedImage castImage() throws ExecutionException {
        throw new ExecutionException(String.format("Failed to cast %s to image",value));
    }
}
