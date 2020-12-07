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
        return "null";
    }

    @Override
    public Integer castInteger()throws ExecutionException {
        throw new ExecutionException(String.format("Failed to null to integer"));
    }

    @Override
    public Float castFloat() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast null to float"));
    }

    @Override
    public Boolean castBoolean() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast null to boolean"));
    }
}
