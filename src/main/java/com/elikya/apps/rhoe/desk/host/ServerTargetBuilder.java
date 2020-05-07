/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.host;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class ServerTargetBuilder {

    private static WebTarget target;

    public static WebTarget getTarget() {
        if (target == null)
            buildTarget();
        return target;
    }


    public static void buildTarget() {
        try {
            Client client = ClientBuilder.newClient();
            target = client.target(BackendHostHandler.BACKEND_URI);
            target.request().get();
        } catch (ProcessingException ignored) {}
    }

}
