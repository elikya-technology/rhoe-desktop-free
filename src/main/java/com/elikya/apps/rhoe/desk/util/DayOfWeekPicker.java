/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.util;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Properties;

public class DayOfWeekPicker {

    public static LocalDate getActualDay() {
        return LocalDate.now();
    }

    public static LocalDate getFirstDayOfWeek() {
        LocalDate now = LocalDate.now();
        TemporalField temporalField = WeekFields.of(getLocale()).dayOfWeek();
        return now.with(temporalField, 1);
    }

    public static Locale getLocale() {
        Properties configs = Configs.get();
        String language = configs.getProperty("language");
        if (language.toLowerCase().equals("english"))
            return Locale.US;
        else return Locale.FRANCE;
    }
}
