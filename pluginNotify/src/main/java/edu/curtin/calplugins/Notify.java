package edu.curtin.calplugins;

import edu.curtin.terminalgriddemo.TerminalAppPlugin;
import edu.curtin.terminalgriddemo.TerminalAppPluginAPI;


/*Plugin that implements notifications for  events*/
public class Notify implements TerminalAppPlugin {

    @Override
    public void startPlugin(TerminalAppPluginAPI api) {
        String notification = api.notifyEvent();
        if (!notification.isEmpty()) {
            System.out.println("NotifyPlugin is starting");
            System.out.println(notification);
        }
    }

}
