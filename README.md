# Calendar
The Calendar Application is a versatile tool designed to display and navigate through calendar events, enhancing user experience with intuitive functionalities. 

## Event Types
The application manages two types of events:

All-Day Events: Events that span the entire day, providing a broad view of significant occurrences.

Time-of-Day Events: Events with specified start times and durations, offering detailed scheduling.

Each event is identified by a title, a free-form text string, and a start date. Time-of-day events additionally include a start time and duration in minutes.

## Navigation
The user interface facilitates seamless navigation through the calendar:

Seven-Day Display: The application showcases events scheduled for seven days, starting from the "current date."

Table Format: Events are presented in a table, where columns represent days, and rows represent times of the day. An exclusive row is reserved for "all-day" events.

Current Date Control: Users can shift the current date forward or backward using designated commands.

## Search Function
The Calendar Application features a robust search function to help users locate specific events effortlessly:

Free-Form Search: Users can input a free-form search string.

Search Scope: The application initiates the search from the current date, exploring all events up to one year beyond the current date.

Event Matching: The application identifies and displays events whose titles contain the specified search term.

## Internationalisation
The Calendar Application prioritizes user accessibility by incorporating internationalization features:

Locale Selection: Users can choose their preferred locale through a menu option, initially using the system's default locale.

Translatable UI Text: All user interface text is translatable based on the selected locale. English and an additional language are supported, ensuring a diverse user experience.

Internationalized Handling: Dates, times, and numbers (durations) within the user interface are properly internationalized, providing a seamless experience for users worldwide.

## Script for Adding Dates
The application includes a script for adding public holidays or notable dates to the calendar. The script creates various appropriately described, all-day events on specific dates. You can find the demonstration input file in which the script is embedded.

## Repeat Plugin
The project includes a plugin with the ID "edu.curtin.calplugins.Repeat." This plugin takes arguments such as title, startDate, startTime, duration, and repeat. It creates an event based on the given details and repeats the event at a specified duration until a year after the start date.

## Notify Plugin
Another plugin with the ID "edu.curtin.calplugins.Notify" is implemented. This plugin takes a single argument, "text." Whenever an event begins with a title containing the specified text, the plugin outputs the complete event details to the user.

## Build Requirements
The application uses Gradle with separate subprojects for the core application, API declarations, and each of the two plugins.

## Usage
To run the core application and load plugins or scripts, use the following command:
./gradlew run --args="calendarfile.cal"
