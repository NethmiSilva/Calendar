package edu.curtin.terminalgriddemo;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Scanner;


/*Class that handles locale changes*/
public class Internationalization {
    private static ResourceBundle resourceBundle;

    public Internationalization(ResourceBundle resourceBundle)
    {
        this.resourceBundle = resourceBundle;
    }

    /*Translates based on user's locale*/
    public static ResourceBundle translate(Locale selectedLocale) {
        String resourceName = "messages";
        try {
            resourceBundle = ResourceBundle.getBundle(resourceName, selectedLocale);

        }catch (MissingResourceException e) {
            System.out.println(resourceBundle.getString("menu.invalidChoice"));
        }

        return resourceBundle;

    }

    /*Change locale after checking resource files*/
    @SuppressWarnings("PMD")
    public static ResourceBundle changeLocale() {
        Scanner scanner = new Scanner(System.in);
        boolean check = false;

        while (!check) {
            System.out.print(resourceBundle.getString("menu.chooseLanguage"));
            String languageTag = scanner.nextLine().trim();
            try {
                check = true;
                Locale selectedLocale = Locale.forLanguageTag(languageTag);
                Locale.setDefault(selectedLocale);
                translate(selectedLocale);
            } catch (MissingResourceException e) {
                check = false;
                System.out.println(resourceBundle.getString("menu.invalidChoice"));
            }
        }
        return resourceBundle;


    }


}
