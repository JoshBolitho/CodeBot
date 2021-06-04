import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ArrayVariable implements Variable{

    //Stores all parsed expressions in the array, ready to be evaluated once the program is running.
    private ArrayList<Expression> expressionArray;

    //When the Array needs to be Evaluated during execution, probably to assign to a variable or to print the array,
    //Expressions in expressionArray will be evaluated and valueArray will be set.
    private ArrayList<Variable> valueArray;

    public ArrayVariable(ArrayList<Expression> expressionArray) {
        this.expressionArray = expressionArray;
    }

    @Override
    public String toString() {
        if(valueArray==null){
            String result = "[";
            for(int i=0;i<expressionArray.size();i++){
                result += expressionArray.get(i);
                //Don't add a comma if it's the last element in the array.
                if(i+1 != expressionArray.size()){
                    result += ", ";
                }
            }
            result += "]";
            return result;
        }
        String result = "[";
        for(int i=0;i<valueArray.size();i++){
            result += valueArray.get(i);
            //Don't add a comma if it's the last element in the array.
            if(i+1 != valueArray.size()){
                result += ", ";
            }
        }
        result += "]";
        return result;
    }

    @Override
    public Object getValue() {
        if(valueArray == null){
            System.out.println("Warning: Accessing valueArray, which is null and may not have been initialised yet.");
        }
        return valueArray;
    }
    public Boolean hasValue(){
        return !(valueArray == null);
    }

    @Override
    public VariableType getType() {
        return VariableType.ARRAY;
    }

    @Override
    public String castString() throws ScriptException{
        if(valueArray==null){
            throw new ScriptException("Trying to cast array to string that has not yet been evaluated");
        }

        String result = "[";
        for(int i=0;i<valueArray.size();i++){
            result += valueArray.get(i).castString();
            //Don't add a comma if it's the last element in the array.
            if(i+1 != valueArray.size()){
                result += ", ";
            }
        }
        result += "]";
        return result;
    }

    @Override
    public Integer castInteger() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast array to integer"));
    }

    @Override
    public Float castFloat() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast array to float"));
    }

    @Override
    public Boolean castBoolean() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast array to boolean"));
    }

    @Override
    public ArrayList<Variable> castArray() throws ScriptException {
        return valueArray;
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast array to image"));
    }

    public ArrayList<Expression> getExpressionArray() {
        return expressionArray;
    }

    //Might need to add some error handling stuff around these methods
    public void addElement(Variable value){
        valueArray.add(value);
    }
    public void removeElement(int i){
        valueArray.remove(i);
    }
    public void setElement(int i, Variable value){
        valueArray.set(i,value);
    }


    public void setValueArray(ArrayList<Variable> valueArray) {
        this.valueArray = valueArray;
    }
}
