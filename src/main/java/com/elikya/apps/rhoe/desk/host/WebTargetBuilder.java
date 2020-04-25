/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.host;

import com.elikya.apps.rhoe.desk.util.BackendHostHandler;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class WebTargetBuilder {

    private static WebTarget target;

    public static WebTarget getSubscribersTarget() {
        if (target == null)
            buildSubscriberTarget();
        return target;
    }

    public static WebTarget getPaymentsTarget() {
        if (target == null)
            buildPaymentTarget();
        return target;
    }


    public static void buildSubscriberTarget() {
        Client client = ClientBuilder.newClient();
        target = client.target(BackendHostHandler.BACKEND_URI)
                .path("subscribers");
    }

    public static void buildPaymentTarget() {
        Client client = ClientBuilder.newClient();
        target = client.target(BackendHostHandler.BACKEND_URI).path("payments");
    }

}
