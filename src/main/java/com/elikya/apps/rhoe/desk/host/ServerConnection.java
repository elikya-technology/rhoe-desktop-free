/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Mafole Loemelah
 */
public class ServerConnection {

    private WebTarget target;

    private static ServerConnection serverConnection;
    private static final String SERVER_URL = "http://8.210.167.16:8080/";

    private ServerConnection() {
        Client client = ClientBuilder.newClient();
        target = client.target(SERVER_URL);
    }

    public static ServerConnection getInstance() {
        if (serverConnection == null)
            serverConnection = new ServerConnection();
        return serverConnection;
    }

    public boolean isConnected() {
        try {
            URL url = new URL(SERVER_URL);
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    public String saveStore(Store store) {
        if (isConnected()) {
            Response response = target.path("stores").request().post(Entity.json(store));
            return response.readEntity(String.class);
        } else {
            Notifier.notify(StagesPaths.ERROR_NOTIF, ControlsHandler.getLanguage().getProperty("server_error"));
            return "";
        }
    }

    public String updateStore(Store store) {
        if (isConnected()) {
            Response response = target.path("stores").request().put(Entity.json(store));
            return response.readEntity(String.class);
        } else {
            Notifier.notify(StagesPaths.ERROR_NOTIF, ControlsHandler.getLanguage().getProperty("server_error"));
            return "";
        }
    }

}
