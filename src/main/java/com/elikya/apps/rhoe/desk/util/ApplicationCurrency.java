/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.util;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;

import java.util.Properties;

public class ApplicationCurrency {

    public static boolean advancedOptionsAreEnabled() {
        return Boolean.parseBoolean(getConfigs().getProperty("advanced_currency_features"));
    }

    public static double getActualRate() {
        if (advancedOptionsAreEnabled()) {
            return Double.parseDouble(getConfigs().getProperty("converted_value"));
        }
        return 1;
    }

    public static String getActualCurrency() {
        if (advancedOptionsAreEnabled()) {
            return getConfigs().getProperty("second_currency_symbol");
        } else {
            return getConfigs().getProperty("currency");
        }
    }

    public static String getDefaultCurrency() {
        return getConfigs().getProperty("currency");
    }

    private static Properties getConfigs() {
        return RhoeConfig.get();
    }

}
