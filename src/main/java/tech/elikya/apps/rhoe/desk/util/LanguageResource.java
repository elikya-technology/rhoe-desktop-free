/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.util;

import java.io.*;
import java.util.Properties;

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
