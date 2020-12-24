import java.util.ArrayList;

public class ArrayVariable implements Variable{

    //Stores all parsed expressions in the array, ready to be evaluated once the program is running.
    private ArrayList<Expression> expressionArray;

    //When the Array needs to be Evaluated during execution, probably to assign to a variable,
    //Expressions in expressionArray will be evaluated and valueArray will be set.
    private ArrayList<Variable> valueArray;

    public ArrayVariable(ArrayList<Expression> expressionArray) {
        this.expressionArray = expressionArray;
    }

    @Override
    public String asString() {
        String result = "[\n";
        for(Variable v : valueArray){
            //still prints commas for last element, close enough though. can fix later.
            result = result + v.asString() + ",\n";
        }
        result += "]";
        return result;
    }

    @Override
    public Object getValue() {
        return valueArray;
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

    @Override
    public ArrayList<Variable> castArray() throws ExecutionException {
        return valueArray;
    }

    public ArrayList<Expression> getExpressionArray() {
        return expressionArray;
    }

    public void setValueArray(ArrayList<Variable> valueArray) {
        this.valueArray = valueArray;
    }
}
