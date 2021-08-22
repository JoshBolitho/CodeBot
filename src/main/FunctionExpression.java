package main;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionExpression implements Expression {
    String functionName;
    ArrayList<Expression> parameters;

    public FunctionExpression(String functionName, ArrayList<Expression> parameters){
        this.functionName = functionName;
        this.parameters = parameters;
    }

    @Override
    public Value evaluate(ProgramState programState, HashMap<String, Value> functionVariables) throws ScriptException {
        try {
            if (programState.hasProgramFunction(functionName)) {
                return programState.getProgramFunction(functionName)
                        .executeFunction(parameters, programState, functionVariables);
            }
            throw new ScriptException("No such function Exists: \"" + functionName + "\"");

        }catch (ScriptException | StopException e) {
            throw e;
        }catch (InterruptedException e){
            //Convert interrupt into ScriptException
            throw new ScriptException("Program ran for too long");
        } catch (Exception e){
            e.printStackTrace();
            throw new ScriptException("Function call failed: " + this);
        }
    }

    @Override
    public Expression clone() {
        ArrayList<Expression> newParameters = new ArrayList<>();
        for(Expression e : parameters){
            newParameters.add(e.clone());
        }
        return new FunctionExpression(functionName,newParameters);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(functionName+"(");
        for(int i = 0; i<parameters.size();i++){
            res.append(parameters.get(i));
            if(i != parameters.size()-1){
                res.append(", ");
            }
        }
        res.append(")");
        return res.toString();
    }
}
