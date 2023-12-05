package edu.curtin.terminalgriddemo;

import edu.curtin.terminalgrid.TerminalGrid;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


/*Calendar functions to display, search and navigate*/
public class CalendarImplementation {
    public static ResourceBundle messages;

    /*Constructor*/
     public CalendarImplementation()
    {
    }

    /*Set locale translations*/
    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    /*Function to search events in calendar*/
    public static void searchCalendar(LocalDate currentDate, LocalDate endDate, List<Event> eventList) {

        Scanner searchScanner = new Scanner(System.in);

        boolean check = true;

        /*Search events from current date to one year*/
        do  {
            System.out.println(messages.getString("menu.enterSearch"));
            String searchEvent = searchScanner.nextLine().trim().toLowerCase();

            /*Reference: Lecture 7 Notes*/
            searchEvent = Normalizer.normalize(searchEvent, Normalizer.Form.NFC); // Normalize the search term

            boolean found = false;

            /*Search all the events present*/
            for (Event event : eventList) {
                if (event.getTitle().toLowerCase().contains(searchEvent)) {
                    currentDate = event.getStartDate();
                    endDate = currentDate.plusDays(5);  // Seven days gets printed
                    searchEventView(currentDate, endDate, eventList);
                    eventDetails(event);
                    found = true;
                    break; // Exit the loop after the first match is found
                }
            }

            /*Display no event match found*/
            if (!found) {
                System.out.println(messages.getString("menu.searchNoMatch"));
            }

            System.out.println(messages.getString("menu.option.searchAgain"));
            System.out.println(messages.getString("menu.option.returnMenu"));

            String userChoice = searchScanner.nextLine().trim().toLowerCase();
            switch (userChoice) {
                case "1":
                    check = true; //Do another search
                    break;
                case "2":
                    check = false; // Exit  search
                    break;
                default:
                    System.out.println(messages.getString("menu.invalidChoice"));
                    break;
            }
        }while (check && currentDate.isBefore(endDate.plusYears(1)));
    }



    @SuppressWarnings("PMD")
    public static void viewCalendar(LocalDate currentDate, LocalDate endDate, List<Event> events) {
        Scanner viewScanner = new Scanner(System.in);

        String input = "";

        /*Loop till user wants to return to the menu*/
        while (!input.equals("menu")) {
            searchEventView(currentDate, endDate, events);

            System.out.println(messages.getString("menu.navigateOptions"));

            input = viewScanner.nextLine().trim();

            switch (input) {
                /*Current date and end date edited to navigate*/
                case "+d":
                    currentDate = currentDate.plusDays(1);
                    endDate = endDate.plusDays(1);
                    break;
                case "+w":
                    currentDate = currentDate.plusWeeks(1);
                    endDate = endDate.plusWeeks(1);
                    break;
                case "+m":
                    currentDate = currentDate.plusMonths(1);
                    endDate = endDate.plusMonths(1);
                    break;
                case "+y":
                    currentDate = currentDate.plusYears(1);
                    endDate = endDate.plusYears(1);
                    break;
                case "-d":
                    currentDate = currentDate.minusDays(1);
                    endDate = endDate.minusDays(1);
                    break;
                case "-w":
                    currentDate = currentDate.minusWeeks(1);
                    endDate = endDate.minusWeeks(1);
                    break;
                case "-m":
                    currentDate = currentDate.minusMonths(1);
                    endDate = endDate.minusMonths(1);
                    break;
                case "-y":
                    currentDate = currentDate.minusYears(1);
                    endDate = endDate.minusYears(1);
                    break;
                case "t":
                    currentDate = LocalDate.now();
                    endDate = currentDate.plusDays(5);
                    break;
                case "menu":
                    System.out.println(messages.getString("menu.option.returnMenuString"));
                    return; // Return to the menu
                default:
                    System.out.println(messages.getString("menu.option.returnMenu"));
                    break;
            }
        }
        viewScanner.close();

    }

