/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk;

import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RhoeDesktopApplication extends Application {

    public static void main(String[] args) {
        launch(RhoeDesktopApplication.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        Stages.showDialog(StagesPaths.WELCOME);
    }

    @Override
    public void init() throws InterruptedException {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(RhoeDesktopApplication.class);
            Stages.setApplicationContext(context);
        } catch (BeanCreationException e) {
            Notifier.notify(StagesPaths.ERROR_NOTIF, "Unknown Datasource");
            Thread.sleep(4000);
            Platform.exit();
        }
    }

}
