public class NullVariable implements Variable{

    public NullVariable() {}

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public VariableType getType() {
        return VariableType.NULL;
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
