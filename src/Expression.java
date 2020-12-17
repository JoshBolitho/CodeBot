public class Expression {

    enum Mode{
        Value,
        Function,
        Operation,
        Reference
    }
    Mode myMode;

    //Each following groups are initialised exclusively depending on what kind of expression this is (Mode enum).

    //Value
    Variable value;

    //Function
    Function function;

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
    public Expression(Function function){
        this.function = function;
        myMode = Mode.Function;
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



    public Variable evaluate(ProgramState programState) throws RuntimeException{

        switch (myMode) {

            case Value:
                return value;

            case Reference:
                return programState.getProgramVariable(variableReference);

            case Operation:

                Variable value1 = expression1.evaluate(programState);
                VariableType type1;
                if(value1==null){
                    type1 = VariableType.NULL;
                }else{
                    type1 = value1.getType();
                }


                //Sometimes, Expression2 will be null, as it is not used by the operator.
                // e.g. ! operator only takes one expression, so expression2 here will be null.
                Variable value2;
                VariableType type2;
                if(expression2==null){
                    value2 = null;
                }else{
                    value2 = expression2.evaluate(programState);
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
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

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
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

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
                            //TODO write recursive array equality checker
                            return new BooleanVariable(false);
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
                            return new StringVariable(value1.asString() + value2.asString());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

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
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

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
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

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
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

                    case modulo:
                        if (type1 == VariableType.INTEGER && type2 == VariableType.INTEGER) {
                            return new IntegerVariable((int) value1.getValue() % (int) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

                    case and:
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() && (Boolean) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

                    case or:
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() || (Boolean) value2.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s %s",value1.asString(),operation,value2.asString()));

                    case not:
                        if (type1 == VariableType.BOOLEAN) {
                            return new BooleanVariable(!(Boolean) value1.getValue());
                        }
                        throw new ExecutionException(String.format("Failed to evaluate %s %s",operation,value1.asString()));
                    case castString:
                        return new StringVariable(value1.castString());
                    case castInteger:
                        return new IntegerVariable(value1.castInteger());
                    case castFloat:
                        return new FloatVariable(value1.castFloat());
                    case castBoolean:
                        return new BooleanVariable(value1.castBoolean());
                }
                return new NullVariable();

            case Function:
                return new NullVariable();

            default:
                return new NullVariable();
        }

    }


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
                return "Value(" + value.asString() + ')';
            case Function:
                return "Function";
            case Operation:
                if(operation == Parser.Operation.not) return "not("+expression1.toString()+")";
                return "("+expression1.toString() +" "+ operation +" "+ expression2.toString()+")";
            case Reference:
                return "Reference("+variableReference+")";
        }
        return "Expression(" +
                myMode +
                ')';
    }
}
