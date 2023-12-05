package edu.curtin.calplugins;


import edu.curtin.terminalgriddemo.TerminalAppPlugin;
import edu.curtin.terminalgriddemo.TerminalAppPluginAPI;


/*Plugin that implements repeat events*/
public class Repeat implements TerminalAppPlugin {
       @Override
    public void startPlugin(TerminalAppPluginAPI api) {
           System.out.println("Repeat Plugin is starting");
           System.out.println(api.repeatEvent());

    }


}