    /*Display event in table after searching*/
    public static void searchEventView(LocalDate currentDate, LocalDate endDate, List<Event> eventList) {
        Locale locale = Locale.getDefault();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy", locale);
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("E", locale);

        TerminalGrid terminalGrid = TerminalGrid.create(System.out, 130);
        terminalGrid.setBoxChars(TerminalGrid.ASCII_BOX_CHARS);
        int numDays = (int) ChronoUnit.DAYS.between(currentDate, endDate) + 1;

        String[][] eventData = new String[26][numDays + 2];

        setHeadings(currentDate, locale, eventData);

        // Populate the header row with day and date information
        setRowHeaders( currentDate,  numDays,  locale, eventData, date,  dayFormat);

        List<List<Event>> dayEvents = new ArrayList<>();
        for (int i = 0; i <= numDays; i++) { // Include endDate
            dayEvents.add(new ArrayList<>());
        }

        // Group events by day
        for (Event event : eventList) {
            LocalDate startDate = event.getStartDate();
            if (!startDate.isBefore(currentDate) && !startDate.isAfter(endDate)) {
                int dayIndex = (int) ChronoUnit.DAYS.between(currentDate, startDate);
                dayEvents.get(dayIndex).add(event);
            }
        }

        // Populate the time rows with event information
        setEvents(eventData ,  numDays,dayEvents, time );

        eventData[25][0] = "All Day Events";

        setAllDayEvents( numDays,  dayEvents, eventData );
        terminalGrid.print(eventData);
    }




    public static void  setRowHeaders(LocalDate currentDate, int numDays, Locale locale, String[][] eventData,
                                      DateTimeFormatter date, DateTimeFormatter dayFormat)
    {
        LocalDate currV = currentDate;
        for (int day = 0; day < numDays + 2; day++) {
            if (day == 0) {
                eventData[0][day] = "   ";
            } else {
                eventData[0][day] = dayFormat.withLocale(locale).format(currV) + " "
                        + date.withLocale(locale).format(currV);
                currV = currV.plusDays(1);
            }
        }
    }

    public static void setHeadings(LocalDate currentDate, Locale locale,  String[][] eventData)
    {
        String headingMonthAndYear = currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale));
        System.out.println(headingMonthAndYear);
        eventData[0][1] = headingMonthAndYear;
    }


    public static void setAllDayEvents(int numberOfDays, List<List<Event>> dayEvents, String[][] eventData )
    {
        for (int day = 0; day <= numberOfDays; day++) {
            List<Event> events = dayEvents.get(day);
            boolean checkAllDay = false;
            String allDayTitle = null;

            for (Event event : events) {
                if (event.isAllDayBoolean()) {
                    checkAllDay = true;
                    allDayTitle = event.getTitle();
                    break;
                }
            }

            if (checkAllDay) {
                eventData[25][day + 1] = allDayTitle.replace("\"", ""); // Remove quotation marks
            } else {
                eventData[25][day + 1] = "";
            }
        }

    }
    /*Print statements to display event values*/
    public static void eventDetails(Event event) {
        Locale locale = Locale.getDefault();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy", locale);
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm", locale);
        String menuDate = messages.getString("menu.date") + event.getStartDate().format(date);

        System.out.println(messages.getString("menu.eventMatchingDetails"));
        System.out.println(menuDate);

        /*Checks if the event is an all day event or not*/
        boolean allDay = event.isAllDayBoolean();
        if (allDay) {
            System.out.println(messages.getString("menu.allDayTime"));
        } else {
            String menuTime = messages.getString("menu.time") + event.getStartTime().format(time);
            String menuDuration = messages.getString("menu.duration") + event.getDuration() + " "
                    + messages.getString("menu.minutes");
            System.out.println(menuTime);
            System.out.println(menuDuration);
        }

        System.out.println(messages.getString("menu.title") + event.getTitle());
    }



    public static void setEvents(String[][]allDayEventData , int numberOfDays,List<List<Event>> eventsList,DateTimeFormatter timeFormat )
    {
        int totalHours = 24;
        for (int hour = 0; hour < totalHours; hour++) {
            allDayEventData[hour + 1][0] = String.format("%02d:00", hour);

            for (int day = 0; day <= numberOfDays; day++) {
                List<Event> events = eventsList.get(day);
                if (events.isEmpty()) {
                    allDayEventData[hour + 1][day + 1] = "";
                } else {
                    StringBuilder eventDetails = new StringBuilder();
                    for (Event event : events) {
                        if (!event.isAllDayBoolean() && event.getStartTime().getHour() == hour) {
                            eventDetails.append(event.getStartTime().format(timeFormat))
                                    .append(" - ")
                                    .append(event.getDuration())
                                    .append(" - ")
                                    .append(event.getTitle())
                                    .append("  ");
                        }
                    }
                    allDayEventData[hour + 1][day + 1] = eventDetails.toString().trim();
                }
            }
        }
    }


}
