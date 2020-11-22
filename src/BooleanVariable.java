public class BooleanVariable implements Variable{

    private boolean value;

    public BooleanVariable(boolean value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return Boolean.toString(value);
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
    public String castBoolean() {
        return null;
    }
}
