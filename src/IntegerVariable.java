public class IntegerVariable implements Variable{

    private int value;

    public IntegerVariable(Integer value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return Integer.toString(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public VariableType getType() {
        return VariableType.INTEGER;
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
