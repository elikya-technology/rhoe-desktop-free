/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk;

import com.elikya.apps.rhoe.desk.ui.Stages;
import javafx.application.Application;
import javafx.stage.Stage;
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
        Stages.showPreloader();
    }

    @Override
    public void init() {
        ConfigurableApplicationContext context = SpringApplication.run(RhoeDesktopApplication.class);
        Stages.setApplicationContext(context);
    }

}
