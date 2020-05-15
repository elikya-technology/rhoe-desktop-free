/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.host;

import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.configs.RhoeConfig;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;

import static com.elikya.apps.rhoe.desk.host.ServerTargetBuilder.getTarget;

public class BackendService {

    public static Optional<String> requestSendMail(String email) {
        try {
            WebTarget target = getTarget();
            Response response = target.path("/subscribers/send_mail").request()
                    .post(Entity.json(email));
            return Optional.ofNullable(response.readEntity(String.class));
        } catch (ProcessingException e) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return Optional.empty();
    }

    public static Optional<Subscriber> requestSaveAccount(Subscriber subscriber) {
        try {
            WebTarget target = getTarget();
            Response response = target.path("/subscribers/save_one").request().post(Entity.json(subscriber));
            return Optional.ofNullable(response.readEntity(Subscriber.class));
        } catch (ProcessingException e) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return Optional.empty();
    }

    public static void requestUpdateAccount(Subscriber subscriber) {
        try {
            WebTarget target = getTarget();
            Response response = target.path("/subscribers/update_one").request().put(Entity.json(subscriber));
            response.readEntity(Subscriber.class);
        } catch (ProcessingException e) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
    }

    public static Optional<Boolean> emailExists(String email) {
        try {
            WebTarget target = getTarget();
            Response response = target.path("/subscribers/email_exists")
                    .request().post(Entity.json(email));
            String result = response.readEntity(String.class);
            return Optional.of(Boolean.parseBoolean(result));
        } catch (ProcessingException e) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return Optional.empty();
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
        Properties configs = RhoeConfig.get();
        return configs.getProperty("subs_key");
    }

    public static boolean requestSubmitFeedback(Feedback feedback) {
        try {
            WebTarget target = getTarget();
            Response response = target.path("feedbacks/add-one").request().post(Entity.json(feedback));
            String value = response.readEntity(String.class);
            return Boolean.parseBoolean(value);
        } catch (ProcessingException e) {
            Properties lang = ControlsHandler.getLanguage();
            Notifier.notify(StagesPaths.ERROR_NOTIF, lang.getProperty("server_error"));
        }
        return false;
    }
}
