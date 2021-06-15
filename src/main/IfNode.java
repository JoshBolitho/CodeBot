package main;

import java.util.HashMap;

public class IfNode implements ExecutableNode{

    Expression condition;
    ProgramNode ifBlock;

    boolean hasElseBlock;
    ProgramNode elseBlock;

    public IfNode(Expression condition, ProgramNode ifBlock, ProgramNode elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.hasElseBlock = true;
        this.elseBlock = elseBlock;
    }

    public IfNode(Expression condition, ProgramNode ifBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;

        this.hasElseBlock = false;
        this.elseBlock = new ProgramNode();
    }

    @Override
    public void execute(ProgramState programState, HashMap<String,Variable> functionVariables) {
        Variable conditionResult = condition.evaluate(programState, functionVariables);
        if(conditionResult.getType() != VariableType.BOOLEAN){throw new ScriptException("if statement's condition didn't evaluate to a boolean value");}

        if(conditionResult.castBoolean()){
            //if condition evaluates to true
            ifBlock.execute(programState, functionVariables);

        }else if(hasElseBlock){
            //else
            elseBlock.execute(programState, functionVariables);
        }
    }

    @Override
    public String toString() {

        if(hasElseBlock){
            return "if("+ condition.toString() +"){\n"+ ifBlock.toString() +"\n} else {\n"+ elseBlock.toString() +"}";
        }else{
            return "if("+ condition.toString() +"){\n"+ ifBlock.toString() +"\n}";

        }
    }

    public String display(int depth) {
        StringBuilder res = new StringBuilder();
        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append("if("+condition+"){\n");
        res.append(ifBlock.display(depth+1));

        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append("}");

        if(hasElseBlock){
            res.append("else{\n");

            res.append(elseBlock.display(depth+1));

            for(int i=0; i<depth; i++){
                res.append("    ");
            }
            res.append("}\n");
        }else{
            res.append("\n");
        }
        return res.toString();
    }
}