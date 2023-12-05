package edu.curtin.terminalgriddemo;


import org.python.util.*;

import java.io.*;


/*Script Handler class that handles and runs Python scripts*/
public class ScriptHandler {
    private PythonInterpreter pythonInterpreter;

    //Reference : Software Engineering Concepts (COMP3003)
    //6b. Plugins and Scripting Lecture
    public ScriptHandler() {
        pythonInterpreter = new PythonInterpreter();
    }

    public String runScript(String pythonScript) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        pythonInterpreter.setOut(bout);
        pythonInterpreter.exec(pythonScript);
        String result  = bout.toString();
        return result;
    }
}
