public class Expression {

    enum Mode{
        Value,
        Operation,
        Reference
    }
    Mode myMode;


    Variable value;

    Expression expression1;
    Expression expression2;
    Parser.Operation operation;

    String variableReference;



    //Expression is simply a value
    public Expression(Variable value){
        this.value = value;
        myMode = Mode.Value;
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




    public Variable evaluate(ProgramState programState) {
        switch (myMode) {

            case Value:
                return value;

            case Reference:
                return programState.getProgramVariable(variableReference);

            case Operation:

                Variable value1 = expression1.evaluate(programState);
                Variable value2 = expression2.evaluate(programState);

                VariableType type1 = value1.getType();
                VariableType type2 = value2.getType();

                switch (operation) {
                    case greaterThan:
                        if (bothValuesAreNumbers(type1, type2)) {
                            return new BooleanVariable((double) value1.getValue() > (double) value2.getValue());
                        }
                        break;

                    case getGreaterThanOrEqual:
                        if (bothValuesAreNumbers(type1, type2)) {
                            return new BooleanVariable((double) value1.getValue() >= (double) value2.getValue());
                        }
                        break;

                    case lessThan:
                        if (bothValuesAreNumbers(type1, type2)) {
                            return new BooleanVariable((double) value1.getValue() < (double) value2.getValue());
                        }
                        break;

                    case lessThanOrEqual:
                        if (bothValuesAreNumbers(type1, type2)) {
                            return new BooleanVariable((double) value1.getValue() <= (double) value2.getValue());
                        }
                        break;

                    case equals:

                        if (type1 == VariableType.INTEGER && type2 == VariableType.INTEGER) {
                            return new BooleanVariable((int) value1.getValue() == (int) value2.getValue());
                        }
                        if (type1 == VariableType.FLOAT && type2 == VariableType.FLOAT) {
                            return new BooleanVariable((float) value1.getValue() == (float) value2.getValue());
                        }
                        if (type1 == VariableType.STRING && type2 == VariableType.STRING) {
                            return new BooleanVariable(
                                    ((String) value1.getValue())
                                            .equals
                                                    ((String) value2.getValue())
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

                            //Float addition
                            if (type1 == VariableType.FLOAT || type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() + (float) value2.getValue());
                            }
                            //Integer addition
                            return new IntegerVariable((int) value1.getValue() + (int) value2.getValue());
                        }
                        //String concatenation
                        if (type1 == VariableType.STRING || type2 == VariableType.STRING) {
                            return new StringVariable(value1.asString() + value2.asString());
                        }
                        break;

                    case minus:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT || type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() - (float) value2.getValue());
                            }
                            return new IntegerVariable((int) value1.getValue() - (int) value2.getValue());
                        }
                        break;

                    case times:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT || type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() * (float) value2.getValue());
                            }
                            return new IntegerVariable((int) value1.getValue() * (int) value2.getValue());
                        }
                        break;

                    case divide:
                        if (bothValuesAreNumbers(type1, type2)) {
                            if (type1 == VariableType.FLOAT || type2 == VariableType.FLOAT) {
                                return new FloatVariable((float) value1.getValue() / (float) value2.getValue());
                            }

                            if ((int) value1.getValue() % (int) value2.getValue() == 0) {
                                return new IntegerVariable((int) value1.getValue() / (int) value2.getValue());
                            }
                            return new FloatVariable((float) value1.getValue() / (float) value2.getValue());
                        }
                        break;

                    case modulo:
                        if (type1 == VariableType.INTEGER && type2 == VariableType.INTEGER) {
                            return new IntegerVariable((int) value1.getValue() % (int) value2.getValue());
                        }
                        break;

                    case and:
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() && (Boolean) value2.getValue());
                        }
                        break;

                    case or:
                        if (bothValuesAreBooleans(type1, type2)) {
                            return new BooleanVariable((Boolean) value1.getValue() || (Boolean) value2.getValue());
                        }
                        break;

                    case not:
                        if (type1 == VariableType.BOOLEAN) {
                            return new BooleanVariable(!(Boolean) value1.getValue());
                        }
                        break;
                }
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
}
