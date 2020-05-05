/*
 * Copyright (c) 2020, Elikya Corporation.
 */

package com.elikya.apps.rhoe.desk.controller;

import com.elikya.apps.rhoe.desk.observers.impl.LanguageObserverImpl;
import com.elikya.apps.rhoe.desk.observers.interfaces.LanguageObserver;
import com.elikya.apps.rhoe.desk.ui.ControlsHandler;
import com.elikya.apps.rhoe.desk.ui.Notifier;
import com.elikya.apps.rhoe.desk.ui.Stages;
import com.elikya.apps.rhoe.desk.ui.StagesPaths;
import com.elikya.apps.rhoe.desk.util.LicenseListener;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import javafx.application.Application;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mafole Loemelah
 */
public class MainController extends Application implements Initializable, LanguageObserver {

    @FXML private AnchorPane container;
    @FXML private JFXButton hamburger;
    @FXML private JFXDrawer drawer;
    @FXML private Label title;
    @FXML private Label feedback;
    @FXML private Label faq;

    public static final int DRAWER_WIDTH = 315;

    private static String text = "sales";
    private Properties lang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lang = ControlsHandler.getLanguage();
        setHamburgerListener();
        setDrawerEventHandler();
        setFeedbackMouseEvent();
        setFaqMouseEvent();
        ControlsHandler.setDrawer(drawer);
        if (LicenseListener.licenseIsValid()) {
            LanguageObserverImpl.register(this);
            Stages.setMainContainer(container);
            updateLanguage();
            Stages.showLargeStage(StagesPaths.SALES);
        } else Notifier.notify(StagesPaths.WARNING_NOTIF, lang.getProperty("update_license"));
    }

    @Override
    public void updateLanguage() {
        lang = ControlsHandler.getLanguage();
        title.setText(lang.getProperty(text));
        feedback.setText(lang.getProperty("feedback"));
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
            } catch (IOException exception) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        "DRAWER NOT SHOWN", exception);
            }
            event.consume();
        });
    }

    private void setFeedbackMouseEvent() {
        feedback.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                Stages.showDialog(StagesPaths.FEEDBACK);
            }
        });
    }

    private void setFaqMouseEvent() {
        faq.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                getHostServices().showDocument("http://www.elikyarhoe.com/faq");
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {}
}
