import java.util.ArrayList;

public class StringVariable implements Variable{

    private String value;

    public StringVariable(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VariableType getType() {
        return VariableType.STRING;
    }

    @Override
    public String castString() {
        return value;
    }

    @Override
    public Integer castInteger() throws ExecutionException {
        try{Integer.parseInt(value);} catch (NumberFormatException e){
            throw new ExecutionException(String.format("Failed to cast %s to integer",value));
        }
        return Integer.valueOf(value);
    }

    @Override
    public Float castFloat() throws ExecutionException {
        //ensure this string can evaluate to a float
        try{Float.parseFloat(value);} catch (NumberFormatException e){
            throw new ExecutionException(String.format("Failed to cast %s to float",value));
        }
        return Float.valueOf(value);
    }

    @Override
    public Boolean castBoolean() throws ExecutionException {
        if(value.equals("true")){
            return true;
        }
        if(value.equals("false")){
            return false;
        }
        throw new ExecutionException(String.format("Failed to cast %s to boolean",value));
    }

    @Override
    public ArrayList<Variable> castArray() throws ExecutionException {
        throw new ExecutionException(String.format("Failed to cast %s to array",value));
    }
}
