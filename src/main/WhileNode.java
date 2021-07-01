package main;

import java.util.HashMap;

public class WhileNode implements ExecutableNode{
    Expression condition;
    ProgramNode whileBlock;

    public WhileNode(Expression condition, ProgramNode whileBlock) {
        this.condition = condition;
        this.whileBlock = whileBlock;
    }

    @Override
    public void execute(ProgramState programState, HashMap<String,Variable>functionVariables) throws InterruptedException {
        Variable conditionResult = condition.evaluate(programState, functionVariables);
        if(conditionResult.getType() != VariableType.BOOLEAN){throw new ScriptException("while statement's condition didn't evaluate to a boolean value");}
        Boolean runWhileBlock = conditionResult.castBoolean();

        while(runWhileBlock){

            //Stop execution if the thread is interrupted (program has taken too long to complete execution)
            if (Thread.interrupted()){
                    Thread.currentThread().interrupt();
                    throw new InterruptedException("Thread Interrupted");
            }

            //execute the while block (if it has any nodes in it)
            if(whileBlock.getExecutableNodes().size()==0){return;}
            whileBlock.execute(programState, functionVariables);

            //test the while condition again
            conditionResult = condition.evaluate(programState, functionVariables);
            if(conditionResult.getType() != VariableType.BOOLEAN){throw new ScriptException("while statement's condition didn't evaluate to a boolean value");}
            runWhileBlock = conditionResult.castBoolean();
        }

    }

    @Override
    public String toString() {
        return "while("+condition.toString()+"){\n"+whileBlock.toString()+"\n}";
    }

    public String display(int depth) {
        StringBuilder res = new StringBuilder();
        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append("while("+condition+"){\n");
        res.append(whileBlock.display(depth+1));

        for(int i=0; i<depth; i++){
            res.append("    ");
        }
        res.append("}\n");

        return res.toString();
    }
}
