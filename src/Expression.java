import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;

public class Expression {

    enum Mode{
        Value,
        Function,
        InternalFunction,
        Operation,
        Reference
    }
    Mode myMode;

    //Each following groups are initialised exclusively depending on what kind of expression this is (Mode enum).

    //Value
    Variable value;

    //Function or "Internal Function" (Operations not directly available to users)
    String functionName;
    ArrayList<Expression> parameters;
    boolean isInternalFunction;

    //Operation
    Expression expression1;
    Expression expression2;
    Parser.Operation operation;

    //Reference
    String variableReference;

    //Expression is simply a value
    public Expression(Variable value){
        this.value = value;
        myMode = Mode.Value;
    }

    //Expression represents a function call
    public Expression(String functionName, ArrayList<Expression> parameters, boolean isInternalFunction){
        this.functionName = functionName;
        this.parameters = parameters;

        myMode = isInternalFunction? Mode.InternalFunction : Mode.Function;
    }

    //Expression is an operation on two expressions
    //this can form a tree of expression nodes.
    public Expression(Expression expression1, Expression expression2, Parser.Operation operation) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.operation = operation;
        myMode = Mode.Operation;
    }

    //Expression is a String reference to a variable value which can be retrieved on runtime
    public Expression(String variableReference){
        this.variableReference = variableReference;
        myMode = Mode.Reference;
    }



    public Variable evaluate(ProgramState programState, HashMap<String,Variable> functionVariables) throws RuntimeException{

        switch (myMode) {

            case Value:
                //Arrays are a special case, as they are parsed as an array of Expressions,
                //but these expressions need to be evaluated during execution to an array of Variables.
                //Only then, can the variables be accessed.
                if(value.getType() == VariableType.ARRAY){
                    //if the Variable array is null, evaluate the Expression array, and set the Variable array.
                    if (value.getValue() == null){
                        ArrayList<Variable> variables = new ArrayList<>();
                        ArrayVariable thisArrayVariable = (ArrayVariable)value;
                        for (Expression exp : thisArrayVariable.getExpressionArray()){
                            variables.add(exp.evaluate(programState, functionVariables));
                        }
                        ((ArrayVariable) value).setValueArray(variables);
                    }
                }
                return value;

            case Reference:
                if(functionVariables != null){
                    if(functionVariables.containsKey(variableReference)){
                        return functionVariables.get(variableReference);
                    }
                }
                return programState.getProgramVariable(variableReference);

            case Operation:

                //Sometimes, Expression1 and Expression2 can be null, they may not be used by the operator.
                // e.g. ! operator only takes one expression, so expression2 would be null.
                // random() takes no expressions, so expression1 and expression2 would be null.

                Variable value1;
                VariableType type1;
                if(expression1==null){
                    value1 = null;
                }else{
                    value1 = expression1.evaluate(programState, functionVariables);
                }

                if(value1==null){
                    type1 = VariableType.NULL;
                }else{
                    type1 = value1.getType();
                }

                Variable value2;
                VariableType type2;
                if(expression2==null){
                    value2 = null;
                }else{
                    value2 = expression2.evaluate(programState, functionVariables);
                }

                if(value2==null){
                    type2 = VariableType.NULL;
                }else{
                    type2 = value2.getType();
                }

                switch (operation) {
                    case greaterThan:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if(type1 == VariableType.FLOAT && type2 == VariableType.FLOAT ){
                                return new BooleanVariable((float) value1.getValue() > (float) value2.getValue());
                            }
                            else if(type1 == VariableType.FLOAT && type2 == VariableType.INTEGER ){
                                return new BooleanVariable((float) value1.getValue() > (int) value2.getValue());
                            }
                            else if(type1 == VariableType.INTEGER && type2 == VariableType.FLOAT ){
                                return new BooleanVariable((int) value1.getValue() > (float) value2.getValue());
                            }
                            else {
                                return new BooleanVariable((int) value1.getValue() > (int) value2.getValue());
                            }
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case lessThan:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if(type1 == VariableType.FLOAT && type2 == VariableType.FLOAT ){
                                return new BooleanVariable((float) value1.getValue() < (float) value2.getValue());
                            }
                            else if(type1 == VariableType.FLOAT && type2 == VariableType.INTEGER ){
                                return new BooleanVariable((float) value1.getValue() < (int) value2.getValue());
                            }
                            else if(type1 == VariableType.INTEGER && type2 == VariableType.FLOAT ){
                                return new BooleanVariable((int) value1.getValue() < (float) value2.getValue());
                            }
                            else {
                                return new BooleanVariable((int) value1.getValue() < (int) value2.getValue());
                            }
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case equals:

                        if (type1 == VariableType.INTEGER && type2 == VariableType.INTEGER) {
                            return new BooleanVariable((int) value1.getValue() == (int) value2.getValue());
                        }
                        if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                            return new BooleanVariable((float) value1.getValue() == (float) value2.getValue());
                        }
                        if (type1 == VariableType.STRING && type2 == VariableType.STRING) {
                            return new BooleanVariable(
                                value1.getValue().equals(value2.getValue())
                            );
                        }
                        if (type1 == VariableType.BOOLEAN && type2 == VariableType.BOOLEAN) {
                            return new BooleanVariable(value1.getValue() == value2.getValue());
                        }
                        if (type1 == VariableType.NULL && type2 == VariableType.NULL) {
                            return new BooleanVariable(true);
                        }
                        if (type1 == VariableType.ARRAY && type2 == VariableType.ARRAY) {
                            if(value1.castArray().size() != value2.castArray().size()){return new BooleanVariable(false);}

                            for(int i=0; i<value1.castArray().size(); i++){
                                Variable v1 = value1.castArray().get(i);
                                Variable v2 = value2.castArray().get(i);
                                Variable equalityTester = new Expression(new Expression(v1),new Expression(v2),Parser.Operation.equals).evaluate(programState, functionVariables);

                                if(equalityTester.getType() != VariableType.BOOLEAN ||
                                        !equalityTester.castBoolean()
                                ){
                                    return new BooleanVariable(false);
                                }
                            }
                            //every element of the two arrays have been tested and match.
                            return new BooleanVariable(true);
                        }

                        return new BooleanVariable(false);

                    case plus:
                        if (bothValuesAreNumbers(type1, type2)) {

                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() + (float) value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int)value1.getValue() + (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() + (float) (int)value2.getValue());
                            }
                            return new IntegerVariable((int) value1.getValue() + (int) value2.getValue());
                        }
                        //String concatenation
                        if (type1 == VariableType.STRING || type2 == VariableType.STRING) {
                            return new StringVariable(value1.toString() + value2.toString());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case minus:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() - (float) value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int)value1.getValue() - (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() - (float) (int)value2.getValue());
                            }
                            //both are ints
                            return new IntegerVariable((int) value1.getValue() - (int) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case times:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() * (float) value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int)value1.getValue() * (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() * (float) (int)value2.getValue());
                            }
                            //both are ints
                            return new IntegerVariable((int) value1.getValue() * (int) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case divide:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() / (float) value2.getValue());
                            }
                            if (type1 == VariableType.FLOAT && type2 == VariableType.INTEGER) {
                                return new FloatVariable((float) value1.getValue() / (float) (int)value2.getValue());
                            }
                            if (type1 == VariableType.INTEGER && type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) (int)value1.getValue() / (float) value2.getValue());
                            }

                            //if both are integers: only return float if they don't divide to a whole number.
                            if ((int) value1.getValue() % (int) value2.getValue() == 0) {
                                return new IntegerVariable((int) value1.getValue() / (int) value2.getValue());
                            }
                            return new FloatVariable((float) (int)value1.getValue() / (float) (int)value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case modulo:
                        if (type1 == VariableType.INTEGER && type2 == VariableType.INTEGER) {
                            return new IntegerVariable((int) value1.getValue() % (int) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case and:
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() && (Boolean) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case or:
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() || (Boolean) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1,operation,value2));

                    case not:
                        if (type1 == VariableType.BOOLEAN) {
                            return new BooleanVariable(!(Boolean) value1.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s",operation,value1));
                    case castString:
                        return new StringVariable(value1.castString());
                    case castInteger:
                        return new IntegerVariable(value1.castInteger());
                    case castFloat:
                        return new FloatVariable(value1.castFloat());
                    case castBoolean:
                        return new BooleanVariable(value1.castBoolean());

                    case random:
                        return new FloatVariable((float)Math.random());
                    case length:
                        if(type1==VariableType.STRING){
                            return new IntegerVariable(value1.castString().length());
                        }else if(type1==VariableType.ARRAY){
                            return new IntegerVariable(value1.castArray().size());
                        }
                        //error invalid argument
                        return new NullVariable();
                    case charAt:
                        if(type1 == VariableType.STRING && type2 == VariableType.INTEGER){
                            String s = value1.castString();
                            Integer i = value2.castInteger();
                            return new StringVariable(Character.toString(s.charAt(i)));
                        }
                        //error invalid arguments
                    case get:
                        if(type1 == VariableType.ARRAY && type2 == VariableType.INTEGER){
                            Integer i = value2.castInteger();
                            return value1.castArray().get(i);
                        }
                        //error invalid arguments
                    case type:
                        return new StringVariable(type1.toString());
                }
                //error
                return new NullVariable();

            case Function:
                //evaluate the execution of the function.
                if(programState.hasProgramFunction(functionName)){
                    Variable v = programState.getProgramFunction(functionName).executeFunction(parameters,programState);
//                System.out.println("v: "+v);
                    return v;
                }

                throw new ExecutionException("No such function Exists: \""+functionName+"\"");


            case InternalFunction:
                switch (functionName){
                    case "print":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 1){
                            throw new ExecutionException("Wrong number of parameters: expecting 1, received "+parameters.size());
                        }
                        Expression value = parameters.get(0);
                        programState.print(value.evaluate(programState, functionVariables).toString());

                        //print() returns nothing
                        return new NullVariable();

                    case "add":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 2){
                            throw new ExecutionException("Wrong number of parameters: expecting 2 received "+parameters.size());
                        }
                        ArrayVariable addArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        Variable addValue = parameters.get(1).evaluate(programState, functionVariables);

                        addArray.addElement(addValue);

                        //add() returns nothing
                        return new NullVariable();

                    case "remove":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 2){
                            throw new ExecutionException("Wrong number of parameters: expecting 2, received "+parameters.size());
                        }
                        ArrayVariable removeArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        int removeInt = (Integer) ((IntegerVariable)parameters.get(1).evaluate(programState, functionVariables)).getValue();
                        removeArray.removeElement(removeInt);

                        //remove() returns nothing
                        return new NullVariable();

                    case "set":
                        //check the correct number of parameters have been supplied
                        if(parameters.size() != 3){
                            throw new ExecutionException("Wrong number of parameters: expecting 3, received "+parameters.size());
                        }
                        ArrayVariable setArray = (ArrayVariable)parameters.get(0).evaluate(programState, functionVariables);
                        int setInt = (Integer) ((IntegerVariable)parameters.get(1).evaluate(programState, functionVariables)).getValue();
                        Variable setValue = parameters.get(2).evaluate(programState, functionVariables);

                        setArray.setElement(setInt, setValue);

                        //set() returns nothing
                        return new NullVariable();


                    default:
                        return new NullVariable();
                }

            default:
                //error
                return new NullVariable();
        }

    }


    //helper methods
    private boolean bothValuesAreNumbers(VariableType a, VariableType b){
        return  (a == VariableType.FLOAT || a == VariableType.INTEGER)
                &&
                (b == VariableType.FLOAT || b == VariableType.INTEGER);
    }
    private boolean bothValuesAreBooleans(VariableType a, VariableType b){
        return  (a == VariableType.BOOLEAN)
                &&
                (b == VariableType.BOOLEAN);
    }

    @Override
    public String toString() {
        switch (myMode){
            case Value :
                return "Value(" + value + ')';
            case Function:
                return "Function: "+functionName;
            case InternalFunction:
                return "InternalFunction: "+functionName;
            case Operation:
                switch (operation){
                    case not:
                        return "not("+expression1+")";
                    case random:
                        return "random()";
                    case length:
                        return "length("+expression1+")";
                    case charAt:
                        return "charAt("+expression1+","+expression2+")";
                    case get:
                        return "get("+expression1+","+expression2+")";
                    case type:
                        return "type("+expression1+")";
                    case castString:
                        return("castString("+expression1+")");
                    case castInteger:
                        return("castInteger("+expression1+")");
                    case castFloat:
                        return("castFloat("+expression1+")");
                    case castBoolean:
                        return("castBoolean("+expression1+")");

                    default:
                        return "("+expression1 +" "+ operation +" "+ expression2 +")";

                }

            case Reference:
                return "Reference("+variableReference+")";
        }
        return "Expression(" +
                myMode +
                ')';
    }
}
