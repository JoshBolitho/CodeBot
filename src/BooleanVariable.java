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
    public String castString() {
        return Boolean.toString(value);
    }

    @Override
    public Integer castInteger() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast %s to integer",value));
    }

    @Override
    public Float castFloat() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast %s to float",value));
    }

    @Override
    public Boolean castBoolean() {
        return value;
    }

    @Override
    public ArrayList<Variable> castArray() throws ExecutionException {
        throw new ExecutionException(String.format("Failed to cast %s to array",value));
    }
}
