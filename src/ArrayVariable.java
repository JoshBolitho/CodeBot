import java.util.ArrayList;

public class ArrayVariable implements Variable{

    private ArrayList<Variable> value;

    public ArrayVariable(ArrayList<Variable> value) {
        this.value = value;
    }

    @Override
    public String asString() {
        String result = "[\n";
        for(Variable v : value){
            //still prints commas for last element, close enough though. can fix later.
            result = result + v.asString() + ",\n";
        }
        result += "]";
        return result;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VariableType getType() {
        return VariableType.ARRAY;
    }

    @Override
    public String castString() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast array to string"));
    }

    @Override
    public Integer castInteger() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast array to integer"));
    }

    @Override
    public Float castFloat() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast array to float"));
    }

    @Override
    public Boolean castBoolean() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast array to boolean"));
    }
}
