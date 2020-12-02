public class FloatVariable implements Variable{

    private float value;

    public FloatVariable(float value) {
        this.value = value;
    }

    @Override
    public String asString() {
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
