public interface Variable {
    public String asString();
    public Object getValue();
    public VariableType getType();

    //Used for casting attempts, if failed, these will throw errors to the user.
    public String castString();
    public Integer castInteger();
    public Float castFloat();
    public String castBoolean();

}