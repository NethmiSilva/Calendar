package edu.curtin.terminalgriddemo;


public class RepeatApiImplmentationTerminal implements TerminalAppPluginAPI {
    private TerminalGridDemo obj;
    public RepeatApiImplmentationTerminal(TerminalGridDemo obj){
        this.obj = obj;
    }

    @Override
    public String repeatEvent() {
       return obj.createCalendarEvent();
    }

    @Override
    public String notifyEvent() {
        return obj.notifyEvent();
    }




}
