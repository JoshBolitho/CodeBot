package test;

import main.ScriptExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScriptExecutorTest {

    @Test
    void getConsoleOutput() {

        ScriptExecutor scriptExecutor = new ScriptExecutor("print(\"Hello\")", null );

        scriptExecutor.run();
        Assertions.assertEquals("Hello\n",scriptExecutor.getResult());
    }

}