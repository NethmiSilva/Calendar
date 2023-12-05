package edu.curtin.terminalgriddemo;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import edu.curtin.terminalgriddemo.Parser.MyEventParser;
import edu.curtin.terminalgriddemo.Parser.ParseException;

/**
 * This illustrates different ways to use TerminalGrid. You may not feel you
 * _need_ all the
 * different features shown here.
 */
public class TerminalGridDemo {
    private static String filepath;
    private static String notifyEventTitle = null;

    private Map<String, String> pluginValues = new HashMap<>();

    public static List<Event> eventList = new ArrayList<>();

    private static Internationalization international;


    private static CalendarImplementation calendarImplementation;

    public static ResourceBundle resourceBundle;



    public static void main(String[] args) {
         international = new Internationalization(resourceBundle);
        calendarImplementation = new CalendarImplementation();


        Locale mainLocale = Locale.getDefault();

        Locale locale = mainLocale;
        resourceBundle = international.translate(locale);
        calendarImplementation.setMessages(resourceBundle);
        new TerminalGridDemo().run(args);


    }

    public void handleEvents(String event)
    {
        if (event.startsWith("event")) {
            String[] splitArray = event.split("\\s+");
            if (splitArray.length >= 4) {
                LocalDate startDate = LocalDate.parse(splitArray[1]);
                String title = event.substring(event.indexOf("\"") + 1, event.lastIndexOf("\"")).trim();
                String allDayCheck = splitArray[2];

                if (allDayCheck.equals("all-day")) {
                    eventList.add(new Event(startDate, "All Day", title));
                } else if (splitArray.length >= 5) {
                    LocalTime startTime = LocalTime.parse(splitArray[2]);
                    int duration = Integer.parseInt(splitArray[3]);
                    eventList.add(new Event(startDate, startTime, duration, title));
                }
            }
        }
    }


    public void handleScripts(String event)
    {
        if (event.startsWith("script")) {
            int scriptBegin = event.indexOf("\"");
            int scriptEnd = event.lastIndexOf("\"");
            if (scriptBegin == -1 && scriptEnd == -1) {
                System.out.println(resourceBundle.getString("menu.invalidScript") + event);
            } else {
                String pythonCode = event.substring(scriptBegin + 1, scriptEnd);
                pythonCode = pythonCode.replace("\"\"", "\"");
                pythonCode = pythonCode.replace("\\", "\\\\");

                /*Script handler called to run python code*/
                ScriptHandler sc = new ScriptHandler();
                String output = sc.runScript(pythonCode);
                System.out.println(resourceBundle.getString("menu.Python.result") + output);
            }
        }
    }


