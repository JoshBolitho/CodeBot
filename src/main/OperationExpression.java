package main;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static main.VariableType.*;

public class OperationExpression implements Expression {

    Expression expression1;
    Expression expression2;
    Parser.Operation operation;
    Map<Parser.Operation, String> operations = Map.ofEntries(
            entry(Parser.Operation.equals, "="),
            entry(Parser.Operation.plus, "+"),
            entry(Parser.Operation.minus, "-"),
            entry(Parser.Operation.times, "*"),
            entry(Parser.Operation.divide, "/"),
            entry(Parser.Operation.modulo, "%"),
            entry(Parser.Operation.and, "&"),
            entry(Parser.Operation.or, "|"),
            entry(Parser.Operation.lessThan, "<"),
            entry(Parser.Operation.greaterThan, ">")
    );

    public OperationExpression(Expression expression1, Expression expression2, Parser.Operation operation) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.operation = operation;
    }

    @Override
    public Variable evaluate(ProgramState programState, HashMap<String, Variable> functionVariables) {
        //Evaluate expression1 to a Variable
        Variable value1 = expression1 == null ? null : expression1.evaluate(programState, functionVariables);

        Variable value2 = expression2 == null ? null : expression2.evaluate(programState, functionVariables);

        try {
            switch (operation) {
                case greaterThan -> {
                    boolean result;
                    if (bothValuesAreNumbers(value1, value2)) {
                        if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                            result = value1.castFloat() > value2.castFloat();
                        } else {
                            result = value1.castInteger() > value2.castInteger();
                        }
                        assertNotNull(result);
                        return new BooleanVariable(result);
                    }
                    fail();
                }
                case lessThan -> {
                    boolean result;
                    if (bothValuesAreNumbers(value1, value2)) {
                        if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                            result = value1.castFloat() < value2.castFloat();
                        }else{
                            result = value1.castInteger() < value2.castInteger();
                        }
                        assertNotNull(result);
                        return new BooleanVariable(result);
                    }
                    fail();
                }
                case equals -> {
                    boolean result;

                    assertNotNull(value1);
                    assertNotNull(value2);

                    if (value1.isType(INTEGER) && value2.isType(INTEGER)) {
                        result = value1.castInteger().equals(value2.castInteger());
                    }
                    else if (value1.isType(FLOAT) && value2.isType(FLOAT)) {
                        result = value1.castFloat().equals(value2.castFloat());
                    }
                    else if (value1.isType(BOOLEAN) && value2.isType(BOOLEAN)) {
                        result = value1.castBoolean() == value2.castBoolean();
                    }
                    else if (value1.isType(STRING) && value2.isType(STRING)) {
                        result = value1.castString().equals(value2.castString());
                    }
                    else if (value1.isType(NULL) && value2.isType(NULL)) {
                        return new BooleanVariable(true);
                    }
                    else if (value1.isType(ARRAY) && value2.isType(ARRAY)) {

                        //ensure both arrays are the same length
                        if (value1.castArray().size() != value2.castArray().size()) {
                            return new BooleanVariable(false);
                        }

                        //test whether each element in both arrays match up.
                        for (int i = 0; i < value1.castArray().size(); i++) {
                            Variable v1 = value1.castArray().get(i);
                            Variable v2 = value2.castArray().get(i);

                            Variable equalityTester = new OperationExpression(
                                    new ValueExpression(v1),
                                    new ValueExpression(v2),
                                    Parser.Operation.equals
                            ).evaluate(programState, functionVariables);

                            assertNotNull(equalityTester.castBoolean());
                            if (!equalityTester.castBoolean()) {
                                return new BooleanVariable(false);
                            }
                        }
                        //every element of the two arrays have been tested and match.
                        return new BooleanVariable(true);
                    }
                    else{
                        result = false;
                    }
                    assertNotNull(result);
                    return new BooleanVariable(result);

                }
                case plus -> {
                    if (bothValuesAreNumbers(value1, value2)) {
                        if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                            float result = value1.castFloat() + value2.castFloat();
                            assertValidFloat(result);
                            return new FloatVariable(result);
                        }
                        Integer result = value1.castInteger() + value2.castInteger();
                        assertNotNull(result);
                        return new IntegerVariable(result);
                    }

                    if (notNull(value1) && notNull(value2)
                            && (value1.isType(STRING) || value2.isType(STRING))
                    ) {
                        String result = value1.castString() + value2.castString();
                        assertNotNull(result);
                        return new StringVariable(result);
                    }

                    fail();
                }
                case minus -> {
                    if (bothValuesAreNumbers(value1, value2)) {
                        if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                            float result = value1.castFloat() - value2.castFloat();
                            assertValidFloat(result);
                            return new FloatVariable(result);
                        }
                        Integer result = value1.castInteger() - value2.castInteger();
                        assertNotNull(result);
                        return new IntegerVariable(result);
                    }
                    fail();
                }
                case times -> {
                    if (bothValuesAreNumbers(value1, value2)) {
                        if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                            float result = value1.castFloat() * value2.castFloat();
                            assertValidFloat(result);
                            return new FloatVariable(result);
                        }
                        //both are ints
                        Integer result = value1.castInteger() * value2.castInteger();
                        assertNotNull(result);
                        return new IntegerVariable(result);
                    }
                    fail();
                }
                case divide -> {
                    if (bothValuesAreNumbers(value1, value2)) {

                        if (value1.isType(FLOAT) || value2.isType(FLOAT)) {
                            if (value2.castFloat() == 0f) { fail(); }
                            float result = value1.castFloat() / value2.castFloat();
                            assertValidFloat(result);
                            return new FloatVariable(result);
                        } else {
                            if (value2.castInteger() == 0) { fail(); }
                            //if both are integers: only return float if they don't divide to a whole number.
                            if (value1.castInteger() % value2.castInteger() == 0) {
                                Integer result = value1.castInteger() / value2.castInteger();
                                assertNotNull(result);
                                return new IntegerVariable(result);
                            }
                            float result = value1.castFloat() / value2.castFloat();
                            assertValidFloat(result);
                            return new FloatVariable(result);
                        }
                    }
                    fail();
                }
                case modulo -> {
                    if (notNull(value1) && notNull(value2)
                            && value1.isType(INTEGER) && value2.isType(INTEGER)
                    ) {
                        if (value2.castInteger() == 0) {
                            fail();
                        }
                        Integer result = value1.castInteger() % value2.castInteger();
                        assertNotNull(result);
                        return new IntegerVariable(result);
                    }
                    fail();
                }
                case and -> {
                    if (bothValuesAreBooleans(value1, value2)) {
                        return new BooleanVariable(value1.castBoolean() && value2.castBoolean());
                    }
                    fail();
                }
                case or -> {
                    if (bothValuesAreBooleans(value1, value2)) {
                        return new BooleanVariable(value1.castBoolean() || value2.castBoolean());
                    }
                    fail();
                }
                case not -> {
                    if (notNull(value1) && value1.isType(BOOLEAN)) {
                        return new BooleanVariable(!value1.castBoolean());
                    }
                    fail();
                }
            }
        }catch (ScriptException | StopException e){throw e;}
        catch (Exception e){ fail();}

        fail();
        return new NullVariable();
    }

    //Operation helper methods
    private boolean bothValuesAreNumbers(Variable a, Variable b){
        if(a==null || b==null){return false;}
        return  (a.isType(FLOAT) || a.isType(INTEGER))
            &&  (b.isType(FLOAT) || b.isType(INTEGER));
    }
    private boolean bothValuesAreBooleans(Variable a, Variable b){
        if(a==null || b==null){return false;}
        return  a.isType(BOOLEAN) && b.isType(BOOLEAN);
    }
    private boolean notNull(Variable v){
        return v != null;
    }
    private void fail() throws ScriptException {
        throw new ScriptException(String.format("Failed to evaluate %s", this.toString()));
    }
    private void assertValidFloat(Float f) throws ScriptException {
        if(     f == null || 
                f.isNaN() || 
                f.isInfinite()
        ){
            fail();
        }
    }
    private void assertNotNull(Object o) throws ScriptException{
        if(o==null){
            fail();
        }
    }
    
    @Override
    public String toString() {
        if (operation == Parser.Operation.not) {
            return "!(" + expression1 + ")";
        }
        if (operations.containsKey(operation)) {
            return "(" + expression1 + " " + operations.get(operation) + " " + expression2 + ")";
        }
        return "(" + expression1 + " " + operation + " " + expression2 + ")";
    }
}
