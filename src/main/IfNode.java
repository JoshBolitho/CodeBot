package main;

import java.util.HashMap;

public class IfNode implements ExecutableNode{

    Expression condition;
    ProgramNode ifBlock;
    ProgramNode elseBlock;

    public IfNode(Expression condition, ProgramNode ifBlock, ProgramNode elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    public IfNode(Expression condition, ProgramNode ifBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
    }

    @Override
    public void execute(ProgramState programState, HashMap<String, Value> functionVariables) throws InterruptedException{
        //Stop execution if the thread is interrupted (program has taken too long to complete execution)
        if (Thread.interrupted()){
                    Thread.currentThread().interrupt();
                    throw new InterruptedException("Thread Interrupted");
                }

        Value conditionResult = condition.evaluate(programState, functionVariables);
        if(conditionResult.getType() != ValueType.BOOLEAN){throw new ScriptException("\"If\" statement's condition did not evaluate to a boolean value");}

        if(conditionResult.castBoolean()){
            //if condition evaluates to true
            ifBlock.execute(programState, functionVariables);

        }else if(hasElseBlock()){
            //else
            elseBlock.execute(programState, functionVariables);
        }
    }

    @Override
    public String toString() {

        if(hasElseBlock()){
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

        if(hasElseBlock()){
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

    @Override
    public ExecutableNode clone() {
        Expression newCondition = condition.clone();

        ProgramNode newIfBlock = new ProgramNode();
        for(ExecutableNode e : ifBlock.getExecutableNodes()){
            newIfBlock.addExecutableNode(e.clone());
        }

        if(elseBlock!=null) {
            ProgramNode newElseBlock = new ProgramNode();
            for (ExecutableNode e : elseBlock.getExecutableNodes()) {
                newElseBlock.addExecutableNode(e.clone());
            }
            return new IfNode(newCondition,newIfBlock,newElseBlock);
        }else{
            return new IfNode(newCondition,newIfBlock);
        }
    }

    private boolean hasElseBlock(){
        return elseBlock != null;
    }
}
