/*
 * Copyright (c) 2020, Elikya Technology.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.configs.RhoeConfig;
import com.elikya.apps.rhoe.desk.observers.impl.LanguageObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.LanguageObserver;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author Mafole Loemelah
 */
public class MainController extends Application implements Initializable, LanguageObserver {

    @FXML private AnchorPane container;
    @FXML private JFXButton hamburger;
    @FXML private JFXDrawer drawer;
    @FXML private Label title;
    @FXML private Label faq;
    @FXML private Label getStarted;

    public static final int DRAWER_WIDTH = 315;

    private static String text = "sales";
    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lang = ControlsHandler.getLanguage();
        setHamburgerListener();
        setDrawerEventHandler();
        setFaqMouseEvent();
        setGetStartedMouseHandler();
        ControlsHandler.setDrawer(drawer);
        LanguageObserverImpl.register(this);
        Stages.setMainContainer(container);
        updateLanguage();
        Stages.showLargeStage(StagesPaths.SALES);
        showGetStarted();
    }

    private void showGetStarted() {
            Platform.runLater(() -> {
                boolean showStarers = Boolean.parseBoolean(RhoeConfig.get().getProperty("show_starter"));
                if (showStarers) {
                    Platform.runLater(() -> {
                        try {
                            Thread.sleep(5000);
                            Stages.showDialog(StagesPaths.WELCOME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

    }

    @Override
    public void updateLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty(text));
        getStarted.setText(lang.getProperty("get_started"));
    }

    public static void setText(String _text) {
        text = _text;
    }

    private void setDrawerEventHandler() {
        drawer.setOnMouseClicked(event -> {
            ControlsHandler.closeDrawer();
            event.consume();
        });
    }

    private void setHamburgerListener() {
        hamburger.setOnAction(event -> {
            try {
                MenuController.setTitle(title);
                Parent parent = FXMLLoader.load(Stages.class.getResource(StagesPaths.MENU.getPath()));
                drawer.setSidePane(parent);
                if (drawer.isShown()) {
                    ControlsHandler.closeDrawer();
                } else {
                    drawer.setDefaultDrawerSize(DRAWER_WIDTH);
                    drawer.open();
                    drawer.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());
                }
            } catch (IOException ignored) {

            }
            event.consume();
        });
    }

    private void setFaqMouseEvent() {
        faq.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                getHostServices().showDocument("http://rhoe.elikya.tech/faq");
            }
        });
    }

    private void setGetStartedMouseHandler() {
        getStarted.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                Stages.showDialog(StagesPaths.WELCOME);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {}
}
