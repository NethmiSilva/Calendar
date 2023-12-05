package edu.curtin.terminalgriddemo;

import java.time.LocalDate;
import java.time.LocalTime;


/*Event class for events created in the calendar*/
public class Event {
    private LocalDate startDate;
    private LocalTime startTime;
    private int duration;
    private String title;
    private boolean allDayBoolean;
    private String allDayDuration;

    private boolean eventNotifiedBoolean;

    /*Constructor*/
    public Event(LocalDate startDate, LocalTime startTime, int duration, String title) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.duration = duration;
        this.title = title;
        this.allDayDuration = null;
        this.allDayBoolean = false;
    }

    public Event(LocalDate startDate, String allDayDuration, String title) {
        this.startDate = startDate;
        this.allDayBoolean = true;
        this.allDayDuration = allDayDuration;
        this.title = title;
        this.startTime = LocalTime.of(0, 0);
        this.duration = 1440;
    }



    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAllDayBoolean() {
        return allDayBoolean;
    }

    public boolean isEventNotifiedBoolean() {
        return eventNotifiedBoolean;
    }


    public void setEventNotifiedBoolean(boolean b) {
        eventNotifiedBoolean = b;
    }
}
