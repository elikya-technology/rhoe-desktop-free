/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.util;

import java.io.*;
import java.time.LocalDate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mafole Loemelah
 */
public class Configs {

    private static final String CONFIGS_PATH = "cf/rhoe.properties";
    private static Properties configs;

    public static void load() {
        if (configs == null) {
            Properties properties = new Properties();
            try (InputStream input = new FileInputStream(CONFIGS_PATH)) {
                properties.load(input);
                configs = ConfigsEncryptor.decryptProperties(properties);
            } catch (IOException ex) {
                Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Properties get() {
        return configs;
    }

    public static void write(Properties properties) {
        try (OutputStream output = new FileOutputStream(CONFIGS_PATH)) {
            Properties encrypted = ConfigsEncryptor.encryptProperties(properties);
            encrypted.store(output, null);
        } catch (IOException ex) {
            Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void checkPropertiesFile() {
        try {
            File file = new File(CONFIGS_PATH);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists()) parent.mkdirs();
                if (file.createNewFile())
                    initProperties();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void initProperties() {
        Properties properties = new Properties();
        properties.put("password", "");
        properties.put("website", "http://www.rhoe.com/");
        properties.put("address", "");
        properties.put("version", "1.0");
        properties.put("currency", "$");
        properties.put("enclosing_layout", "true");
        properties.put("close_sale", "true");
        properties.put("converted_value", "");
        properties.put("picture", "");
        properties.put("mail_address", "");
        properties.put("min_on_sale", "minimum_quantity");
        properties.put("close_product", "true");
        properties.put("business_words", "");
        properties.put("language", "");
        properties.put("install_date", "");
        properties.put("second_currency_symbol", "");
        properties.put("advanced_currency_features", "false");
        properties.put("enterprise", "");
        properties.put("decimals", "2");
        properties.put("subs_key", "");
        properties.put("due_date", LocalDate.now().plusWeeks(2));
        write(properties);
    }

}
