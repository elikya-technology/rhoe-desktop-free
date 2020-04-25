/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.util;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 *
 * @author Mafole Loemelah
 */
public class BackendHostHandler {

    public static String BACKEND_URI = "http://localhost:8080";

    public static boolean isConnected() {
        try {
            URL url = new URL(BACKEND_URI);
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (IOException exception) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return false;
    }

}
