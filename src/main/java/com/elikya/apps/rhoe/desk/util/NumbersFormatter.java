/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class NumbersFormatter {

    public static String getFormattedString(BigDecimal value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat(buildPattern(), symbols);
        return decimalFormat.format(value);
    }

    public static String removeThousandsSeparator(String value) {
        return value.replace(",", "");
    }

    private static String buildPattern() {
        Properties preferences = Configs.get();
        String decimals = preferences.getProperty("decimals");
        int number = Integer.parseInt(decimals);
        StringBuilder pattern = new StringBuilder("#, ###.");
        for (int i = 0; i < number; i++) {
            pattern.append("0");
        }
        pattern.append(";-").append(pattern.toString());
        return pattern.toString();
    }


}
