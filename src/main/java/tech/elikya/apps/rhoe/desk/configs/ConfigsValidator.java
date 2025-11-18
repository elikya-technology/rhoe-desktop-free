/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.configs;

import tech.elikya.apps.rhoe.desk.encoding.InternalId;
import tech.elikya.apps.rhoe.desk.ui.Notifier;
import tech.elikya.apps.rhoe.desk.ui.StagesPaths;
import javafx.application.Platform;

import java.util.Properties;

public class ConfigsValidator {

    public static void exitOnUnknownApplicationConfig(Properties configs) {
        try {
            if (!configs.getProperty("internal_id").equals(InternalId.get())) {
                Notifier.notify(StagesPaths.ERROR_NOTIF, "Unknowns application configuration");
                Thread.sleep(4000);
                Platform.exit();
            }
        } catch (InterruptedException e) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, "Could not load application configuration");
        }
    }

}
