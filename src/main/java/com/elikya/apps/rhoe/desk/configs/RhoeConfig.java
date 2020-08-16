/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.configs;

import com.elikya.apps.rhoe.desk.encoding.InternalId;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import javafx.application.Platform;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class RhoeConfig {

    private static Properties configs;
    private static final String CONFIGS_PATH = "cf/rhoe.properties";

    public static void load() {
        if (configs == null) {
            Properties properties = new Properties();
            try (InputStream input = new FileInputStream(CONFIGS_PATH)) {
                properties.load(input);
                configs = ConfigsEncryptor.decryptProperties(properties);
                ConfigsValidator.exitOnUnknownApplicationConfig(configs);
            } catch (IOException ex) {
                Notifier.notify(StagesPaths.ERROR_NOTIF, "Could not load configurations");
                Platform.exit();
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
            Notifier.notify(StagesPaths.ERROR_NOTIF, "Could not override configurations");
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
            Notifier.notify(StagesPaths.ERROR_NOTIF, "Could not create configurations folder");
        }
    }

    private static void initProperties() {
        Properties properties = new Properties();
        properties.put("password", "");
        properties.put("website", "http://rhoe.elikya.tech");
        properties.put("address", "");
        properties.put("version", "1.2.3");
        properties.put("currency", "$");
        properties.put("enclosing_layout", "true");
        properties.put("close_sale", "true");
        properties.put("converted_value", "");
        properties.put("picture", "");
        properties.put("min_on_sale", "minimum_quantity");
        properties.put("close_product", "true");
        properties.put("business_words", "");
        properties.put("language", "");
        properties.put("install_date", "");
        properties.put("second_currency_symbol", "");
        properties.put("advanced_currency_features", "false");
        properties.put("enterprise", "");
        properties.put("decimals", "2");
        properties.put("internal_id", InternalId.get());
        properties.put("show_starter", "true");
        write(properties);
    }

}