    public void handlePlugins(String event)
    {
        if (event.startsWith("plugin")) {
            pluginValues.clear();
            /*Split using white space characters*/
            String[] splitParts = event.split("\\s+");
            if (splitParts.length >= 2) {
                String pluginId = splitParts[1];

                switch (pluginId) {
                    case "edu.curtin.calplugins.Repeat":
                        String arguments = event.substring(event.indexOf("{") + 1, event.lastIndexOf("}"));
                        String[] argumentV = arguments.split(",");
                        for (String pair : argumentV) {
                            String[] pluginValues = pair.split(":", 2);
                            if (pluginValues.length == 2) {
                                String pluginData = pluginValues[0].trim();
                                String result = pluginValues[1].trim();
                                if (result.startsWith("\"") && result.endsWith("\""))
                                {
                                    result = result.substring(1, result.length() - 1);
                                }
                                this.pluginValues.put(pluginData, result);
                            } else {
                                System.out.println("Error in argument" + pair);
                            }
                        }

                        /*Reference iLecture recording*/
                        var plugins = new ArrayList<TerminalAppPlugin>();

                        try {
                            String apiName = pluginId;
                            Class<?> pluginClass = Class.forName(apiName);
                            plugins.add((TerminalAppPlugin) pluginClass.getConstructor().newInstance());

                        } catch (ReflectiveOperationException | ClassCastException e) {
                            System.out.printf("%s: %s %n", e.getClass().getName(), e.getMessage());
                        }
                        RepeatApiImplmentationTerminal apiImpl = new RepeatApiImplmentationTerminal(this);

                        for (TerminalAppPlugin plugin : plugins) {
                            plugin.startPlugin(apiImpl);
                        }

                        break;

                    case "edu.curtin.calplugins.Notify":
                        String arg2 = event.substring(event.indexOf("{") + 1, event.lastIndexOf("}"));
                        String[] argumentV2 = arg2.split(",");

                        for (String pair : argumentV2) {
                            String[] valuesArray = pair.split(":", 2);
                            if (valuesArray.length == 2) {
                                String pluginData = valuesArray[0].trim();
                                String value = valuesArray[1].trim();
                                if (value.startsWith("\"") && value.endsWith("\"")) {
                                    value = value.substring(1, value.length() - 1);
                                }
                                if ("text".equals(pluginData)) {
                                    notifyEventTitle = value;
                                }
                            } else {
                                System.out.println("Error in argument" + pair);
                            }
                        }

                        var plugins2 = new ArrayList<TerminalAppPlugin>();

                        /*Thread to run notification plugin in the background*/
                        Thread notificationThread = new Thread(() -> {
                            while (true) {

                                /*Reference iLecture recording*/
                                try {
                                    String apiName = pluginId;
                                    Class<?> pluginClass = Class.forName(apiName);
                                    plugins2.add((TerminalAppPlugin) pluginClass.getConstructor().newInstance());
                                } catch (ReflectiveOperationException | ClassCastException e) {
                                    System.out.printf("%s: %s %n", e.getClass().getName(), e.getMessage());
                                }
                                RepeatApiImplmentationTerminal apiImpl2 = new RepeatApiImplmentationTerminal(this);

                                for (TerminalAppPlugin plugin : plugins2) {
                                    plugin.startPlugin(apiImpl2);
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        System.out.println("Error: " + e);
                                    }
                                }
                            }
                        });
                        /*Thread closes when daemon closes till that the thread is running*/
                        notificationThread.setDaemon(true);
                        notificationThread.start();
                        break;
                    default:
                        System.out.println("Unknown plugin ID: " + pluginId);
                        break;
                }
            }
        }
    }



    public void run(String[] args) {

        /*Get file path*/
        if (args.length > 0) {
            filepath = args[0];

        } else {
            System.out.println(resourceBundle.getString("menu.commandLineMessage"));
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(5);

        if (filepath.contains("utf8")) {
            try {
                Charset charset = StandardCharsets.UTF_8;

                String fileData;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), charset))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    fileData = stringBuilder.toString();
                }

                InputStream inputStream = new ByteArrayInputStream(fileData.getBytes(charset));

                try {
                    MyEventParser parser = new MyEventParser(inputStream);

                    List<String> eventsList = parser.Events();
                    for (String event : eventsList) {
                       //  System.out.println(event);
                        handleEvents(event);
                        handlePlugins(event);
                        handleScripts(event);

                    }
                    inputStream.close();
                } catch (ParseException e) {
                    System.err.println("Parser error: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("File IO error: " + e.getMessage());
            }
        }

        else {
            try {
                Charset charset = StandardCharsets.UTF_16;

                String fileContent;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), charset))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    fileContent = stringBuilder.toString();
                }

                InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(charset));

                try {
                    MyEventParser myEventParser = new MyEventParser(inputStream);


                    List<String> eventsList = myEventParser.Events();
                    for (String event : eventsList) {
                        //System.out.println(event);
                        handleEvents(event);
                        handlePlugins(event);
                        handleScripts(event);


                    }
                    inputStream.close();
                } catch (ParseException e) {
                    System.err.println("Parser error: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("File IO error: " + e.getMessage());
            }

        }

        System.out.println("\n" );
        menu(currentDate, endDate);

    }


    public void menu(LocalDate currentDate,LocalDate endDate)
    {
        try(Scanner menuScanner = new Scanner(System.in))
        {
            boolean continueMenu = false;
            while (continueMenu == false) {
                String view = resourceBundle.getString("menu.option.navigate");
                String search =resourceBundle.getString("menu.option.searchCalendar");
                String localeChange = resourceBundle.getString("menu.option.changeLocale");
                String exit = resourceBundle.getString("menu.option.exit");
                String enter = resourceBundle.getString("menu.option.choice");
                System.out.println("\n" + view + "\n" + search + "\n" + localeChange+ "\n"  + exit +"\n" + enter );
                System.out.println();

                String choice = menuScanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        calendarImplementation.viewCalendar(currentDate, endDate, eventList);
                        break;
                    case "2":
                        calendarImplementation.searchCalendar( currentDate, endDate, eventList);
                        break;
                    case "3":
                        resourceBundle = international.changeLocale();
                        calendarImplementation.setMessages(resourceBundle);
                        break;
                    case "4":
                        continueMenu = true;
                        System.out.println(resourceBundle.getString("menu.exitingProgram"));
                        break;
                    default:
                        System.out.println(resourceBundle.getString("menu.invalidChoice"));
                        break;
                }
            }

        }


    }



    public String createCalendarEvent() {
        String title = pluginValues.get("title");
        String startDate = pluginValues.get("startDate");
        String startTime = pluginValues.get("startTime");
        String duration = pluginValues.get("duration");
        String repeat = pluginValues.get("repeat");
        int repeatS = Integer.parseInt(repeat);

        LocalDate startDateSS = LocalDate.parse(startDate);

        if (startTime != null && duration != null) {
            LocalTime startTimeS = LocalTime.parse(startTime);
            int durationS = Integer.parseInt(duration);
            eventList.add(new Event(startDateSS, startTimeS, durationS, title));
        } else {
            /* If startTime or duration is missing, create an all-day event*/
            eventList.add(new Event(startDateSS, "All Day", title));
        }

        if (repeatS != 0 && !repeat.isEmpty()) {
            int repeatDuration = Integer.parseInt(repeat);
            LocalDate currentDate = startDateSS.plusDays(repeatDuration);
            LocalDate oneYearLater = startDateSS.plusYears(1);

            while (currentDate.isBefore(oneYearLater)) {
                if (startTime != null && duration != null) {
                    LocalTime startTimeS = LocalTime.parse(startTime);
                    int durationV = Integer.parseInt(duration);

                    eventList.add(new Event(currentDate, startTimeS, durationV, title));
                } else {
                    eventList.add(new Event(currentDate, "All Day", title));
                }

                currentDate = currentDate.plusDays(repeatDuration);
            }
        }

        return "Plugin Event Created";
    }

    public String notifyEvent() {
        Locale locale = Locale.getDefault();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", locale);

        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();

        for (Event event : eventList) {
            if (event.getTitle().contains(notifyEventTitle) && event.getStartDate().equals(currentDate)) {
                LocalTime eventStartTime = event.getStartTime();
                Duration timeUntilEventStart = Duration.between(currentTime, eventStartTime);

                if (timeUntilEventStart.getSeconds() >= 0 && timeUntilEventStart.getSeconds() <= 1) {
                    if (!event.isEventNotifiedBoolean()) {
                        String detail = "Event starting";
                        String title = resourceBundle.getString("menu.title") + event.getTitle();
                        String time = resourceBundle.getString("menu.time") + eventStartTime.format(timeFormatter);
                        String date = resourceBundle.getString("menu.date") + event.getStartDate();
                        String duration = resourceBundle.getString("menu.duration") + event.getDuration();
                        String notification = "\n"+ detail +  "\n"+ title +"\n"+ time +"\n"+ date +"\n"+ duration +"\n";
                        event.setEventNotifiedBoolean(true);
                        return notification;
                    }
                }
            }
        }

        return "";
    }


}
