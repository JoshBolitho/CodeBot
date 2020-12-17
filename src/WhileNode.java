public class WhileNode implements ExecutableNode{
    Expression condition;
    ProgramNode whileBlock;

    public WhileNode(Expression condition, ProgramNode whileBlock) {
        this.condition = condition;
        this.whileBlock = whileBlock;
    }


    @Override
    public void execute(ProgramState programState) {
        Variable conditionResult = condition.evaluate(programState);
        if(conditionResult.getType() != VariableType.BOOLEAN){throw new ExecutionException("while statement's condition didn't evaluate to a boolean value");}
        Boolean runWhileBlock = conditionResult.castBoolean();

        while(runWhileBlock){
            //execute the while block
            whileBlock.execute(programState);

            //test the while condition again
            conditionResult = condition.evaluate(programState);
            if(conditionResult.getType() != VariableType.BOOLEAN){throw new ExecutionException("while statement's condition didn't evaluate to a boolean value");}
            runWhileBlock = conditionResult.castBoolean();
        }

    }

    @Override
    public String toString() {
        return "while("+condition.toString()+"){\n"+whileBlock.toString()+"\n}";
    }
}
