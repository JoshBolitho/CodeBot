package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import main.*;

class ScriptExecutorTest {

    @Test
    void getConsoleOutput() {
        ScriptExecutor scriptExecutor = new ScriptExecutor("print(\"Hello\")");
        scriptExecutor.run();
        Assertions.assertEquals("Hello\n",scriptExecutor.getResult());
    }

}