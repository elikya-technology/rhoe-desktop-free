/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.configs;

import com.elikya.apps.rhoe.desk.encoding.CriticalDataEncoder;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import javafx.application.Platform;

import java.util.Properties;

public class ConfigsValidator {

    public static void exitOnUnknownApplicationConfig(Properties configs) {
        try {
            if (!configs.getProperty("host_id").equals(CriticalDataEncoder.encodeHomeDirectory())) {
                Notifier.notify(StagesPaths.ERROR_NOTIF, "UNKNOWN APPLICATION CONFIGS");
                Thread.sleep(4000);
                Platform.exit();
            }
        } catch (InterruptedException e) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, "COULD NOT LOAD APPLICATION CONFIGS");
        }
    }

}
