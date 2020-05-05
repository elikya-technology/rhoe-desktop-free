/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.host;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.Configs;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.OptionalInt;
import java.util.Properties;

import static com.elikya.apps.rhoe.desk.host.ServerTargetBuilder.*;

public class BackendService {

    public static String requestSendMail(String email) {
        WebTarget target = getTarget();
        Response response = target.path("/subscribers/send_mail").request()
                .post(Entity.json(email));
        return response.readEntity(String.class);
    }

    public static Subscriber requestSaveAccount(Subscriber subscriber) {
        WebTarget target = getTarget();
        Response response = target.path("/subscribers/save_one").request().post(Entity.json(subscriber));
        return response.readEntity(Subscriber.class);
    }

    public static Subscriber requestUpdateAccount(Subscriber subscriber) {
        WebTarget target = getTarget();
        Response response = target.path("/subscribers/update_one").request().put(Entity.json(subscriber));
        return response.readEntity(Subscriber.class);
    }

    public static boolean emailExists(String email) {
        WebTarget target = getTarget();
        Response response = target.path("/subscribers/email_exists")
                .request().post(Entity.json(email));
        return response.readEntity(Boolean.class);
    }

    public static OptionalInt requestNotSynchedMonths() {
        try {
            WebTarget target = getTarget();
            Response response = target.path("subscribers/payments/not_synched")
                    .request().post(Entity.json(getSubscriberKey()));
            String text = response.readEntity(String.class);
            return OptionalInt.of(Integer.parseInt(text));
        } catch (ProcessingException | NumberFormatException e) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return OptionalInt.empty();
    }

    private static String getSubscriberKey() {
        Properties configs = Configs.get();
        return configs.getProperty("subs_key");
    }

    public static boolean requestSubmitFeedback(Feedback feedback) {
        try {
            WebTarget target = getTarget();
            Response response = target.path("feedbacks/add-one").request().post(Entity.json(feedback));
            String value = response.readEntity(String.class);
            return Boolean.parseBoolean(value);
        } catch (ProcessingException e) {
            e.printStackTrace();
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return false;
    }
}
