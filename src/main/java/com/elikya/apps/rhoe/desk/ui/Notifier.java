/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.ui;

import com.elikya.apps.rhoe.desk.controller.ErrorNotifController;
import com.elikya.apps.rhoe.desk.controller.InfoNotifController;
import com.elikya.apps.rhoe.desk.controller.SuccessNotifController;
import com.elikya.apps.rhoe.desk.controller.WarningNotifController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

/**
 *
 * @author Mafole Loemelah
 */
public class Notifier {

    public static void notify(StagesPaths name, String text) {
        Platform.runLater(() -> {
            Stage stage = new Stage(StageStyle.UNDECORATED);
            try {
                setNotifText(name, text);
                Parent parent = FXMLLoader.load(Notifier.class.getResource(name.getPath()));
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.setX(ScreenUtils.getScreenWidth() - 325);
                stage.setY(ScreenUtils.getScreenHeight() - 70);
                ScreenUtils.setFadeTransition(1, parent);
                stage.show();
                ScreenUtils.setDelay(stage, 3, ScreenUtils.NextStageContext.NONE);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    private static void setNotifText(StagesPaths name, String text) {
        switch (name) {
            case ERROR_NOTIF:
                ErrorNotifController.setContent(text);
                break;
            case INFO_NOTIF:
                InfoNotifController.setContent(text);
                break;
            case WARNING_NOTIF:
                WarningNotifController.setContent(text);
                break;
            default:
                SuccessNotifController.setContent(text);
                break;
        }
    }

}
