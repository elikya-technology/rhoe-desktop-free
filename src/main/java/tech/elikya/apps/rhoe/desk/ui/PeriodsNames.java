/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.ui;

import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class PeriodsNames {

    private static Properties getLanguage() {return ControlsHandler.getLanguage();}

    public static String getMonthName(int index) {
        switch (index) {
            case 1: return getLanguage().getProperty("january");
            case 2: return getLanguage().getProperty("february");
            case 3: return getLanguage().getProperty("march");
            case 4: return getLanguage().getProperty("april");
            case 5: return getLanguage().getProperty("may");
            case 6: return getLanguage().getProperty("june");
            case 7: return getLanguage().getProperty("july");
            case 8: return getLanguage().getProperty("august");
            case 9: return getLanguage().getProperty("september");
            case 10: return getLanguage().getProperty("october");
            case 11: return getLanguage().getProperty("november");
            default: return getLanguage().getProperty("december");
        }
    }

    public static String getWeekDayName(int index) {
        switch (index) {
            case 1: return getLanguage().getProperty("monday");
            case 2: return getLanguage().getProperty("tuesday");
            case 3: return getLanguage().getProperty("wednesday");
            case 4: return getLanguage().getProperty("thursday");
            case 5: return getLanguage().getProperty("friday");
            case 6: return getLanguage().getProperty("saturday");
            default: return getLanguage().getProperty("sunday");
                
        }
    }

}
