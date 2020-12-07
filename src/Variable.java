public interface Variable {
    public String asString();
    public Object getValue();
    public VariableType getType();

    //Used for casting attempts, if failed, these will throw errors to the user.
    public String castString() throws ExecutionException;
    public Integer castInteger() throws ExecutionException;
    public Float castFloat() throws ExecutionException;
    public Boolean castBoolean() throws ExecutionException;

}