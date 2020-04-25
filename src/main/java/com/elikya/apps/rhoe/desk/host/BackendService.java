/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.host;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.Configs;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.net.ConnectException;
import java.util.OptionalInt;
import java.util.Properties;

import static com.elikya.apps.rhoe.desk.host.WebTargetBuilder.*;

public class BackendService {

    public static String requestSendMail(String email) {
        WebTarget target = getSubscribersTarget();
        Response response = target.path("send_mail").request()
                .post(Entity.json(email));
        return response.readEntity(String.class);
    }

    public static Subscriber requestSaveAccount(Subscriber subscriber) {
        WebTarget target = getSubscribersTarget();
        Response response = target.path("save_one").request().post(Entity.json(subscriber));
        return response.readEntity(Subscriber.class);
    }

    public static Subscriber requestUpdateAccount(Subscriber subscriber) {
        WebTarget target = getSubscribersTarget();
        Response response = target.path("update_one").request().put(Entity.json(subscriber));
        return response.readEntity(Subscriber.class);
    }

    public static boolean emailExists(String email) {
        WebTarget target = getSubscribersTarget();
        Response response = target.path("email_exists")
                .request().post(Entity.json(email));
        return response.readEntity(Boolean.class);
    }

    public static OptionalInt requestNotSynchedMonths() {
        try {
            WebTarget target = getPaymentsTarget();
            Response response = target.path("not_synched").request().post(Entity.json(getSubscriberKey()));
            String text = response.readEntity(String.class);
            return OptionalInt.of(Integer.parseInt(text));
        } catch (ProcessingException e) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return OptionalInt.empty();
    }

    private static String getSubscriberKey() {
        Properties configs = Configs.get();
        return configs.getProperty("subs_key");
    }

}
