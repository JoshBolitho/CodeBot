public class StringVariable implements Variable{

    private String value;

    public StringVariable(String value) {
        this.value = value;
    }

    @Override
    public String asString() {
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
