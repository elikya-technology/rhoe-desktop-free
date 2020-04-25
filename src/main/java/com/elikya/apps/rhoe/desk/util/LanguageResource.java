/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.util;

import com.elikya.apps.rhoe.desk.observers.interfaces.LanguageObserver;
import org.jasypt.properties.EncryptableProperties;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LanguageResource {

    public static Properties read(Target target) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = LanguageResource.class
                    .getClassLoader().getResourceAsStream(target.path);
            properties.load(inputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return properties;
    }

    public enum Target {
        LANG_ENGLISH("lang/lang_english.properties"),
        LANG_FRENCH("lang/lang_french.properties");

        public final String path;
        Target(String target) {this.path = target;}
    }
}
