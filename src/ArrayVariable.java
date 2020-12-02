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
    public String castString() {
        return null;
    }

    @Override
    public Integer castInteger() {
        return null;
    }

    @Override
    public Float castFloat() {
        return null;
    }

    @Override
    public Boolean castBoolean() {
        return null;
    }
}
